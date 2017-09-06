package org.systic.practice.matching;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.practice.Practice;
import org.systic.practice.ladders.Ladder;

import java.util.ArrayList;
import java.util.List;

public class QueueManager extends BukkitRunnable{

    public final List<Queue> queues = new ArrayList<>();

    public QueueManager(){
        for(Ladder ladder : Practice.inst().ladder_manager.all()){
            if(ladder.unranked) queues.add(new Queue(ladder, MatchType.UNRANKED));
            if(ladder.ranked) queues.add(new Queue(ladder, MatchType.RANKED));
        }

        runTaskTimerAsynchronously(Practice.inst(), 100, 100);
    }

    /*
     * Attempt to match queued players with each other.
     */
    public void attemptMatchmaking(){
        Practice.inst().profiler.begin("Matchmaking");

        for(Queue queue : queues) queue.attemptMatchmaking();

        Practice.inst().profiler.end("Matchmaking");
    }

    /*
     * Create a new queue with the given requirements.
     */
    public void createQueue(Ladder ladder, MatchType type){
        queues.add(new Queue(ladder, type));
    }

    /*
     * Get a queue by its requirements.
     */
    public Queue getQueue(Ladder ladder, MatchType type){
        for(Queue queue : queues){
            if(queue.ladder.name.equals(ladder.name) && queue.type.equals(type)) return queue;
        }

        return null;
    }

    /*
     * Get the queue a player is in.
     */
    public Queue getQueue(Player player){
        for(Queue queue : queues){
            if(queue.queue.containsKey(player.getUniqueId())) return queue;
        }

        return null;
    }

    /*
     * Get the total size of the queue which matches the requirements.
     */
    public int sizeOf(Ladder ladder, MatchType type){
        return getQueue(ladder, type).queue.size();
    }

    @Override
    public void run() {
        for(Queue queue : queues){
            queue.incrementSearches();
            queue.attemptMatchmaking();
        }
    }
}
