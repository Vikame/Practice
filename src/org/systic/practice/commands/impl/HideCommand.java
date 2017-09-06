package org.systic.practice.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.vanish.VanishManager;

public class HideCommand extends PlayerCommand {

    private final VanishManager vanish_manager;

    public HideCommand() {
        super("hide", "systic.hide");
        vanish_manager = Practice.inst().vanish_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(args.length <= 0){
            player.sendMessage(C.c("&cUsage: /" + label + " <player>"));
            return;
        }

        if(args[0].equalsIgnoreCase("*")){
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                vanish_manager.hide(player, p);
            }

            player.sendMessage(Practice.getMessage("admin.hide-all"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
            return;
        }

        vanish_manager.hide(player, target);
        player.sendMessage(Practice.getMessage("admin.hide").replace("%player%", target.getName()));
    }
}
