package org.lemonkingdommods.charms.charmClasses;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.lemonkingdommods.charms.CharmBase;
import org.lemonkingdommods.charms.Charms;
import org.lemonkingdommods.charms.ConfigManager;
import org.lemonkingdommods.charms.Data;

import java.util.*;

public class VampireCurse extends CharmBase {
    public VampireCurse(Data data, Charms plugin, ConfigManager configManager) {
        super(data, plugin, configManager);
    }
    private List<ItemStack> foods = new ArrayList<>();
    private Map<UUID, Integer> cooldownMap = new HashMap<>();
    private List<Entity> entities = new ArrayList<>();

    private final String curseName = data.charmStrings.get(2);
    protected void saveConfigValues(){
        configManager.addDefaultConfig(curseName + ".GainedFoodFromAnimal", 5);
        configManager.addDefaultConfig(curseName + ".GainedSaturationFromAnimal", 2);
        configManager.addDefaultConfig(curseName + ".DamageToAnimal", 40);
        configManager.addDefaultConfig(curseName + ".CoolDownSeconds", 5);
        configManager.addDefaultConfig(curseName + ".FoodAmountFromFood",2);
        configManager.addDefaultConfig(curseName + ".SaturationAmountFromFood", 2);
    }
    int foodFromAnimal = 5;
    int saturationFromAnimal = 5;
    int damageToAnimal = 40;
    int cooldown = 5;
    int foodFromFood = 2;
    int saturationFromFood = 2;
    protected void loadConfigValues(){
        FileConfiguration config = configManager.getConfig();
        foodFromAnimal = config.getInt(curseName + ".GainedFoodFromAnimal", 5);
        saturationFromAnimal = config.getInt(curseName + ".GainedSaturationFromAnimal", 2);
        damageToAnimal = config.getInt(curseName + ".DamageToAnimal", 40);
        cooldown = config.getInt(curseName + ".CoolDownSeconds", 5);
        foodFromFood = config.getInt(curseName + ".FoodAmountFromFood",2);
        saturationFromFood = config.getInt(curseName + ".SaturationAmountFromFood", 2);
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (data.hasCharm(player.getUniqueId(),data.charmStrings.get(2))){//Vampire curse
            if (!(event.getRightClicked() instanceof Player)) {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    if (event.getRightClicked() instanceof LivingEntity){
                        if ((event.getRightClicked()) instanceof Animals){
                            if (player.isSneaking()) {
                                if (checkCooldown(player)) {
                                    // Perform your actions here
                                    entities.add(event.getRightClicked());
                                    cooldownMap.put(player.getUniqueId(),cooldown);
                                    ((LivingEntity) event.getRightClicked()).damage(damageToAnimal,player);
                                    player.spawnParticle(Particle.REDSTONE, event.getRightClicked().getLocation(), 50, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.RED,2));
                                    player.setFoodLevel(player.getFoodLevel() + Math.min(20 - player.getFoodLevel(),foodFromAnimal));
                                    player.setSaturation(player.getSaturation() + Math.min(20 - player.getSaturation(),saturationFromAnimal));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 5));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // Check if the event is related to a player
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (data.hasAnyCharm(player.getUniqueId())) {
                if (data.hasCharm(player.getUniqueId(), data.charmStrings.get(2))) {//Vampire curse
                    if (event.getItem() != null) {
                        if (foods.contains(event.getItem())) {
                            foods.remove(event.getItem());
                            int newFoodLevel = event.getEntity().getFoodLevel() + foodFromFood;
                            newFoodLevel = Math.max(0, Math.min(20, newFoodLevel));
                            event.setFoodLevel(newFoodLevel);
                            player.setSaturation(player.getSaturation() + Math.min(20 - player.getSaturation(),saturationFromFood));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        // Check if the consumed item is food
        if (event.getItem().getType().isEdible()) {
            if (data.hasAnyCharm(player.getUniqueId())){
                if (data.hasCharm(player.getUniqueId(),data.charmStrings.get(2))){//Vampire curse
                    foods.add(event.getItem());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (entities.contains(event.getEntity())){
            entities.remove(event.getEntity());
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }


    @Override
    protected void EverySecond(){
        for (UUID playerId : cooldownMap.keySet()) {
            int remainingCooldown = cooldownMap.get(playerId);
            if (remainingCooldown > 0) {
                cooldownMap.put(playerId, remainingCooldown - 1);
            }else{
                cooldownMap.remove(playerId);
            }
        }
    }


    // Check if the player is on cooldown
    private boolean checkCooldown(Player player) {
        if (cooldownMap.containsKey(player.getUniqueId())) {
            int remainingCooldown = cooldownMap.get(player.getUniqueId());
            return remainingCooldown <= 0;
        }
        return true; // No cooldown information, allow the interaction
    }

}
