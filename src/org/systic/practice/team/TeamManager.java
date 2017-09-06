package org.systic.practice.team;

import org.bukkit.entity.Player;
import org.systic.practice.gui.TeamListGUI;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    private final List<Team> teams = new ArrayList<>();

    /*
     * Gets all teams.
     */
    public List<Team> all(){
        return teams;
    }

    /*
     * Add a team.
     */
    public void add(Team team){
        teams.add(team);
        TeamListGUI.inst().update();
    }

    /*
     * Get a player's team.
     */
    public Team get(Player player){
        for(Team team : teams){
            if(team.isMember(player)) return team;
        }

        return null;
    }

    /*
     * Get a team by it's name.
     */
    public Team get(String name){
        for(Team team : teams){
            if(team.name.equalsIgnoreCase(name)) return team;
        }

        return null;
    }

    /*
     * Remove a team.
     */
    public void remove(Team team){
        teams.remove(team);
        TeamListGUI.inst().update();
    }
}
