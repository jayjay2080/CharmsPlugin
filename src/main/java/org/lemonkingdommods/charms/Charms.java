package org.lemonkingdommods.charms;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;


public final class Charms extends JavaPlugin {
    private Data data;
    private ConfigManager configManager;
    private DataLoadingAndSaving dataLoadingAndSaving;

    @Override
    public void onLoad() {
        // Plugin Load logic
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandAPI.onEnable();
        new ConfigManager(this);
        data = new Data();
        configManager = new ConfigManager(this);
        dataLoadingAndSaving = new DataLoadingAndSaving(data);

        dataLoadingAndSaving.loadDataFromYaml();
        dataLoadingAndSaving.createDataFolder();

        new CharmRegister(data, this, configManager);

        CharmCommand command = new CharmCommand(data);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CommandAPI.onDisable();

        dataLoadingAndSaving.saveToYaml();
    }
}
