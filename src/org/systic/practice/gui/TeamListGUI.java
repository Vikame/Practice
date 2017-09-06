package org.systic.practice.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.systic.practice.Practice;
import org.systic.practice.gui.page.Page;
import org.systic.practice.gui.page.PageGUI;
import org.systic.practice.team.Team;
import org.systic.practice.team.TeamManager;
import org.systic.practice.util.Item;

import java.util.*;

public class TeamListGUI extends PageGUI {

    private static TeamListGUI instance;

    private TeamManager team_manager;
    private String item_names;
    private String lore_format;

    private Map<Team, ItemStack> items;

    private TeamListGUI(){
        super(Practice.getMessage("guis.team.name"));

        instance = this;
        team_manager = Practice.inst().team_manager;
        item_names = Practice.getMessage("guis.team.item-names");
        lore_format = Practice.getMessage("guis.team.lore-format");
        items = new HashMap<>();
    }

    @Override
    public void onClick(Player player, ItemStack item, ClickType type) {
        if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        Team t = team_manager.get(player);
        if(t == null){ // Shouldn't happen, but just in case?
            player.closeInventory();
            return;
        }

        Team team = team_manager.get(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
        if(team == null) return; // Should never happen.

        if(t == team){
            player.sendMessage(Practice.getMessage("team.cannot-duel-team"));
            return;
        }

        player.openInventory(new TeamDuelGUI(t, team).update());
    }

    public Inventory update(){
        for(Page page : pages){
            page.clear();
        }

        for(Team team : team_manager.all()){
            update(team);
        }

        if(pages.isEmpty()) return null;

        return pages.get(0).getInventory();
    }

    public void update(Team team) {
        if(pages.isEmpty()) new Page(this, 0);

        List<UUID> matchable = team.getMatchable();

        if(matchable.isEmpty()) return;

        Page page = pages.get(pages.size() - 1);
        if (!page.hasSpace()) page = new Page(this, pages.size() + 1);

        ItemStack item;
        if(items.containsKey(team)) item = items.get(team);
        else item = new ItemStack(Material.BEACON);

        List<String> names = new ArrayList<>();

        for (UUID uuid : matchable) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) names.add(lore_format.replace("%name%", player.getName()));
        }

        page.addItem(Item.create(item).name(item_names.replace("%name%", team.name)).lore(names).build());
    }

    public static TeamListGUI inst(){
        return (instance == null ? new TeamListGUI() : instance);
    }

}
