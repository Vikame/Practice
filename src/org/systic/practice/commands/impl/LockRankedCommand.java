package org.systic.practice.commands.impl;

import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LockRankedCommand extends PlayerCommand {

    public LockRankedCommand() {
        super("lockranked", "systic.lockranked");
    }

    @Override
    public void run(Player player, String label, String[] args) {
        boolean val = (Practice.inst().ranked_locked = !Practice.inst().ranked_locked);

        player.sendMessage(Practice.getMessage("admin.lock-ranked." + (val ? "locked" : "unlocked")));
        Bukkit.broadcastMessage(Practice.getMessage("admin.lock-ranked." + (val ? "locked-bc" : "unlocked-bc")));
    }

}
