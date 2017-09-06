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
import org.systic.citadel.util.Ints;
import org.systic.practice.Practice;
import org.systic.practice.generic.StaffManager;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StaffListGUI implements Listener {

    private static StaffListGUI instance;

    private StaffManager staff_manager;
    private String item_names;
    private Inventory inventory;

    private StaffListGUI() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());

        staff_manager = Practice.inst().staff_manager;
        item_names = Practice.getMessage("guis.staff.item-names");
        inventory = Bukkit.createInventory(null, Ints.roundUpToNearest(staff_manager.staff.size(), 9), Practice.getMessage("guis.staff.name"));
    }

    public Inventory update() {
        Inventory inv = null;

        inventory.clear();
        for (UUID uuid : staff_manager.staff) {
            Player p = Bukkit.getPlayer(uuid);

            if (p != null && p.isOnline()) {
                ItemStack item = Item.create(Material.SKULL_ITEM).data(3).name(item_names.replace("%player%", p.getName())).build();

                Map<Integer, ItemStack> extra = inventory.addItem(item);
                if (!extra.isEmpty()) {
                    inv = Bukkit.createInventory(null, Ints.roundUpToNearest(staff_manager.staff.size(), 9), Practice.getMessage("guis.staff.name"));
                    inv.setContents(inventory.getContents());
                    inv.addItem(item);
                }
            }
        }

        if (inv != null) {
            List<Player> players = new ArrayList<>();

            for (HumanEntity viewer : inventory.getViewers()) {
                if (!(viewer instanceof Player)) continue;

                players.add((Player) viewer);
            }

            for (Player p : players) {
                p.closeInventory();
                p.openInventory(inv);
            }

            inventory = inv;
        }

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();

        if (inv == null || item == null) return;

        if (inv.getName().equals(this.inventory.getName())) {
            e.setCancelled(true);
        }
    }

    public static StaffListGUI inst() {
        return (instance == null ? new StaffListGUI() : instance);
    }

}
