package org.systic.practice.commands.impl;

import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.util.InventorySnapshot;

import java.util.UUID;

public class DuelInventoryCommand extends PlayerCommand {

    public DuelInventoryCommand() {
        super("_", null);
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(args.length <= 0){
            player.sendMessage(C.c("&cUsage: /" + label + " <id>"));
            return;
        }

        UUID uuid = null;
        try{
            uuid = UUID.fromString(args[0]);
        }catch(IllegalArgumentException e){
            player.sendMessage(C.c("&c" + args[0] + " is not a valid id."));
            return;
        }

        InventorySnapshot snapshot = InventorySnapshot.get(uuid);
        if(snapshot == null){
            player.sendMessage(Practice.getMessage("generic.no-snapshot"));
            return;
        }

        player.openInventory(snapshot.inventory);
    }

}
