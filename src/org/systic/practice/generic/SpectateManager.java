package org.systic.practice.generic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SpectateManager {

    public Map<UUID, UUID> spectating;

    public SpectateManager(){
        spectating = new HashMap<>();
    }

    public List<Player> getAllSpectating(Player player){
        List<Player> ret = new ArrayList<>();
        if(!spectating.containsValue(player.getUniqueId())) return ret;

        for(Map.Entry<UUID, UUID> entry : spectating.entrySet()){
            if(entry.getValue().equals(player.getUniqueId())){
                Player p = Bukkit.getPlayer(entry.getKey());

                if(p != null && p.isOnline()) ret.add(p);
            }
        }

        return ret;
    }

    public Player getSpectating(Player player){
        if(!spectating.containsKey(player.getUniqueId())) return null;

        return Bukkit.getPlayer(spectating.get(player.getUniqueId()));
    }

    public void spectate(Player spectator, Player player){
        spectating.put(spectator.getUniqueId(), player.getUniqueId());
    }

    public void stopSpectating(Player player){
        spectating.remove(player.getUniqueId());
    }

}
