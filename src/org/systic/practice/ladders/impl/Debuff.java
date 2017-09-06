package org.systic.practice.ladders.impl;

import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class Debuff extends Ladder {

    public Debuff() {
        super("Debuff", Items.POISON);

        this.items[0] = Item.create(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.FIRE_ASPECT).enchant(Enchantment.DURABILITY).build();
        this.items[1] = Items.PEARL;
        this.items[2] = Items.SPEED;
        this.items[3] = Items.FIRE_RES;
        this.items[4] = Items.POISON;
        this.items[5] = Items.SLOWNESS;
        this.items[8] = Items.FOOD;

        this.items[9] = Items.SPEED;
        this.items[10] = Items.POISON;
        this.items[11] = Items.SLOWNESS;
        this.items[18] = Items.SPEED;
        this.items[19] = Items.POISON;
        this.items[20] = Items.SLOWNESS;
        this.items[27] = Items.SPEED;
        this.items[28] = Items.POISON;
        this.items[29] = Items.SLOWNESS;

        for(int i = 0; i < this.items.length; i++){
            if(this.items[i] == null) this.items[i] = Items.HEALING;
        }

        this.armor[3] = Item.create(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();
        this.armor[2] = Item.create(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();
        this.armor[1] = Item.create(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY).build();
        this.armor[0] = Item.create(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY)
                .enchant(Enchantment.PROTECTION_FALL).build();

        this.editor[0] = this.armor[3];
        this.editor[1] = this.items[4];
        this.editor[9] = this.armor[2];
        this.editor[10] = this.items[5];
        this.editor[18] = this.armor[1];
        this.editor[27] = this.armor[0];

        this.editor[8] = this.items[0];
        this.editor[17] = this.items[1];
        this.editor[26] = this.items[2];
        this.editor[35] = this.items[3];
        this.editor[44] = this.items[8];

        for(int i = 0; i < this.editor.length; i++){
            if(this.editor[i] == null) this.editor[i] = Items.HEALING;
        }
    }

}
