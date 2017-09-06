package org.systic.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Depend {

    public static boolean exists(String plugin){
        Plugin pl = Bukkit.getPluginManager().getPlugin(plugin);

        return pl != null && pl.isEnabled();
    }

}
