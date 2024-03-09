package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.HashMap;

public class ProvocationCurse extends CharmBase {
    public ProvocationCurse(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }

    private final String curseName = data.charmStrings.get(4);
    protected void saveConfigValues(){
        configManager.addDefaultConfig(curseName + ".OverrideDefaultChance", false);
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".1", "chance=5/100");
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".2", "chance=10/100");
        configManager.addDefaultConfig(curseName + ".OverrideChance" + ".10", "chance=1/4");
        configManager.addDefaultConfig(curseName + ".EntitySearchSize", "x=10,y=5,z=10");
    }
    boolean overrideDefault = false;
    int searchX = 10;
    int searchY = 5;
    int searchZ = 10;
    HashMap<Integer, String> effectOverrides = new HashMap<>();
    protected void loadConfigValues(){
        FileConfiguration config = configManager.getConfig();
        overrideDefault = config.getBoolean(curseName + ".OverrideDefaultChance", false);
        String strSearchSize = config.getString(curseName + ".EntitySearchSize", "x=10,y=5,z=10");
        String[] xyzStrings = strSearchSize.split(",");
        if (xyzStrings.length == 3){
            for (String strParts : xyzStrings){
                String[] strPart = strParts.split("=");
                if (strPart.length == 2){
                    try {
                        int intCoord = Integer.parseInt(strPart[1].trim());
                        if (strPart[0].equals("x")){
                            searchX = intCoord;
                        }
                        if (strPart[0].equals("y")){
                            searchY = intCoord;
                        }
                        if (strPart[0].equals("z")){
                            searchZ = intCoord;
                        }
                    } catch (NumberFormatException ignored){}
                }
            }
        }
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

    @Override
    public void PlayerCharmLoop(Player player){
        if (data.hasCharm(player.getUniqueId(),curseName)) {//ProvocationCurse
            for (Entity en : player.getNearbyEntities(searchX,searchY,searchZ)){
                if (!overrideDefault){
                    DefaultLevelEffects(player, en);
                }else{
                    OverrideLevelEffects(player, en);
                }
            }
        }
    }


    public void DefaultLevelEffects(Player player, Entity entity){
        Data.CharmObj curse = data.getCharm(player.getUniqueId(), curseName);
        double probability = (((double)curse.getLevel())/200); //(x/200)
        double randVal = random.nextDouble();
        if (randVal < probability) {
            AngerEntity(player, entity);
        }
    }

    public void OverrideLevelEffects(Player player, Entity entity){
        Data.CharmObj curse = data.getCharm(player.getUniqueId(), curseName);
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
                                AngerEntity(player, entity);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
    }

    public void AngerEntity(Player player, Entity en){
        if (en.getType() == EntityType.ENDERMAN) {
            ((Enderman) en).setTarget(player);
        }
        if (en.getType() == EntityType.LLAMA) {
            ((Llama) en).setTarget(player);
        }
        if (en.getType() == EntityType.POLAR_BEAR) {
            ((PolarBear) en).setTarget(player);
        }
        if (en.getType() == EntityType.BEE) {
            ((Bee) en).setTarget(player);
        }
        if (en.getType() == EntityType.WOLF) {
            ((Wolf) en).setTarget(player);
        }
        if (en.getType() == EntityType.DOLPHIN) {
            ((Dolphin) en).setTarget(player);
        }
        if (en.getType() == EntityType.TRADER_LLAMA) {
            ((TraderLlama) en).setTarget(player);
        }
        if (en.getType() == EntityType.PANDA) {
            ((Panda) en).setTarget(player);
        }
        if (en.getType() == EntityType.SPIDER) {
            ((Spider) en).setTarget(player);
        }
    }

}
