package org.systic.practice.ladders;

import java.util.ArrayList;
import java.util.List;

public class LadderManager {

    public final List<Ladder> ladders = new ArrayList<>();

    /*
     * Returns all registered ladders.
     */
    public List<Ladder> all(){
        return ladders;
    }

    /*
     * Add a ladder.
     */
    public void add(Ladder ladder){
        ladders.add(ladder);
    }

    /*
     * Remove a ladder.
     */
    public void remove(Ladder ladder){
        ladders.remove(ladder);
    }

   /*
    * Get a ladder by its name (CaSe-InSeNsItIvE)
    */
    public Ladder get(String name){
        for(Ladder ladder : ladders){
            if(ladder.name.equalsIgnoreCase(name)) return ladder;
        }

        return null;
    }

}
