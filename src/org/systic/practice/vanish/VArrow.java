package org.systic.practice.vanish;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;

public class VArrow extends EntityArrow implements VBaseEntity{

    private static VanishManager vanish_manager = Practice.inst().vanish_manager;

    public VArrow(Player shooter, float force){
        super(((CraftWorld)shooter.getWorld()).getHandle(), ((CraftPlayer)shooter).getHandle(), force * 2.0F);
        setCritical(force == 1.0F);
        fromPlayer = 2;
    }

    public void collide(Entity entity){
        if(!(shooter instanceof EntityPlayer) || !(entity instanceof EntityPlayer)){
            super.collide(entity);
            return;
        }

        Player p = ((EntityPlayer)shooter).getBukkitEntity();
        Player other = ((EntityPlayer)entity).getBukkitEntity();

        if (vanish_manager.canSee(p, other) && vanish_manager.canSee(other, p)) {
            super.collide(entity);
        }
    }

}
