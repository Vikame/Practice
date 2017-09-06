package org.systic.practice.gui.page;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PageGUI {

    public String title;
    public List<Page> pages;

    public PageGUI(String title){
        this.title = title;
        this.pages = new ArrayList<>();
    }

    public final Page getPage(int index){
        if(pages.size() <= index) return null;

        return pages.get(index);
    }

    public abstract void onClick(Player player, ItemStack item, ClickType type);

}
