package org.systic.practice.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.systic.citadel.util.C;
import org.systic.citadel.util.Ints;
import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArenaListGUI implements Listener{

    private static ArenaListGUI instance;

    private ArenaManager manager;
    private Inventory inventory;

    private ArenaListGUI(){
        instance = this;

        this.manager = Practice.inst().arena_manager;
        this.inventory = Bukkit.createInventory(null, Ints.roundUpToNearest(manager.all().size(), 9), Practice.getMessage("guis.arena-list"));

        update();

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    public Inventory update(){
        Inventory inv = null;

        inventory.clear();
        for(Arena arena : manager.all()){
            ItemStack item = Item.create(Material.NETHER_STAR).name(C.c("&c" + arena.name)).build();

            Map<Integer, ItemStack> extra = inventory.addItem(item);
            if(!extra.isEmpty()){
                inv = Bukkit.createInventory(null, Ints.roundUpToNearest(manager.all().size(), 9), Practice.getMessage("guis.arena-list"));
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

            Arena arena = manager.get(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
            if(arena != null){
                p.teleport(arena.one);
                p.sendMessage(C.c("&7Teleported to spawn-point of arena " + arena.name + "."));
            }
        }
    }

    public static ArenaListGUI inst(){
        return (instance == null ? new ArenaListGUI() : instance);
    }

}
