package org.systic.practice.arena;

import org.systic.practice.util.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Selection {

    private static final Map<UUID, Selection> SELECTIONS = new HashMap<>();
    public static final ItemStack WAND = Item.create(Material.BLAZE_ROD)
            .name("&6Arena Selector")
            .addLore("&fLeft click to set the first spawn-position.")
            .addLore("&fRight click to set the second spawn-position")
            .build();

    public Location one, two;

    private Selection(Player player){
        SELECTIONS.put(player.getUniqueId(), this);
    }

    public void reset(){
        one = null;
        two = null;
    }

    public boolean isValid(){
        return one != null && two != null && one.getWorld().getName().equals(two.getWorld().getName());
    }

    public static Selection getSelection(Player player){
        if(SELECTIONS.containsKey(player.getUniqueId()))
            return SELECTIONS.get(player.getUniqueId());

        return new Selection(player);
    }

}
