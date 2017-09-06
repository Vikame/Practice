package org.systic.practice.matching;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.systic.citadel.settings.PlayerSettings;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.generic.StaffManager;
import org.systic.practice.kit.Kit;
import org.systic.practice.kit.KitManager;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.location.LocationManager;
import org.systic.practice.team.Team;
import org.systic.practice.util.Inventories;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;
import org.systic.practice.vanish.VanishManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TeamMatch {

    private static final VanishManager vanish_manager = Practice.inst().vanish_manager;
    private static final KitManager kit_manager = Practice.inst().kit_manager;
    private static final TeamMatchManager team_match_manager = Practice.inst().team_match_manager;
    private static final LocationManager location_manager = Practice.inst().location_manager;
    private static final StaffManager staff_manager = Practice.inst().staff_manager;

    public final Ladder ladder;
    public final Team one, two;
    public final List<UUID> one_players, two_players;
    public final Set<UUID> hide;
    public final long start;
    public final long pre;
    public final Set<UUID> dead;

    public TeamMatch(Ladder ladder, Arena arena, Team one, Team two){
        this.ladder = ladder;

        this.one = one;
        this.two = two;
        this.one_players = one.getMatchable();
        this.two_players = two.getMatchable();

        this.dead = new HashSet<>();

        boolean swtch = ThreadLocalRandom.current().nextBoolean();

        one.teleport(swtch ? arena.two : arena.one);
        two.teleport(swtch ? arena.one : arena.two);

        for(Player p : one.getOnlineMembers()){
            vanish_manager.hideCompletely(p);
        }

        for(Player p : two.getOnlineMembers()){
            vanish_manager.hideCompletely(p);
        }

        for(Player p : one.getOnlineMembers()) {
            for(Player other : two.getOnlineMembers()) {
                vanish_manager.show(p, other);
            }
            for(Player other : one.getOnlineMembers()){
                vanish_manager.show(p, other);
            }

            if (staff_manager.isInStaff(p)) staff_manager.disableStaff(p);

            if(p.getGameMode() != GameMode.SURVIVAL) p.setGameMode(GameMode.SURVIVAL);

            if(p.getAllowFlight()) p.setAllowFlight(false);

            for(PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());

            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setSaturation(10);
            p.setFireTicks(0);

            giveKitItems(p);
        }

        for(Player p : two.getOnlineMembers()){
            vanish_manager.hideCompletely(p);

            for(Player other : one.getOnlineMembers()){
                vanish_manager.show(p, other);
            }
            for(Player other : two.getOnlineMembers()) {
                vanish_manager.show(p, other);
            }

            if (staff_manager.isInStaff(p)) staff_manager.disableStaff(p);

            if(p.getGameMode() != GameMode.SURVIVAL) p.setGameMode(GameMode.SURVIVAL);

            if(p.getAllowFlight()) p.setAllowFlight(false);

            for(PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());

            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setSaturation(10);
            p.setFireTicks(0);

            giveKitItems(p);
        }

        this.hide = new HashSet<>();
        this.start = System.currentTimeMillis() + 5000;
        this.pre = start;

        team_match_manager.add(this);
    }

    public boolean isPreMatch(){
        return pre - System.currentTimeMillis() > 0;
    }

    public void message(String message){
        for(UUID uuid : one_players){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) p.sendMessage(message);
        }

        for(UUID uuid : two_players){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) p.sendMessage(message);
        }
    }

    public void message(FancyMessage message){
        for(UUID uuid : one_players){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) message.send(p);
        }

        for(UUID uuid : two_players){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) message.send(p);
        }
    }

    public Team opposite(Player player){
        if(one_players.contains(player.getUniqueId())) return two;
        if(two_players.contains(player.getUniqueId())) return one;

        return null;
    }

    public Team get(Player player){
        if(one_players.contains(player.getUniqueId())) return one;
        if(two_players.contains(player.getUniqueId())) return two;

        return null;
    }

    public Team getWinner(){
        boolean one_alive = false, two_alive = false;
        for(UUID uuid : one_players){
            if(!dead.contains(uuid)){
                one_alive = true;
                break;
            }
        }

        for(UUID uuid : two_players){
            if(!dead.contains(uuid)){
                two_alive = true;
                break;
            }
        }

        if(one_alive && two_alive) return null;

        if(one_alive) return one;
        else return two;
    }

    public Set<UUID> getHiddenOnFinish(){
        return hide;
    }

    public void hideOnFinish(Player player) {
        hide.add(player.getUniqueId());
    }

    // Only used for forcing a player death.
    public void die(Player p, String path){
        dead.add(p.getUniqueId());
        Team winner = getWinner();

        Team team = get(p);
        if(team != null){
            team.message(Practice.getMessage(path).replace("%player%", p.getName()));
        }

        message(Practice.getMessage("match.team-death").replace("%player%", p.getName()));

        if(winner != null){

            for(UUID uuid : one_players){
                Player player = Bukkit.getPlayer(uuid);

                if(player != null && player.isOnline()) {
                    handle(player);

                    for (UUID other : getHiddenOnFinish()) {
                        Player pl = Bukkit.getPlayer(other);
                        if(pl != null && pl.isOnline()){
                            if (!PlayerSettings.get(pl).get("show players in lobby")) {
                                vanish_manager.hide(pl, player);
                            }
                        }
                    }
                }
            }

            for(UUID uuid : two_players){
                Player player = Bukkit.getPlayer(uuid);

                if(player != null && player.isOnline()) {
                    handle(player);

                    for (UUID other : getHiddenOnFinish()) {
                        Player pl = Bukkit.getPlayer(other);
                        if(pl != null && pl.isOnline()){
                            if (!PlayerSettings.get(pl).get("show players in lobby")) {
                                vanish_manager.hide(pl, player);
                            }
                        }
                    }
                }
            }

            message(Practice.getMessage("match.team-win").replace("%team%", winner.name));
            team_match_manager.end(this);
        }

        handle(p);
    }

    private void handle(Player p){
        Inventories.giveDefault(p);

        boolean ps = PlayerSettings.get(p).get("show players in lobby", true);

        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            if (ps) {
                vanish_manager.show(p, player);
            } else vanish_manager.hide(p, player);
        }

        if(!p.isDead()) {
            if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"))
                p.teleport(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));

            p.setHealth(20);
            p.setFireTicks(0);
            p.setFoodLevel(20);
            p.setSaturation(10);

            for (PotionEffect eff : p.getActivePotionEffects()) {
                p.removePotionEffect(eff.getType());
            }
        }
    }

    private void giveKitItems(Player player){
        List<Kit> kits = kit_manager.getKits(player, ladder);

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        if(kits.isEmpty()){
            ladder.applyDefault(player);
        }else{
            player.getInventory().setItem(0, Items.DEFAULT_KIT);

            for(int i = 0; i < kits.size(); i++){
                int slot = i + 2;
                if(slot > 8) break;

                player.getInventory().setItem(slot, Item.create(Material.BOOK).name(C.c("&7Kit #" + (i + 1))).build());
            }
        }
    }
}
