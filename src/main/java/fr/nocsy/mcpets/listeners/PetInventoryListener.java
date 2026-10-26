package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PetInventoryListener implements Listener {

    @EventHandler
    public void inventory(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        PetInventory petInventory = PetInventory.fromCurrentView(p);

        if (petInventory != null) {
            petInventory.setInventory(e.getView().getTopInventory());
            petInventory.close(p);
        } else if (p.hasMetadata("MCPets;petInventory")) {
            // Stale marker (e.g. plugin reloaded while the GUI was open): drop it so a
            // later, unrelated inventory close is never mistaken for a pet inventory.
            p.removeMetadata("MCPets;petInventory", MCPets.getInstance());
        }
    }
}