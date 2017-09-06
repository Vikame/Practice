package org.systic.practice.commands.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.arena.Arena;
import org.systic.practice.arena.ArenaManager;
import org.systic.practice.arena.Selection;
import org.systic.practice.commands.PlayerCommand;
import org.systic.practice.gui.ArenaListGUI;

import java.util.Map;

public class ArenaCommand extends PlayerCommand {

    public ArenaCommand() {
        super("arena", "systic.arena");
    }

    @Override
    public void run(Player player, String label, String[] args) {
        if(args.length <= 0){
            String format = Practice.getMessage("help.arena").replace("%command%", label);

            player.sendMessage(format.replace("%subcommand%", "wand").replace("%description%", "Get an arena selection wand."));
            player.sendMessage(format.replace("%subcommand%", "create <name>").replace("%description%", "Create an arena with your current selection."));
            player.sendMessage(format.replace("%subcommand%", "delete <name>").replace("%description%", "Delete an arena."));
            player.sendMessage(format.replace("%subcommand%", "list").replace("%description%", "List all arenas."));
            return;
        }

        String sub = args[0];
        if(sub.equalsIgnoreCase("wand")){
            Map<Integer, ItemStack> extra = player.getInventory().addItem(Selection.WAND);

            if(!extra.isEmpty()){
                player.sendMessage(Practice.getMessage("admin.wand.full-inventory"));
            }else{
                player.sendMessage(Practice.getMessage("admin.wand.success"));
            }
        }else if(sub.equalsIgnoreCase("create")){
            Selection selection = Selection.getSelection(player);

            if(!selection.isValid()){
                player.sendMessage(Practice.getMessage("admin.arena.create.invalid-selection"));
                return;
            }

            String name = args[1];
            ArenaManager manager = Practice.inst().arena_manager;

            if(manager.get(name) != null){
                player.sendMessage(Practice.getMessage("admin.arena.create.arena-exists").replace("%arena%", name));
                return;
            }

            new Arena(name, selection.one.add(0, 1, 0), selection.two.add(0, 1, 0));
            player.sendMessage(Practice.getMessage("admin.arena.create.success").replace("%arena%", name));
        }else if(sub.equalsIgnoreCase("delete")){
            String name = args[1];
            ArenaManager manager = Practice.inst().arena_manager;

            Arena arena = manager.get(name);
            if(arena == null){
                player.sendMessage(Practice.getMessage("admin.arena.delete.arena-doesnt-exist").replace("%arena%", name));
                return;
            }

            manager.remove(arena);
            player.sendMessage(Practice.getMessage("admin.arena.delete.success").replace("%arena%", name));
        }else if(sub.equalsIgnoreCase("list")){
            ArenaManager manager = Practice.inst().arena_manager;

            if(manager.all().size() <= 0){
                player.sendMessage(Practice.getMessage("admin.arena.no-arenas"));
                return;
            }

            player.openInventory(ArenaListGUI.inst().update());
        }else{
            player.sendMessage(C.c("&c" + sub + " is not a recognized sub-command."));
        }
    }

}
