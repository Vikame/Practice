package org.systic.practice.vanish;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.systic.practice.Practice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VPotion extends EntityPotion implements VBaseEntity {

    private static VanishManager vanish_manager = Practice.inst().vanish_manager;

    public VPotion(Player shooter, ItemStack item) {
        this((CraftPlayer) shooter, item);
    }

    public VPotion(CraftPlayer player, ItemStack item) {
        super(((CraftWorld) player.getWorld()).getHandle(), player.getHandle(), CraftItemStack.asNMSCopy(item));
    }

    protected void a(MovingObjectPosition movingObjectPosition) {
        if(movingObjectPosition.entity instanceof EntityPlayer){
            Player ent = ((EntityPlayer)movingObjectPosition.entity).getBukkitEntity();
            Player shot = ((EntityPlayer)shooter).getBukkitEntity();

            if (!vanish_manager.canSee(ent, shot) || !vanish_manager.canSee(shot, ent)) return;
        }

        if (!this.world.isClientSide) {
            List list = Items.POTION.h(this.item);

            if(list != null && !list.isEmpty()) {
                AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List<?> list1 = this.world.a(EntityLiving.class, axisalignedbb);

                if (list1 != null && !list1.isEmpty()) {
                    Iterator iterator = list1.iterator();
                    HashMap<LivingEntity, Double> affected = new HashMap<>();

                    while (iterator.hasNext()) {
                        EntityLiving entity = (EntityLiving) iterator.next();
                        double d0 = this.h(entity);

                        if (d0 < 16.0D) {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                            if (entity == movingObjectPosition.entity) {
                                d1 = 1.0D;
                            }

                            affected.put((LivingEntity) entity.getBukkitEntity(), d1);
                        }
                    }

                    PotionSplashEvent event1 = CraftEventFactory.callPotionSplashEvent(this, affected);
                    if (!event1.isCancelled()) {

                        for (LivingEntity victim : event1.getAffectedEntities()) {
                            if (!(victim instanceof Player) || vanish_manager.canSee(((Player) victim), ((Player) this.shooter.getBukkitEntity()))) {

                                double d11 = event1.getIntensity(victim);
                                EntityLiving entityliving = ((CraftLivingEntity) victim).getHandle();
                                double d = event1.getIntensity(victim);

                                for(Object o : list){
                                    MobEffect mobeffect = (MobEffect)o;

                                    int i = mobeffect.getEffectId();

                                    if (this.getShooter() instanceof EntityPlayer && entityliving instanceof EntityPlayer) {
                                        if (MobEffectList.byId[i].isInstant()) {
                                            MobEffectList.byId[i].applyInstantEffect(this, this.getShooter(), entityliving, mobeffect.getAmplifier(), d11);
                                        } else {
                                            int j = (int) (d * (double) mobeffect.getDuration() + 0.5D);

                                            if (j > 20) {
                                                entityliving.addEffect(new MobEffect(i, j, mobeffect.getAmplifier()));
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                Player shooter = (Player) this.shooter.getBukkitEntity();

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if ((player == shooter) || ((vanish_manager.canSee(player, shooter) && vanish_manager.canSee(shooter, player)))) {
                        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

                        entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldEvent(2002, new BlockPosition(this), getPotionValue(), false));
                    }
                }

                die();
            }
        }

    }
}
