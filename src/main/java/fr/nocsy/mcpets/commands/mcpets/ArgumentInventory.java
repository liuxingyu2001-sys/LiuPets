package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.sql.Databases;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArgumentInventory extends AArgument {

    public ArgumentInventory(CommandSender sender, String[] args) {
        super("inventory", new int[]{3}, sender, args, "/mcpets inventory <player> <petId>");
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player && sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        String playerName = args[1];
        String petId = args[2];
        Pet pet = Pet.getFromId(petId);

        if (pet == null) {
            Language.PET_DOESNT_EXIST.sendMessage(sender);
            return;
        }

        OfflinePlayer playerToOpen = Bukkit.getOfflinePlayer(playerName);

        if (!playerToOpen.hasPlayedBefore()) {
            Language.PLAYER_OR_PET_DOESNT_EXIST.sendMessage(sender);
            return;
        }

        final UUID owner = playerToOpen.getUniqueId();
        if (GlobalConfig.getInstance().isDatabaseSupport() && !(playerToOpen instanceof Player && playerToOpen.isOnline())) {
            // Offline target: load its row off-thread, then open on the main thread.
            Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> {
                Databases.loadData(owner);
                Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> open(sender, owner, petId));
            });
            return;
        }

        open(sender, owner, petId);
    }

    private void open(final CommandSender sender, final UUID owner, final String petId) {
        PetInventory inventory = PetInventory.get(owner, petId);

        if (inventory == null) {
            Language.PET_INVENTORY_COULDNOT_OPEN.sendMessage(sender);
            return;
        }

        if (sender instanceof Player player)
            inventory.open(player);
    }
}
