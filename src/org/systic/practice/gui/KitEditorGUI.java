package org.systic.practice.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.systic.citadel.util.C;
import org.systic.citadel.util.Ints;
import org.systic.practice.Practice;
import org.systic.practice.kit.KitManager;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.ladders.LadderManager;
import org.systic.practice.location.LocationManager;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitEditorGUI implements Listener{

    private static KitEditorGUI instance;

    private LadderManager ladder_manager;
    private LocationManager location_manager;
    private KitManager kit_manager;
    private String item_name;
    private List<String> item_lore;
    private Inventory inventory;

    private KitEditorGUI(){
        instance = this;

        FileConfiguration config = Practice.inst().getConfig();

        this.ladder_manager = Practice.inst().ladder_manager;
        this.location_manager = Practice.inst().location_manager;
        this.kit_manager = Practice.inst().kit_manager;
        this.inventory = Bukkit.createInventory(null, getSize(), Practice.getMessage("guis.editor.name"));
        this.item_name = Practice.getMessage("guis.editor.item-names");
        this.item_lore = new ArrayList<>();

        if(config.contains("guis.editor.item-lore")){
            for(String s : config.getStringList("guis.editor.item-lore")){
                item_lore.add(C.c(s));
            }
        }

        update();

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    public Inventory update(){
        Inventory inv = null;

        inventory.clear();
        for(Ladder ladder : ladder_manager.all()){
            if(!ladder.editable) continue;

            ItemStack item = Item.create(ladder.icon).name(item_name.replace("%name%", ladder.name)).build();

            Map<Integer, ItemStack> extra = inventory.addItem(item);
            if(!extra.isEmpty()){
                inv = Bukkit.createInventory(null, getSize(), Practice.getMessage("guis.editor.name"));
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

            if(item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();

                if(meta.hasDisplayName()) {
                    Ladder ladder = ladder_manager.get(ChatColor.stripColor(meta.getDisplayName()));

                    if(location_manager.contains("kit editor")){
                        p.teleport(location_manager.get("kit editor"));
                    }
                    p.sendMessage(Practice.getMessage("editor").replace("%ladder%", ladder.name));

                    kit_manager.startEditing(p, ladder);

                    p.getInventory().clear();
                    p.getInventory().setArmorContents(new ItemStack[4]);

                    p.closeInventory();

                    update();
                }
            }
        }
    }

    private int getSize(){
        int total = 0;
        for(Ladder ladder : ladder_manager.all()){
            if(ladder.editable) total++;
        }

        return Ints.roundUpToNearest(total, 9);
    }

    public static KitEditorGUI inst(){
        return (instance == null ? new KitEditorGUI() : instance);
    }

}
