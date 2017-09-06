package org.systic.practice.matching;

import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.team.Team;

public class TeamMatchRequest {

    public final Ladder ladder;
    public final Team one, two;
    public final Arena arena;

    public TeamMatchRequest(Ladder ladder, Team one, Team two){
        this.ladder = ladder;
        this.one = one;
        this.two = two;
        this.arena = Practice.inst().arena_manager.random();
    }

    public TeamMatchRequest(Ladder ladder, Team one, Team two, Arena arena) {
        this.ladder = ladder;
        this.one = one;
        this.two = two;
        this.arena = arena;
    }

}
