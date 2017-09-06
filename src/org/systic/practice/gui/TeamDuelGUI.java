package org.systic.practice.gui;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.citadel.util.C;
import org.systic.citadel.util.Ints;
import org.systic.practice.Practice;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.ladders.LadderManager;
import org.systic.practice.matching.TeamMatchRequest;
import org.systic.practice.matching.TeamRequestManager;
import org.systic.practice.team.Team;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamDuelGUI implements Listener{

    private Team sender, target;
    private LadderManager ladder_manager;
    private TeamRequestManager team_request_manager;
    private String item_name;
    private Inventory inventory;

    public TeamDuelGUI(Team sender, Team target){
        this.sender = sender;
        this.target = target;
        this.team_request_manager = Practice.inst().team_request_manager;
        this.ladder_manager = Practice.inst().ladder_manager;
        this.inventory = Bukkit.createInventory(null, getSize(), Practice.getMessage("guis.team-duel.name"));
        this.item_name = Practice.getMessage("guis.team-duel.item-names");

        update();

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    public Inventory update(){
        Inventory inv = null;

        inventory.clear();
        for(Ladder ladder : ladder_manager.all()){
            ItemStack item = Item.create(ladder.icon).name(item_name.replace("%name%", ladder.name)).build();

            Map<Integer, ItemStack> extra = inventory.addItem(item);
            if(!extra.isEmpty()){
                inv = Bukkit.createInventory(null, getSize(), Practice.getMessage("guis.team-duel.name"));
                inv.setContents(inventory.getContents());
                inv.addItem(item);
            }
        }

        if(inv != null) {
            List<Player> players = new ArrayList<>();

            for (HumanEntity viewer : inventory.getViewers()) {
                if (!(viewer instanceof Player)) continue;

                players.add((Player)viewer);
            }

            for(Player p : players){
                p.closeInventory();
                p.openInventory(inv);
            }

            inventory = inv;
        }

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player)e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();

        if(inv == null || item == null) return;

        if(inv.getName().equals(this.inventory.getName())){
            e.setCancelled(true);

            if(target == null || target.disbanded){
                p.sendMessage(Practice.getMessage("team.doesnt-exist"));
                p.closeInventory();
                return;
            }

            if(target.getOwner() == null || !target.getOwner().isOnline()){
                p.sendMessage(Practice.getMessage("team.no-owner-online"));
                p.closeInventory();
                return;
            }

            if(item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();

                if(meta.hasDisplayName()) {
                    Ladder ladder = ladder_manager.get(ChatColor.stripColor(meta.getDisplayName()));

                    p.closeInventory();

                    if (p.hasPermission("systic.selectmap")) {
                        new BukkitRunnable() {
                            public void run() {
                                p.openInventory(new TeamArenaSelectionGUI(ladder, sender, target).update());
                            }
                        }.runTaskLater(Practice.inst(), 1);
                    } else {
                        team_request_manager.add(new TeamMatchRequest(ladder, sender, target));
                        sender.message(Practice.getMessage("team.duel.send").replace("%team%", target.name).replace("%ladder%", ladder.name));
                        target.message(Practice.getMessage("team.duel.receive").replace("%team%", sender.name).replace("%ladder%", ladder.name));
                        new FancyMessage(C.complete(Practice.getMessage("team.duel.receive-accept"))).command("/team accept " + p.getName()).send(target.getOwner());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(!(e.getPlayer() instanceof Player)) return;

        Inventory inv = e.getInventory();

        if(inv.getName().equals(this.inventory.getName())){
            HandlerList.unregisterAll(this);
        }
    }

    private int getSize(){
        int total = 0;
        for(Ladder ladder : ladder_manager.all()){
            if(ladder.unranked) total++;
        }

        return Ints.roundUpToNearest(total, 9);
    }

}
