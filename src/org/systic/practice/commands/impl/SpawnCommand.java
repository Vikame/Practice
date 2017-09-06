package org.systic.practice.commands.impl;

import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.location.LocationManager;

public class SpawnCommand extends PlayerCommand {

    private final LocationManager location_manager;

    public SpawnCommand() {
        super("spawn", "systic.spawn");
        location_manager = Practice.inst().location_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if (!location_manager.contains(player.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
            player.sendMessage(Practice.getMessage("admin.no-spawn"));
            return;
        }

        player.sendMessage(Practice.getMessage("admin.spawn"));
        player.teleport(location_manager.get(player.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
    }

}
