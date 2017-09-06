package org.systic.practice.arena;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.systic.practice.Practice;
import org.systic.practice.gui.ArenaListGUI;
import org.systic.practice.util.Config;
import org.systic.practice.util.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager implements Listener{

    private final List<Arena> arenas = new ArrayList<>();

    public ArenaManager(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e){
        Chunk chunk = e.getChunk();

        for(Arena arena : arenas){
            Chunk c1 = arena.one.getChunk();
            Chunk c2 = arena.two.getChunk();

            if((c1.getX() == chunk.getX() && c1.getZ() == chunk.getZ()) || (c2.getX() == chunk.getX() && c2.getZ() == chunk.getZ())){
                e.setCancelled(true);
                return;
            }
        }
    }

    /*
     * Returns all registered arenas.
     */
    public List<Arena> all(){
        return arenas;
    }

    /*
     * Adds an arena.
     */
    public void add(Arena arena){
        arenas.add(arena);
        ArenaListGUI.inst().update();
    }

    /*
     * Removes an arena.
     */
    public void remove(Arena arena){
        arenas.remove(arena);

        Practice.inst().arenas.config.set(arena.name, null);

        ArenaListGUI.inst().update();
    }

    /*
     * Gets a random arena.
     */
    public Arena random(){
        if(arenas.isEmpty()) return null;

        return arenas.get(ThreadLocalRandom.current().nextInt(arenas.size()));
    }

    /*
     * Get an arena by its name (CaSe-InSeNsItIvE)
     */
    public Arena get(String name){
        for(Arena arena : arenas){
            if(arena.name.equalsIgnoreCase(name)) return arena;
        }

        return null;
    }

    /*
     * Save all arenas.
     */
    public void save(){
        Config config = Practice.inst().arenas;

        for(Arena arena : arenas){
            config.config.set(arena.name, Locations.toString(arena.one) + ";" + Locations.toString(arena.two));
        }

        config.save();
    }

    /*
     * Load all arenas.
     */
    public void load(){
        Config config = Practice.inst().arenas;

        for (String s : config.config.getKeys(false)) {
            String[] parts = config.config.getString(s).split(";");

            new Arena(s, Locations.fromString(parts[0]), Locations.fromString(parts[1]));
        }
    }

}
