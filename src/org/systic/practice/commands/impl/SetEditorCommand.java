package org.systic.practice.commands.impl;

import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.location.LocationManager;
import org.bukkit.entity.Player;

public class SetEditorCommand extends PlayerCommand {

    private final LocationManager location_manager;

    public SetEditorCommand() {
        super("seteditor", "systic.seteditor");
        location_manager = Practice.inst().location_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        location_manager.add("kit editor", player.getLocation());
        player.sendMessage(Practice.getMessage("admin.set-editor"));
    }

}
