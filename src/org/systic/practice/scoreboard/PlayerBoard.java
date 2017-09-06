package org.systic.practice.scoreboard;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class PlayerBoard {

    private static final Map<UUID, PlayerBoard> BOARDS = new HashMap<>();

    private static final TeamWrapper HEADER = new TeamWrapper("header").prefix("&7&m------------").name("-").suffix("---");
    private static final TeamWrapper FOOTER = new TeamWrapper("footer").prefix("&7&m------------").name("--").suffix("--");
    private static final TeamWrapper GAMEMODE = new TeamWrapper("gamemode").name("&eGameMode: &6");
    private static final TeamWrapper TPS = new TeamWrapper("tps").name("&bTPS: &6");

    private static final String TITLE = ChatColor.GOLD + "Name";

    public final UUID uuid;
    public volatile Scoreboard board;
    public volatile Objective objective;

    private volatile boolean visible;
    private volatile boolean updateVisibility;

    private PlayerBoard(Player player){
        this.uuid = player.getUniqueId();

        if(player.getScoreboard() == null || player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())){
            board = Bukkit.getScoreboardManager().getNewScoreboard();
        }else board = player.getScoreboard();

        if ((objective = board.getObjective(player.getName())) != null) {
            objective.unregister();
        }

        objective = board.registerNewObjective(player.getName(), "dummy");

        visible = true;

        BOARDS.put(uuid, this);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if(visible != this.visible) {
            this.visible = visible;
            this.updateVisibility = true;
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Scoreboard getBoard() {
        return board;
    }

    public Objective getObjective() {
        return objective;
    }

    public void update(){
        Player player = getPlayer();

        if(player == null || !player.isOnline()) return;

        if (!player.getScoreboard().equals(board))
            player.setScoreboard(board);

        if(objective != null) {
            if(!visible){
                if(updateVisibility) {
                    HEADER.score(-1).send(objective);
                    FOOTER.score(-1).send(objective);
                    updateVisibility = false;
                }
                return;
            }

            int index = 2;

            boolean hf_send = false;

            if(player.hasPermission("scoreboard.gamemode")){
                hf_send = true;

                GAMEMODE.suffix(player.getGameMode().name()).score(index++).send(objective);
            }

            TPS.send(objective);

            if(hf_send){
                FOOTER.score(1).send(objective);
                HEADER.score(index).send(objective);
            }else{
                FOOTER.score(-1).send(objective);
                HEADER.score(-1).send(objective);
            }

            if (objective.getDisplaySlot() != DisplaySlot.SIDEBAR)
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            if (!objective.getDisplayName().equals(TITLE))
                objective.setDisplayName(TITLE);
        }
    }

    public void destroy(){
        BOARDS.remove(uuid);

        if(objective != null) {
            objective.unregister();

            Player player = getPlayer();
            if (player != null && player.isOnline()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
    }

    public static void updateStatics(){
        TPS.suffix("" + MinecraftServer.getServer().recentTps[0]);
    }

    public static PlayerBoard get(Player p) {
        if(BOARDS.containsKey(p.getUniqueId()))
            return BOARDS.get(p.getUniqueId());

        return new PlayerBoard(p);
    }

    public static boolean exists(Player p){
        return BOARDS.containsKey(p.getUniqueId());
    }
}