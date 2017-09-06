package org.systic.practice.arena;

import org.systic.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SelectionListener implements Listener {

    public SelectionListener(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(p.getGameMode() != GameMode.CREATIVE) return;

        ItemStack item = e.getItem();
        if(item == null || e.getClickedBlock() == null || !item.isSimilar(Selection.WAND)) return;

        Selection selection = Selection.getSelection(p);

        Location l = e.getClickedBlock().getLocation().add(0.5, 0, 0.5);
        l.setPitch(0);
        l.setYaw(p.getLocation().getYaw());

        if(e.getAction() == Action.LEFT_CLICK_BLOCK){

            selection.one = l;
            p.sendMessage(Practice.getMessage("admin.wand.select").replace("%val%", "first"));

            e.setCancelled(true);

        }else if(e.getAction() == Action.RIGHT_CLICK_BLOCK){

            selection.two = l;
            p.sendMessage(Practice.getMessage("admin.wand.select").replace("%val%", "second"));

            e.setCancelled(true);

        }
    }

}
