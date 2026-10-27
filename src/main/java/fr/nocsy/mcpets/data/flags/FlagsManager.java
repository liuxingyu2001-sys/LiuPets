package fr.nocsy.mcpets.data.flags;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;

import fr.nocsy.mcpets.MCPets;

public class FlagsManager {

    private static final List<AbstractFlag> flags = new ArrayList<>();

    @Getter
    private static boolean registered;

    public static void init(MCPets instance) {
        flags.clear();
        registered = true;

        if (instance == null) {
            MCPets.getLog().warning("插件主实例为空，无法注册 WorldGuard 旗标。");
            return;
        }

        flags.add(new DismountPetFlag(instance));
        flags.add(new DespawnPetFlag(instance));
        flags.add(new DismountFlyPetFlag(instance));
        flags.add(new PetDamageableByPlayerFlag(instance));

        for (AbstractFlag flag : flags) {
            flag.register();
        }
    }

    /**
     * Get the abstract flag registered
     */
    public static AbstractFlag getFlag(String flagName) {
        return flags.stream().filter(f -> f.getFlagName().equals(flagName)).findFirst().orElse(null);
    }

    /**
     * Start the schedulers
     */
    public static void launchFlags() {
        MCPets.getLog().info("-=- 正在启动 WorldGuard 旗标 -=-");
        int count = 0;
        for (AbstractFlag flag : flags) {
            if (!(flag instanceof StoppableFlag stoppableFlag)) continue;
            stoppableFlag.launch();
            count++;
        }
        MCPets.getLog().info("已启动 " + count + " 个旗标。");
    }

    /**
     * Stop the schedulers
     */
    public static void stopFlags() {
        for (AbstractFlag flag : flags) {
            if (!(flag instanceof StoppableFlag stoppableFlag)) continue;
            stoppableFlag.stop();
        }
        flags.clear();
    }

}
