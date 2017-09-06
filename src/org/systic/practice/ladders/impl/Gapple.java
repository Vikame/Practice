package org.systic.practice.ladders.impl;

import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Gapple extends Ladder {

    public Gapple() {
        super("Gapple", new ItemStack(Material.GOLDEN_APPLE, 1, (short)1));

        this.items[0] = Item.create(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL).enchant(Enchantment.FIRE_ASPECT).enchant(Enchantment.DURABILITY).build();
        this.items[1] = new ItemStack(Material.GOLDEN_APPLE, 64, (short)1);
        this.items[2] = Items.SPEED_EXTEND;
        this.items[3] = Items.STRENGTH_EXTEND;
        this.items[4] = Items.SPEED_EXTEND;
        this.items[5] = Items.STRENGTH_EXTEND;

        this.armor[3] = Item.create(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).enchant(Enchantment.DURABILITY).build();
        this.armor[2] = Item.create(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).enchant(Enchantment.DURABILITY).build();
        this.armor[1] = Item.create(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).enchant(Enchantment.DURABILITY).build();
        this.armor[0] = Item.create(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).enchant(Enchantment.DURABILITY)
                .enchant(Enchantment.PROTECTION_FALL).build();

        this.editable = false;
    }

}
