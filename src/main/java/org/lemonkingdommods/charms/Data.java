package org.lemonkingdommods.charms;

import java.io.Serializable;
import java.util.*;

public class Data {



    public static class CharmObj implements Serializable {
        private String charmName;
        private int level;
        private boolean temp;
        private int extra;
        public CharmObj(String name, int level, boolean temp){
            this.charmName = name;
            this.level = level;
            this.temp = temp;
            this.extra = -1;
        }
        public CharmObj(String name, int level, boolean temp, int extra){
            this.charmName = name;
            this.level = level;
            this.temp = temp;
            this.extra = extra;
        }
        public String getCharmName() {
            return charmName;
        }

        public int getLevel() {
            return level;
        }

        public boolean isTemp() {
            return temp;
        }

        public int getExtra() {
            return extra;
        }
        public void setExtra(int newExtra){extra = newExtra;}
    }


    public Map<UUID, Map<String, CharmObj>> charms = new HashMap<>();

    public List<String> charmStrings = List.of(
            "LagCurse", // lag
            "UndeadCurse", // sunlight weakens/burns
            "VampireCurse", //need blood for food
            "DryCurse", // cant touch water
            "ProvocationCurse", // anger enderman/bees/dogs/polarBears/llama randomly
            "VampireBlessing",
            "ReflectBlessing",
            "StrongSoulBlessing",
            "SunBlessing"
    );


    public void TestFunction(UUID uuid){
        charms.put(uuid,convertToHashMap(charmStrings.get(0),new CharmObj(charmStrings.get(0),2,false)));
    }
    public HashMap<String, CharmObj> convertToHashMap(String charmName, CharmObj charmObj) {
        HashMap<String, CharmObj> hashMap = new HashMap<>();
        hashMap.put(charmName, charmObj);
        return hashMap;
    }
    public void addCharm(UUID uuid, String charmName, Integer level, Boolean temp) {
        if (!charmStrings.contains(charmName)) {
            return;
        }

        Map<String, CharmObj> playerCharms = charms.getOrDefault(uuid, new HashMap<>());

        if (playerCharms.containsKey(charmName)) {
            CharmObj charmTemp = playerCharms.get(charmName);
            if (charmTemp.isTemp() == temp) {
                playerCharms.remove(charmName);
            }
        }

        playerCharms.put(charmName, new CharmObj(charmName, level, temp));
        charms.put(uuid, playerCharms);
    }

    public void addCharm(UUID uuid, String charmName, Integer level, Boolean temp, Integer extra) {
        if (!charmStrings.contains(charmName)) {
            return;
        }

        Map<String, CharmObj> playerCharms = charms.getOrDefault(uuid, new HashMap<>());

        if (playerCharms.containsKey(charmName)) {
            CharmObj charmTemp = playerCharms.get(charmName);
            if (charmTemp.isTemp() == temp && Objects.equals(charmTemp.getExtra(), extra)) {
                playerCharms.remove(charmName);
            }
        }

        playerCharms.put(charmName, new CharmObj(charmName, level, temp, extra));
        charms.put(uuid, playerCharms);
    }
    public void removeCharm(UUID uuid, String charmName){
        charms.get(uuid).remove(charmName);
    }

    //GET

    public boolean hasAnyCharm(UUID uuid){
        return charms.containsKey(uuid);
    }
    public boolean hasCharm(UUID uuid, String charmName){
        if (hasAnyCharm(uuid)){
            return charms.get(uuid).containsKey(charmName);
        }
        return false;
    }
    public CharmObj getCharm(UUID uuid, String charmName){
        if (hasAnyCharm(uuid)){
            if (charms.get(uuid).containsKey(charmName)){
                return charms.get(uuid).get(charmName);
            }
        }
        return null;
    }
    public Map<String, CharmObj> getAllCharms(UUID uuid){
        if (hasAnyCharm(uuid)){
            return charms.get(uuid);
        }
        return null;
    }
}
