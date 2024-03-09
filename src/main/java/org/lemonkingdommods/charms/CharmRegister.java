package org.lemonkingdommods.charms;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.lemonkingdommods.charms.charmClasses.*;

import java.util.ArrayList;
import java.util.List;

public class CharmRegister {
    public CharmRegister(Data data, Charms plugin, ConfigManager configManager){
        List<CharmBase> charms = new ArrayList<>();
        charms.add(new LagCurse(data, plugin, configManager));
        charms.add(new UndeadCurse(data, plugin, configManager));
        charms.add(new VampireCurse(data, plugin, configManager));
        charms.add(new DryCurse(data, plugin, configManager));
        charms.add(new ProvocationCurse(data, plugin, configManager));
        charms.add(new VampireBlessing(data, plugin, configManager));
        charms.add(new ReflectBlessing(data, plugin, configManager));
        charms.add(new StrongSoulBlessing(data, plugin, configManager));
        charms.add(new SunBlessing(data, plugin, configManager));

        charms.forEach(CharmBase::register);
        charms.forEach(CharmBase::saveConfigValues);
        configManager.saveConfig();
        charms.forEach(CharmBase::loadConfigValues);

        new BukkitRunnable(){
            @Override
            public void run(){
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                    if (data.hasAnyCharm(onlinePlayer.getUniqueId())){
                        charms.forEach(charmBase -> {charmBase.PlayerCharmLoop(onlinePlayer);});
                    }
                }
                // non charm stuff here
                charms.forEach(CharmBase::EverySecond);
            }
        }.runTaskTimer(plugin,0,20);
    }

}
