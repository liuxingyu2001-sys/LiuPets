package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import fr.nocsy.mcpets.utils.MenuPaginationHelper.PaginationConfig;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Lists the pets/mounts currently summoned by the player so they can be managed
 * without typing {@code /liupets menu}.
 */
public final class ActivePetsMenu {

    private static final String TITLE = "§0LiuPets §8· 正在跟随";

    @Getter
    private final Inventory inventory;
    @Getter
    private final UUID owner;
    @Getter
    private final int page;

    private ActivePetsMenu(final Player p, final int page) {
        this.page = Math.max(0, page);
        this.owner = p.getUniqueId();

        final List<Pet> activePets = Pet.getActivePetsForOwner(p.getUniqueId());

        if (activePets.isEmpty()) {
            this.inventory = new PetInventoryHolder(27, TITLE,
                    PetInventoryHolder.Type.ACTIVE_PETS_MENU).getInventory();
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                inventory.setItem(slot, button(Material.GRAY_STAINED_GLASS_PANE, " ", ""));
            }
            inventory.setItem(13, button(Material.BARRIER, "§c当前没有跟随的宠物",
                    "§7先从“我的宠物”或“我的坐骑”中召唤一只吧。"));
            return;
        }

        final PaginationConfig config = MenuPaginationHelper.calculatePagination(this.page, activePets.size());
        final List<Pet> selected = new ArrayList<>();
        for (int i = config.startIndex(); i < config.startIndex() + config.itemsToShow()
                && i < activePets.size(); i++) {
            selected.add(activePets.get(i));
        }

        this.inventory = new PetInventoryHolder(config.invSize(), TITLE,
                PetInventoryHolder.Type.ACTIVE_PETS_MENU).getInventory();

        int slot = config.startSlot();
        for (final Pet pet : selected) {
            inventory.setItem(slot++, pet.buildItem(pet.getIcon(), true));
        }

        final int maxPages = Math.max(1, MenuPaginationHelper.calculateMaxPages(activePets.size()));
        final int currentPage = Math.clamp(this.page, 0, maxPages - 1);
        if (config.needsPreviousButton())
            inventory.setItem(0, Items.previousPage(currentPage, p, maxPages));
        if (config.needsNextButton())
            inventory.setItem(config.invSize() - 1, Items.nextPage(currentPage, p, maxPages));
    }

    public static void open(final Player p) {
        open(p, 0);
    }

    public static void open(final Player p, final int page) {
        new ActivePetsMenu(p, page).show(p);
    }

    private void show(final Player p) {
        p.openInventory(inventory);
    }

    private static ItemStack button(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material);
        final var meta = item.getItemMeta();
        meta.displayName(Utils.toComponent(name));
        meta.lore(Arrays.stream(lore).map(Utils::toComponent).toList());
        item.setItemMeta(meta);
        return item;
    }
}