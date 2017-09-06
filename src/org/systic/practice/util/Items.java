package org.systic.practice.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;

public class Items {

    public static final ItemStack KIT_EDITOR = Item.create(Material.BOOK).name(Practice.getMessage("items.kit-editor")).build();
    public static final ItemStack UNRANKED = Item.create(Material.IRON_SWORD).name(Practice.getMessage("items.unranked")).build();
    public static final ItemStack RANKED = Item.create(Material.DIAMOND_SWORD).name(Practice.getMessage("items.ranked")).build();
    public static final ItemStack CREATE_TEAM = Item.create(Material.REDSTONE).name(Practice.getMessage("items.create-team")).build();

    public static final ItemStack RANKED_LOCKED_1_8 = Item.create(Material.BARRIER).name(Practice.getMessage("items.ranked-locked")).build();
    public static final ItemStack RANKED_LOCKED_1_7 = Item.create(Material.DIAMOND_SWORD).name(Practice.getMessage("items.ranked-locked")).build();

    public static final ItemStack LEAVE_QUEUE = Item.create(Material.INK_SACK).data(1).name(Practice.getMessage("items.leave-queue")).build();

    public static final ItemStack SPEED = new Potion(PotionType.SPEED, 2).toItemStack(1);
    public static final ItemStack FIRE_RES = new Potion(PotionType.FIRE_RESISTANCE, 1).extend().toItemStack(1);
    public static final ItemStack HEALING = new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1);
    public static final ItemStack FOOD = new ItemStack(Material.COOKED_BEEF, 64);
    public static final ItemStack SOUP = new ItemStack(Material.MUSHROOM_SOUP);
    public static final ItemStack PEARL = new ItemStack(Material.ENDER_PEARL, 16);

    public static final ItemStack SPEED_EXTEND = new Potion(PotionType.SPEED, 2).extend().toItemStack(1);
    public static final ItemStack STRENGTH_EXTEND = new Potion(PotionType.STRENGTH, 2).extend().toItemStack(1);

    public static final ItemStack POISON = new Potion(PotionType.POISON).splash().toItemStack(1);
    public static final ItemStack SLOWNESS = new Potion(PotionType.SLOWNESS).splash().toItemStack(1);

    public static final ItemStack DEFAULT_KIT = Item.create(Material.BOOK).name(C.c("&7Default Kit")).build();

    public static final ItemStack TEAM_LIST = Item.create(Material.NETHER_STAR).name(Practice.getMessage("items.team-list")).build();

    public static final ItemStack LEAVE_SPECTATOR = Item.create(Material.REDSTONE_TORCH_ON).name(Practice.getMessage("items.leave-spectator")).build();

    public static final ItemStack RANDOM_TELEPORT = Item.create(Material.WATCH).name(Practice.getMessage("items.random-teleport")).build();
    public static final ItemStack COMPASS = Item.create(Material.COMPASS).name(Practice.getMessage("items.compass")).build();
    public static final ItemStack BETTER_VIEW = Item.create(Material.CARPET).name(ChatColor.BLACK + "").build();
    public static final ItemStack VIEW_INVENTORY = Item.create(Material.BOOK).name(Practice.getMessage("items.view-inventory")).build();
    public static final ItemStack ONLINE_STAFF = Item.create(Material.SKULL_ITEM).data(3).name(Practice.getMessage("items.online-staff")).build();
}
