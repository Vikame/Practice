package org.systic.practice.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.systic.citadel.util.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item {

    private ItemStack stack;
    private ItemMeta meta;

    public Item(ItemStack base){
        this.stack = base.clone();
        this.meta = stack.getItemMeta();
    }

    public Item(Material base){
        this(new ItemStack(base));
    }

    public Item name(String name){
        this.meta.setDisplayName(C.c(name));
        return this;
    }

    public Item lore(List<String> lore){
        this.meta.setLore(lore);
        return this;
    }

    public Item lore(String... lore){
        this.meta.setLore(Arrays.asList(lore));
        return this;
    }

    public Item addLore(String lore){
        List<String> list = (this.meta.hasLore() ? this.meta.getLore() : new ArrayList<>());
        list.add(C.c(lore));

        this.meta.setLore(list);
        return this;
    }

    public Item amount(int amount){
        this.stack.setAmount(amount);
        return this;
    }

    public Item data(int data){
        return this.data((short)data);
    }

    public Item data(short data){
        this.stack.setDurability(data);
        return this;
    }

    public Item enchant(Enchantment enchant){
        return enchant(enchant, enchant.getMaxLevel());
    }

    public Item enchant(Enchantment enchant, int level){
        this.meta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemStack build(){
        this.stack.setItemMeta(meta);

        return this.stack;
    }

    public static Item create(ItemStack base){
        return new Item(base);
    }

    public static Item create(Material base){
        return new Item(base);
    }

}