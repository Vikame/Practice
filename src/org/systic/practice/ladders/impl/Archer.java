package org.systic.practice.ladders.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;

public class Archer extends Ladder{

    public Archer() {
        super("Archer", new ItemStack(Material.BOW));

        this.items[0] = Item.create(Material.BOW).enchant(Enchantment.ARROW_INFINITE).enchant(Enchantment.DURABILITY).build();
        this.items[1] = Items.FOOD;
        this.items[8] = new ItemStack(Material.ARROW, 64);

        this.armor[3] = Item.create(Material.LEATHER_HELMET).enchant(Enchantment.DURABILITY, 10).build();
        this.armor[2] = Item.create(Material.LEATHER_CHESTPLATE).enchant(Enchantment.DURABILITY, 10).build();
        this.armor[1] = Item.create(Material.LEATHER_LEGGINGS).enchant(Enchantment.DURABILITY, 10).build();
        this.armor[0] = Item.create(Material.LEATHER_BOOTS).enchant(Enchantment.DURABILITY, 10)
                .enchant(Enchantment.PROTECTION_FALL).build();

        this.editable = false;
    }

}
