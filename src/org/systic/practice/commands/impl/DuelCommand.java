package org.systic.practice.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.AsyncPlayerCommand;
import org.systic.practice.gui.DuelGUI;
import org.systic.practice.matching.MatchRequest;
import org.systic.practice.matching.Queue;
import org.systic.practice.matching.QueueManager;
import org.systic.practice.matching.RequestManager;
import org.systic.practice.util.Inventories;

public class DuelCommand extends AsyncPlayerCommand {

    private final RequestManager request_manager;
    private final QueueManager queue_manager;

    public DuelCommand() {
        super("duel", null);
        request_manager = Practice.inst().request_manager;
        queue_manager = Practice.inst().queue_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(args.length <= 0){
            player.sendMessage(C.c("&cUsage: /" + label + " <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
            return;
        }

        if(player == target){
            player.sendMessage(Practice.getMessage("commands.cannot-target-self"));
            return;
        }

        if (!PlayerSettings.get(target).get("duel requests", true)) {
            player.sendMessage(Practice.getMessage("duel.disabled-requests").replace("%player%", target.getName()));
            return;
        }

        Queue queue = queue_manager.getQueue(player);
        if (queue != null) {
            queue.unqueue(player);
            Inventories.giveDefault(player);

            player.sendMessage(Practice.getMessage("queue.leave").replace("%ladder%", queue.ladder.name));
            return;
        }

        MatchRequest request = request_manager.get(player, target);

        if(request != null){
            player.sendMessage(Practice.getMessage("duel.wait").replace("%player%", target.getName()));
            return;
        }

        player.openInventory(new DuelGUI(player, target).update());
    }
}
