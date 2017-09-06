package org.systic.practice;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.citadel.lag.Profile;
import org.systic.citadel.lag.Profiler;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.citadel.util.DatabaseConnection;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.arena.SelectionListener;
import org.systic.practice.commands.impl.*;
import org.systic.practice.generic.FreezeManager;
import org.systic.practice.generic.SpectateManager;
import org.systic.practice.generic.StaffManager;
import org.systic.practice.kit.KitManager;
import org.systic.practice.ladders.LadderManager;
import org.systic.practice.ladders.impl.*;
import org.systic.practice.listener.CitadelListener;
import org.systic.practice.listener.PlayerListener;
import org.systic.practice.listener.WorldListener;
import org.systic.practice.location.LocationManager;
import org.systic.practice.matching.*;
import org.systic.practice.runnable.QueueCheckTask;
import org.systic.practice.scoreboard.BoardHandler;
import org.systic.practice.stats.FlatFileStatManager;
import org.systic.practice.stats.MySQLStatManager;
import org.systic.practice.stats.StatManager;
import org.systic.practice.team.TeamManager;
import org.systic.practice.util.Config;
import org.systic.practice.util.Inventories;
import org.systic.practice.vanish.PLCompat;
import org.systic.practice.vanish.VanishManager;

import java.util.HashMap;
import java.util.Map;

public class Practice extends JavaPlugin {

    private static Practice instance;

    public Config arenas;
    public Config locations;
    public ArenaManager arena_manager;
    public LadderManager ladder_manager;
    public VanishManager vanish_manager;
    public QueueManager queue_manager;
    public MatchManager match_manager;
    public LocationManager location_manager;
    public Config kits;
    public KitManager kit_manager;
    public StatManager stat_manager;
    public TeamManager team_manager;
    public TeamMatchManager team_match_manager;
    public RequestManager request_manager;
    public MatchListener match_listener;
    public TeamRequestManager team_request_manager;
    public FreezeManager freeze_manager;
    public SpectateManager spectate_manager;
    public StaffManager staff_manager;
    public DatabaseConnection database_connection;
    public Config stats;
    public Profiler profiler;

    public boolean ranked_locked;
    public Map<String, String> messages;

    public void onEnable(){
        instance = this;
        saveDefaultConfig();

        arenas = new Config(this, "arenas.yml");
        locations = new Config(this, "locations.yml");
        kits = new Config(this, "kits.yml");
        (location_manager = new LocationManager()).load();
        (arena_manager = new ArenaManager()).load();
        spectate_manager = new SpectateManager();
        ladder_manager = new LadderManager();
        vanish_manager = new VanishManager();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PLCompat());
        queue_manager = new QueueManager();
        match_manager = new MatchManager();
        (kit_manager = new KitManager()).load();
        team_manager = new TeamManager();
        team_match_manager = new TeamMatchManager();
        request_manager = new RequestManager();
        team_request_manager = new TeamRequestManager();
        staff_manager = new StaffManager();
        profiler = new Profiler(this);

        String stat = getConfig().getString("stat-manager");
        if (stat.equalsIgnoreCase("mysql")) {
            System.out.println("[" + getName() + "] Using MySQLStatManager for statistic persistence.");

            database_connection = new DatabaseConnection(this, "practice");
            stat_manager = new MySQLStatManager();
        } else if (stat.equalsIgnoreCase("file")) {
            System.out.println("[" + getName() + "] Using FlatFileStatManager for statistic persistence.");

            stats = new Config(this, "stats.yml");
            stat_manager = new FlatFileStatManager();
        } else {
            System.out.println("[" + getName() + "] Invalid stat manager {" + stat + "}, defaulting to FlatFileStatManager.");
            System.out.println("[" + getName() + "] To fix this issue, set 'stat-manager' to either 'mysql' or 'file'.");

            stat_manager = new FlatFileStatManager();
        }

        if (getConfig().contains("last-stat-manager")) {
            String last = getConfig().getString("last-stat-manager");

            if (last.equalsIgnoreCase("mysql") && stat.equalsIgnoreCase("file")) {

                System.out.println("[" + getName() + "] Migrating MySQLStatManager to FlatFileStatManager.");
                long now = System.currentTimeMillis();

                database_connection = new DatabaseConnection(this, "practice");
                MySQLStatManager manager = new MySQLStatManager();
                manager.load();

                stat_manager.migrate(manager);

                database_connection = null;

                System.out.println("[" + getName() + "] Migrated MySQLStatManager to FlatFileStatManager in " + (System.currentTimeMillis() - now) + "ms.");

            } else if (last.equalsIgnoreCase("file") && stat.equalsIgnoreCase("mysql")) {

                System.out.println("[" + getName() + "] Migrating FlatFileStatManager to MySQLStatManager.");
                long now = System.currentTimeMillis();

                stats = new Config(this, "stats.yml");

                FlatFileStatManager manager = new FlatFileStatManager();
                manager.load();

                stat_manager.migrate(manager);

                stats = null;

                System.out.println("[" + getName() + "] Migrated FlatFileStatManager to MySQLStatManager in " + (System.currentTimeMillis() - now) + "ms.");

            }
        }

        System.out.println("[" + getName() + "] Loading statistics...");
        long now = System.currentTimeMillis();

        stat_manager.load();

        System.out.println("[" + getName() + "] Loaded statistics in " + (System.currentTimeMillis()-now) + "ms.");

        System.out.println("[" + getName() + "] Registering Citadel profiles...");

        Practice.inst().profiler.register(new Profile("Matchmaking", Material.DIAMOND_SWORD));
        Practice.inst().profiler.register(new Profile("Retrieve Match", Material.MAGMA_CREAM));
        Practice.inst().profiler.register(new Profile("Retrieve Team Match", Material.MAGMA_CREAM));
        Practice.inst().profiler.register(new Profile("Mass Scoreboard Update (ASync)", Material.NETHER_STAR));
        Practice.inst().profiler.register(new Profile("Save", Material.FEATHER));

        System.out.println("[" + getName() + "] Finished registering Citadel profiles.");

        ranked_locked = false;

        new QueueCheckTask();

        // Initialize listeners.
        new SelectionListener();
        new PlayerListener();
        new WorldListener();
        match_listener = new MatchListener();
        freeze_manager = new FreezeManager();
        new CitadelListener();

        // Citadel
        PlayerSettings.DEFAULTS.put("duel requests", true);
        PlayerSettings.DEFAULTS.put("show players in lobby", true);
        PlayerSettings.DEFAULTS.put("time", true);
        PlayerSettings.ENABLED_MATERIALS.put("time", Material.WATCH);
        PlayerSettings.DISABLED_MATERIALS.put("time", Material.WATCH);
        PlayerSettings.ENABLED_NAMES.put("time", "&eDay");
        PlayerSettings.DISABLED_NAMES.put("time", "&9Night");

        // Initialize commands.
        new ArenaCommand();
        new SetSpawnCommand();
        new SpawnCommand();
        new SetEditorCommand();
        new DuelInventoryCommand();
        new LockRankedCommand();
        new TeleportCommand();
        new HideCommand();
        new ShowCommand();
        new LeaderboardCommand();
        new TeamCommand();
        new DuelCommand();
        new AcceptCommand();
        new FreezeCommand();
        new SpectateCommand();
        new PingCommand();
        new StaffCommand();
        new ToggleDuelCommand();
        new TogglePlayersCommand();

        // Initialize ladders.
        new NoDebuff();
        new Gapple();
        new NoEnchants();
        new Debuff();
        new Archer();
        new Soup();
        new AxePvP();

        // Other
        new BoardHandler();

        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            boolean ps = PlayerSettings.get(p).get("show players in lobby", true);

            for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                if (PlayerSettings.get(pl).get("show players in lobby", true)) {
                    vanish_manager.show(pl, p);
                } else vanish_manager.hide(pl, p);

                if (ps) {
                    vanish_manager.show(p, pl);
                } else vanish_manager.hide(p, pl);
            }

            if (location_manager.contains("spawn")) {
                p.teleport(location_manager.get("spawn"));
            }

            Inventories.giveDefault(p);
        }

        new BukkitRunnable() {
            public void run() {
                Practice.inst().profiler.begin("Save");
                arena_manager.save();
                location_manager.save();
                kit_manager.save();
                stat_manager.save();

                String last = stat_manager instanceof FlatFileStatManager ? "file" : "mysql";
                if (!getConfig().contains("last-stat-manager") || !getConfig().getString("last-stat-manager").equals(last)) {
                    getConfig().set("last-stat-manager", last);
                    saveConfig();
                }

                Practice.inst().profiler.end("Save");
            }
        }.runTaskTimerAsynchronously(this, 20 * 60 * 10, 20 * 60 * 10);
    }

    public void onDisable() {
        arena_manager.save();
        location_manager.save();
        kit_manager.save();
        stat_manager.save();

        String last = stat_manager instanceof FlatFileStatManager ? "file" : "mysql";
        if (!getConfig().contains("last-stat-manager") || !getConfig().getString("last-stat-manager").equals(last)) {
            getConfig().set("last-stat-manager", last);
            saveConfig();
        }

        if (database_connection != null) database_connection.close();
    }

    public static Practice inst(){
        return instance;
    }

    public static String getMessage(String path){
        if(instance.messages == null) instance.messages = new HashMap<>();
        else {
            if (instance.messages.containsKey(path))
                return instance.messages.get(path);
        }

        FileConfiguration config = instance.getConfig();

        if(!config.contains(path)){
            System.out.println("Missing " + path + " in config.");

            String msg = C.c("&cCould not find message.");

            instance.messages.put(path, msg);
            return msg;
        }

        String val = C.c(config.getString(path));

        instance.messages.put(path, val);

        return val;
    }

}
