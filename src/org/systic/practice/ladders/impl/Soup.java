package org.systic.practice.ladders.impl;

import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class Soup extends Ladder{

    public Soup() {
        super("Soup", Items.SOUP);

        this.items[0] = Item.create(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY).build();
        this.items[1] = Items.SPEED;
        this.items[9] = Items.SPEED;

        for(int i = 0; i < this.items.length; i++){
            if(this.items[i] == null) this.items[i] = Items.SOUP;
        }

        this.armor[3] = Item.create(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();
        this.armor[2] = Item.create(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();
        this.armor[1] = Item.create(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();
        this.armor[0] = Item.create(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();

        this.editable = false;
    }

}
