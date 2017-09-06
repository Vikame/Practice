package org.systic.practice.kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.systic.citadel.util.C;
import org.systic.practice.Practice;
import org.systic.practice.ladders.Ladder;
import org.systic.practice.ladders.LadderManager;
import org.systic.practice.location.LocationManager;
import org.systic.practice.util.Config;
import org.systic.practice.util.Inventories;

import java.util.*;

public class KitManager implements Listener{

    private final Config config = Practice.inst().kits;
    private final LadderManager ladder_manager;
    private final LocationManager location_manager;
    public final Map<UUID, Map<Ladder, List<Kit>>> loaded = new HashMap<>();
    public final Map<UUID, Ladder> editing = new HashMap<>();

    public KitManager(){
        Bukkit.getPluginManager().registerEvents(this, Practice.inst());
        ladder_manager = Practice.inst().ladder_manager;
        location_manager = Practice.inst().location_manager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();

        if(!isEditing(p) || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block b = e.getClickedBlock();
        if(b.getType() == Material.CHEST){
            e.setCancelled(true);

            Ladder ladder = getEditing(p);

            Inventory inventory = Bukkit.createInventory(null, 54, C.c("&7"));
            inventory.setContents(ladder.editor);

            p.openInventory(inventory);
        }else if(b.getState() instanceof Sign){
            Sign sign = (Sign)b.getState();

            if(sign.getLine(0) != null){
                String line = sign.getLine(0);

                if(line.equalsIgnoreCase("Leave editor")) {
                    stopEditing(p);

                    if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
                        p.teleport(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
                    }

                    new BukkitRunnable() {
                        public void run() {
                            Inventories.giveDefault(p);
                        }
                    }.runTaskLaterAsynchronously(Practice.inst(), 1);
                }else if(line.equalsIgnoreCase("Save kit")){
                    addKit(p, getEditing(p), Kit.fromPlayer(p));

                    stopEditing(p);

                    if (location_manager.contains(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn")) {
                        p.teleport(location_manager.get(p.getName().equalsIgnoreCase("IDrainq") ? "idrainq" : "spawn"));
                    }

                    new BukkitRunnable() {
                        public void run() {
                            Inventories.giveDefault(p);
                        }
                    }.runTaskLaterAsynchronously(Practice.inst(), 1);
                }else if(line.equalsIgnoreCase("Clear")){
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(new ItemStack[4]);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player)e.getWhoClicked();
        if(!isEditing(p) || e.getCurrentItem() == null || e.getClickedInventory() == null) return;

        Inventory inv = e.getClickedInventory();

        if(!inv.getName().contains(ChatColor.COLOR_CHAR + "")) return;

        if(p.getOpenInventory().getTopInventory().equals(inv)){
            new BukkitRunnable(){
                public void run(){
                    inv.setContents(getEditing(p).editor);
                }
            }.runTaskLaterAsynchronously(Practice.inst(), 1);
        }
    }

    public List<Kit> getKits(Player player, Ladder ladder){
        if(loaded.containsKey(player.getUniqueId())){
            Map<Ladder, List<Kit>> kits = loaded.get(player.getUniqueId());

            if(kits.containsKey(ladder)) return kits.get(ladder);
        }

        return new ArrayList<>();
    }

    public void addKit(Player player, Ladder ladder, Kit kit){
        if(!loaded.containsKey(player.getUniqueId())){
            Map<Ladder, List<Kit>> map = new HashMap<>();

            List<Kit> kits = new ArrayList<>();
            kits.add(kit);

            map.put(ladder, kits);

            loaded.put(player.getUniqueId(), map);
        }else{
            Map<Ladder, List<Kit>> map = loaded.get(player.getUniqueId());
            if(map.containsKey(ladder)){
                map.get(ladder).add(kit);
            }else{
                List<Kit> kits = new ArrayList<>();
                kits.add(kit);

                map.put(ladder, kits);
            }
        }
    }

    public void delete(Player player, Ladder ladder, int kit){
        YamlConfiguration config = this.config.config;

        config.set(player.getUniqueId().toString() + "." + ladder.name + "." + kit, null);
    }

    public void save(){
        YamlConfiguration config = this.config.config;

        for(Map.Entry<UUID, Map<Ladder, List<Kit>>> entry : loaded.entrySet()){
            UUID uuid = entry.getKey();

            if(uuid == null) continue; // Null pointer? Why?

            for(Map.Entry<Ladder, List<Kit>> ent : entry.getValue().entrySet()){
                Ladder ladder = ent.getKey();
                List<Kit> kits = ent.getValue();

                if(ladder == null || kits == null) continue; // Another null pointer? Why??????

                for(int k = 0; k < kits.size(); k++){
                    Kit kit = kits.get(k);

                    String path = uuid.toString() + "." + ladder.name + "." + k + ".";

                    for(int i = 0; i < kit.contents.length; i++){
                        if(kit.contents[i] == null) continue;
                        config.set(path + "items." + i, kit.contents[i]);
                    }
                    for(int i = 0; i < kit.armor.length; i++){
                        if(kit.armor[i] == null) continue;
                        config.set(path + "armor." + i, kit.armor[i]);
                    }
                }
            }
        }

        this.config.save();
    }

    public void load(){
        YamlConfiguration config = this.config.config;

        for(String s : config.getKeys(false)){
            ConfigurationSection sec = config.getConfigurationSection(s);

            HashMap<Ladder, List<Kit>> kits = new HashMap<>();

            UUID uuid = UUID.fromString(s);

            for(String str : sec.getKeys(false)){
                ConfigurationSection section = sec.getConfigurationSection(str);

                Ladder ladder = ladder_manager.get(str);

                for(String string : section.getKeys(false)) {
                    ConfigurationSection kit_section = section.getConfigurationSection(string);
                    Kit kit = new Kit();

                    for (int i = 0; i < 36; i++) {
                        if(kit_section.contains("items." + i)) {
                            kit.contents[i] = kit_section.getItemStack("items." + i);
                        }
                    }
                    for (int i = 0; i < 4; i++) {
                        if(kit_section.contains("armor." + i)) {
                            kit.armor[i] = kit_section.getItemStack("armor." + i);
                        }
                    }

                    if (!kits.containsKey(ladder)) {
                        List<Kit> list = new ArrayList<>();
                        list.add(kit);

                        kits.put(ladder, list);
                    } else kits.get(ladder).add(kit);
                }
            }

            if(!loaded.containsKey(uuid)) loaded.put(uuid, kits);
            else loaded.get(uuid).putAll(kits);
        }
    }

    public void startEditing(Player player, Ladder ladder){
        editing.put(player.getUniqueId(), ladder);
    }

    public void stopEditing(Player player){
        editing.remove(player.getUniqueId());
    }

    public boolean isEditing(Player player){
        return editing.containsKey(player.getUniqueId());
    }

    public Ladder getEditing(Player player){
        return editing.get(player.getUniqueId());
    }

}
