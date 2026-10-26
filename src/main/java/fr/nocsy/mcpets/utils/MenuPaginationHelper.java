package fr.nocsy.mcpets.utils;

import java.util.function.BiConsumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import fr.nocsy.mcpets.data.config.GlobalConfig;

/** Stable page capacity: navigation never changes which items belong to a page. */
public class MenuPaginationHelper {
    public record PaginationConfig(int invSize, int startIndex, int itemsToShow,
                                   boolean needsNextButton, boolean needsPreviousButton, int startSlot) {}

    private static int normalizeSize(int size) {
        return Math.max(9, Math.min(54, ((Math.max(1, size) + 8) / 9) * 9));
    }

    public static PaginationConfig calculateFixedSizePagination(int page, int totalItems, int configuredSize) {
        int size = normalizeSize(configuredSize);
        int capacity = size - 2;
        int total = Math.max(0, totalItems);
        int current = Math.clamp(page, 0, calculateMaxPagesFixedSize(total, size) - 1);
        int start = current * capacity;
        return new PaginationConfig(size, start, Math.min(capacity, total - start),
                start + capacity < total, current > 0, 1);
    }

    public static PaginationConfig calculateDynamicSizePagination(int page, int totalItems) {
        PaginationConfig config = calculateFixedSizePagination(page, totalItems, 54);
        int size = normalizeSize(config.itemsToShow() + 2);
        return new PaginationConfig(size, config.startIndex(), config.itemsToShow(),
                config.needsNextButton(), config.needsPreviousButton(), 1);
    }

    public static PaginationConfig calculatePagination(int page, int totalItems) {
        int size = GlobalConfig.getInstance().getAdaptiveInventory();
        return size > 0 ? calculateFixedSizePagination(page, totalItems, size)
                : calculateDynamicSizePagination(page, totalItems);
    }

    public static int calculateMaxPagesFixedSize(int totalItems, int configuredSize) {
        int capacity = normalizeSize(configuredSize) - 2;
        return Math.max(1, (int) Math.ceil((double) Math.max(0, totalItems) / capacity));
    }

    public static int calculateMaxPagesDynamicSize(int totalItems) {
        return calculateMaxPagesFixedSize(totalItems, 54);
    }

    public static int calculateMaxPages(int totalItems) {
        int size = GlobalConfig.getInstance().getAdaptiveInventory();
        return calculateMaxPagesFixedSize(totalItems, size > 0 ? size : 54);
    }

    public static boolean handlePagination(ItemStack item, Player player, String previousTag,
                                          String nextTag, BiConsumer<Player, Integer> menuOpener) {
        if (item == null || !item.hasItemMeta()) return false;
        String tag = PDCTag.get(item.getItemMeta());
        if (tag == null || !(tag.startsWith(previousTag) || tag.startsWith(nextTag))) return false;
        try {
            String[] parts = tag.split(";");
            int page = Integer.parseInt(parts[parts.length - 1]);
            menuOpener.accept(player, tag.startsWith(previousTag) ? Math.max(page - 1, 0) : page + 1);
        } catch (NumberFormatException ignored) {
            return true;
        }
        return true;
    }
}
