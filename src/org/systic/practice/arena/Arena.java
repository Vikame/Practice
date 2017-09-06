package org.systic.practice.arena;

import org.systic.practice.Practice;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class Arena {

    public String name;
    public World world;
    public Location one, two;

    public Arena(String name, Location one, Location two){
        if(!one.getWorld().getName().equals(two.getWorld().getName()))
            throw new IllegalArgumentException("Arena spawn points are not within the same world.");

        this.name = name;
        world = one.getWorld();
        this.one = one;
        this.two = two;

        Chunk c1 = one.getChunk();
        if(!c1.isLoaded()) c1.load();

        Chunk c2 = two.getChunk();
        if(!c2.isLoaded()) c2.load();

        Practice.inst().arena_manager.add(this);
    }

}