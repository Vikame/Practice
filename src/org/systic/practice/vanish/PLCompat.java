package org.systic.practice.vanish;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTabComplete;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.systic.practice.Practice;

import java.util.ArrayList;
import java.util.List;

public class PLCompat extends PacketAdapter {

    private VanishManager vanish_manager;

    public PLCompat() {
        super(Practice.inst(), PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Server.BLOCK_BREAK_ANIMATION, PacketType.Play.Client.TAB_COMPLETE);
        vanish_manager = Practice.inst().vanish_manager;
    }

    public void onPacketSending(PacketEvent event){
        Player p = event.getPlayer();

        PacketContainer packet = event.getPacket();
        PacketType type = packet.getType();

        if(type == PacketType.Play.Server.BLOCK_BREAK_ANIMATION){
            Entity e = packet.getEntityModifier(p.getWorld()).read(0);

            if (e == null) return;

            if(e instanceof Player){
                Player player = (Player)e;

                if (!vanish_manager.canSee(p, player)) event.setCancelled(true);
            }
        }else if(type == PacketType.Play.Server.SPAWN_ENTITY){
            Entity e = packet.getEntityModifier(p.getWorld()).read(0);

            if(e instanceof Item){
                if(!e.hasMetadata("droppedBy")) return;

                Player drop = Bukkit.getPlayer(e.getMetadata("droppedBy").get(0).asString());
                if(drop == null || p == drop) return;

                if (!vanish_manager.canSee(p, drop)) event.setCancelled(true);
            }else if(e instanceof Projectile){
                Projectile proj = (Projectile)e;

                if(proj.getShooter() == null || !(proj.getShooter() instanceof Player)) return;

                Player shot = (Player)proj.getShooter();
                if (!vanish_manager.canSee(p, shot)) event.setCancelled(true);
            }
        }else if(type == PacketType.Play.Server.NAMED_SOUND_EFFECT){
            double x = packet.getIntegers().read(0) / 8.0D;
            double y = packet.getIntegers().read(1) / 8.0D;
            double z = packet.getIntegers().read(2) / 8.0D;

            Player nearestPlayer = null;
            double nearestDistance = 5.0D;
            for (Player player : Bukkit.getServer().getOnlinePlayers()){
                double deltaX = player.getLocation().getX() - x;
                double deltaY = player.getLocation().getY() - y;
                double deltaZ = player.getLocation().getZ() - z;

                double distance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
                if (distance < nearestDistance){
                    nearestPlayer = player;
                    nearestDistance = distance;
                }
            }

            if (nearestPlayer != null && !vanish_manager.canSee(p, nearestPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        Player p = e.getPlayer();

        PacketContainer packet = e.getPacket();

        if (e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            String str = packet.getStrings().read(0).toLowerCase();

            e.setCancelled(true);

            if (str.startsWith("/") && !str.contains(" ")) return;

            String last = str;

            if (str.contains(" ")) last = str.substring(str.lastIndexOf(' ') + 1);

            List<String> list = new ArrayList<>();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(last)) {
                    list.add(player.getName());
                }
            }

            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutTabComplete(list.toArray(new String[list.size()])));
        }
    }

}
