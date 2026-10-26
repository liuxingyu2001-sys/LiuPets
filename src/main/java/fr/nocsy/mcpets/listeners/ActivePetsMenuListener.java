package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.ActivePetsMenu;
import fr.nocsy.mcpets.data.inventories.MountInteractionMenu;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/** Handles clicks in the "currently following" overview. */
public final class ActivePetsMenuListener implements Listener {

    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)
                || holder.getType() != PetInventoryHolder.Type.ACTIVE_PETS_MENU) {
            return;
        }
        if (!(e.getWhoClicked() instanceof final Player p)) {
            return;
        }

        e.setCancelled(true);
        if (e.getRawSlot() < 0 || e.getRawSlot() >= e.getInventory().getSize()) {
            return;
        }

        final ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) {
            return;
        }

        if (MenuPaginationHelper.handlePagination(item, p,
                "AlmPetPreviousPage;", "AlmPetNextPage;",
                (player, page) -> ActivePetsMenu.open(player, page))) {
            return;
        }

        final Pet clicked = Pet.getFromIcon(item);
        if (clicked == null) {
            return;
        }

        // Resolve the live instance: the icon only carries the pet id.
        final Pet active = Pet.getActivePetsForOwner(p.getUniqueId()).stream()
                .filter(pet -> pet.getId().equalsIgnoreCase(clicked.getId()))
                .findFirst()
                .orElse(null);
        if (active == null) {
            Language.NO_ACTIVE_PET.sendMessage(p);
            p.closeInventory();
            return;
        }

        p.closeInventory();
        if (active.isMountable()) {
            new MountInteractionMenu(active, p.getUniqueId()).open(p);
        } else {
            new PetInteractionMenu(active, p.getUniqueId()).open(p);
        }
        active.setLastOpInteracted(p);
    }
}