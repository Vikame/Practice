package org.systic.practice.util;

import com.ngxdev.protocolsupport.api.ProtocolSupportAPI;
import com.ngxdev.protocolsupport.api.ProtocolVersion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.systic.practice.Practice;
import org.systic.practice.team.Team;
import org.systic.practice.team.TeamManager;

public class Inventories {

    private static final TeamManager team_manager = Practice.inst().team_manager;

    public static void giveDefault(Player player){
        PlayerInventory inv = clear(player);

        inv.setItem(0, Items.KIT_EDITOR);

        Team team = team_manager.get(player);
        if (team != null) {
            if (player.getUniqueId().equals(team.owner)) {
                inv.setItem(4, Items.TEAM_LIST);
            }
        } else inv.setItem(4, Items.CREATE_TEAM);

        inv.setItem(7, Items.UNRANKED);

        if ((!player.hasPermission("systic.rankedbypass") && Practice.inst().stat_manager.getUnrankedWins(player) < 15) || Practice.inst().ranked_locked) {
            ProtocolVersion version = ProtocolSupportAPI.getProtocolVersion(player);
            if (version.isAfterOrEq(ProtocolVersion.MINECRAFT_1_8)) {
                inv.setItem(8, Items.RANKED_LOCKED_1_8);
            } else inv.setItem(8, Items.RANKED_LOCKED_1_7);
        } else inv.setItem(8, Items.RANKED);

        player.updateInventory();
    }

    public static void giveQueued(Player player){
        clear(player).setItem(8, Items.LEAVE_QUEUE);

        player.updateInventory();
    }

    public static void giveSpectator(Player player){
        clear(player).setItem(8, Items.LEAVE_SPECTATOR);

        player.updateInventory();
    }

    public static void giveStaff(Player player) {
        PlayerInventory inv = clear(player);

        inv.setItem(0, Items.COMPASS);
        inv.setItem(1, Items.VIEW_INVENTORY);
        inv.setItem(4, Items.BETTER_VIEW);
        inv.setItem(7, Items.ONLINE_STAFF);
        inv.setItem(8, Items.RANDOM_TELEPORT);

        player.updateInventory();
    }

    public static PlayerInventory clear(Player player){
        PlayerInventory inv = player.getInventory();

        inv.clear();
        inv.setArmorContents(new ItemStack[4]);

        return inv;
    }

}
