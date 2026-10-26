package fr.nocsy.mcpets.compat;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.events.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

/** Optional PassAPI adapter; no compile-time dependency on a local plugin jar. */
public final class LiuIntegrations implements Listener {
    private static YamlConfiguration config;
    private static boolean warned;

    public static void reload() {
        File file = new File(MCPets.getInstance().getDataFolder(), "integrations.yml");
        if (!file.exists()) MCPets.getInstance().saveResource("integrations.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
        warned = false;
    }

    public static boolean enabled(String path, boolean fallback) {
        return config == null ? fallback : config.getBoolean(path, fallback);
    }

    public static Plugin findPlugin(String name) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(name) && plugin.isEnabled()) return plugin;
        }
        return null;
    }

    private void progress(UUID uuid, String event) {
        if (uuid == null || config == null || !config.getBoolean("liupass.enabled", true)) return;
        Plugin pass = findPlugin("Liupass");
        if (pass == null) return;
        String task = config.getString("liupass.tasks." + event, "").trim();
        if (task.isEmpty()) return;
        try {
            Class<?> api = Class.forName("com.liupass.core.api.PassAPI", true, pass.getClass().getClassLoader());
            String passId = config.getString("liupass.pass-id", "").trim();
            if (passId.isEmpty()) passId = (String) api.getMethod("getDefaultPassId", UUID.class).invoke(null, uuid);
            if (passId == null || passId.isBlank()) return;
            Method add = api.getMethod("addTaskProgress", UUID.class, String.class, String.class, int.class);
            add.invoke(null, uuid, passId, task, 1);
        } catch (ReflectiveOperationException | LinkageError ex) {
            if (!warned) {
                warned = true;
                MCPets.getLog().warning("战令接口调用失败，请检查 Liupass 版本：" + ex.getClass().getSimpleName());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void feed(PetFedByPlayerEvent event) { progress(event.getPlayer().getUniqueId(), "feed"); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void tame(PetTamedByPlayerEvent event) { progress(event.getPlayer().getUniqueId(), "tame"); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void level(PetLevelUpEvent event) {
        if (event.getPet() != null && event.getPet().getOwner() != null) {
            progress(event.getPet().getOwner(), "level-up");
        }
    }
}
