package org.systic.practice.stats;

import org.bukkit.configuration.ConfigurationSection;
import org.systic.practice.Practice;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.util.Config;

import java.util.Map;
import java.util.UUID;

public class FlatFileStatManager extends StatManager {

    public void load() {
        Config config = Practice.inst().stats;

        for (String s : config.config.getKeys(false)) {
            UUID uuid = UUID.fromString(s);
            ConfigurationSection section = config.config.getConfigurationSection(s);

            for (String str : section.getKeys(false)) {
                if (s.equalsIgnoreCase("name")) {
                    updateName(uuid, section.getString(str));
                } else if (s.equalsIgnoreCase("unranked")) {
                    unranked_wins.put(uuid, section.getInt(str));
                } else {
                    setElo(uuid, ladder_manager.get(str), section.getInt(str));
                }
            }
        }
    }

    public void save() {
        Config config = Practice.inst().stats;

        for (Map.Entry<UUID, Map<Ladder, Integer>> entry : elo.entrySet()) {
            String uid = entry.getKey().toString();

            for (Map.Entry<Ladder, Integer> ent : entry.getValue().entrySet()) {
                config.config.set(uid + "." + ent.getKey().name, ent.getValue());
            }
        }

        for (Map.Entry<UUID, String> entry : names.entrySet()) {
            UUID uuid = entry.getKey();
            String uid = entry.getKey().toString();

            config.config.set(uid + ".name", entry.getValue());

            if (elo.containsKey(uuid)) {
                for (Map.Entry<Ladder, Integer> ent : elo.get(uuid).entrySet()) {
                    config.config.set(uid + "." + ent.getKey().name, ent.getValue());
                }
            }

            if (unranked_wins.containsKey(uuid)) {
                config.config.set(uid + ".unranked", entry.getValue());
            }
        }

        config.save();
    }

}
