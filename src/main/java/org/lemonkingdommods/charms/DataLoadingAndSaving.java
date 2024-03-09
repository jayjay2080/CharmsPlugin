package org.lemonkingdommods.charms;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataLoadingAndSaving {

    private Data data;
    public DataLoadingAndSaving(Data data) {
        this.data = data;
    }

    //SAVE AND LOAD STUFF
    public Map<UUID, List<Map<String, Object>>> convertDataStructure() {
        Map<UUID, List<Map<String, Object>>> convertedData = new HashMap<>();


        for (Map.Entry<UUID, Map<String, Data.CharmObj>> entry : data.charms.entrySet()) {
            UUID uuid = entry.getKey();
            Map<String, Data.CharmObj> charmObjs = entry.getValue();
            List<Map<String, Object>> convertedCharmObjs = new ArrayList<>();

            for (Map.Entry<String, Data.CharmObj> charmEntry : charmObjs.entrySet()) {
                String charmName = charmEntry.getKey();
                Data.CharmObj charmObj = charmEntry.getValue();

                Map<String, Object> convertedCharm = new HashMap<>();
                convertedCharm.put("charmName", charmName);
                convertedCharm.put("level", charmObj.getLevel());
                convertedCharm.put("temp", charmObj.isTemp());
                convertedCharm.put("extra", charmObj.getExtra()); // Assuming extra is Serializable

                convertedCharmObjs.add(convertedCharm);
            }

            convertedData.put(uuid, convertedCharmObjs);
        }

        return convertedData;
    }

    public void loadDataFromConvertedStructure(Map<UUID, List<Map<String, Object>>> convertedData) {
        for (Map.Entry<UUID, List<Map<String, Object>>> entry : convertedData.entrySet()) {
            UUID uuid = entry.getKey();
            List<Map<String, Object>> charmObjs = entry.getValue();
            Map<String, Data.CharmObj> originalCharmObjs = new HashMap<>();

            for (Map<String, Object> charmObj : charmObjs) {
                String charmName = (String) charmObj.get("charmName");
                int level = (int) charmObj.get("level");
                boolean temp = (boolean) charmObj.get("temp");
                int extra = -1;
                if (charmObj.get("extra") != null) {
                    extra = (int) charmObj.get("extra");
                }

                originalCharmObjs.put(charmName, new Data.CharmObj(charmName, level, temp, extra));
            }

            data.charms.put(uuid, originalCharmObjs);
        }
    }


    public void createDataFolder() {
        File folder = new File("plugins/Charms");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }


    public void saveToYaml() {
        Map<UUID, List<Map<String, Object>>> convertedStructure = convertDataStructure();
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, List<Map<String, Object>>> entry : convertedStructure.entrySet()) {
            UUID id = entry.getKey();
            List<Map<String, Object>> charms = entry.getValue();
            for (int i = 0; i < charms.size(); i++) {
                Map<String, Object> charm = charms.get(i);
                String path = "charms." + id.toString() + "." + i;
                config.set(path + ".charmName", charm.get("charmName"));
                config.set(path + ".level", charm.get("level"));
                config.set(path + ".temp", charm.get("temp"));
                // Assuming extra is Serializable, handle it accordingly
                config.set(path + ".extra", charm.get("extra"));
            }
        }

        createDataFolder();
        File dataFile = new File("plugins/Charms/charms.yml"); // Modify the path as per your plugin's structure
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDataFromYaml() {
        File dataFile = new File("plugins/Charms/charms.yml");

        if (dataFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

            Map<UUID, List<Map<String, Object>>> convertedData = new HashMap<>();
            if (config.getConfigurationSection("charms") == null){return;}
            for (String uuidString : config.getConfigurationSection("charms").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                List<Map<String, Object>> charmObjs = new ArrayList<>();

                for (String key : config.getConfigurationSection("charms." + uuidString).getKeys(false)) {
                    Map<String, Object> charmData = new HashMap<>();
                    charmData.put("charmName", config.getString("charms." + uuidString + "." + key + ".charmName"));
                    charmData.put("level", config.getInt("charms." + uuidString + "." + key + ".level"));
                    charmData.put("temp", config.getBoolean("charms." + uuidString + "." + key + ".temp"));

                    charmData.put("extra", config.getInt("charms." + uuidString + "." + key + ".extra"));
                    charmObjs.add(charmData);
                }

                convertedData.put(uuid, charmObjs);
            }

            loadDataFromConvertedStructure(convertedData);
        }
    }


}
