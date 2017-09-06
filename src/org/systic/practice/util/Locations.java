package org.systic.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations {

    public static String toString(Location location){
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location fromString(String string) {
        String[] parts = string.split(",");

        World world = Bukkit.getWorld(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);

        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location minimum(Location l1, Location l2){
        double x = l1.getX() > l2.getX() ? l2.getX() : l1.getX();
        double y = l1.getY() > l2.getY() ? l2.getY() : l1.getY();
        double z = l1.getZ() > l2.getZ() ? l2.getZ() : l1.getZ();

        return new Location(l1.getWorld(), x, y, z, l1.getYaw(), l1.getYaw());
    }

    public static Location maximum(Location l1, Location l2){
        double x = l1.getX() < l2.getX() ? l2.getX() : l1.getX();
        double y = l1.getY() < l2.getY() ? l2.getY() : l1.getY();
        double z = l1.getZ() < l2.getZ() ? l2.getZ() : l1.getZ();

        return new Location(l1.getWorld(), x, y, z, l1.getYaw(), l1.getYaw());
    }

}
