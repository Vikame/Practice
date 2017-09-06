package org.systic.practice.matching;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.location.LocationManager;
import org.systic.practice.stats.StatManager;
import org.systic.practice.util.Inventories;

import java.util.*;

public class Queue {

    private static final ArenaManager arena_manager = Practice.inst().arena_manager;
    private static final LocationManager location_manager = Practice.inst().location_manager;
    private static final StatManager stat_manager = Practice.inst().stat_manager;
    public final Ladder ladder;
    public final MatchType type;
    public final Map<UUID, Long> queue;
    public final Map<UUID, Integer> search;

    public Queue(Ladder ladder, MatchType type){
        this.ladder = ladder;
        this.type = type;
        this.queue = new HashMap<>();
        this.search = new HashMap<>();
    }

    public void incrementSearches(){
        List<UUID> rem = new ArrayList<>();

        if(type == MatchType.RANKED){
            for(Map.Entry<UUID, Integer> entry : search.entrySet()){
                entry.setValue(entry.getValue() + 50);

                Player p = Bukkit.getPlayer(entry.getKey());
                if(p != null && p.isOnline()){
                    int min = stat_manager.getElo(p, ladder)-entry.getValue();
                    int max = stat_manager.getElo(p, ladder)+entry.getValue();

                    if(min <= 0){
                        rem.add(entry.getKey());
                        p.sendMessage(C.c("&cCould not find a ranked match."));
                        continue;
                    }

                    p.sendMessage(Practice.getMessage("generic.ranked-search").replace("%min%", "" + min)
                            .replace("%max%", "" + max));
                }else{
                    rem.add(entry.getKey());
                }
            }
        }

        for(UUID uuid : rem){
            queue.remove(uuid);
            search.remove(uuid);
        }
    }

    public void queue(Player player){
        queue.put(player.getUniqueId(), System.currentTimeMillis());

        if(type == MatchType.RANKED){
            search.put(player.getUniqueId(), 100);
        }
    }

    public void unqueue(Player player){
        queue.remove(player.getUniqueId());
        search.remove(player.getUniqueId());
    }

    public long getQueueTime(Player player){
        return System.currentTimeMillis() - queue.get(player.getUniqueId());
    }

    public void attemptMatchmaking() {
        List<UUID> remove = new ArrayList<>();

        if(type == MatchType.UNRANKED){
            Iterator<UUID> queue = this.queue.keySet().iterator();
            while(queue.hasNext()){
                Player p = Bukkit.getPlayer(queue.next());

                if(p == null || !p.isOnline()){
                    queue.remove();
                    continue;
                }

                if(!queue.hasNext()) continue;

                Player other = Bukkit.getPlayer(queue.next());
                if(other == null || !other.isOnline()){
                    queue.remove();
                    continue;
                }

                Arena arena = arena_manager.random();

                remove.add(p.getUniqueId());
                remove.add(other.getUniqueId());

                if(arena == null){
                    p.sendMessage(C.c("&cNo arenas have been set up!"));
                    other.sendMessage(C.c("&cNo arenas have been set up!"));

                    Inventories.giveDefault(p);
                    Inventories.giveDefault(other);

                    if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
                        Location spawn = location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn");

                        p.teleport(spawn);
                        other.teleport(spawn);
                    }
                    continue;
                }

                new Match(ladder, MatchType.UNRANKED, arena, p, other);

                p.sendMessage(Practice.getMessage("match.start").replace("%opposite%", other.getName()).replace("%elo%", "" + stat_manager.getElo(other, ladder)));
                other.sendMessage(Practice.getMessage("match.start").replace("%opposite%", p.getName()).replace("%elo%", "" + stat_manager.getElo(p, ladder)));
            }
        }else if(type == MatchType.RANKED){
            Iterator<UUID> queue = this.queue.keySet().iterator();
            while(queue.hasNext()){
                Player p = Bukkit.getPlayer(queue.next());

                if(p == null || !p.isOnline()){
                    queue.remove();
                    continue;
                }

                if(!queue.hasNext()) continue;

                Player other = Bukkit.getPlayer(queue.next());
                if(other == null || !other.isOnline()){
                    queue.remove();
                    continue;
                }

                int elo1 = stat_manager.getElo(p, ladder);
                int elo2 = stat_manager.getElo(other, ladder);

                int search1 = search.get(p.getUniqueId());
                int search2 = search.get(other.getUniqueId());

                if((elo1 - search1 <= elo2 || elo1 + search1 >= elo2) || (elo2 - search2 <= elo1 || elo2 + search2 >= elo1)){
                    Arena arena = arena_manager.random();

                    remove.add(p.getUniqueId());
                    remove.add(other.getUniqueId());

                    if(arena == null){
                        p.sendMessage(C.c("&cNo arenas have been set up!"));
                        other.sendMessage(C.c("&cNo arenas have been set up!"));

                        Inventories.giveDefault(p);
                        Inventories.giveDefault(other);

                        if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
                            Location spawn = location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn");

                            p.teleport(spawn);
                            other.teleport(spawn);
                        }
                        continue;
                    }

                    new Match(ladder, MatchType.RANKED, arena, p, other);
                    p.sendMessage(Practice.getMessage("match.start-ranked").replace("%opposite%", other.getName()).replace("%elo%", "" + stat_manager.getElo(p, ladder)));
                    other.sendMessage(Practice.getMessage("match.start-ranked").replace("%opposite%", p.getName()).replace("%elo%", "" + stat_manager.getElo(p, ladder)));
                }
            }
        }

        for(UUID uuid : remove){
            queue.remove(uuid);
            search.remove(uuid);
        }
    }
}
