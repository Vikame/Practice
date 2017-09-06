package org.systic.practice.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.vanish.VanishManager;

public class ShowCommand extends PlayerCommand {

    private final VanishManager vanish_manager;

    public ShowCommand() {
        super("show", "systic.show");
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
                vanish_manager.show(player, p);
            }

            player.sendMessage(Practice.getMessage("admin.show-all"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
            return;
        }

        vanish_manager.show(player, target);
        player.sendMessage(Practice.getMessage("admin.show").replace("%player%", target.getName()));
    }
}
