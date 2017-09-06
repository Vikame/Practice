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
import org.systic.practice.util.Inventories;
import org.systic.practice.util.Item;
import org.systic.practice.util.Items;
import org.systic.practice.vanish.VanishManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Match {

    private static final VanishManager vanish_manager = Practice.inst().vanish_manager;
    private static final KitManager kit_manager = Practice.inst().kit_manager;
    private static final MatchManager match_manager = Practice.inst().match_manager;
    private static final LocationManager location_manager = Practice.inst().location_manager;
    private static final StaffManager staff_manager = Practice.inst().staff_manager;

    public final Ladder ladder;
    public final MatchType type;
    public final Player one, two;
    public final Set<UUID> hide;
    public final long start;
    public final long pre;

    public Match(Ladder ladder, MatchType type, Arena arena, Player one, Player two){
        this.ladder = ladder;
        this.type = type;
        this.one = one;
        this.two = two;

        if (staff_manager.isInStaff(one)) staff_manager.disableStaff(one);
        if (staff_manager.isInStaff(two)) staff_manager.disableStaff(two);

        if(one.getGameMode() != GameMode.SURVIVAL) one.setGameMode(GameMode.SURVIVAL);
        if(two.getGameMode() != GameMode.SURVIVAL) two.setGameMode(GameMode.SURVIVAL);

        if(one.getAllowFlight()) one.setAllowFlight(false);
        if(two.getAllowFlight()) two.setAllowFlight(false);

        for(PotionEffect eff : one.getActivePotionEffects()) one.removePotionEffect(eff.getType());
        for(PotionEffect eff : two.getActivePotionEffects()) two.removePotionEffect(eff.getType());

        one.setHealth(one.getMaxHealth());
        two.setHealth(two.getMaxHealth());
        one.setFoodLevel(20);
        one.setSaturation(10);
        two.setFoodLevel(20);
        two.setSaturation(10);
        one.setFireTicks(0);
        two.setFireTicks(0);

        boolean swtch = ThreadLocalRandom.current().nextBoolean();

        one.teleport(swtch ? arena.two : arena.one);
        two.teleport(swtch ? arena.one : arena.two);

        vanish_manager.hideCompletely(one);
        vanish_manager.hideCompletely(two);
        vanish_manager.show(one, two);
        vanish_manager.show(two, one);

        giveKitItems(one);
        giveKitItems(two);

        this.hide = new HashSet<>();
        this.start = System.currentTimeMillis() + 5000;
        this.pre = start;

        match_manager.add(this);
    }

    public boolean isPreMatch(){
        return pre - System.currentTimeMillis() > 0;
    }

    public void message(String message){
        one.sendMessage(message);
        two.sendMessage(message);
    }

    public void message(FancyMessage message){
        message.send(one);
        message.send(two);
    }

    public Player opposite(Player player){
        if(player.equals(one)) return two;
        if(player.equals(two)) return one;

        return null;
    }

    public Set<UUID> getHiddenOnFinish(){
        return hide;
    }

    public void hideOnFinish(Player player) {
        hide.add(player.getUniqueId());
    }

    public void draw(String reason){
        match_manager.end(this);

        for(UUID uuid : hide){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()){
                if (!PlayerSettings.get(player).get("show players in lobby", true)) {
                    vanish_manager.hide(player, one);
                    vanish_manager.hide(player, two);
                }
            }
        }

        message(Practice.getMessage("generic.draw").replace("%reason%", reason));

        handle(one);
        handle(two);
    }

    private void handle(Player p){
        Inventories.giveDefault(p);

        boolean ps = PlayerSettings.get(p).get("show players in lobby", true);

        if (ps) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                vanish_manager.hide(p, player);
            }
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
