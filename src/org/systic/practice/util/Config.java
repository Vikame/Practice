package org.systic.practice.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {

    public File file;
    public YamlConfiguration config;

    public Config(JavaPlugin plugin, String name){
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        file = new File(plugin.getDataFolder(), name);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
