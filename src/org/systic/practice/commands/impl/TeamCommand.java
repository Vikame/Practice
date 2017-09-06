package org.systic.practice.commands.impl;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.gui.TeamDuelGUI;
import org.systic.practice.matching.*;
import org.systic.practice.team.Team;
import org.systic.practice.team.TeamManager;
import org.systic.practice.util.Inventories;

public class TeamCommand extends PlayerCommand {

    private final MatchManager match_manager;
    private final TeamManager team_manager;
    private final TeamMatchManager team_match_manager;
    private final TeamRequestManager team_request_manager;
    private final ArenaManager arena_manager;

    public TeamCommand() {
        super("team", null);
        match_manager = Practice.inst().match_manager;
        team_manager = Practice.inst().team_manager;
        team_match_manager = Practice.inst().team_match_manager;
        team_request_manager = Practice.inst().team_request_manager;
        arena_manager = Practice.inst().arena_manager;
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if (match_manager.get(player) != null || team_match_manager.get(player) != null) {
            player.sendMessage(Practice.getMessage("team.in-match"));
            return;
        }

        if(args.length <= 0){
            String format = Practice.getMessage("help.team").replace("%command%", label);

            player.sendMessage(format.replace("%subcommand%", "create").replace("%description%", "Create a team."));
            player.sendMessage(format.replace("%subcommand%", "invite <player>").replace("%description%", "Invite a player to your team."));
            player.sendMessage(format.replace("%subcommand%", "deinvite <player>").replace("%description%", "Revoke a players invite to your team."));
            player.sendMessage(format.replace("%subcommand%", "join <player>").replace("%description%", "Join a players team."));
            player.sendMessage(format.replace("%subcommand%", "duel <player>").replace("%description%", "Duel another players team."));
            player.sendMessage(format.replace("%subcommand%", "accept <player>").replace("%description%", "Accept a duel request from another players team."));
            player.sendMessage(format.replace("%subcommand%", "disband").replace("%description%", "Disband your team."));
            return;
        }

        String sub = args[0];
        if(sub.equalsIgnoreCase("create")){
            if(team_manager.get(player) != null){
                player.sendMessage(Practice.getMessage("team.already-in-team"));
                return;
            }

            new Team(player);
            player.sendMessage(Practice.getMessage("team.create"));

            Inventories.giveDefault(player);
        }else if(sub.equalsIgnoreCase("invite")){
            if(args.length <= 1){
                player.sendMessage(C.c("&cUsage: /" + label + " invite <player>"));
                return;
            }

            Team team = team_manager.get(player);
            if(team == null){
                player.sendMessage(Practice.getMessage("team.not-in-team"));
                return;
            }

            if(!team.owner.equals(player.getUniqueId())){
                player.sendMessage(Practice.getMessage("team.not-owner"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null || !player.isOnline()){
                player.sendMessage(Practice.getMessage("command.player-not-found").replace("%player%", args[1]));
                return;
            }

            if(team_manager.get(target) != null){
                player.sendMessage(Practice.getMessage("team.already-on-team").replace("%player%", target.getName()));
                return;
            }

            team.invite(target);
            new FancyMessage(C.complete(Practice.getMessage("team.invite").replace("%player%", player.getName()))).command("/team join " + player.getName()).send(target);
            team.message(Practice.getMessage("team.invited").replace("%player%", target.getName()));
        }else if(sub.equalsIgnoreCase("deinvite")){
            if(args.length <= 1){
                player.sendMessage(C.c("&cUsage: /" + label + " deinvite <player>"));
                return;
            }

            Team team = team_manager.get(player);
            if(team == null){
                player.sendMessage(Practice.getMessage("team.not-in-team"));
                return;
            }

            if(!team.owner.equals(player.getUniqueId())){
                player.sendMessage(Practice.getMessage("team.not-owner"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null || !player.isOnline()){
                player.sendMessage(Practice.getMessage("command.player-not-found").replace("%player%", args[1]));
                return;
            }

            if(!team.isInvited(target)){
                player.sendMessage(Practice.getMessage("team.player-not-invited").replace("%player%", target.getName()));
                return;
            }

            team.deinvite(target);
            team.message(Practice.getMessage("team.deinvited").replace("%player%", target.getName()));
        }else if(sub.equalsIgnoreCase("join")){
            if(args.length <= 1){
                player.sendMessage(C.c("&cUsage: /" + label + " join <player>"));
                return;
            }

            Team team = team_manager.get(player);
            if(team != null){
                player.sendMessage(Practice.getMessage("team.already-in-team"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null || !player.isOnline()){
                player.sendMessage(Practice.getMessage("command.player-not-found").replace("%player%", args[1]));
                return;
            }

            team = team_manager.get(target);

            if(team == null){
                player.sendMessage(Practice.getMessage("team.no-team-found"));
                return;
            }

            if (!team.isInvited(player) && !team.open) {
                player.sendMessage(Practice.getMessage("team.player-not-invited").replace("%player%", player.getName()));
                return;
            }

            if(team.addMember(player)){
                team.deinvite(player);
                team.addMember(player);
                player.sendMessage(Practice.getMessage("team.join").replace("%team%", team.name));
                team.message(Practice.getMessage("team.player-joined").replace("%player%", player.getName()));
            }else{
                player.sendMessage(Practice.getMessage("team.full").replace("%team%", team.name));
            }
        }else if(sub.equalsIgnoreCase("disband")) {
            Team team = team_manager.get(player);
            if (team == null) {
                player.sendMessage(Practice.getMessage("team.not-in-team"));
                return;
            }

            if (!team.owner.equals(player.getUniqueId())) {
                player.sendMessage(Practice.getMessage("team.not-owner"));
                return;
            }

            team.message(Practice.getMessage("team.disband"));
            team.disband();

            Inventories.giveDefault(player);
        }else if(sub.equalsIgnoreCase("duel")){
            Team team = team_manager.get(player);
            if (team == null) {
                player.sendMessage(Practice.getMessage("team.not-in-team"));
                return;
            }

            if (!team.owner.equals(player.getUniqueId())) {
                player.sendMessage(Practice.getMessage("team.not-owner"));
                return;
            }

            if(args.length <= 1){
                player.sendMessage(C.c("&cUsage: /" + label + " duel <player>"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
                return;
            }

            Team other = team_manager.get(target);

            if(team == other){
                player.sendMessage(Practice.getMessage("team.cannot-target-friendly"));
                return;
            }

            if(team_match_manager.get(team) != null){
                player.sendMessage(Practice.getMessage("team.duel.in-match"));
                return;
            }
            if(team_match_manager.get(other) != null){
                player.sendMessage(Practice.getMessage("team.duel.other-in-match").replace("%team%", other.name));
                return;
            }

            TeamMatchRequest request = team_request_manager.get(team, other);

            if(request != null){
                player.sendMessage(Practice.getMessage("team.duel.wait").replace("%player%", target.getName()));
                return;
            }

            player.openInventory(new TeamDuelGUI(team, other).update());
        }else if(sub.equalsIgnoreCase("accept")){
            Team team = team_manager.get(player);
            if (team == null) {
                player.sendMessage(Practice.getMessage("team.not-in-team"));
                return;
            }

            if (!team.owner.equals(player.getUniqueId())) {
                player.sendMessage(Practice.getMessage("team.not-owner"));
                return;
            }

            if(args.length <= 1){
                player.sendMessage(C.c("&cUsage: /" + label + " accept <player>"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                player.sendMessage(Practice.getMessage("commands.player-not-found").replace("%player%", args[0]));
                return;
            }

            Team other = team_manager.get(target);

            if(team == other){
                player.sendMessage(Practice.getMessage("team.cannot-target-friendly"));
                return;
            }

            TeamMatchRequest request = team_request_manager.get(other, team);

            if(request == null){
                player.sendMessage(Practice.getMessage("team.duel.not-found").replace("%team%", other.name));
                return;
            }

            team_request_manager.remove(request);

            new TeamMatch(request.ladder, request.arena, request.one, request.two);
        } else if (sub.equalsIgnoreCase("open") && player.hasPermission("systic.team.open")) {
            Team team = team_manager.get(player);
            if (team == null) {
                player.sendMessage(Practice.getMessage("team.not-in-team"));
                return;
            }

            if (!team.owner.equals(player.getUniqueId())) {
                player.sendMessage(Practice.getMessage("team.not-owner"));
                return;
            }

            player.sendMessage(C.c("&cPlease note that abuse of this command will lead to your permission being revoked."));

            team.open = !team.open;
            player.sendMessage(C.c("&" + (team.open ? "aYour team is now open, anyone can join your team." : "cYour team is now closed, only invited players will be able to join your team.")));
            if (team.open) Bukkit.broadcastMessage(C.c("&c[Team] &f" + team.name + " is now open."));
        }


        else{
            player.sendMessage(C.c("&c" + sub + " is not a recognized sub-command."));
        }
    }

}
