package org.systic.practice.stats;

import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.ladders.LadderManager;

import java.util.*;

public abstract class StatManager {

    public final LadderManager ladder_manager = Practice.inst().ladder_manager;
    public final Map<UUID, Map<Ladder, Integer>> elo = new HashMap<>();
    public final Map<UUID, String> names = new HashMap<>();
    public final Map<UUID, Integer> unranked_wins = new HashMap<>();

    public final int getEloChange(int winner, int loser) {
        double r1 = Math.pow(10.0D, loser / 400.0D);
        double r2 = Math.pow(10.0D, winner / 400.0D);
        double expected = r1 / (r1 + r2);

        int change = (int) Math.round(32.0D * (1.0D - expected));

        return (change < 5 ? 5 : change);
    }

    public final void addUnrankedWin(UUID uuid) {
        unranked_wins.put(uuid, getUnrankedWins(uuid) + 1);
    }

    public final void addUnrankedWin(Player player) {
        addUnrankedWin(player.getUniqueId());
    }

    public final int getUnrankedWins(UUID uuid) {
        return unranked_wins.containsKey(uuid) ? unranked_wins.get(uuid) : 0;
    }

    public final int getUnrankedWins(Player player) {
        return getUnrankedWins(player.getUniqueId());
    }

    public final void setElo(UUID uuid, Ladder ladder, int amount) {
        if (!elo.containsKey(uuid)) {
            Map<Ladder, Integer> map = new HashMap<>();

            map.put(ladder, amount);

            elo.put(uuid, map);
        } else {
            elo.get(uuid).put(ladder, amount);
        }
    }

    public final void setElo(Player player, Ladder ladder, int amount) {
        setElo(player.getUniqueId(), ladder, amount);
    }

    public final int getElo(UUID uuid, Ladder ladder) {
        if (!elo.containsKey(uuid)) {
            return 1000;
        }

        Map<Ladder, Integer> map = elo.get(uuid);

        if (!map.containsKey(ladder)) return 1000;
        else return map.get(ladder);
    }

    public final int getElo(Player player, Ladder ladder) {
        return getElo(player.getUniqueId(), ladder);
    }

    public final int getGlobalElo(UUID uuid) {
        if (!elo.containsKey(uuid)) return 1000;

        int overall = 0;
        int count = 0;

        for (Map.Entry<Ladder, Integer> entry : elo.get(uuid).entrySet()) {
            overall += entry.getValue();
            count++;
        }

        if (overall == 0 || count == 0) return 0;

        return overall / count;
    }

    public final int getGlobalElo(Player player) {
        return getGlobalElo(player.getUniqueId());
    }

    public final TreeMap<Integer, String> getLeaderboards(int size) {
        TreeMap<Integer, String> ret = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int i1 = o1;
                int i2 = o2;

                return i1 == i2 ? 0 : i1 > i2 ? 1 : -1;
            }
        });

        for (UUID uuid : elo.keySet()) {
            ret.put(getGlobalElo(uuid), getName(uuid));

            if (ret.size() > size) {
                ret.remove(ret.lastKey());
            }
        }

        return ret;
    }

    public final String getName(UUID uuid) {
        return names.getOrDefault(uuid, "Unknown");
    }

    public final void updateName(Player player) {
        names.put(player.getUniqueId(), player.getName());
    }

    public final void updateName(UUID uuid, String name) {
        names.put(uuid, name);
    }

    public final void migrate(StatManager manager) {
        for (Map.Entry<UUID, Map<Ladder, Integer>> entry : manager.elo.entrySet()) {
            for (Map.Entry<Ladder, Integer> ent : entry.getValue().entrySet()) {
                setElo(entry.getKey(), ent.getKey(), ent.getValue());
            }
        }

        for (Map.Entry<UUID, Integer> entry : manager.unranked_wins.entrySet()) {
            unranked_wins.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<UUID, String> entry : manager.names.entrySet()) {
            names.put(entry.getKey(), entry.getValue());
        }
    }

    public abstract void save();

    public abstract void load();
}
