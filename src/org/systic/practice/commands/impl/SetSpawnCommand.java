package org.systic.practice.commands.impl;

import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.location.LocationManager;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends PlayerCommand {

    private final LocationManager location_manager;

    public SetSpawnCommand() {
        super("setspawn", "systic.setspawn");
        location_manager = Practice.inst().location_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        location_manager.add("spawn", player.getLocation());
        player.sendMessage(Practice.getMessage("admin.set-spawn"));
    }

}
