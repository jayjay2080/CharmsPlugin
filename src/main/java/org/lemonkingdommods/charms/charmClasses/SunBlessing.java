package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.HashMap;

public class SunBlessing extends CharmBase {

    public SunBlessing(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }

    private final String curseName = data.charmStrings.get(8);
    protected void saveConfigValues(){
        configManager.addDefaultConfig(curseName + ".OverrideDefaultEffects", false);
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".1", "effect=ABSORPTION-0");
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".2", "effect=ABSORPTION-1");
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".3", "effect=ABSORPTION-1,effect=SPEED-0");
    }
    boolean overrideDefault = false;
    HashMap<Integer, String> effectOverrides = new HashMap<>();
    protected void loadConfigValues(){
        FileConfiguration config = configManager.getConfig();
        overrideDefault = config.getBoolean(curseName + ".OverrideDefaultEffects", false);
        if (overrideDefault){
            ConfigurationSection effectSection = config.getConfigurationSection(curseName + ".OverrideEffects");
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

    @Override
    public void PlayerCharmLoop(Player player){
        if (data.hasCharm(player.getUniqueId(), curseName)) {//undead curse
            Location loc = player.getLocation();
            Block block = loc.getBlock();
            World world = player.getWorld();
            long time = world.getTime();
            if (block.getLightFromSky() == 15) {
                if ((time >= 23000 && time <= 24000) || (time >= 0 && time < 12400)) {
                    if (world.isClearWeather()) {
                        if (!overrideDefault) {
                            DefaultLevelEffects(player);
                        }else{
                            OverrideEffects(player);
                        }
                    }
                }
            }
        }
    }






    public void OverrideEffects(Player player){
        Data.CharmObj curse = data.getCharm(player.getUniqueId(),curseName);
        if (effectOverrides.containsKey(curse.getLevel())){
            String effect = effectOverrides.get(curse.getLevel());
            String[] strEffectChunks = effect.split(",");
            for (String strEffectChunk : strEffectChunks){
                if (strEffectChunk.contains("effect=")) {
                    String[] wholestr = strEffectChunk.split("=");
                    if (wholestr.length == 2){
                        String[] parts = wholestr[1].split(",");
                        for (String strEffect : parts){
                            String[] strEffectParts = strEffect.split("-");
                            if (strEffectParts.length == 2){
                                PotionEffectType potionEffect = null;
                                try {
                                    potionEffect = PotionEffectType.getByName(strEffectParts[0].trim());
                                    try {
                                        int intPower = Integer.parseInt(strEffectParts[1].trim());
                                        assert potionEffect != null;
                                        player.addPotionEffect(potionEffect.createEffect(34,intPower));
                                    } catch (NumberFormatException ignored){}
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }
        }
    }


    public void DefaultLevelEffects(Player player){
        Data.CharmObj curse = data.getCharm(player.getUniqueId(), curseName);
        player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(34, 0));
    }
}
