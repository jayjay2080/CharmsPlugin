package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.HashMap;
import java.util.UUID;

public class ReflectBlessing extends CharmBase {
    public ReflectBlessing(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }
    private final String curseName = data.charmStrings.get(6);


    protected void saveConfigValues() {
        configManager.addDefaultConfig(curseName + ".OverrideDefaultChance", false);
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".1", "chance=1/10");
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".2", "chance=1/5");
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".10", "chance=1/3");
    }
    boolean overrideDefault = false;
    HashMap<Integer, String> effectOverrides = new HashMap<>();
    protected void loadConfigValues(){
        FileConfiguration config = configManager.getConfig();
        overrideDefault = config.getBoolean(curseName + ".OverrideDefaultChance", false);
        if (overrideDefault){
            ConfigurationSection effectSection = config.getConfigurationSection(curseName + ".OverrideChance");
            if (effectSection != null) {
                for (String key : effectSection.getKeys(false)) {
                    String value = effectSection.getString(key, "null");
                    if (!value.equals("null")){
                        try {
                            int intKey = Integer.parseInt(key);
                            effectOverrides.put(intKey, value);
                        } catch (NumberFormatException ignored){}

                    }
                }
            }
        }
    }



    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if (event.getHitEntity() instanceof Player){
            UUID uuid = ((Player)event.getHitEntity()).getUniqueId();
            if (data.hasAnyCharm(uuid)) {
                if (data.hasCharm(uuid, curseName)) {
                    Data.CharmObj curse = data.getCharm(uuid, curseName);
                    if (curse != null) {
                        if (!overrideDefault){
                            double probability = (((double)curse.getLevel())/100); //(x/100)
                            double randVal = random.nextDouble();
                            if (randVal < probability){
                                event.setCancelled(true);
                            }
                        }else{
                            if (effectOverrides.containsKey(curse.getLevel())) {
                                String effect = effectOverrides.get(curse.getLevel());
                                if (effect.contains("chance=")) {
                                    String[] parts = effect.split("=");
                                    if (parts.length == 2) {
                                        String value = parts[1].trim();
                                        String[] numbers = value.split("/");
                                        if (numbers.length == 2){
                                            try {
                                                double numNum = Double.parseDouble(numbers[0]);
                                                double numOutOf = Double.parseDouble(numbers[1]);
                                                double probability = (numNum/numOutOf);
                                                double randVal = random.nextDouble();
                                                if (randVal < probability){
                                                    event.setCancelled(true);
                                                }
                                            } catch (NumberFormatException ignored) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
