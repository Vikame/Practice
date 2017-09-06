package org.systic.practice.gui.page;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.practice.Practice;
import org.systic.practice.util.Item;

public class Page implements Listener{

    private static final ItemStack BACK = Item.create(Material.ARROW).name(ChatColor.GRAY + "Back").build();
    private static final ItemStack FORWARD = Item.create(Material.ARROW).name(ChatColor.GRAY + "Forward").build();

    public final PageGUI parent;
    public final int page;
    public final Inventory inventory;

    public Page(PageGUI parent, int page){
        this.parent = parent;
        this.page = page;
        this.inventory = Bukkit.createInventory(null, 54, parent.title.replace("%page%", "" + page));

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());

        parent.pages.add(this);
    }

    public PageGUI getParent() {
        return parent;
    }

    public int getPage() {
        return page;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player)e.getWhoClicked();

        if(e.getClickedInventory() == null || e.getCurrentItem() == null) return;

        if(e.getClickedInventory().getName().equals(this.inventory.getName())) {
            onClick(p, e.getCurrentItem(), e.getClick());
            e.setCancelled(true);
        }
    }

    @SuppressWarnings("all")
    public void onClick(Player p, ItemStack item, ClickType type){
        if(item.equals(FORWARD)){

            p.closeInventory();

            new BukkitRunnable(){
                public void run(){
                    p.openInventory(parent.getPage(page + 1).getInventory());
                }
            }.runTaskLater(Practice.inst(), 1);

            return;

        }else if(item.equals(BACK)){

            p.closeInventory();

            new BukkitRunnable(){
                public void run(){
                    p.openInventory(parent.getPage(page - 1).getInventory());
                }
            }.runTaskLater(Practice.inst(), 1);

            return;

        }

        parent.onClick(p, item, type);
    }

    public void clear(){
        this.inventory.clear();

        int last = 53;
        if(parent.getPage(page + 1) != null) this.inventory.setItem(last, FORWARD);
        if(page > 1) this.inventory.setItem(last-8, BACK);
    }

    public Inventory getInventory() {
        int last = 53;
        if(parent.getPage(page + 1) != null) this.inventory.setItem(last, FORWARD);
        if(page > 1) this.inventory.setItem(last-8, BACK);

        return inventory;
    }

    public boolean addItem(ItemStack item){
        return inventory.addItem(item).isEmpty();
    }

    public boolean hasSpace(){
        int max = 54;
        int curr = 0;

        for(ItemStack i : inventory.getContents()){
            if(i == null || i.getType() == Material.AIR) continue;

            curr++;
        }

        return curr < max;
    }
}