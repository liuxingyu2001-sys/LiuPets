package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ArgumentClearStats extends AArgument {

    public ArgumentClearStats(CommandSender sender, String[] args) {
        super("clearStats", new int[]{3, 2}, sender, args, "/liupets clearStats <玩家|宠物ID> [宠物ID]");
    }

    @Override
    public boolean additionalConditions() {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if (args.length == 2) {
            // Either it's a player clear
            final OfflinePlayer player = resolvePlayer(args[1]);
            if (player != null) {
                clearPlayerStats(sender, player.getUniqueId());
                return;
            }

            // Or it's a pet clear
            final String petId = args[1];
            if (Pet.getFromId(petId) != null) {
                PetStats.remove(petId);
                Language.STATS_CLEARED_FOR_PET.sendMessageFormatted(sender, new FormatArg("%petId%", petId));
                PlayerData.saveDB();
                return;
            }

            // In that case it's not a pet clear so he probably failed to give a player name
            Language.PLAYER_OR_PET_DOESNT_EXIST.sendMessage(sender);
        }
        else if (args.length == 3) {
            final String petId = args[2];
            final Pet pet = Pet.getFromId(petId);
            if (pet == null) {
                Language.PLAYER_OR_PET_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            final OfflinePlayer player = resolvePlayer(args[1]);
            if (player == null) {
                Language.PLAYER_OR_PET_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            clearPlayerStats(sender, player.getUniqueId(), petId, args[1]);
        }
    }

    private OfflinePlayer resolvePlayer(final String name) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player.hasPlayedBefore() || player.isOnline())
            return player;
        return null;
    }

    private void clearPlayerStats(final CommandSender sender, final UUID uuid) {
        runWithPlayerData(sender, uuid, () -> {
            PetStats.remove(uuid);
            PlayerData.get(uuid).save();
            Language.STATS_CLEARED.sendMessage(sender);
        });
    }

    private void clearPlayerStats(final CommandSender sender, final UUID uuid,
                                  final String petId, final String playerName) {
        runWithPlayerData(sender, uuid, () -> {
            PetStats.remove(petId, uuid);
            PlayerData.get(uuid).save();
            Language.STATS_CLEARED_FOR_PET_FOR_PLAYER.sendMessageFormatted(sender,
                    new FormatArg("%petId%", petId),
                    new FormatArg("%player%", playerName));
        });
    }

    /**
     * With MySQL, an offline player's row is not in memory yet. Load it off-thread
     * first, otherwise the clear would be applied to an empty (unloaded) snapshot.
     */
    private void runWithPlayerData(final CommandSender sender, final UUID uuid, final Runnable action) {
        if (!GlobalConfig.getInstance().isDatabaseSupport() || PlayerData.isRegistered(uuid)) {
            action.run();
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> {
            Databases.loadData(uuid);
            Bukkit.getScheduler().runTask(MCPets.getInstance(), action);
        });
    }
}