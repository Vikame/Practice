package org.systic.practice.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;

public class PingCommand extends PlayerCommand {

    public PingCommand() {
        super("ping", null);
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(args.length <= 0){
            player.sendMessage(Practice.getMessage("generic.ping-self").replace("%ping%", "" + ((CraftPlayer)player).getHandle().ping));
            return;
        }

        Player p = Bukkit.getPlayer(args[0]);

        if(p == null){
            player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
            return;
        }

        if(p == player){
            player.sendMessage(Practice.getMessage("generic.ping-self").replace("%ping%", "" + ((CraftPlayer)player).getHandle().ping));
            return;
        }

        player.sendMessage(Practice.getMessage("generic.ping-other").replace("%player%", p.getName()).replace("%ping%", "" + ((CraftPlayer)p).getHandle().ping));
    }

}
