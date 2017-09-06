package org.systic.practice.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.systic.practice.Practice;

import java.util.HashMap;
import java.util.HashSet;

public class TimedSet<E> extends HashSet<E> {

    private final int time;

    public TimedSet(int time){
        this.time = time;
    }

    public boolean add(E e){
        boolean ret = super.add(e);

        if(ret) {
            new BukkitRunnable() {
                public void run() {
                    if(remove(e)) if (e instanceof Callable) ((Callable)e).call();
                }
            }.runTaskLater(Practice.inst(), 20 * time);
        }

        return ret;
    }

}
