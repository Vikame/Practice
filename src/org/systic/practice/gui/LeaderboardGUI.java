package org.systic.practice.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.systic.practice.Practice;
import org.systic.practice.stats.StatManager;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardGUI implements Listener{

    private static LeaderboardGUI instance;

    private StatManager stat_manager;
    private String item_name;
    private Inventory inventory;

    private LeaderboardGUI(){
        instance = this;

        this.stat_manager = Practice.inst().stat_manager;
        this.inventory = Bukkit.createInventory(null, 9, Practice.getMessage("guis.leaderboard.name"));
        this.item_name = Practice.getMessage("guis.leaderboard.item-names");

        update();

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    public Inventory update(){
        Inventory inv = null;

        inventory.clear();

        for(Map.Entry<Integer, String> entry : stat_manager.getLeaderboards(9).entrySet()){
            ItemStack item = Item.create(Material.SKULL_ITEM).data(3).name(item_name.replace("%name%", entry.getValue()).replace("%elo%", "" + entry.getKey())).build();

            Map<Integer, ItemStack> extra = inventory.addItem(item);
            if(!extra.isEmpty()){
                inv = Bukkit.createInventory(null, 9, Practice.getMessage("guis.leaderboard.name"));
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

        if(inv.getName().equals(this.inventory.getName())) {
            e.setCancelled(true);
        }
    }

    public static LeaderboardGUI inst(){
        return (instance == null ? new LeaderboardGUI() : instance);
    }

}
