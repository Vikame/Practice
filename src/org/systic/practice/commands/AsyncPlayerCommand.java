package org.systic.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.citadel.lag.Profile;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncPlayerCommand implements CommandExecutor, TabCompleter{

    public String name;
    public String permission;

    public AsyncPlayerCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;

        PluginCommand cmd = Practice.inst().getCommand(name);
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);

        Practice.inst().profiler.register(new Profile("Command (/" + this.name + ", ASync)", Material.BOOK_AND_QUILL));
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Practice.getMessage("commands.player-only").replace("%command%", label));
            return true;
        }
        if(permission != null && (!sender.isOp() && !sender.hasPermission(permission))){
            sender.sendMessage(C.c(Practice.getMessage("commands.no-permission")));
            return true;
        }

        new BukkitRunnable(){
            public void run(){
                String name = "Command (/" + AsyncPlayerCommand.this.name + ", ASync)";

                Practice.inst().profiler.begin(name);
                AsyncPlayerCommand.this.run((Player)sender, label, args);
                Practice.inst().profiler.end(name);
            }
        }.runTaskAsynchronously(Practice.inst());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) throws IllegalArgumentException {
        if(!(sender instanceof Player)) return new ArrayList<>();

        List<String> ret = new ArrayList<>();

        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            if(args.length == 0 || p.getName().toLowerCase().startsWith(args[0].toLowerCase())) ret.add(p.getName());
        }

        return ret;
    }

    public abstract void run(Player player, String label, String[] args);

}
