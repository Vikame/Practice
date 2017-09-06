package org.systic.practice.stats;

import org.bukkit.scheduler.BukkitRunnable;
import org.systic.citadel.util.DatabaseConnection;
import org.systic.practice.Practice;
import org.systic.practice.ladders.Ladder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class MySQLStatManager extends StatManager {

    private final DatabaseConnection connection = Practice.inst().database_connection;

    public void load(){
        if(connection == null) return;

        new BukkitRunnable(){
            public void run(){
                try {
                    PreparedStatement s = connection.prepareStatement("SELECT * FROM stats;");

                    if (s == null) return;

                    ResultSet set = s.executeQuery();

                    while(set.next()){
                        UUID uuid = UUID.fromString(set.getString("UUID"));
                        String name = set.getString("NAME");
                        int unranked = set.getInt("UNRANKED");

                        unranked_wins.put(uuid, unranked);
                        names.put(uuid, name);

                        for(Ladder ladder : ladder_manager.all()){
                            setElo(uuid, ladder, set.getInt(ladder.name.replace(" ", "_").toUpperCase()));
                        }
                    }

                    set.close();
                    s.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Practice.inst());
    }

    public void save() {
        if (connection == null) return;

        if (Practice.inst().isEnabled()) {
            new BukkitRunnable() {
                public void run() {
                    try {
                        PreparedStatement s = connection.prepareStatement("INSERT INTO stats(UUID, NAME, NO_DEBUFF, GAPPLE, NO_ENCHANTS, DEBUFF, ARCHER, SOUP, AXEPVP, UNRANKED) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE NAME = VALUES(NAME), NO_DEBUFF = VALUES(NO_DEBUFF), GAPPLE = VALUES(GAPPLE), " +
                                "NO_ENCHANTS = VALUES(NO_ENCHANTS), DEBUFF = VALUES(DEBUFF), ARCHER = VALUES(ARCHER), SOUP = VALUES(SOUP), AXEPVP = VALUES(AXEPVP), UNRANKED = VALUES(UNRANKED);");

                        if (s == null) return;

                        for (Map.Entry<UUID, String> entry : names.entrySet()) {
                            UUID uuid = entry.getKey();

                            s.setString(1, uuid.toString());
                            s.setString(2, getName(uuid));
                            s.setInt(3, getElo(uuid, ladder_manager.get("No Debuff")));
                            s.setInt(4, getElo(uuid, ladder_manager.get("Gapple")));
                            s.setInt(5, getElo(uuid, ladder_manager.get("No Enchants")));
                            s.setInt(6, getElo(uuid, ladder_manager.get("Debuff")));
                            s.setInt(7, getElo(uuid, ladder_manager.get("Archer")));
                            s.setInt(8, getElo(uuid, ladder_manager.get("Soup")));
                            s.setInt(9, getElo(uuid, ladder_manager.get("AxePvP")));
                            s.setInt(10, unranked_wins.getOrDefault(uuid, 0));

                            s.addBatch();
                        }

                        s.executeBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(Practice.inst());
        } else {
            try {
                PreparedStatement s = connection.prepareStatement("INSERT INTO stats(UUID, NAME, NO_DEBUFF, GAPPLE, NO_ENCHANTS, DEBUFF, ARCHER, SOUP, AXEPVP, UNRANKED) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE NAME = VALUES(NAME), NO_DEBUFF = VALUES(NO_DEBUFF), GAPPLE = VALUES(GAPPLE), " +
                        "NO_ENCHANTS = VALUES(NO_ENCHANTS), DEBUFF = VALUES(DEBUFF), ARCHER = VALUES(ARCHER), SOUP = VALUES(SOUP), AXEPVP = VALUES(AXEPVP), UNRANKED = VALUES(UNRANKED);");

                if (s == null) return;

                for (Map.Entry<UUID, String> entry : names.entrySet()) {
                    UUID uuid = entry.getKey();

                    s.setString(1, uuid.toString());
                    s.setString(2, getName(uuid));
                    s.setInt(3, getElo(uuid, ladder_manager.get("No Debuff")));
                    s.setInt(4, getElo(uuid, ladder_manager.get("Gapple")));
                    s.setInt(5, getElo(uuid, ladder_manager.get("No Enchants")));
                    s.setInt(6, getElo(uuid, ladder_manager.get("Debuff")));
                    s.setInt(7, getElo(uuid, ladder_manager.get("Archer")));
                    s.setInt(8, getElo(uuid, ladder_manager.get("Soup")));
                    s.setInt(9, getElo(uuid, ladder_manager.get("AxePvP")));
                    s.setInt(10, getUnrankedWins(uuid));

                    s.addBatch();
                }

                s.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
