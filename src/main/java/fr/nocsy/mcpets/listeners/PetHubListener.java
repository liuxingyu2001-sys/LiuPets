package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.compat.LiuIntegrations;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;

import java.lang.reflect.Method;
import java.util.ArrayList;

public final class PetHubListener implements Listener {
    @EventHandler
    public void click(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PetInventoryHolder holder)
                || holder.getType() != PetInventoryHolder.Type.PET_HUB) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)
                || event.getRawSlot() < 0 || event.getRawSlot() >= event.getInventory().getSize()) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        switch (event.getRawSlot()) {
            case 11 -> new PetMenu(player, 0).open(player);
            case 13 -> new MountMenu(player, 0).open(player);
            case 15 -> ActivePetsMenu.open(player);
            case 22 -> {
                for (Pet pet : new ArrayList<>(Pet.getActivePetsForOwner(player.getUniqueId()))) pet.despawn(PetDespawnReason.REVOKE);
                Language.REVOKED.sendMessage(player);
                PetHub.open(player);
            }
            case 40 -> player.closeInventory();
            default -> {}
        }
    }

    @EventHandler
    public void drag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof PetInventoryHolder holder)
                || holder.getType() == PetInventoryHolder.Type.PET_INVENTORY_MENU) return;
        if (event.getRawSlots().stream().anyMatch(slot -> slot < event.getInventory().getSize())) event.setCancelled(true);
    }

    /** Covers skill damage paths that do not consult the target filter. */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void damage(EntityDamageByEntityEvent event) {
        if (!LiuIntegrations.enabled("mcskills.protect-pets", true) || Pet.getFromEntity(event.getEntity()) == null) return;
        Player source = event.getDamager() instanceof Player p ? p
                : event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player p ? p : null;
        var skills = LiuIntegrations.findPlugin("mcSkills");
        if (source == null || skills == null) return;
        try {
            Class<?> utils = Class.forName("com.liuxingyu.utils.SkillDamageUtils", true, skills.getClass().getClassLoader());
            Method method = utils.getMethod("isSkillDamage", Player.class);
            if (Boolean.TRUE.equals(method.invoke(null, source))) event.setCancelled(true);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Older versions still benefit from mcSkills' target filter adapter.
        }
    }
}
