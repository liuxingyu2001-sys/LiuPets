package fr.nocsy.mcpets.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import fr.nocsy.mcpets.data.inventories.MountMenu;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;

/**
 * Listener to handle interactions in the mounts menu
 */
public class MountMenuListener implements Listener {

    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)) {
            return;
        }

        if (holder.getType() != PetInventoryHolder.Type.MOUNT_MENU) {
            return;
        }

        if (!(e.getWhoClicked() instanceof final Player p)) {
            return;
        }

        // Cancel every click in this GUI: empty slots must not accept items either
        e.setCancelled(true);

        // Only the top inventory contains menu icons; never read player's own slots
        if (e.getRawSlot() < 0 || e.getRawSlot() >= e.getInventory().getSize()) {
            return;
        }

        final ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) {
            return;
        }

        if (MenuPaginationHelper.handlePagination(it, p,
                "AlmPetPreviousPage;", "AlmPetNextPage;",
            (player, page) -> new MountMenu(player, page).open(player))) {
            return;
        }

        final Pet petObject = Pet.getFromIcon(it);
        if (petObject != null) {
            final Pet active = Pet.getActivePetsForOwner(p.getUniqueId()).stream()
                    .filter(pet -> pet.getId().equalsIgnoreCase(petObject.getId()))
                    .findFirst().orElse(null);
            if (active != null) {
                p.closeInventory();
                active.setLastInteractedWith(p);
                new fr.nocsy.mcpets.data.inventories.MountInteractionMenu(active, p.getUniqueId()).open(p);
                return;
            }
            p.closeInventory();
            final Pet pet = petObject.copy();
            pet.spawnWithMessage(p);
        }
    }

}
