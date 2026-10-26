package fr.nocsy.mcpets.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;

public class PetMenuListener implements Listener {

    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)) {
            return;
        }

        if (holder.getType() != PetInventoryHolder.Type.PET_MENU) {
            return;
        }

        if (!(e.getWhoClicked() instanceof final Player p)) {
            return;
        }

        e.setCancelled(true);

        if (e.getRawSlot() < 0 || e.getRawSlot() >= e.getInventory().getSize()) return;
        final ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) {
            return;
        }

        if (MenuPaginationHelper.handlePagination(it, p,
                "AlmPetPreviousPage;", "AlmPetNextPage;",
            // Keep the flat pet list: PetMenu#open would bounce back to the categories
            // menu when categories exist, making pages beyond the first unreachable.
            (player, page) -> player.openInventory(new PetMenu(player, page).getInventory()))) {
            return;
        }

        final Pet petObject = Pet.getFromIcon(it);
        if (petObject != null) {
            final Pet active = Pet.getActivePetsForOwner(p.getUniqueId()).stream()
                    .filter(pet -> pet.getId().equals(petObject.getId())).findFirst().orElse(null);
            if (active != null) {
                active.setLastInteractedWith(p);
                new fr.nocsy.mcpets.data.inventories.PetInteractionMenu(active, p.getUniqueId()).open(p);
                return;
            }
            p.closeInventory();
            final Pet pet = petObject.copy();
            pet.spawnWithMessage(p);
        }
    }

}
