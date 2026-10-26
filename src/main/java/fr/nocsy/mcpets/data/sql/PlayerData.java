package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    @Getter
    private static final ConcurrentHashMap<UUID, PlayerData> registeredData = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private ConcurrentHashMap<String, String> mapOfRegisteredNames = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private ConcurrentHashMap<String, String> mapOfRegisteredInventories = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private ConcurrentHashMap<String, String> mapOfRegisteredPetStats = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private String lastActivePet;

    @Setter
    @Getter
    private UUID uuid;

    /**
     * False while a MySQL row has not been read yet. Prevents writing an empty
     * snapshot over real data when a player joins during DB initialization.
     */
    @Setter
    @Getter
    private boolean loaded = false;

    private static final String ACTIVE_PET_SKIN_DELIMITER = ":";

    private PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    private PlayerData() {
    }

    public static boolean isRegistered(UUID player) {
        return registeredData.containsKey(player);
    }

    public static PlayerData get(UUID owner) {
        return registeredData.computeIfAbsent(owner, PlayerData::loadOrCreate);
    }

    private static PlayerData loadOrCreate(final UUID owner) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) {
            final PlayerData data = new PlayerData();
            data.setUuid(owner);
            final PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(owner);
            data.setMapOfRegisteredNames(pdn.mapOfRegisteredNames);
            data.setMapOfRegisteredInventories(pdn.mapOfRegisteredInventories);
            data.setLastActivePet(pdn.getLastActivePet());
            data.setLoaded(true);
            return data;
        }

        return new PlayerData(owner);
    }

    public static PlayerData getEmpty(UUID owner) {
        PlayerData data = new PlayerData();
        data.setUuid(owner);
        return data;
    }

    public static String encodeActivePet(String petId, String skinPathId) {
        if (petId == null)
            petId = "";
        return (skinPathId == null || skinPathId.isEmpty())
                ? petId
                : petId + ACTIVE_PET_SKIN_DELIMITER + skinPathId;
    }

    public static String decodeActivePetId(String value) {
        if (value == null)
            return null;
        int idx = value.indexOf(ACTIVE_PET_SKIN_DELIMITER);
        return idx < 0 ? value : value.substring(0, idx);
    }

    public static String decodeActiveSkinId(String value) {
        if (value == null)
            return null;
        int idx = value.indexOf(ACTIVE_PET_SKIN_DELIMITER);
        return idx < 0 ? null : value.substring(idx + ACTIVE_PET_SKIN_DELIMITER.length());
    }

    public static void saveDB() {
        if(GlobalConfig.getInstance().isDatabaseSupport())
            Databases.saveData();
        else {
            PlayerDataNoDatabase.getCacheMap().values().forEach(PlayerDataNoDatabase::save);
        }
    }

    public static void reloadAllPlayerData() {
        Databases.loadData();
    }

    /**
     * Register the Pet inventory for future save
     */
    public void setPetInventory(PetInventory petInventory) {
        mapOfRegisteredInventories.put(petInventory.getPetId(), petInventory.serialize());
    }

    public void save() {
        if (GlobalConfig.getInstance().isDatabaseSupport())
            Databases.savePlayerData(uuid);
        else {
            PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(uuid);
            pdn.setMapOfRegisteredNames(mapOfRegisteredNames);
            pdn.setLastActivePet(lastActivePet);

            // Keep serialized inventories that were never materialized (lazy DB load),
            // then overlay the live inventories currently cached in memory.
            final var inventories = PetInventory.getPetInventories().get(this.getUuid());
            if (inventories != null) {
                for (var entry : inventories.entrySet()) {
                    mapOfRegisteredInventories.put(entry.getKey(), entry.getValue().serialize());
                }
            }

            pdn.setMapOfRegisteredInventories(mapOfRegisteredInventories);
            pdn.save();
        }
    }

    public static void initAll() {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            Databases.loadData();
        }
        else {
            PlayerDataNoDatabase.getCacheMap().values().forEach(PlayerDataNoDatabase::reload);
        }
    }

    public static void remove(UUID uuid) {
        registeredData.remove(uuid);
        PlayerDataNoDatabase.getCacheMap().remove(uuid);
    }

    /** Drop every cached player data entry (plugin disable / reload). */
    public static void clearCache() {
        registeredData.clear();
        PlayerDataNoDatabase.getCacheMap().clear();
    }

    public static void reloadAll(UUID uuid) {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            Databases.loadData(uuid);
        }
        else {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }

    public void reload() {
        if (!Databases.loadData()) {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }
}
