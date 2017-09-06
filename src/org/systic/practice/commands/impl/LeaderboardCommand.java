package org.systic.practice.commands.impl;

import org.systic.practice.commands.AsyncPlayerCommand;
import org.systic.practice.gui.LeaderboardGUI;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends AsyncPlayerCommand {

    public LeaderboardCommand() {
        super("leaderboard", null);
    }

    @Override
    public void run(Player player, String label, String[] args) {
        player.openInventory(LeaderboardGUI.inst().update());
    }

}
