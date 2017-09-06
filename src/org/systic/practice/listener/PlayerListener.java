package org.systic.practice.listener;

import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.practice.Practice;
import org.systic.practice.generic.SpectateManager;
import org.systic.practice.generic.StaffManager;
import org.systic.practice.gui.KitEditorGUI;
import org.systic.practice.gui.RankedGUI;
import org.systic.practice.gui.TeamListGUI;
import org.systic.practice.gui.UnrankedGUI;
import org.systic.practice.kit.KitManager;
import org.systic.practice.ladders.impl.Soup;
import org.systic.practice.location.LocationManager;
import org.systic.practice.matching.*;
import org.systic.practice.stats.StatManager;
import org.systic.practice.util.Inventories;
import org.systic.practice.util.Items;
import org.systic.practice.vanish.VanishManager;

public class PlayerListener implements Listener {

    private final QueueManager queue_manager;
    private final LocationManager location_manager;
    private final StatManager stat_manager;
    private final TeamMatchManager team_match_manager;
    private final MatchManager match_manager;
    private final KitManager kit_manager;
    private final SpectateManager spectate_manager;
    private final VanishManager vanish_manager;
    private final StaffManager staff_manager;

    public PlayerListener(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
        queue_manager = Practice.inst().queue_manager;
        location_manager = Practice.inst().location_manager;
        stat_manager = Practice.inst().stat_manager;
        team_match_manager = Practice.inst().team_match_manager;
        match_manager = Practice.inst().match_manager;
        kit_manager = Practice.inst().kit_manager;
        spectate_manager = Practice.inst().spectate_manager;
        vanish_manager = Practice.inst().vanish_manager;
        staff_manager = Practice.inst().staff_manager;
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();

            if(match_manager.get(p) == null && team_match_manager.get(p) == null){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();

            if(match_manager.get(p) == null && team_match_manager.get(p) == null){
                p.setHealth(20);
            }
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();

            Match match = match_manager.get(p);
            TeamMatch team_match = team_match_manager.get(p);

            if (match == null && team_match == null || ((match != null && match.ladder instanceof Soup) || (team_match != null && team_match.ladder instanceof Soup))) {
                p.setSaturation(10);
                e.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(null);

        boolean ps = PlayerSettings.get(p).get("show players in lobby", true);

        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if(staff_manager.isInStaff(pl)){
                vanish_manager.show(pl, p);
                vanish_manager.hide(p, pl);
            }else {
                if (PlayerSettings.get(pl).get("show players in lobby", true)) {
                    vanish_manager.show(pl, p);
                } else vanish_manager.hide(pl, p);

                if (ps) {
                    vanish_manager.show(p, pl);
                } else vanish_manager.hide(p, pl);
            }
        }

        p.setGameMode(GameMode.SURVIVAL);

        for (PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());

        stat_manager.updateName(p);

        if (location_manager.contains("spawn")) {
            p.teleport(location_manager.get("spawn"));
        }
        Inventories.giveDefault(p);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);

        Player p = e.getPlayer();
        Queue queue = queue_manager.getQueue(p);
        if(queue != null) queue.queue.remove(p.getUniqueId());

        for(Player pl : Bukkit.getServer().getOnlinePlayers()){
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle()));
        }

        stat_manager.updateName(p);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.setDeathMessage(null);
        e.getDrops().clear();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Inventories.giveDefault(e.getPlayer());

        if (location_manager.contains(e.getPlayer().getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
            e.setRespawnLocation(location_manager.get(e.getPlayer().getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (!p.hasPermission("systic.interact") && match_manager.get(p) == null && team_match_manager.get(p) == null)
            e.setCancelled(true);

        if(item == null || (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)) return;

        if(item.isSimilar(Items.KIT_EDITOR)){

            p.openInventory(KitEditorGUI.inst().update());

        }else if(item.isSimilar(Items.UNRANKED)){

            p.openInventory(UnrankedGUI.inst().update());

        } else if (item.isSimilar(Items.RANKED) || item.isSimilar(Items.RANKED_LOCKED_1_8) || item.isSimilar(Items.RANKED_LOCKED_1_7)) {

            if(Practice.inst().ranked_locked){
                p.sendMessage(Practice.getMessage("generic.ranked-locked"));
                return;
            }

            if(stat_manager.getUnrankedWins(p) < 15 && !p.hasPermission("systic.rankedbypass")){
                p.sendMessage(Practice.getMessage("generic.insufficient-unranked").replace("%amount%", "" + (15 - stat_manager.getUnrankedWins(p))));
                return;
            }

            p.openInventory(RankedGUI.inst().update());

        }else if(item.isSimilar(Items.LEAVE_QUEUE)){
            Queue queue = queue_manager.getQueue(p);

            if(queue != null){
                queue.unqueue(p);
                p.sendMessage(Practice.getMessage("queue.leave").replace("%ladder%", queue.ladder.name));
            }

            if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
                p.teleport(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
            }
            Inventories.giveDefault(p);
        }else if(item.isSimilar(Items.TEAM_LIST)){
            p.openInventory(TeamListGUI.inst().update());
        }else if(item.isSimilar(Items.LEAVE_SPECTATOR)){

            spectate_manager.stopSpectating(p);
            p.sendMessage(Practice.getMessage("spectator.stop-spectating"));

            if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
                p.teleport(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
            }
            Inventories.giveDefault(p);

        } else if (item.isSimilar(Items.CREATE_TEAM)) {
            Bukkit.dispatchCommand(p, "team create");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();

        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();


        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();

        if (!location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"))
            return;

        if(e.getTo().getY() <= 0){
            Match match = match_manager.get(p);

            if (match != null) return;
            else {
                TeamMatch team_match = team_match_manager.get(p);

                if (team_match != null) return;
            }

            e.setTo(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();

        if(kit_manager.isEditing(p)){
            e.getItemDrop().remove();
        }else if(match_manager.get(p) == null && team_match_manager.get(p) == null){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
        e.setCancelled(true);
    }

}
