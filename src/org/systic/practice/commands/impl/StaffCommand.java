package org.systic.practice.commands.impl;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.generic.StaffManager;
import org.systic.practice.location.LocationManager;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatchManager;

public class StaffCommand extends PlayerCommand {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;
    private final StaffManager staff_manager;
    private final LocationManager location_manager;

    public StaffCommand() {
        super("staff", "systic.staff");
        staff_manager = Practice.inst().staff_manager;
        location_manager = Practice.inst().location_manager;
        match_manager = Practice.inst().match_manager;
        team_match_manager = Practice.inst().team_match_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if (match_manager.get(player) != null || team_match_manager.get(player) != null) {
            player.sendMessage(Practice.getMessage("admin.staff.in-match"));
            return;
        }

        if(staff_manager.isInStaff(player)){
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Practice.getMessage("admin.staff.disable"));
            staff_manager.disableStaff(player);

            if (location_manager.contains(player.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"))
                player.teleport(location_manager.get(player.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
        }else{
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Practice.getMessage("admin.staff.enable"));
            staff_manager.enableStaff(player);
        }
    }
}
