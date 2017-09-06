package org.systic.practice.matching;

import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.util.TimedSet;

import java.util.Set;

public class RequestManager {

    public final Set<MatchRequest> requests = new TimedSet<>(10);

    public void add(MatchRequest request){
        requests.add(request);
    }

    public void remove(MatchRequest request){
        requests.remove(request);
    }

    public MatchRequest get(Player send, Player accept){
        Practice.inst().profiler.begin("Retrieve Match");

        for(MatchRequest request : requests) if(request.one == send && request.two == accept){
            Practice.inst().profiler.end("Retrieve Match");
            return request;
        }

        Practice.inst().profiler.end("Retrieve Match");
        return null;
    }

}
