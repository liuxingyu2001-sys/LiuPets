package fr.nocsy.mcpets.compat;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.livingpets.PetStats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPICompat extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        String defaultOutput = "";
        
        if (player == null) return defaultOutput;
        
        Pet pet = MCPetsAPI.getActivePet(player.getUniqueId());
        if (pet == null) return defaultOutput;

        PetStats stats = pet.getPetStats();

        final boolean hasEntity = pet.getActiveMob() != null && pet.getActiveMob().getEntity() != null;

        return switch(identifier.toUpperCase()){
            case "PET_NAME" -> pet.getCurrentName() != null ? pet.getCurrentName() : "";
            case "PET_ID" -> pet.getId();
            case "PET_DISTANCE" -> String.valueOf(pet.getDistance());
            case "PET_HEALTH" -> String.valueOf(Math.round(stats != null
                    ? stats.getCurrentHealth()
                    : (hasEntity ? pet.getActiveMob().getEntity().getHealth() : 0)
            ));
            case "PET_MAX_HEALTH" -> String.valueOf(Math.round(stats != null && stats.getCurrentLevel() != null
                    ? stats.getCurrentLevel().getMaxHealth()
                    : (hasEntity ? pet.getActiveMob().getEntity().getMaxHealth() : 0)
            ));
            case "PET_ICON_NAME" -> pet.getIcon() != null && pet.getIcon().getItemMeta() != null
                    ? pet.getIcon().getItemMeta().getDisplayName()
                    : "No icon name found.";
            case "PET_LEVEL_NAME" -> stats != null && stats.getCurrentLevel() != null
                    ? stats.getCurrentLevel().getLevelName()
                    : "Level not found: Pet is not a living pet.";
            case "PET_LEVEL_INDEX" -> stats != null && stats.getCurrentLevel() != null
                    ? String.valueOf(stats.getCurrentLevel().getLevelId())
                    : "Level not found: Pet is not a living pet.";
            case "PET_EXP" -> stats != null
                    ? String.valueOf(Math.round(stats.getExperience()))
                    : "Exp not found: Pet is not a living pet.";
            case "PET_OWNER_NAME" -> {
                if (pet.getOwner() == null) yield "";
                final Player owner = Bukkit.getPlayer(pet.getOwner());
                yield owner != null ? owner.getName() : "";
            }
            case "PET_OWNER_UUID" -> pet.getOwner() != null ? pet.getOwner().toString() : "";
            case "PET_POWER" -> stats != null
                    ? String.valueOf(Math.round(stats.getPower()))
                    : "Power not found: Pet is not a living pet.";
            case "PET_DAMAGE_MODIFIER" -> stats != null
                    ? String.valueOf(Math.round(stats.getDamageModifier()))
                    : "Damage modifier not found: Pet is not a living pet.";
            case "PET_RESISTANCE_MODIFIER" -> stats != null
                    ? String.valueOf(Math.round(stats.getResistanceModifier()))
                    : "Resistance modifier not found: Pet is not a living pet.";
            default -> defaultOutput;
        };
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "mcpets";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "MCPets";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

}
