package org.systic.practice.team;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.systic.practice.Practice;
import org.systic.practice.matching.MatchManager;
import org.systic.practice.matching.TeamMatchManager;

import java.util.*;

public class Team {

    private static final TeamManager team_manager = Practice.inst().team_manager;
    private static final MatchManager match_manager = Practice.inst().match_manager;
    private static final TeamMatchManager team_match_manager = Practice.inst().team_match_manager;

    public String name;
    public UUID owner;
    public Set<UUID> members;
    public Set<UUID> invited;
    public List<Team> duels;
    public boolean disbanded;
    public boolean open;

    public Team(Player owner){
        this.name = owner.getName() + "'s Team";
        this.owner = owner.getUniqueId();
        this.members = new HashSet<>();
        this.invited = new HashSet<>();
        this.duels = new ArrayList<>();
        this.disbanded = false;
        this.open = false;

        team_manager.add(this);
    }

    public void disband(){
        team_manager.remove(this);
        disbanded = true;
    }

    public Player getOwner(){
        return Bukkit.getPlayer(owner);
    }

    public void setOwner(Player player){
        this.owner = player.getUniqueId();
    }

    public boolean isMember(Player player){
        return owner.equals(player.getUniqueId()) || members.contains(player.getUniqueId());
    }

    public List<UUID> getMatchable(){
        List<UUID> ret = new ArrayList<>();

        Player owner = getOwner();
        if (owner != null && owner.isOnline() && match_manager.get(owner) == null && team_match_manager.get(owner) == null)
            ret.add(this.owner);

        for(UUID uuid : members){
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline() && match_manager.get(p) == null && team_match_manager.get(p) == null)
                ret.add(uuid);
        }

        return ret;
    }

    public List<UUID> getOnline(){
        List<UUID> ret = new ArrayList<>();

        Player owner = getOwner();
        if(owner != null && owner.isOnline()) ret.add(this.owner);

        for(UUID uuid : members){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) ret.add(uuid);
        }

        return ret;
    }

    public List<Player> getOnlineMembers(){
        List<Player> ret = new ArrayList<>();

        Player owner = getOwner();
        if(owner != null && owner.isOnline()) ret.add(owner);

        for(UUID uuid : members){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) ret.add(p);
        }

        return ret;
    }

    public boolean addMember(Player player){
        if(members.size() > 7) return false;

        members.add(player.getUniqueId());
        return true;
    }

    public boolean isInvited(Player player){
        return invited.contains(player.getUniqueId());
    }

    public void invite(Player player){
        invited.add(player.getUniqueId());
    }

    public void deinvite(Player player){
        invited.remove(player.getUniqueId());
    }

    public void teleport(Location location){
        for(UUID uuid : members){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) p.teleport(location);
        }

        Player p = Bukkit.getPlayer(owner);
        if(p != null && p.isOnline()) p.teleport(location);
    }

    public void message(String message){
        for(UUID uuid : members){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) p.sendMessage(message);
        }

        Player p = Bukkit.getPlayer(owner);
        if(p != null && p.isOnline()) p.sendMessage(message);
    }

}
