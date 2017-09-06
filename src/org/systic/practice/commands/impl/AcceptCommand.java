package org.systic.practice.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.generic.FreezeManager;
import org.systic.practice.generic.SpectateManager;
import org.systic.practice.matching.*;

public class AcceptCommand extends PlayerCommand {

    private final RequestManager request_manager;
    private final MatchManager match_manager;
    private final ArenaManager arena_manager;
    private final SpectateManager spectate_manager;
    private final FreezeManager freeze_manager;
    private final QueueManager queue_manager;

    public AcceptCommand() {
        super("accept", null);
        request_manager = Practice.inst().request_manager;
        match_manager = Practice.inst().match_manager;
        arena_manager = Practice.inst().arena_manager;
        spectate_manager = Practice.inst().spectate_manager;
        freeze_manager = Practice.inst().freeze_manager;
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

        if(match_manager.get(player) != null){
            player.sendMessage(Practice.getMessage("duel.in-match"));
            return;
        }
        if(match_manager.get(target) != null){
            player.sendMessage(Practice.getMessage("duel.other-in-match").replace("%player%", target.getName()));
            return;
        }
        if(spectate_manager.getSpectating(player) != null){
            player.sendMessage(Practice.getMessage("duel.spectating"));
            return;
        }
        if(spectate_manager.getSpectating(target) != null){
            player.sendMessage(Practice.getMessage("duel.other-spectating").replace("%player%", target.getName()));
            return;
        }
        if(freeze_manager.isFrozen(player)){
            player.sendMessage(Practice.getMessage("duel.frozen"));
            return;
        }
        if(freeze_manager.isFrozen(target)){
            player.sendMessage(Practice.getMessage("duel.other-frozen").replace("%player%", target.getName()));
            return;
        }
        if (queue_manager.getQueue(player) != null) {
            player.sendMessage(C.c("&cYou cannot accept a duel request while in a queue."));
            return;
        }
        if (queue_manager.getQueue(target) != null) {
            player.sendMessage(C.c("&c" + target.getName() + " is currently in a matchmaking queue and cannot be dueled."));
            return;
        }

        MatchRequest request = request_manager.get(target, player);

        if(request == null){
            player.sendMessage(Practice.getMessage("duel.not-found").replace("%player%", target.getName()));
            return;
        }

        request_manager.remove(request);

        new Match(request.ladder, MatchType.DUEL, request.arena, player, target);
    }
}
