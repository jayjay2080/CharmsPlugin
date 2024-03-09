package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.HashMap;
import java.util.UUID;

public class LagCurse extends CharmBase {
    public LagCurse(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }

    private final String curseName = data.charmStrings.get(0);
    protected void saveConfigValues(){
        configManager.addDefaultConfig(curseName + ".OverrideDefaultChance", false);
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".1", "chance=5/100");
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".2", "chance=10/100");
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".10", "chance=1/4");
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
    public void onBlockBreak(BlockBreakEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        if (data.hasAnyCharm(uuid)){
            if (data.hasCharm(uuid,curseName)){//Lag curse
                Data.CharmObj curse = data.getCharm(uuid, curseName);//Lag curse
                if (curse != null){
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        if (data.hasAnyCharm(uuid)){
            if (data.hasCharm(uuid,curseName)){//Lag curse
                Data.CharmObj curse = data.getCharm(uuid, curseName);//Lag curse
                if (curse != null){
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
                                            double intNum = Double.parseDouble(numbers[0]);
                                            double intOutOf = Double.parseDouble(numbers[1]);
                                            double probability = (intNum/intOutOf);
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
