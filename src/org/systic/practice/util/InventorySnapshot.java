package org.systic.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.systic.citadel.Common;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;

import java.util.Map;
import java.util.UUID;

public class InventorySnapshot implements Listener{

    private static final Map<UUID, InventorySnapshot> inventories = new TimedMap<>(60);

    public final Inventory inventory;
    public final UUID uniqueId;

    private InventorySnapshot(Player player){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());

        inventory = Bukkit.createInventory(null, 54, Practice.getMessage("guis.inventory-snapshot").replace("%player%", player.getName()));
        uniqueId = UUID.randomUUID();

        PlayerInventory inv = player.getInventory();

        for(int i = 0; i < 36; i++){
            inventory.setItem(i, inv.getItem(i));
        }
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i + 36, inv.getArmorContents()[i]);
        }

        inventory.setItem(48, Item.create(Material.SKULL_ITEM).data(player.isDead() ? 0 : 3)
                .name(Practice.getMessage("items.inventory-snapshot.health")
                        .replace("%health%", Common.DECIMAL_FORMAT0x0.format(Math.round(player.getHealth()) / 2.0D))).build());

        inventory.setItem(49, Item.create(Material.COOKED_BEEF).name(Practice.getMessage("items.inventory-snapshot.food")
                .replace("%food%", "" + player.getFoodLevel())).build());

        Item potions = Item.create(Material.POTION).name(Practice.getMessage("items.inventory-snapshot.potions"));
        for(PotionEffect effect : player.getActivePotionEffects()){
            int seconds = effect.getDuration() / 20;
            int minutes = 0;

            while(seconds >= 60){
                seconds-=60;
                minutes++;
            }

            potions.addLore(C.c(getColoredName(effect.getType()) + " " + toSimpleRoman(effect.getAmplifier()) + ": &f"
                    + minutes + ":" + (seconds < 10 && seconds > 0 ? "0" : "") + seconds));
        }

        inventory.setItem(50, potions.build());

        inventories.put(uniqueId, this);
    }

    private String getColoredName(PotionEffectType type){
        String ret;

        switch(type.getId()){
            case 1:
                ret = C.c("&bSpeed");
                break;
            case 2:
                ret = C.c("&7Slowness");
                break;
            case 3:
                ret = C.c("&eHaste");
                break;
            case 4:
                ret = C.c("&3Mining Fatigue");
                break;
            case 5:
                ret = C.c("&4Strength");
                break;
            case 6:
                ret = C.c("&cInstant Health"); // Shouldn't happen, just in case though?
                break;
            case 7:
                ret = C.c("&8Instant Damage"); // Also shouldn't happen.
                break;
            case 8:
                ret = C.c("&aJump Boost");
                break;
            case 9:
                ret = C.c("&7Confusion");
                break;
            case 10:
                ret = C.c("&dRegeneration");
                break;
            case 11:
                ret = C.c("&7Resistance");
                break;
            case 12:
                ret = C.c("&6Fire Resistance");
                break;
            case 13:
                ret = C.c("&1Water Breathing");
                break;
            case 14:
                ret = C.c("&7Invisibility");
                break;
            case 15:
                ret = C.c("&8Blindness");
                break;
            case 16:
                ret = C.c("&5Night Vision");
                break;
            case 17:
                ret = C.c("&2Hunger");
                break;
            case 18:
                ret = C.c("&8Weakness");
                break;
            case 19:
                ret = C.c("&2Poison");
                break;
            case 20:
                ret = C.c("&0Wither");
                break;
            case 21:
                ret = C.c("&cHealth Boost");
                break;
            case 22:
                ret = C.c("&6Absorption");
                break;
            case 23:
                ret = C.c("&eSaturation");
                break;
            default:
                ret = C.c("&cUnknown");
                break;
        }

        return ret;
    }

    private String toSimpleRoman(int amp){
        switch (amp) {
            case 0:
                return "I";
            case 1:
                return "II";
            case 2:
                return "III";
            case 3:
                return "IV";
            case 4:
                return "V";
            case 5:
                return "VI";
            case 6:
                return "VII";
            case 7:
                return "VIII";
            case 8:
                return "IX";
            case 9:
                return "X";
            default:
                return "Level " + amp;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();

        if(inv == null || item == null) return;

        if(inv.getName().equals(this.inventory.getName())) {
            e.setCancelled(true);
        }
    }

    public static InventorySnapshot create(Player player){
        return new InventorySnapshot(player);
    }

    public static InventorySnapshot get(UUID uuid){
        return inventories.get(uuid);
    }

}
