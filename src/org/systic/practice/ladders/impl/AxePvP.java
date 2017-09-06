package org.systic.practice.ladders.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;

public class AxePvP extends Ladder{

    public AxePvP() {
        super("AxePvP", new ItemStack(Material.IRON_AXE));

        this.items[0] = Item.create(Material.IRON_AXE).enchant(Enchantment.DAMAGE_ALL, 1).build();
        this.items[1] = new ItemStack(Material.GOLDEN_APPLE, 16);
        this.items[2] = Items.SPEED;
        for(int i = 3; i < 9; i++){
            this.items[i] = Items.HEALING;
        }

        this.armor[3] = Item.create(Material.IRON_HELMET).build();
        this.armor[2] = Item.create(Material.IRON_CHESTPLATE).build();
        this.armor[1] = Item.create(Material.IRON_LEGGINGS).build();
        this.armor[0] = Item.create(Material.IRON_BOOTS).build();

        this.editable = false;
    }

}
