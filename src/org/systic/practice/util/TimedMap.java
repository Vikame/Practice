package org.systic.practice.util;

import org.systic.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class TimedMap<K, V> extends HashMap<K, V> {

    private final int time;

    public TimedMap(int time){
        this.time = time;
    }

    public V put(K k, V v){
        V val = super.put(k, v);

        new BukkitRunnable(){
            public void run(){
                remove(k);
                if(k instanceof Callable) ((Callable)k).call();
                if(v instanceof Callable) ((Callable)v).call();
            }
        }.runTaskLater(Practice.inst(), 20 * time);

        return val;
    }

}
