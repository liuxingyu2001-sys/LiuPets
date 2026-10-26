package fr.nocsy.mcpets.api;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Entity;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.luckperms.api.node.Node;

/** Permanent entitlement adapter shared by shop and pass command rewards. */
public final class LiuPetsAPI {
    private LiuPetsAPI() {}

    public static boolean isPet(Entity entity) { return Pet.getFromEntity(entity) != null; }

    public static UUID getOwner(Entity entity) {
        Pet pet = Pet.getFromEntity(entity);
        return pet == null ? null : pet.getOwner();
    }

    public static CompletableFuture<Void> unlock(UUID uuid, String petId) {
        Pet pet = Pet.getFromId(petId);
        if (pet == null) return CompletableFuture.failedFuture(new IllegalArgumentException("宠物不存在：" + petId));
        if (!pet.isCheckPermission()) return CompletableFuture.completedFuture(null);
        if (pet.getPermission() == null || pet.getPermission().isBlank())
            return CompletableFuture.failedFuture(new IllegalStateException("宠物未配置解锁权限"));
        var luckPerms = MCPets.getLuckPerms();
        if (luckPerms == null) return CompletableFuture.failedFuture(new IllegalStateException("永久解锁需要 LuckPerms"));
        return luckPerms.getUserManager().modifyUser(uuid,
                user -> user.data().add(Node.builder(pet.getPermission()).build()));
    }
}
