package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractConfig {

    public static String getPath() {
        return MCPets.getInstance().getDataFolder().getPath() + File.separator;
    }

    /** Copy legacy data once; leave the original directory intact. */
    public static void migrateLegacyData() {
        final java.nio.file.Path target = MCPets.getInstance().getDataFolder().toPath();
        final java.nio.file.Path legacy = target.resolveSibling("MCPets");
        if (java.nio.file.Files.exists(target) || !java.nio.file.Files.isDirectory(legacy)) return;
        try (var paths = java.nio.file.Files.walk(legacy)) {
            for (var source : paths.toList()) {
                var destination = target.resolve(legacy.relativize(source));
                if (java.nio.file.Files.isDirectory(source)) java.nio.file.Files.createDirectories(destination);
                else java.nio.file.Files.copy(source, destination);
            }
            MCPets.getLog().info("已复制 MCPets 旧数据至 LiuPets，原目录保留作为备份。");
        } catch (IOException ex) {
            // A failed migration must not take the whole server down: keep the legacy
            // directory untouched and continue with a fresh data folder.
            MCPets.getLog().log(java.util.logging.Level.SEVERE,
                    "旧宠物数据迁移失败，已继续启动；请手动把 plugins/MCPets 复制到 plugins/LiuPets", ex);
        }
    }

    @Getter
    @Setter
    private String folderName;

    @Getter
    @Setter
    private String fileName;

    @Getter
    private FileConfiguration config;

    public void init(final String folderName, final String fileName) {
        this.fileName = fileName;
        this.folderName = folderName;

        final File folder = new File(getPath() + folderName);
        if (!folder.exists())
            folder.mkdirs();

        final File file = new File(getFullPath());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                MCPets.getLog().log(Level.SEVERE, "Failed to create config file: " + getFullPath(), e);
            }
        }

        loadConfig();
    }

    public String getFullPath() {
        return (getPath() + folderName + "/" + fileName).replace("//", "/");
    }

    public void save() {
        final File file = new File(getFullPath());
        try {
            config.save(file);
        } catch (final IOException e) {
            MCPets.getLog().log(Level.SEVERE, "Failed to save config file: " + getFullPath(), e);
        }
    }

    public boolean delete() {
        final File file = new File(getFullPath());
        return file.delete();
    }

    public void loadConfig() {
        final File file = new File(getFullPath());
        config = YamlConfiguration.loadConfiguration(file);

        // A non-empty file that parses to no keys is almost always corrupted YAML.
        // Keep a backup before init()/save() regenerates defaults on top of it.
        if (file.length() > 0 && config.getKeys(false).isEmpty()) {
            final File backup = new File(file.getPath() + ".broken-" + System.currentTimeMillis());
            try {
                java.nio.file.Files.copy(file.toPath(), backup.toPath());
                MCPets.getLog().severe("配置文件无法解析，已备份为 " + backup.getName() + " 并重新生成默认值：" + file.getName());
            } catch (IOException ex) {
                MCPets.getLog().log(java.util.logging.Level.SEVERE, "无法备份损坏的配置文件 " + file.getName(), ex);
            }
        }
    }

    protected void reorderKeys(String... keyOrder) {
        Map<String, Object> ordered = new LinkedHashMap<>();
        for (String key : keyOrder) {
            if (config.contains(key)) {
                ordered.put(key, config.get(key));
            }
        }
        for (String key : new ArrayList<>(config.getKeys(false))) {
            if (!ordered.containsKey(key)) {
                ordered.put(key, config.get(key));
            }
        }
        for (String key : new ArrayList<>(config.getKeys(false))) {
            config.set(key, null);
        }
        for (Map.Entry<String, Object> entry : ordered.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    public abstract void reload();
}
