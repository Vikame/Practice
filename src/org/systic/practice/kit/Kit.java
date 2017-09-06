package org.systic.practice.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kit {

    public ItemStack[] contents = new ItemStack[36];
    public ItemStack[] armor = new ItemStack[4];

    public void apply(Player player){
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
    }

    public static Kit fromPlayer(Player player){
        PlayerInventory inv = player.getInventory();

        Kit kit = new Kit();

        kit.contents = inv.getContents();
        kit.armor = inv.getArmorContents();

        return kit;
    }

}
