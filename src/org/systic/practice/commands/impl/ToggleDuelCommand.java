package org.systic.practice.commands.impl;

import org.bukkit.entity.Player;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.practice.commands.PlayerCommand;

public class ToggleDuelCommand extends PlayerCommand {

    public ToggleDuelCommand() {
        super("toggleduels", null);
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if (PlayerSettings.get(player).toggle("duel requests")) {
            player.sendMessage(C.c("&aYou have enabled duel requests."));
        } else {
            player.sendMessage(C.c("&cYou have disabled duel requests."));
        }
    }

}
