package org.systic.practice.commands.impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.generic.FreezeManager;
import org.systic.practice.generic.SpectateManager;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatchManager;
import org.systic.practice.vanish.VanishManager;

public class SpectateCommand extends PlayerCommand {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;
    private final VanishManager vanish_manager;
    private final SpectateManager spectate_manager;
    private final FreezeManager freeze_manager;

    public SpectateCommand() {
        super("spectate", null);
        match_manager = Practice.inst().match_manager;
        vanish_manager = Practice.inst().vanish_manager;
        team_match_manager = Practice.inst().team_match_manager;
        spectate_manager = Practice.inst().spectate_manager;
        freeze_manager = Practice.inst().freeze_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        player.sendMessage(ChatColor.RED + "Coming soon.");
//        if(match_manager.get(player) != null || team_match_manager.get(player) != null){
//            player.sendMessage(Practice.getMessage("spectate.in-match"));
//            return;
//        }
//
//        if(args.length <= 0){
//            player.sendMessage(C.c("&cUsage: /" + label + " <player>"));
//            return;
//        }
//
//        if(freeze_manager.isFrozen(player)){
//            player.sendMessage(Practice.getMessage("spectate.frozen"));
//            return;
//        }
//
//        Player target = Bukkit.getPlayer(args[0]);
//        if(target == null) {
//            player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
//            return;
//        }
//
//        for(Player p : Bukkit.getServer().getOnlinePlayers()){
//            vanish_manager.hide(player, p);
//        }
//
//        Match match = match_manager.get(target);
//        TeamMatch team_match = team_match_manager.get(target);
//
//        if(match != null){
//            vanish_manager.show(player, match.one);
//            vanish_manager.show(player, match.two);
//
//            match.hideOnFinish(player);
//
//            spectate_manager.spectate(player, target);
//            player.sendMessage(Practice.getMessage("spectate.duel").replace("%one%", match.one.getName()).replace("%two%", match.two.getName()));
//            player.teleport(target);
//
//            Inventories.giveSpectator(player);
//        }else if(team_match != null) {
//            for(UUID uuid : team_match.one_players){
//                Player p = Bukkit.getPlayer(uuid);
//                if(p != null && p.isOnline()){
//                    vanish_manager.show(player, p);
//                }
//            }
//            for(UUID uuid : team_match.two_players){
//                Player p = Bukkit.getPlayer(uuid);
//                if(p != null && p.isOnline()){
//                    vanish_manager.show(player, p);
//                }
//            }
//
//            team_match.hideOnFinish(player);
//
//            spectate_manager.spectate(player, target);
//            player.sendMessage(Practice.getMessage("spectate.team-duel").replace("%one%", team_match.one.name).replace("%two%", team_match.two.name));
//            player.teleport(target);
//
//            Inventories.giveSpectator(player);
//        }else{
//            player.sendMessage(Practice.getMessage("spectate.no-match").replace("%player%", target.getName()));
//        }
    }
}
