package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.data.sql.PlayerDataNoDatabase;
import fr.nocsy.mcpets.utils.BukkitSerialization;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetInventory {

    @Getter
    private static Map<UUID, Map<String, PetInventory>> petInventories = new java.util.concurrent.ConcurrentHashMap<>();

    @Getter
    private Inventory inventory;

    private final Pet pet;

    /**
     * Constructor
     * Inventory can either be forced or created automatically using null
     */
    private PetInventory(Pet pet, @Nullable Inventory premadeInventory, UUID owner) {
        if (pet == null)
            throw new NullPointerException("Pet can not be null.");
        this.pet = pet;
        this.pet.setOwner(owner);

        String title = Language.PET_INVENTORY_TITLE.getMessageFormatted(new FormatArg("%pet%", pet.getIcon().getItemMeta().getDisplayName()));

        this.inventory = new PetInventoryHolder(pet.getInventorySize(), title, PetInventoryHolder.Type.PET_INVENTORY_MENU).getInventory();
        if (premadeInventory != null) {
            if (premadeInventory.getContents().length <= inventory.getContents().length)
                inventory.setContents(premadeInventory.getContents());
            else {
                for(ItemStack it : premadeInventory.getContents()) {
                    if (it != null)
                        inventory.addItem(it);
                }
            }
        }

        final Map<String, PetInventory> builtIn =
                petInventories.computeIfAbsent(pet.getOwner(), k -> new java.util.concurrent.ConcurrentHashMap<>());
        builtIn.put(pet.getId(), this);
    }

    public static void removePlayer(UUID owner) {
        petInventories.remove(owner);
    }

    /**
     * Flush every currently open pet inventory into its owner's PlayerData.
     * Called on disable so items survive a restart even if the GUI was never closed.
     */
    public static void saveAllOpen() {
        for (java.util.Map.Entry<UUID, Map<String, PetInventory>> entry : petInventories.entrySet()) {
            final PlayerData pd = PlayerData.get(entry.getKey());
            if (pd == null) continue;
            for (PetInventory inventory : entry.getValue().values()) {
                pd.setPetInventory(inventory);
            }
        }
    }

    /** Drop all cached pet inventories (plugin disable / reload). */
    public static void clearCache() {
        petInventories.clear();
    }

    public static PetInventory get(UUID owner, String petId) {
        Pet pet = Pet.getFromId(petId);
        if (pet == null)
            return null;
        pet.setOwner(owner);
        return get(pet);
    }

    /**
     * Get the corresponding pet inventory of the said pet
     * null if the pet has no owner
     */
    public static PetInventory get(Pet pet) {
        if (pet.getOwner() == null)
            return null;
        if (pet.getInventorySize() <= 0)
            return null;
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            final PlayerData pd = PlayerData.get(pet.getOwner());
            // Row not read yet: an empty inventory here would be saved over real data.
            if (pd != null && !pd.isLoaded())
                return null;
        }
        Map<String, PetInventory> registeredMap = petInventories.get(pet.getOwner());
        if (registeredMap != null
                && registeredMap.get(pet.getId()) != null
                && registeredMap.get(pet.getId()).getInventory().getSize() == pet.getInventorySize()) {
            return registeredMap.get(pet.getId());
        }

        Inventory inv = null;
        if (registeredMap != null && registeredMap.get(pet.getId()) != null) {
            inv = registeredMap.get(pet.getId()).getInventory();
        }
        if (inv == null) {
            // DB/YAML data is stored serialized; materialize it lazily on the main thread.
            final PlayerData pd = PlayerData.get(pet.getOwner());
            final String serialized = pd != null
                    ? pd.getMapOfRegisteredInventories().get(pet.getId())
                    : null;
            if (serialized != null && !serialized.isBlank()) {
                try {
                    inv = unserializeInventory(serialized);
                } catch (IOException ex) {
                    MCPets.getLog().warning("无法恢复宠物 " + pet.getId() + " 的背包：" + ex.getMessage());
                }
            }
        }
        return new PetInventory(pet, inv, pet.getOwner());
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        PlayerData pd = PlayerData.get(pet.getOwner());
        pd.setPetInventory(this);
    }

    /**
     * Unserialize the pet inventory from the DB
     * associate it to the said owner
     */
    public static PetInventory unserialize(String serialized, @NotNull UUID owner) {
        if (serialized == null) return null;
        String[] data = serialized.split(";", 2);
        if (data.length != 2) {
            MCPets.getLog().warning("已跳过格式错误的宠物背包数据：" + serialized);
            return null;
        }
        String petId = data[0];

        Pet pet = Pet.getFromId(petId);
        if (pet == null)
            return null;

        if (pet.getInventorySize() <= 0)
            return null;

        pet.setOwner(owner);
        String serializedInventory = data[1];
        try {
            Inventory inventory = unserializeInventory(serializedInventory);
            return new PetInventory(pet, inventory, owner);
        }
        catch(IOException ex) {
            return null;
        }
    }

    /**
     * Serialize the pet inventory formatted for the DB
     */
    public String serialize() {
        return serializeInventory();
    }

    /**
     * Open the inventory to the said player
     * and add the tracing metadata
     */
    public void open(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) return;
                p.openInventory(inventory);
                // Set the marker immediately after the view switches: the previous
                // view's InventoryCloseEvent already ran inside openInventory(), and a
                // player can close this GUI on the very next tick. The old 2-tick delay
                // lost the save and could tag a later, unrelated GUI as a pet inventory.
                p.setMetadata("MCPets;petInventory", new FixedMetadataValue(MCPets.getInstance(), pet.getId()));
            }
        }.runTaskLater(MCPets.getInstance(), 2L);
    }

    /**
     * Close the inventory of the said player, removes the metadata
     * and save the inventory in the DB
     */
    public void close(Player p) {
        p.setMetadata("MCPets;petInventory", new FixedMetadataValue(MCPets.getInstance(), null));
        final PlayerData pd = PlayerData.getRegisteredData().get(p.getUniqueId());
        if (pd == null)
            return;
        new BukkitRunnable() {
            @Override
            public void run() {
                // Only this player's data: saveDB() used to rewrite every row on each
                // inventory close. Captured reference also avoids recreating a cache
                // entry after the player has already quit.
                pd.save();
            }
        }.runTaskAsynchronously(MCPets.getInstance());
    }

    /**
     * Get the PetInventory related to the player looking at
     * a certain inventory
     */
    public static PetInventory fromCurrentView(Player p) {
        if (p.hasMetadata("MCPets;petInventory")) {
            if (!p.getMetadata("MCPets;petInventory").isEmpty() &&
                p.getMetadata("MCPets;petInventory").getFirst() != null &&
                p.getMetadata("MCPets;petInventory").getFirst().value() instanceof String)
            {
                String petId = (String)p.getMetadata("MCPets;petInventory").getFirst().value();
                UUID owner = p.getUniqueId();
                Map<String, PetInventory> map = petInventories.get(owner);
                if (map != null) {
                    return map.get(petId);
                }
            }
        }

        return null;
    }

    /**
     * Serialize the inventory only
     */
    private String serializeInventory() {
        return BukkitSerialization.toBase64(this.inventory);
    }

    /**
     * Unserialize the inventory only
     */
    private static Inventory unserializeInventory(String serialized) throws IOException {
        return BukkitSerialization.fromBase64(serialized);
    }

    /**
     * Get the pet ID
     */
    public String getPetId() {
        return pet.getId();
    }
}
