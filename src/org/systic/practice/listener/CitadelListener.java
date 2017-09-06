package org.systic.practice.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.systic.citadel.event.CitadelCommandEvent;
import org.systic.citadel.event.CitadelTeleportEvent;
import org.systic.citadel.event.PlayerToggleSettingEvent;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.commands.impl.TeleportCommand;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatchManager;
import org.systic.practice.vanish.VanishManager;

public class CitadelListener implements Listener {

    private final MatchManager match_manager;
    private final TeamMatchManager team_match_manager;
    private final VanishManager vanish_manager;

    public CitadelListener() {
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
        match_manager = Practice.inst().match_manager;
        team_match_manager = Practice.inst().team_match_manager;
        vanish_manager = Practice.inst().vanish_manager;
    }

    @EventHandler
    public void onCommand(CitadelCommandEvent e) {
        if (!(e.sender instanceof Player)) return;

        Player p = (Player) e.sender;

        if (match_manager.get(p) == null && team_match_manager.get(p) == null) return;

        if (e.command.equalsIgnoreCase("fly")) {
            p.sendMessage(Practice.getMessage("admin.fly.in-match"));
            e.setCancelled(true);
        } else if (e.command.equalsIgnoreCase("gamemode")) {
            p.sendMessage(Practice.getMessage("admin.gamemode.in-match"));
            e.setCancelled(true);
        }/*else if(e.command.equalsIgnoreCase("teleport")){
            p.sendMessage(Practice.getMessage("admin.teleport.in-match"));
            e.setCancelled(true);
        }*/
    }

    @EventHandler
    public void onTeleport(CitadelTeleportEvent e) {
        if (e.command.equalsIgnoreCase("teleport")) {
            if (match_manager.get(e.player) != null || team_match_manager.get(e.player) != null) {
                e.player.sendMessage(Practice.getMessage("admin.teleport.in-match"));
            } else {
                TeleportCommand.teleport(e.player, e.target);
            }

            e.setCancelled(true);
        } else if (e.command.equalsIgnoreCase("teleporthere")) {
            if (match_manager.get(e.target) != null || team_match_manager.get(e.target) != null) {
                e.player.sendMessage(Practice.getMessage("admin.teleporthere.in-match"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSettingToggle(PlayerToggleSettingEvent e) {
        if (e.setting.equalsIgnoreCase("show players in lobby")) {
            if (match_manager.get(e.player) != null || team_match_manager.get(e.player) != null) {
                e.player.sendMessage(C.c("&cYou cannot change player visibility whilst in a match."));
                e.setCancelled(true);
                return;
            }

            if (e.state) {
                for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    vanish_manager.show(e.player, pl);
                }
            } else {
                for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    vanish_manager.hide(e.player, pl);
                }
            }
        } else if (e.setting.equalsIgnoreCase("time")) {
            if (e.state) {
                e.player.setPlayerTime(6000, false);
            } else e.player.setPlayerTime(18000, false);
        }
    }

}
