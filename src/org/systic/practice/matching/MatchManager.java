package org.systic.practice.matching;

import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.generic.SpectateManager;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.vanish.VanishManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MatchManager {

    private final SpectateManager spectate_manager;
    private final VanishManager vanish_manager;
    public final List<Match> matches;

    public MatchManager(){
        spectate_manager = Practice.inst().spectate_manager;
        vanish_manager = Practice.inst().vanish_manager;
        matches = new ArrayList<>();
    }

    /*
     * Get the total number of matches that fit the requirements.
     */
    public int totalOf(Ladder ladder, MatchType type){
        int ret = 0;

        for(Match match : matches){
            if(match.ladder.equals(ladder) && match.type.equals(type)) ret++;
        }

        return ret;
    }

    /*
     * Add a match.
     */
    public void add(Match match){
        matches.add(match);

        for(Player p : spectate_manager.getAllSpectating(match.one)){
            p.teleport(match.one);

            vanish_manager.show(p, match.one);
            vanish_manager.show(p, match.two);

            match.hideOnFinish(p);
        }
        for(Player p : spectate_manager.getAllSpectating(match.two)){
            p.teleport(match.two);

            vanish_manager.show(p, match.one);
            vanish_manager.show(p, match.two);

            match.hideOnFinish(p);
        }
    }

    /*
     * Get a random match.
     */
    public Match random(){
        return matches.isEmpty() ? null : matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    /*
     * Get a player's match.
     */
    public Match get(Player player){
        for(Match match : matches){
            if(match.one.equals(player) || match.two.equals(player)) return match;
        }

        return null;
    }

    /*
     * End a match.
     */
    public void end(Match match){
        matches.remove(match);
    }

    /*
     * End a player's match.
     */
    public void end(Player player){
        Match match = get(player);
        if(match != null) matches.remove(match);
    }

}
