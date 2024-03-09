package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.HashMap;

public class VampireBlessing extends CharmBase {
    public VampireBlessing(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }

    private final String curseName = data.charmStrings.get(5);
    protected void saveConfigValues(){
        configManager.addDefaultConfig(curseName + ".OverrideDefaultEffects", false);
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".1", "health=2");
        configManager.addDefaultConfig(curseName + ".OverrideEffects" + ".2", "damageToHealthPercent=1/4");
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

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player player){
            if (data.hasCharm(player.getUniqueId(),data.charmStrings.get(5))){//Vampire blessing
                if (event.getEntity() instanceof LivingEntity){
                    if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
                        if (!overrideDefault){
                            DefaultLevelEffects(player, event.getDamage());
                        }else{
                            OverrideEffects(player, event.getDamage());
                        }
                    }
                }
            }
        }
    }

    public void DefaultLevelEffects(Player player, double damage){
        int level = data.getCharm(player.getUniqueId(),data.charmStrings.get(5)).getLevel();
        double newHealth = player.getHealth() + ((damage / 100) * level);
        player.setHealth(Math.max(0, Math.min(20, newHealth)));
    }

    public void OverrideEffects(Player player, double damage){
        Data.CharmObj curse = data.getCharm(player.getUniqueId(),curseName);
        if (effectOverrides.containsKey(curse.getLevel())){
            String effect = effectOverrides.get(curse.getLevel());
            String[] strEffectChunks = effect.split(",");
            for (String strEffectChunk : strEffectChunks){
                if (strEffectChunk.contains("health=")) {
                    String[] parts = strEffectChunk.split("=");
                    if (parts.length == 2) {
                        String value = parts[1].trim();
                        try {
                            double newHealth = Double.parseDouble(value);
                            player.setHealth(Math.max(0, Math.min(20, newHealth)));
                        } catch (NumberFormatException ignored){}
                    }
                }
                if (strEffectChunk.contains("damageToHealthPercent=")) {
                    String[] parts = effect.split("=");
                    if (parts.length == 2) {
                        String value = parts[1].trim();
                        String[] numbers = value.split("/");
                        if (numbers.length == 2){
                            try {
                                double numNum = Double.parseDouble(numbers[0]);
                                double numOutOf = Double.parseDouble(numbers[1]);
                                double newHealth = player.getHealth() + ((damage / numOutOf) * numNum);
                                player.setHealth(Math.max(0, Math.min(20, newHealth)));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        }
    }


}
