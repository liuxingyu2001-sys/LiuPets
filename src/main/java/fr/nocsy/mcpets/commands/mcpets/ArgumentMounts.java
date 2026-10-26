package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Argument to filter the categories menu by only showing mounts
 */
public class ArgumentMounts extends AArgument {

    public ArgumentMounts(CommandSender sender, String[] args) {
        super("mounts", new int[]{1}, sender, args);
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player && sender.hasPermission(PPermission.USE.getPermission());
    }

    @Override
    public void commandEffect() {
        if (!(sender instanceof Player player)) return;

        if (fr.nocsy.mcpets.data.Category.getCategories(CategoryType.MOUNT).isEmpty()) {
            // No mount category configured: still show the flat mount list instead of an empty GUI.
            new fr.nocsy.mcpets.data.inventories.MountMenu(player, 0).open(player);
            return;
        }
        CategoriesMenu.openFiltered(player, CategoryType.MOUNT);
    }
}
