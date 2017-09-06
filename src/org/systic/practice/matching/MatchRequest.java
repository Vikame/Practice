package org.systic.practice.matching;

import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Callable;

public class MatchRequest implements Callable{

    public final Ladder ladder;
    public final Player one, two;
    public final Arena arena;
    private final String twoName;

    public MatchRequest(Ladder ladder, Player one, Player two){
        this.ladder = ladder;
        this.one = one;
        this.two = two;
        this.arena = Practice.inst().arena_manager.random();
        this.twoName = two.getName(); // For use later, just in case 'two' logged out.
    }

    public MatchRequest(Ladder ladder, Player one, Player two, Arena arena) {
        this.ladder = ladder;
        this.one = one;
        this.two = two;
        this.arena = arena;
        this.twoName = two.getName(); // For use later, just in case 'two' logged out.
    }

    @Override
    public void call() {
        if(one != null && one.isOnline()){
            one.sendMessage(Practice.getMessage("duel.timed-out").replace("%player%", twoName));
        }
    }
}
