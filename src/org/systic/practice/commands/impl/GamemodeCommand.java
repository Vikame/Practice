package org.systic.practice.commands.impl;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatchManager;

public class GamemodeCommand extends PlayerCommand {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;

    public GamemodeCommand() {
        super("gamemode", "practice.gamemode");
        match_manager = Practice.inst().match_manager;
        team_match_manager = Practice.inst().team_match_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(match_manager.get(player) != null || team_match_manager.get(player) != null){
            player.sendMessage(Practice.getMessage("admin.gamemode.in-match"));
            return;
        }

        GameMode gamemode = null;
        if(label.equalsIgnoreCase("gms") || label.equalsIgnoreCase("practice:gms")){
            gamemode = GameMode.SURVIVAL;
        }else if(label.equalsIgnoreCase("gmc") || label.equalsIgnoreCase("practice:gmc")){
            gamemode = GameMode.CREATIVE;
        }else if(label.equalsIgnoreCase("gma") || label.equalsIgnoreCase("practice:gma")){
            gamemode = GameMode.ADVENTURE;
        }else{

            if(args.length <= 0){
                player.sendMessage(C.c("&cUsage: /" + label + " <gamemode>"));
                return;
            }

            String gm = args[0];
            if(gm.equalsIgnoreCase("s") || gm.equalsIgnoreCase("survival") || gm.equalsIgnoreCase("0")){
                gamemode = GameMode.SURVIVAL;
            }else if(gm.equalsIgnoreCase("c") || gm.equalsIgnoreCase("creative") || gm.equalsIgnoreCase("1")){
                gamemode = GameMode.CREATIVE;
            }else if(gm.equalsIgnoreCase("a") || gm.equalsIgnoreCase("adventure") || gm.equalsIgnoreCase("2")){
                gamemode = GameMode.ADVENTURE;
            }

            if(gamemode == null){
                player.sendMessage(Practice.getMessage("admin.gamemode.not-found").replace("%gamemode%", args[0]));
                return;
            }
        }

        if(player.getGameMode() == gamemode){
            player.sendMessage(Practice.getMessage("admin.gamemode.same-gamemode").replace("%gamemode%", gamemode.name()));
            return;
        }

        player.setGameMode(gamemode);
        player.sendMessage(Practice.getMessage("admin.gamemode.switch").replace("%gamemode%", gamemode.name()));
    }
}
