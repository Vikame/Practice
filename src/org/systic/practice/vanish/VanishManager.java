package org.systic.practice.vanish;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftProjectile;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.systic.practice.Practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VanishManager implements Listener {

    private final Map<UUID, List<UUID>> hidden = new HashMap<>();

    public VanishManager() {
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    public boolean canSee(Player p, Player other) {
        return p.canSee(other);
    }

    public void hideCompletely(Player p) {
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            hide(p, pl);
            hide(pl, p);
        }
    }

    public void hideAll(Player p) {
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            hide(p, pl);
        }
    }

    public void hide(Player p, Player hide) {
        if (p.getName().equals(hide.getName())) return;

        if (canSee(p, hide)) {

            p.hidePlayer(hide);

            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) hide).getHandle()));

        }
    }

    public void showAll(Player p) {
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            show(p, pl);
        }
    }

    public void show(Player p, Player show) {
        if (!canSee(p, show)) {
            p.showPlayer(show);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e){
        Projectile proj = e.getEntity();

        if((proj instanceof CraftProjectile && ((CraftProjectile)proj).getHandle() instanceof VBaseEntity) || proj.getShooter() == null || !(proj.getShooter() instanceof Player)) return;

        Player player = (Player)proj.getShooter();

        Entity entity;
        switch(proj.getType()){
            case ENDER_PEARL:
                entity = new VEnderPearl(player);
                break;
            case SPLASH_POTION:
                entity = new VPotion(player, ((ThrownPotion)proj).getItem());
                break;
            default: return;
        }

        e.setCancelled(true);
        ((CraftPlayer)player).getHandle().getWorld().addEntity(entity);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e){
        if(!(e.getEntity() instanceof Player)) return;

        Player player = (Player)e.getEntity();

        e.setCancelled(true);
        ((CraftPlayer)player).getHandle().getWorld().addEntity(new VArrow(player, e.getForce()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrop(PlayerDropItemEvent e){
        if(!e.isCancelled()){
            Player p = e.getPlayer();
            e.getItemDrop().setMetadata("droppedBy", new FixedMetadataValue(Practice.inst(), p.getName()));
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        Item item = e.getItem();
        if(!item.hasMetadata("droppedBy")) return;

        Player player = e.getPlayer();
        Player drop = Bukkit.getPlayer(item.getMetadata("droppedBy").get(0).asString());

        if (drop != null && (!canSee(player, drop) || !canSee(player, drop))) e.setCancelled(true);
    }

}
