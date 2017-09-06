package org.systic.practice.commands.impl;

import org.bukkit.entity.Player;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.practice.commands.PlayerCommand;

public class TogglePlayersCommand extends PlayerCommand {

    public TogglePlayersCommand() {
        super("toggleplayers", null);
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if (PlayerSettings.get(player).toggle("show players in lobby")) {
            player.sendMessage(C.c("&aYou are now able to see all players in the lobby."));
        } else {
            player.sendMessage(C.c("&cYou are no longer able to see players in the lobby."));
        }
    }

}
