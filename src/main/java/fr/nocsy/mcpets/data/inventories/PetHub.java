package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Main hub with fixed navigation and contextual counts. */
public final class PetHub {
    private PetHub() {}

    public static ItemStack button(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        var meta = item.getItemMeta();
        meta.displayName(Utils.toComponent(name));
        meta.lore(java.util.Arrays.stream(lore).map(Utils::toComponent).toList());
        item.setItemMeta(meta);
        return item;
    }

    public static void open(Player player) {
        Inventory menu = new PetInventoryHolder(45, "§0LiuPets §8· 宠物中心",
                PetInventoryHolder.Type.PET_HUB).getInventory();
        ItemStack frame = button(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < menu.getSize(); slot++) menu.setItem(slot, frame);
        List<Pet> available = Pet.getAvailablePets(player);
        menu.setItem(11, button(Material.BONE, "§a我的宠物", "§7已解锁：§f" + available.size() + " 只", "§e点击查看宠物列表"));
        menu.setItem(13, button(Material.SADDLE, "§6我的坐骑", "§7查看可以骑乘的伙伴", "§e点击打开坐骑列表"));
        menu.setItem(15, button(Material.LEAD, "§b正在跟随", "§7当前召唤：§f" + Pet.getActivePetsForOwner(player.getUniqueId()).size() + " 只", "§e点击管理已召唤宠物"));
        menu.setItem(22, button(Material.BARRIER, "§c收回所有宠物", "§7让伙伴休息一下", "§e点击收回当前召唤的全部宠物"));
        menu.setItem(40, button(Material.OAK_DOOR, "§7关闭菜单"));
        player.openInventory(menu);
    }
}
