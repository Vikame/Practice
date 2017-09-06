package org.systic.practice.commands.impl;

import lh.lolicon.skynet.Skynet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.generic.FreezeManager;
import org.systic.practice.matching.Match;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatch;
import org.systic.practice.matching.TeamMatchManager;
import org.systic.practice.util.Depend;
import org.systic.practice.util.Inventories;

public class FreezeCommand extends PlayerCommand {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;
    private final FreezeManager freeze_manager;

    public FreezeCommand() {
        super("freeze", "systic.freeze");
        match_manager = Practice.inst().match_manager;
        freeze_manager = Practice.inst().freeze_manager;
        team_match_manager = Practice.inst().team_match_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(match_manager.get(player) != null || team_match_manager.get(player) != null){
            player.sendMessage(Practice.getMessage("admin.freeze.in-match"));
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

        if(Depend.exists("Skynet")) {
            if (Skynet.getViolationHandler().getBanDates().containsKey(target)) {
                player.sendMessage(C.c("&cYou are unable to freeze a player who is scheduled for an auto-ban by Skynet."));
                return;
            }
        }

        if (target.hasPermission("systic.freeze.bypass")) {
            player.sendMessage(Practice.getMessage("admin.freeze.freeze-bypass").replace("%player%", target.getName()));
            return;
        }

        Match match = match_manager.get(target);
        TeamMatch team_match = team_match_manager.get(target);

        if(match != null) match.draw(target.getName() + " being frozen");
        else if(team_match != null) team_match.die(target, "match.player-frozen");

        if(!freeze_manager.isFrozen(target)){

            player.sendMessage(Practice.getMessage("admin.freeze.frozen-message").replace("%player%", target.getName()));
            target.sendMessage(Practice.getMessage("admin.freeze.freeze-message"));
            freeze_manager.freeze(target);

            Inventories.clear(target);

        }else{

            player.sendMessage(Practice.getMessage("admin.freeze.unfrozen-message").replace("%player%", target.getName()));
            target.sendMessage(Practice.getMessage("admin.freeze.unfreeze-message"));
            freeze_manager.unfreeze(target);

            Inventories.giveDefault(target);

        }
    }
}
