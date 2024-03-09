package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DryCurse extends CharmBase {
    public DryCurse(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }

    private final String curseName = data.charmStrings.get(3);
    protected void saveConfigValues(){
        configManager.addDefaultConfig(curseName + ".OverrideDefaultEffects", false);
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".1", "effect=WEAKNESS-0");
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".2", "effect=SLOW-0,effect=WEAKNESS-1");
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".3", "damage=1");
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".10", "damage=5,effect=SLOW-1");
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


    private final List<Biome> BiomesWithoutRain = Arrays.asList(
            Biome.DESERT,
            Biome.SAVANNA,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.WINDSWEPT_SAVANNA,
            Biome.SNOWY_TAIGA,
            Biome.SNOWY_BEACH,
            Biome.SNOWY_PLAINS,
            Biome.SNOWY_SLOPES,
            Biome.ICE_SPIKES,
            Biome.JAGGED_PEAKS,
            Biome.FROZEN_PEAKS,
            Biome.FROZEN_OCEAN,
            Biome.FROZEN_RIVER,
            Biome.DEEP_FROZEN_OCEAN
    );

    @Override
    public void PlayerCharmLoop(Player player){
        if (data.hasCharm(player.getUniqueId(),curseName)) {//dry curse
            Location loc = player.getLocation();
            Block block = loc.getBlock();
            World world = player.getWorld();
            if (block.getType().equals(Material.WATER)||((block.getLightFromSky() == 15)&&(world.isThundering() || world.hasStorm()))) {
                if (!BiomesWithoutRain.contains(block.getBiome())||block.getType().equals(Material.WATER)){
                    if (!overrideDefault) {
                        DefaultLevelEffects(player);
                    }else{
                        OverrideEffects(player);
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
                if (strEffectChunk.contains("damage=")) {
                    String[] parts = strEffectChunk.split("=");
                    if (parts.length == 2) {
                        String value = parts[1].trim();
                        try {
                            double damage = Double.parseDouble(value);
                            player.damage(damage);
                        } catch (NumberFormatException ignored){}
                    }
                }
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
        Data.CharmObj curse = data.getCharm(player.getUniqueId(),curseName);
        if (curse.getLevel() >=1 && curse.getLevel() <= 9){
            player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(34,0));
        }
        if (curse.getLevel() >=2 && curse.getLevel() <= 9){
            player.addPotionEffect(PotionEffectType.SLOW.createEffect(34,0));
        }
        if (curse.getLevel() >=3 && curse.getLevel() <= 9){
            player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(34,0));
        }
        if (curse.getLevel() >= 10 ){ //&& curse.getLevel() <= 20
            player.damage(curse.getLevel()-9);
        }
    }



}
