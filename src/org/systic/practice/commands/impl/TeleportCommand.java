package org.systic.practice.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.matching.Match;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatch;
import org.systic.practice.matching.TeamMatchManager;
import org.systic.practice.vanish.VanishManager;

import java.util.UUID;

public class TeleportCommand extends PlayerCommand {

    private static MatchManager match_manager;
    private static TeamMatchManager team_match_manager;
    private static VanishManager vanish_manager;

    public TeleportCommand() {
        super("teleport", "systic.teleport");
        match_manager = Practice.inst().match_manager;
        vanish_manager = Practice.inst().vanish_manager;
        team_match_manager = Practice.inst().team_match_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(match_manager.get(player) != null || team_match_manager.get(player) != null){
            player.sendMessage(Practice.getMessage("admin.teleport.in-match"));
            return;
        }

        if(args.length <= 0){
            player.sendMessage(C.c("&cUsage: /" + label + " <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
            return;
        }

        teleport(player, target);
    }

    public static void teleport(Player player, Player target){
        if (!PlayerSettings.get(player).get("show players in lobby", true)) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                Practice.inst().vanish_manager.hide(player, p);
            }
        }

        player.teleport(target);

        Match match = Practice.inst().match_manager.get(target);
        TeamMatch team_match = Practice.inst().team_match_manager.get(target);

        if(match != null){
            vanish_manager.show(player, match.one);
            vanish_manager.show(player, match.two);

            if (vanish_manager.canSee(match.one, player)) vanish_manager.hide(match.one, player);
            if (vanish_manager.canSee(match.two, player)) vanish_manager.hide(match.two, player);

            match.hideOnFinish(player);

            player.sendMessage(Practice.getMessage("admin.teleport.duel").replace("%one%", match.one.getName()).replace("%two%", match.two.getName()));
        }else if(team_match != null) {
            for(UUID uuid : team_match.one_players){
                Player p = Bukkit.getPlayer(uuid);
                if(p != null && p.isOnline()){
                    vanish_manager.show(player, p);
                    if (vanish_manager.canSee(p, player)) vanish_manager.hide(p, player);
                }
            }
            for(UUID uuid : team_match.two_players){
                Player p = Bukkit.getPlayer(uuid);
                if(p != null && p.isOnline()){
                    vanish_manager.show(player, p);
                    if (vanish_manager.canSee(p, player)) vanish_manager.hide(p, player);
                }
            }

            team_match.hideOnFinish(player);
            player.sendMessage(Practice.getMessage("admin.teleport.team-duel").replace("%one%", team_match.one.name).replace("%two%", team_match.two.name));
        }else{
            vanish_manager.show(player, target);
            player.sendMessage(Practice.getMessage("admin.teleport.single").replace("%player%", target.getName()));
        }
    }
}
