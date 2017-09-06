package org.systic.practice.matching;

import org.systic.practice.Practice;
import org.systic.practice.team.Team;
import org.systic.practice.util.TimedSet;

import java.util.Set;

public class TeamRequestManager {

    public final Set<TeamMatchRequest> requests = new TimedSet<>(10);

    public void add(TeamMatchRequest request){
        requests.add(request);
    }

    public void remove(TeamMatchRequest request){
        requests.remove(request);
    }

    public TeamMatchRequest get(Team send, Team accept){
        Practice.inst().profiler.begin("Retrieve Team Match");

        for(TeamMatchRequest request : requests) if(request.one == send && request.two == accept){
            Practice.inst().profiler.end("Retrieve Team Match");
            return request;
        }

        Practice.inst().profiler.end("Retrieve Team Match");
        return null;
    }

}
