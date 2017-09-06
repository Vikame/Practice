package org.systic.practice.vanish;

import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;

public class VEnderPearl extends EntityEnderPearl implements VBaseEntity{

    private static VanishManager vanish_manager = Practice.inst().vanish_manager;

    public VEnderPearl(Player shooter){
        super(((CraftWorld)shooter.getWorld()).getHandle(), ((CraftPlayer)shooter).getHandle());
    }

    public void t_() {
        if (getBukkitEntity().getLocation().getBlock().getType() == Material.GLASS || !shooter.isAlive()) {
            die();
            return;
        }

        super.t_();
    }

    protected void a(MovingObjectPosition movingObjectPosition){
        if(movingObjectPosition.entity instanceof EntityPlayer){
            Player ent = ((EntityPlayer)movingObjectPosition.entity).getBukkitEntity();
            Player shot = ((EntityPlayer)shooter).getBukkitEntity();

            if (!vanish_manager.canSee(ent, shot) || !vanish_manager.canSee(shot, ent)) return;
        }

        if (getBukkitEntity().getLocation().getBlock().getType() == Material.GLASS || !shooter.isAlive()) {
            die();
            return;
        }

        super.a(movingObjectPosition);
    }

}
