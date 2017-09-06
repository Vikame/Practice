package org.systic.practice.generic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.practice.Practice;
import org.systic.practice.commands.impl.TeleportCommand;
import org.systic.practice.gui.StaffListGUI;
import org.systic.practice.matching.Match;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatch;
import org.systic.practice.matching.TeamMatchManager;
import org.systic.practice.util.Inventories;
import org.systic.practice.util.Items;
import org.systic.practice.vanish.VanishManager;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class StaffManager implements Listener {

    public final Set<UUID> staff;
    public MatchManager match_manager;
    public TeamMatchManager team_match_manager;
    public VanishManager vanish_manager;

    public StaffManager(){
        staff = new HashSet<>();
        match_manager = Practice.inst().match_manager;
        team_match_manager = Practice.inst().team_match_manager;
        vanish_manager = Practice.inst().vanish_manager;

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        disableStaff(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (!p.hasPermission("systic.staff") || !staff.contains(p.getUniqueId())) return;

        if(item == null || (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)) return;

        if(item.isSimilar(Items.RANDOM_TELEPORT)){

            if(ThreadLocalRandom.current().nextBoolean()){
                Match match = match_manager.random();

                if(match == null){
                    TeamMatch team_match = team_match_manager.random();

                    if(team_match == null){
                        p.sendMessage(Practice.getMessage("admin.staff.random-teleport.fail"));
                        return;
                    }

                    Player target = getRandomPlayer(team_match, ThreadLocalRandom.current().nextBoolean() ? team_match.one_players : team_match.two_players);
                    if(target == null){
                        p.sendMessage(Practice.getMessage("admin.staff.random-teleport.fail"));
                        return;
                    }

                    TeleportCommand.teleport(p, target);

                }else{

                    TeleportCommand.teleport(p, ThreadLocalRandom.current().nextBoolean() ? match.one : match.two);

                }
            }else{
                TeamMatch team_match = team_match_manager.random();

                if(team_match != null){

                    Player target = getRandomPlayer(team_match, ThreadLocalRandom.current().nextBoolean() ? team_match.one_players : team_match.two_players);
                    if(target == null){
                        p.sendMessage(Practice.getMessage("admin.staff.random-teleport.fail"));
                        return;
                    }

                    TeleportCommand.teleport(p, target);

                }else{

                    Match match = match_manager.random();

                    if(match == null){
                        p.sendMessage(Practice.getMessage("admin.staff.random-teleport.fail"));
                        return;
                    }

                    TeleportCommand.teleport(p, ThreadLocalRandom.current().nextBoolean() ? match.one : match.two);

                }

            }

        }else if(item.isSimilar(Items.BETTER_VIEW)){
            e.setCancelled(true);
        } else if (item.isSimilar(Items.ONLINE_STAFF)) {
            e.setCancelled(true);
            p.openInventory(StaffListGUI.inst().update());
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPermission("systic.staff") || !staff.contains(p.getUniqueId())) return;

        if (!(e.getRightClicked() instanceof Player)) return;

        ItemStack item = p.getItemInHand();
        if (item == null || !item.isSimilar(Items.VIEW_INVENTORY)) return;

        p.openInventory(((Player) e.getRightClicked()).getInventory());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (staff.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (staff.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && staff.contains(e.getEntity().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() == null || e.getCurrentItem() == null || !(e.getWhoClicked() instanceof Player)) return;

        if (staff.contains(((Player) e.getWhoClicked()).getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e){
        if(staff.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    private Player getRandomPlayer(TeamMatch match, List<UUID> members){
        List<Player> choice = new ArrayList<>();
        for(UUID uuid : members){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline() && !match.dead.contains(uuid)) choice.add(player);
        }

        return choice.isEmpty() ? null : choice.get(ThreadLocalRandom.current().nextInt(choice.size()));
    }

    public boolean isInStaff(Player player){
        return staff.contains(player.getUniqueId());
    }

    public void enableStaff(Player player){
        if(staff.add(player.getUniqueId())) {
            Inventories.giveStaff(player);

            Items.ONLINE_STAFF.setAmount(staff.size());

            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if(staff.contains(p.getUniqueId())){
                    vanish_manager.show(player, p);
                    vanish_manager.show(p, player);

                    p.getInventory().setItem(7, Items.ONLINE_STAFF);
                }else{
                    vanish_manager.show(player, p);
                    vanish_manager.hide(p, player);
                }
            }
        }
    }

    public void disableStaff(Player player){
        if(staff.remove(player.getUniqueId())){
            Inventories.giveDefault(player);

            Items.ONLINE_STAFF.setAmount(staff.size());

            boolean ps = PlayerSettings.get(player).get("show players in lobby", true);

            for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                if (staff.contains(pl.getUniqueId())) {
                    pl.getInventory().setItem(7, Items.ONLINE_STAFF);
                }

                if (PlayerSettings.get(pl).get("show players in lobby", true)) {
                    vanish_manager.show(pl, player);
                } else vanish_manager.hide(pl, player);

                if (ps) {
                    vanish_manager.show(player, pl);
                } else vanish_manager.hide(player, pl);
            }
        }
    }

}
