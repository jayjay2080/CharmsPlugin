package org.lemonkingdommods.charms;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    Charms plugin;
    FileConfiguration config;
    public ConfigManager(Charms plugin){
        this.plugin = plugin;
        plugin.saveConfig();
        this.config = plugin.getConfig();
    }

    public void addDefaultConfig(String path, Object value){
        config.addDefault(path, value);
        config.options().copyDefaults(true);
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void saveConfig(){
        plugin.saveConfig();
    }


}
