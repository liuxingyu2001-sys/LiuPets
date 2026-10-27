package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DismountFlyPetFlag extends AbstractFlag implements StoppableFlag {

    private int task;

    public static String NAME = "mcpets-dismount-flying";

    public DismountFlyPetFlag(final MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void launch() {
        if (getFlag() == null) {
            MCPets.getLog().warning("旗标 " + getFlagName() + " 为空，无法启动。");
            return;
        }
        else {
            MCPets.getLog().info("正在启动旗标 " + getFlagName() + "。");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), () -> {
            if (MCPets.getMythicMobs() == null)
                return;

            for (final UUID owner : Pet.getActivePets().keySet()) {
                for (final Pet pet : Pet.getActivePetsForOwner(owner)) {

                    if (!pet.isMountable())
                        continue;

                    if (!MCPets.getModeler().isFlyingMount(pet, owner))
                        continue;

                    final Player p = Bukkit.getPlayer(owner);
                    if (p != null) {
                        if (!pet.hasMount(p))
                            continue;

                        final boolean hasToBeEjected = testState(p.getLocation());

                        if (hasToBeEjected) {
                            pet.dismount(p);
                            Language.NOT_MOUNTABLE_HERE.sendMessage(p);
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }
}
