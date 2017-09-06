package org.systic.practice.matching;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.generic.SpectateManager;
import org.systic.practice.team.Team;
import org.systic.practice.vanish.VanishManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TeamMatchManager {

    private final SpectateManager spectate_manager;
    private final VanishManager vanish_manager;
    public final List<TeamMatch> matches;

    public TeamMatchManager(){
        spectate_manager = Practice.inst().spectate_manager;
        vanish_manager = Practice.inst().vanish_manager;
        matches = new ArrayList<>();
    }

    /*
     * Add a match.
     */
    public void add(TeamMatch match){
        matches.add(match);

        for(UUID uuid : match.one_players){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()){

                for(Player player : spectate_manager.getAllSpectating(p)) {
                    player.teleport(p);

                    for(UUID other : match.one_players) {
                        Player pl = Bukkit.getPlayer(other);
                        if (pl != null && pl.isOnline()) {
                            vanish_manager.show(player, pl);
                        }
                    }
                    for(UUID other : match.two_players) {
                        Player pl = Bukkit.getPlayer(other);
                        if (pl != null && pl.isOnline()) {
                            vanish_manager.show(player, pl);
                        }
                    }

                    match.hideOnFinish(p);
                }

            }
        }
        for(UUID uuid : match.two_players){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()){

                for(Player player : spectate_manager.getAllSpectating(p)) {
                    player.teleport(p);

                    for(UUID other : match.one_players) {
                        Player pl = Bukkit.getPlayer(other);
                        if (pl != null && pl.isOnline()) {
                            vanish_manager.show(player, pl);
                        }
                    }
                    for(UUID other : match.two_players) {
                        Player pl = Bukkit.getPlayer(other);
                        if (pl != null && pl.isOnline()) {
                            vanish_manager.show(player, pl);
                        }
                    }

                    match.hideOnFinish(p);
                }

            }
        }
    }

   /*
    * Get a random match.
    */
    public TeamMatch random(){
        return matches.isEmpty() ? null : matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    /*
     * Get a player's match.
     */
    public TeamMatch get(Player player){
        for(TeamMatch match : matches){
            if(match.one_players.contains(player.getUniqueId()) || match.two_players.contains(player.getUniqueId())) return match;
        }

        return null;
    }

    /*
     * Get a team's match.
     */
    public TeamMatch get(Team team){
        for(TeamMatch match : matches){
            if(match.one.equals(team) || match.two.equals(team)) return match;
        }

        return null;
    }

    /*
     * End a match.
     */
    public void end(TeamMatch match){
        matches.remove(match);
    }

    /*
     * End a player's match.
     */
    public void end(Player player){
        TeamMatch match = get(player);
        if(match != null) matches.remove(match);
    }

}
