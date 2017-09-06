package org.systic.practice.commands.impl;

import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatchManager;

public class FlyCommand extends PlayerCommand {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;

    public FlyCommand() {
        super("fly", "systic.fly");
        match_manager = Practice.inst().match_manager;
        team_match_manager = Practice.inst().team_match_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(match_manager.get(player) != null || team_match_manager.get(player) != null){
            player.sendMessage(Practice.getMessage("admin.fly.in-match"));
            return;
        }

        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(Practice.getMessage("admin.fly." + (player.getAllowFlight() ? "enabled" : "disabled")));
    }
}
