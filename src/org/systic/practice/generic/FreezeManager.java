package org.systic.practice.generic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.practice.Practice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FreezeManager extends BukkitRunnable implements Listener {

    public List<UUID> frozen;

    public FreezeManager(){
        frozen = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
        runTaskTimerAsynchronously(Practice.inst(), 100, 100);
    }

    public void freeze(Player player){
        frozen.add(player.getUniqueId());
    }

    public void unfreeze(Player player){
        frozen.remove(player.getUniqueId());
    }

    public boolean isFrozen(Player player){
        return frozen.contains(player.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();

        if(isFrozen(p)) {
            Location f = e.getFrom();
            Location t = e.getTo();

            if (f.getBlockX() != t.getBlockX() || f.getBlockY() < t.getBlockY() || f.getBlockZ() != t.getBlockZ()) {
                e.setTo(f.clone().setDirection(t.getDirection()));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        if(frozen.contains(p.getUniqueId())){
            String msg = Practice.getMessage("admin.freeze.quit").replace("%player%", p.getName());

            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                if(player.hasPermission("systic.freeze.notify")) player.sendMessage(msg);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        if(frozen.contains(p.getUniqueId())){
            String msg = Practice.getMessage("admin.freeze.join").replace("%player%", p.getName());

            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                if(player.hasPermission("systic.freeze.notify")) player.sendMessage(msg);
            }
        }
    }

    @Override
    public void run() {
        for(UUID uuid : frozen){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()){
                player.sendMessage(Practice.getMessage("admin.freeze.freeze-message"));
            }
        }
    }
}
