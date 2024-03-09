package org.lemonkingdommods.charms;


import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public abstract class CharmBase implements Listener {
    protected final Data data;
    protected final Charms plugin;
    protected final ConfigManager configManager;

    protected Random random = new Random();
    public CharmBase(Data data, Charms plugin, ConfigManager configManager){
        this.data = data;
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected void EverySecond(){}
    protected void PlayerCharmLoop(Player player){}

    protected abstract void saveConfigValues();
    protected abstract void loadConfigValues();
}
