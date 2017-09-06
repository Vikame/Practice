package org.systic.practice.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.practice.Practice;

public class BoardHandler extends BukkitRunnable implements Listener {

    public BoardHandler(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
        runTaskTimerAsynchronously(Practice.inst(), 2, 2);
    }

    @Override
    public void run() {
        PlayerBoard.updateStatics();

        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            if(PlayerBoard.exists(p)) {
                PlayerBoard.get(p).update();
            }else{
                new BukkitRunnable(){
                    public void run(){
                        PlayerBoard.get(p);
                    }
                }.runTask(Practice.inst());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        PlayerBoard.get(e.getPlayer()).update();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        PlayerBoard.get(e.getPlayer()).destroy();
    }

}