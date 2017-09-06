package org.systic.practice.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class TeamWrapper {

    private String teamName;
    private String prefix;
    private String name;
    private String suffix;
    private int score;

    public TeamWrapper(String teamName){
        this.teamName = teamName;
        prefix(null).suffix(null).score = -1;
    }

    public TeamWrapper prefix(String prefix){
        this.prefix = prefix;
        return this;
    }

    public String prefix(){
        return prefix;
    }

    public TeamWrapper name(String name){
        this.name = name;
        return this;
    }

    public String name(){
        return name;
    }

    public TeamWrapper suffix(String suffix){
        this.suffix = suffix;
        return this;
    }

    public String suffix(){
        return suffix;
    }

    public TeamWrapper score(int score){
        this.score = score;
        return this;
    }

    public int score(){
        return score;
    }

    public boolean send(Objective objective){
        Scoreboard board = objective.getScoreboard();

        Team team = board.getTeam(teamName);
        if(team == null){
            team = board.registerNewTeam(teamName);
        }

        String mid = name;

        if(mid == null){
            mid = "" + ChatColor.values()[score] + ChatColor.RESET;
        }else mid = ChatColor.translateAlternateColorCodes('&', mid);

        Set<String> set = team.getEntries();

        if(set.size() <= 0){
            team.addEntry(mid);
        }else{
            boolean has = false;

            for(String s : set){
                if(!s.equals(mid)){
                    board.resetScores(mid);
                    team.removeEntry(s);
                }else has = true;
            }

            if(!has){
                team.addEntry(mid);
            }
        }

        if(score <= -1){
            board.resetScores(mid);
            return false;
        }

        if(prefix != null) team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
        else team.setPrefix("");

        if(suffix != null) team.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));
        else team.setSuffix("");

        objective.getScore(mid).setScore(score);

        return true;
    }

    public TeamWrapper clone(){
        try {
            return (TeamWrapper)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}