package org.systic.practice.ladders;

import org.systic.practice.Practice;
import org.systic.practice.matching.MatchType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Ladder {

    public final String name;
    public final ItemStack icon;
    public ItemStack[] items = new ItemStack[36];
    public ItemStack[] armor = new ItemStack[4];
    public ItemStack[] editor = new ItemStack[54];
    public boolean ranked = true;
    public boolean unranked = true;
    public boolean editable = true;

    public Ladder(String name, ItemStack icon){
        this.name = name;
        this.icon = icon;

        Practice.inst().ladder_manager.add(this);
        if(ranked) Practice.inst().queue_manager.createQueue(this, MatchType.RANKED);
        if(unranked) Practice.inst().queue_manager.createQueue(this, MatchType.UNRANKED);
    }

    public void applyDefault(Player player){
        player.getInventory().setContents(items);
        player.getInventory().setArmorContents(armor);
    }

}
