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
import org.systic.practice.ladders.Ladder;
import org.systic.practice.ladders.LadderManager;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.MatchType;
import org.systic.practice.matching.Queue;
import org.systic.practice.matching.QueueManager;
import org.systic.practice.util.Inventories;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RankedGUI implements Listener{

    private static RankedGUI instance;

    private QueueManager queue_manager;
    private LadderManager ladder_manager;
    private MatchManager match_manager;
    private String item_name;
    private List<String> item_lore;
    private Inventory inventory;

    private RankedGUI(){
        instance = this;

        FileConfiguration config = Practice.inst().getConfig();

        this.queue_manager = Practice.inst().queue_manager;
        this.ladder_manager = Practice.inst().ladder_manager;
        this.match_manager = Practice.inst().match_manager;
        this.inventory = Bukkit.createInventory(null, getSize(), Practice.getMessage("guis.ranked.name"));
        this.item_name = Practice.getMessage("guis.ranked.item-names");
        this.item_lore = new ArrayList<>();

        if(config.contains("guis.ranked.item-lore")){
            for(String s : config.getStringList("guis.ranked.item-lore")){
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
            if(!ladder.ranked) continue;

            ItemStack item = Item.create(ladder.icon).name(item_name.replace("%name%", ladder.name)).lore(getLore(ladder)).build();

            Map<Integer, ItemStack> extra = inventory.addItem(item);
            if(!extra.isEmpty()){
                inv = Bukkit.createInventory(null, getSize(), Practice.getMessage("guis.ranked.name"));
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

                if (meta.hasDisplayName()) {
                    Ladder ladder = ladder_manager.get(ChatColor.stripColor(meta.getDisplayName()));

                    Queue queue = queue_manager.getQueue(ladder, MatchType.RANKED);
                    queue.queue(p);

                    p.sendMessage(Practice.getMessage("queue.ranked-join").replace("%ladder%", ladder.name));

                    p.closeInventory();
                    Inventories.giveQueued(p);

                    queue.attemptMatchmaking();

                    update();
                }
            }
        }
    }

    private int getSize(){
        int total = 0;
        for(Ladder ladder : ladder_manager.all()){
            if(ladder.ranked) total++;
        }

        return Ints.roundUpToNearest(total, 9);
    }

    private List<String> getLore(Ladder ladder){
        List<String> ret = new ArrayList<>();

        for(String s : item_lore){
            ret.add(s.replace("%queued%", "" + queue_manager.sizeOf(ladder, MatchType.RANKED)).replace("%matches%", "" + match_manager.totalOf(ladder, MatchType.RANKED)));
        }

        return ret;
    }

    public static RankedGUI inst(){
        return (instance == null ? new RankedGUI() : instance);
    }

}
