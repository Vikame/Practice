package org.systic.practice.gui;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.systic.citadel.util.C;
import org.systic.citadel.util.Ints;
import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.matching.TeamMatchRequest;
import org.systic.practice.matching.TeamRequestManager;
import org.systic.practice.team.Team;
import org.systic.practice.util.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamArenaSelectionGUI implements Listener {

    private Team sender, target;
    private Ladder ladder;
    private ItemStack random;
    private ItemStack spacer;
    private ArenaManager arena_manager;
    private TeamRequestManager team_request_manager;
    private String item_name;
    private Inventory inventory;

    public TeamArenaSelectionGUI(Ladder ladder, Team sender, Team target) {
        this.sender = sender;
        this.target = target;
        this.ladder = ladder;
        this.arena_manager = Practice.inst().arena_manager;
        this.team_request_manager = Practice.inst().team_request_manager;
        this.inventory = Bukkit.createInventory(null, Ints.roundUpToNearest(arena_manager.all().size(), 9), Practice.getMessage("guis.arena-select.name"));
        this.item_name = Practice.getMessage("guis.arena-select.item-names");
        this.random = Item.create(Material.NETHER_STAR).name(C.c("&cRandom")).build();
        this.spacer = Item.create(Material.STAINED_GLASS_PANE).name(C.c("&0")).data(15).build();

        update();

        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    public Inventory update() {
        Inventory inv = null;

        inventory.clear();

        Map<Integer, ItemStack> extra = inventory.addItem(random);
        if (!extra.isEmpty()) {
            inv = Bukkit.createInventory(null, Ints.roundUpToNearest(arena_manager.all().size(), 9), Practice.getMessage("guis.arena-select.name"));
            inv.setContents(inventory.getContents());
            inv.addItem(random);
        }

        Map<Integer, ItemStack> extra2 = inventory.addItem(spacer);
        if (!extra2.isEmpty()) {
            inv = Bukkit.createInventory(null, Ints.roundUpToNearest(arena_manager.all().size(), 9), Practice.getMessage("guis.arena-select.name"));
            inv.setContents(inventory.getContents());
            inv.addItem(spacer);
        }

        for (Arena arena : arena_manager.all()) {
            ItemStack item = Item.create(Material.EMPTY_MAP).name(item_name.replace("%name%", arena.name)).build();

            Map<Integer, ItemStack> extra3 = inventory.addItem(item);
            if (!extra3.isEmpty()) {
                inv = Bukkit.createInventory(null, Ints.roundUpToNearest(arena_manager.all().size(), 9), Practice.getMessage("guis.arena-select.name"));
                inv.setContents(inventory.getContents());
                inv.addItem(item);
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

        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();

        if (inv == null || item == null) return;

        if (inv.getName().equals(this.inventory.getName())) {
            e.setCancelled(true);

            if (target == null || target.disbanded) {
                p.sendMessage(Practice.getMessage("team.doesnt-exist"));
                p.closeInventory();
                return;
            }

            if (target.getOwner() == null || !target.getOwner().isOnline()) {
                p.sendMessage(Practice.getMessage("team.no-owner-online"));
                p.closeInventory();
                return;
            }

            if (item.isSimilar(random)) {
                Arena arena = arena_manager.random();

                p.closeInventory();

                team_request_manager.add(new TeamMatchRequest(ladder, sender, target, arena));
                sender.message(Practice.getMessage("team.duel.send").replace("%team%", target.name).replace("%ladder%", ladder.name));
                target.message(Practice.getMessage("team.duel.receive").replace("%team%", sender.name).replace("%ladder%", ladder.name));
                new FancyMessage(C.complete(Practice.getMessage("team.duel.receive-accept"))).command("/team accept " + p.getName()).send(target.getOwner());
            } else if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();

                if (meta.hasDisplayName()) {
                    Arena arena = arena_manager.get(ChatColor.stripColor(meta.getDisplayName()));
                    if (arena == null) return;

                    p.closeInventory();

                    team_request_manager.add(new TeamMatchRequest(ladder, sender, target, arena));
                    sender.message(Practice.getMessage("team.duel.send").replace("%team%", target.name).replace("%ladder%", ladder.name));
                    target.message(Practice.getMessage("team.duel.receive").replace("%team%", sender.name).replace("%ladder%", ladder.name));
                    new FancyMessage(C.complete(Practice.getMessage("team.duel.receive-accept"))).command("/team accept " + p.getName()).send(target.getOwner());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;

        Inventory inv = e.getInventory();

        if (inv.getName().equals(this.inventory.getName())) {
            HandlerList.unregisterAll(this);
        }
    }

}
