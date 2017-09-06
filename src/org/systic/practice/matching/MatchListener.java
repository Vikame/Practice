package org.systic.practice.matching;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.citadel.Common;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.generic.StaffManager;
import org.systic.practice.kit.Kit;
import org.systic.practice.kit.KitManager;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.location.LocationManager;
import org.systic.practice.stats.StatManager;
import org.systic.practice.team.Team;
import org.systic.practice.util.Inventories;
import org.systic.practice.util.InventorySnapshot;
import org.systic.practice.util.Items;
import org.systic.practice.vanish.VanishManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchListener implements Listener {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;
    private final LocationManager location_manager;
    private final VanishManager vanish_manager;
    private final StatManager stat_manager;
    private final KitManager kit_manager;
    private final StaffManager staff_manager;
    private final Map<UUID, Long> enderpearl_cooldown;

    public MatchListener(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
        match_manager = Practice.inst().match_manager;
        team_match_manager = Practice.inst().team_match_manager;
        location_manager = Practice.inst().location_manager;
        vanish_manager = Practice.inst().vanish_manager;
        stat_manager = Practice.inst().stat_manager;
        kit_manager = Practice.inst().kit_manager;
        staff_manager = Practice.inst().staff_manager;
        enderpearl_cooldown = new HashMap<>();
    }

    public long getPearlCooldown(Player player){
        if(!enderpearl_cooldown.containsKey(player.getUniqueId())) return 0;

        return enderpearl_cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
    }

    public void setPearlCooldown(Player player, long time){
        enderpearl_cooldown.put(player.getUniqueId(), time);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();
            Player damager = (Player)e.getDamager();

            TeamMatch m1 = team_match_manager.get(p);
            TeamMatch m2 = team_match_manager.get(damager);

            if(m1 != null && m2 != null && m1 == m2) {
                if (vanish_manager.canSee(p, damager) && vanish_manager.canSee(damager, p)) {
                    if(m1.get(p) == m1.get(damager)) e.setCancelled(true);
                } else e.setCancelled(true);
            } else if (!(vanish_manager.canSee(p, damager) && vanish_manager.canSee(damager, p))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(!e.hasItem()) return;

        if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) return;

        Player p = e.getPlayer();
        Match match = match_manager.get(p);
        TeamMatch team_match = team_match_manager.get(p);

        if(match == null && team_match == null) return;

        ItemStack item = e.getItem();
        Material type = item.getType();

        if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
            Ladder ladder = null;
            if(match != null) ladder = match.ladder;
            else ladder = team_match.ladder;

            if(item.isSimilar(Items.DEFAULT_KIT)){
                ladder.applyDefault(p);
            }else{
                String name = item.getItemMeta().getDisplayName();

                if(name.startsWith(C.c("&7Kit #"))){
                    int kit = Integer.parseInt(name.substring(7));

                    List<Kit> kits = kit_manager.getKits(p, ladder);
                    if(kits.size() < kit) return;

                    kits.get(kit-1).apply(p);
                }
            }
        }else if(type == Material.ENDER_PEARL) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                p.updateInventory();
                return;
            }

            if ((match == null ? team_match.isPreMatch() : match.isPreMatch())) {

                e.setCancelled(true);
                p.updateInventory();
                p.sendMessage(Practice.getMessage("generic.pearl-prematch"));

            } else {

                if (enderpearl_cooldown.containsKey(p.getUniqueId())) {
                    long time = enderpearl_cooldown.get(p.getUniqueId()) - System.currentTimeMillis();

                    if (time > 0) {
                        e.setCancelled(true);
                        p.sendMessage(Practice.getMessage("generic.pearl-cooldown").replace("%time%", Common.DECIMAL_FORMAT0x0.format((time / 1000.0D))));
                    } else enderpearl_cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 16000);
                } else {
                    enderpearl_cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 16000);
                }

            }
        }else if(type == Material.MUSHROOM_SOUP && (match == null ? team_match.ladder.name.equalsIgnoreCase("Soup") : match.ladder.name.equalsIgnoreCase("Soup"))) {
            if (p.getHealth() >= p.getMaxHealth()) return;

            p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + 7));

            new BukkitRunnable(){
                public void run(){
                    p.setItemInHand(new ItemStack(Material.BOWL));
                    p.updateInventory();
                }
            }.runTaskLaterAsynchronously(Practice.inst(), 1);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();

        enderpearl_cooldown.remove(p.getUniqueId());

        Match match = match_manager.get(p);
        TeamMatch team_match = team_match_manager.get(p);
        if(match != null){
            Player opposite = match.opposite(p);

            InventorySnapshot loser = InventorySnapshot.create(p);
            InventorySnapshot victor = InventorySnapshot.create(opposite);

            enderpearl_cooldown.remove(opposite.getUniqueId());

            match.message(Practice.getMessage("match.win").replace("%winner%", opposite.getName()).replace("%health%", Common.DECIMAL_FORMAT0x0.format(Math.round(opposite.getHealth()) / 2.0D)));

            if(match.type == MatchType.RANKED) {
                int elo1 = stat_manager.getElo(p, match.ladder);
                int elo2 = stat_manager.getElo(opposite, match.ladder);
                int change = stat_manager.getEloChange(elo1, elo2);

                stat_manager.setElo(p, match.ladder, elo1 - change);
                stat_manager.setElo(opposite, match.ladder, elo2 + change);

                p.sendMessage(Practice.getMessage("match.ranked-end").replace("%old_elo%", "" + elo1).replace("%new_elo%", "" + (elo1 - change))
                        .replace("%change%", ChatColor.RED + "-" + change));
                opposite.sendMessage(Practice.getMessage("match.ranked-end").replace("%old_elo%", "" + elo2).replace("%new_elo%", "" + (elo2 + change))
                        .replace("%change%", ChatColor.GREEN + "+" + change));
            }else if(match.type == MatchType.UNRANKED){
                stat_manager.addUnrankedWin(opposite);
            }

            match.message(new FancyMessage(C.complete(Practice.getMessage("match.inventory-snapshot.pre")))
                    .then(C.complete(Practice.getMessage("match.inventory-snapshot.after")).replace("%player%", p.getName()))
                    .command("/_ " + loser.uniqueId.toString())
                    .then(" ")
                    .then(C.complete(Practice.getMessage("match.inventory-snapshot.after")).replace("%player%", opposite.getName()))
                    .command("/_ " + victor.uniqueId.toString()));

            for(UUID uuid : match.getHiddenOnFinish()){
                Player player = Bukkit.getPlayer(uuid);
                if(player != null && player.isOnline()){
                    if (!PlayerSettings.get(player).get("show players in lobby", true)) {
                        vanish_manager.hide(player, p);
                        vanish_manager.hide(player, opposite);
                    }
                }
            }

            match_manager.end(match);

            handle(p);
            handle(opposite);

            new BukkitRunnable(){
                public void run(){
                    if(p.isDead()) p.spigot().respawn();
                }
            }.runTaskLater(Practice.inst(), 5);
        }else if(team_match != null){
            Location death = p.getLocation().clone();

            team_match.dead.add(p.getUniqueId());
            Team winner = team_match.getWinner();

            team_match.message(Practice.getMessage("match.team-death").replace("%player%", p.getName()).replace("%health%", Common.DECIMAL_FORMAT0x0.format(Math.round(p.getHealth()) / 2.0D)));

            if(winner != null){
                for(UUID uuid : team_match.one_players){
                    Player player = Bukkit.getPlayer(uuid);

                    if(player != null && player.isOnline()) {
                        handle(player);

                        for (UUID other : team_match.getHiddenOnFinish()) {
                            Player pl = Bukkit.getPlayer(other);
                            if(pl != null && pl.isOnline()){
                                if (!PlayerSettings.get(pl).get("show players in lobby")) {
                                    vanish_manager.hide(pl, player);
                                }
                            }
                        }
                    }
                }

                for(UUID uuid : team_match.two_players){
                    Player player = Bukkit.getPlayer(uuid);

                    if(player != null && player.isOnline()) {
                        handle(player);

                        for (UUID other : team_match.getHiddenOnFinish()) {
                            Player pl = Bukkit.getPlayer(other);
                            if(pl != null && pl.isOnline()){
                                if (!PlayerSettings.get(pl).get("show players in lobby")) {
                                    vanish_manager.hide(pl, player);
                                }
                            }
                        }
                    }
                }

                team_match.message(Practice.getMessage("match.team-win").replace("%team%", winner.name));
                team_match_manager.end(team_match);
            }

            handle(p);

            new BukkitRunnable(){
                public void run(){
                    if(p.isDead()){
                        p.spigot().respawn();
                    }
                }
            }.runTaskLater(Practice.inst(), 5);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        enderpearl_cooldown.remove(p.getUniqueId());

        Match match = match_manager.get(p);
        TeamMatch team_match = team_match_manager.get(p);
        if(match != null){
            Player opposite = match.opposite(p);

            InventorySnapshot loser = InventorySnapshot.create(p);
            InventorySnapshot victor = InventorySnapshot.create(opposite);

            enderpearl_cooldown.remove(opposite.getUniqueId());

            match.message(Practice.getMessage("match.win").replace("%winner%", opposite.getName()).replace("%health%", Common.DECIMAL_FORMAT0x0.format(Math.round(opposite.getHealth()) / 2.0D)));

            if(match.type == MatchType.RANKED) {
                int elo1 = stat_manager.getElo(p, match.ladder);
                int elo2 = stat_manager.getElo(opposite, match.ladder);
                int change = stat_manager.getEloChange(elo1, elo2);

                stat_manager.setElo(p, match.ladder, elo1 - change);
                stat_manager.setElo(opposite, match.ladder, elo2 + change);

                p.sendMessage(Practice.getMessage("match.ranked-end").replace("%old_elo%", "" + elo1).replace("%new_elo%", "" + (elo1 - change))
                        .replace("%change%", ChatColor.RED + "-" + change));
                opposite.sendMessage(Practice.getMessage("match.ranked-end").replace("%old_elo%", "" + elo2).replace("%new_elo%", "" + (elo2 + change))
                        .replace("%change%", ChatColor.GREEN + "+" + change));
            }else if(match.type == MatchType.UNRANKED){
                stat_manager.addUnrankedWin(opposite);
            }

            match.message(new FancyMessage(C.complete(Practice.getMessage("match.inventory-snapshot.pre")))
                    .then(C.complete(Practice.getMessage("match.inventory-snapshot.after")).replace("%player%", p.getName()))
                    .command("/_ " + loser.uniqueId.toString())
                    .then(" ")
                    .then(C.complete(Practice.getMessage("match.inventory-snapshot.after")).replace("%player%", opposite.getName()))
                    .command("/_ " + victor.uniqueId.toString()));

            for(UUID uuid : match.getHiddenOnFinish()){
                Player player = Bukkit.getPlayer(uuid);
                if(player != null && player.isOnline()){
                    if (!PlayerSettings.get(player).get("show players in lobby", true)) {
                        vanish_manager.hide(player, p);
                        vanish_manager.hide(player, opposite);
                    }
                }
            }

            match_manager.end(match);

            handle(p);
            handle(opposite);
        }else if(team_match != null){
            team_match.dead.add(p.getUniqueId());
            Team winner = team_match.getWinner();

            team_match.message(Practice.getMessage("match.team-death").replace("%player%", p.getName()));

            if(winner != null){

                for(UUID uuid : team_match.one_players){
                    Player player = Bukkit.getPlayer(uuid);

                    if(player != null && player.isOnline()) {
                        handle(player);

                        for (UUID other : team_match.getHiddenOnFinish()) {
                            Player pl = Bukkit.getPlayer(other);
                            if(pl != null && pl.isOnline()){
                                if (!PlayerSettings.get(pl).get("show players in lobby")) {
                                    vanish_manager.hide(pl, player);
                                }
                            }
                        }
                    }
                }

                for(UUID uuid : team_match.two_players){
                    Player player = Bukkit.getPlayer(uuid);

                    if(player != null && player.isOnline()) {
                        handle(player);

                        for (UUID other : team_match.getHiddenOnFinish()) {
                            Player pl = Bukkit.getPlayer(other);
                            if(pl != null && pl.isOnline()){
                                if (!PlayerSettings.get(pl).get("show players in lobby")) {
                                    vanish_manager.hide(pl, player);
                                }
                            }
                        }
                    }
                }

                team_match.message(Practice.getMessage("match.team-win").replace("%team%", winner.name));
                team_match_manager.end(team_match);
            }

            handle(p);
        }
    }

    public void handle(Player p) {
        Inventories.giveDefault(p);

        boolean ps = PlayerSettings.get(p).get("show players in lobby", true);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!ps || staff_manager.isInStaff(player)) {
                vanish_manager.hide(p, player);
            } else {
                vanish_manager.show(p, player);
            }
        }

        if(!p.isDead()) {
            if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"))
                p.teleport(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));

            p.setHealth(20);
            p.setFireTicks(0);
            p.setFoodLevel(20);
            p.setSaturation(10);

            for (PotionEffect eff : p.getActivePotionEffects()) {
                p.removePotionEffect(eff.getType());
            }
        }
    }

}
