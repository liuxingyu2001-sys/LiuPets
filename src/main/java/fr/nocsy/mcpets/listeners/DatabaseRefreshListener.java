package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.events.PetLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DatabaseRefreshListener implements Listener {

    @EventHandler
    public void saveDB(PetLevelUpEvent e) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        final UUID owner = e.getPet().getOwner();
        if (owner == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> Databases.savePlayerData(owner));
    }
}
