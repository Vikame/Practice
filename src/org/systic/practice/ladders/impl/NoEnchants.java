package org.systic.practice.ladders.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.systic.citadel.util.C;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;

public class NoEnchants extends Ladder{

    public NoEnchants() {
        super("No Enchants", new ItemStack(Material.ENCHANTMENT_TABLE));

        this.items[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.items[1] = Items.PEARL;
        this.items[2] = Items.SPEED;
        this.items[8] = Items.FOOD;
        this.items[9] = Items.SPEED;
        this.items[18] = Items.SPEED;
        this.items[27] = Items.SPEED;

        for(int i = 0; i < this.items.length; i++){
            if(this.items[i] == null) this.items[i] = Items.HEALING;
        }

        this.armor[3] = Item.create(Material.DIAMOND_HELMET).build();
        this.armor[2] = Item.create(Material.DIAMOND_CHESTPLATE).build();
        this.armor[1] = Item.create(Material.DIAMOND_LEGGINGS).build();
        this.armor[0] = Item.create(Material.DIAMOND_BOOTS).name(C.c("&fWe lied.")).enchant(Enchantment.PROTECTION_FALL).build();

        this.editor[0] = this.armor[3];
        this.editor[9] = this.armor[2];
        this.editor[18] = this.armor[1];
        this.editor[27] = this.armor[0];

        this.editor[8] = this.items[0];
        this.editor[17] = this.items[1];
        this.editor[26] = this.items[2];
        this.editor[35] = this.items[8];

        for(int i = 0; i < this.editor.length; i++){
            if(this.editor[i] == null) this.editor[i] = Items.HEALING;
        }
    }

}
