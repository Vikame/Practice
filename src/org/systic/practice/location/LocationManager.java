package org.systic.practice.location;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.systic.practice.Practice;
import org.systic.practice.util.Config;
import org.systic.practice.util.Locations;

import java.util.HashMap;
import java.util.Map;

public class LocationManager implements Listener{

    public final Map<String, Location> locations = new HashMap<>();

    public LocationManager(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e){
        Chunk chunk = e.getChunk();

        for(Location location : locations.values()){
            Chunk c = location.getChunk();

            if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ()){
                e.setCancelled(true);
                return;
            }
        }
    }

    /*
     * Get a location by its name.
     */
    public Location get(String name){
        if(!locations.containsKey(name)) return null;

        return locations.get(name).clone().add(0, 1, 0);
    }

    /*
     * Check if a location exists.
     */
    public boolean contains(String name){
        return locations.containsKey(name.toLowerCase());
    }

    /*
     * Add a location.
     */
    public void add(String name, Location location){
        if(locations.containsKey(name.toLowerCase())) locations.remove(name.toLowerCase());
        locations.put(name.toLowerCase(), location);
    }

    /*
     * Remove a location.
     */
    public void remove(String name){
        locations.remove(name.toLowerCase());

        Practice.inst().arenas.config.set(name.toLowerCase(), null);
    }

    /*
     * Save all locations.
     */
    public void save(){
        Config config = Practice.inst().locations;

        for(Map.Entry<String, Location> entry : locations.entrySet()){
            config.config.set(entry.getKey().toLowerCase(), Locations.toString(entry.getValue()));
        }

        config.save();
    }

    /*
     * Load all locations.
     */
    public void load(){
        Config config = Practice.inst().locations;

        for (String s : config.config.getKeys(false)) {
            locations.put(s.toLowerCase(), Locations.fromString(config.config.getString(s)));
        }
    }

}
