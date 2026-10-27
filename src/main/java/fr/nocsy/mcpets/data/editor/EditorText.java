package fr.nocsy.mcpets.data.editor;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Chinese presentation layer for the in-game editor.
 *
 * <p>The editor was written with hard-coded English item names/lores. Rather than
 * translating every builder one by one (and drifting again later), every icon passes
 * through {@link #localize(ItemStack, boolean, Object, EditorExpectationType, boolean)}
 * right before it is shown. Unknown items keep their original name so configuration
 * values (pet names, level names, ...) are never rewritten.</p>
 */
public final class EditorText {

    private static final Map<String, String> NAMES = Map.ofEntries(
            Map.entry("§6Edit configuration", "§6编辑全局配置"),
            Map.entry("§6Edit pets", "§6编辑宠物"),
            Map.entry("§6Edit categories", "§6编辑分类"),
            Map.entry("§6Edit items", "§6编辑物品"),
            Map.entry("§6Edit pet food", "§6编辑宠物食物"),
            Map.entry("§6Prefix", "§6前缀"),
            Map.entry("§6Default pet name", "§6默认宠物名称"),
            Map.entry("§6Use default MythicMobs name", "§6使用 MythicMobs 默认名称"),
            Map.entry("§6Override default name", "§6覆盖默认名称"),
            Map.entry("§6Default mountable", "§6默认允许骑乘"),
            Map.entry("§6Right click to open menu", "§6右键打开菜单"),
            Map.entry("§6Left click to open menu", "§6左键打开菜单"),
            Map.entry("§6Sneak mode to open menu", "§6潜行打开菜单"),
            Map.entry("§6Nameable", "§6允许改名"),
            Map.entry("§6Distance before teleport", "§6传送距离"),
            Map.entry("§6Max name length", "§6名字最大长度"),
            Map.entry("§6Summoning Inventory size", "§6召唤界面大小"),
            Map.entry("§6Enable click back to menu (category)", "§6点击空白返回分类"),
            Map.entry("§6Activate back menu icon", "§6启用返回菜单图标"),
            Map.entry("§6Dismount on damaged", "§6受伤时下坐骑"),
            Map.entry("§6Percent health on respawn", "§6复活生命百分比"),
            Map.entry("§6Auto-save database delay", "§6自动保存间隔"),
            Map.entry("§6Default respawn cooldown", "§6默认复活冷却"),
            Map.entry("§6Enable global respawn cooldown", "§6启用全局复活冷却"),
            Map.entry("§6Autorespawn", "§6自动复活"),
            Map.entry("§6Disable inventory while signal stick", "§6持指挥棒时禁用背包"),
            Map.entry("§6MythicMob", "§6MythicMob"),
            Map.entry("§6Permission", "§6权限"),
            Map.entry("§6Mountable", "§6可骑乘"),
            Map.entry("§6Mount type", "§6坐骑类型"),
            Map.entry("§6Despawn on Dismount", "§6下坐骑时收回"),
            Map.entry("§6Auto-ride", "§6自动骑乘"),
            Map.entry("§6Mount permission", "§6骑乘权限"),
            Map.entry("§6Despawn skill", "§6收回技能"),
            Map.entry("§6Come back distance", "§6返回距离"),
            Map.entry("§6Spawn range", "§6召唤范围"),
            Map.entry("§6Close up come back distance", "§6靠近后返回距离"),
            Map.entry("§6Inventory size", "§6背包大小"),
            Map.entry("§6Taming - Progress skill", "§6驯服 - 进度技能"),
            Map.entry("§6Taming - Finished skill", "§6驯服 - 完成技能"),
            Map.entry("§6Signals", "§6信号列表"),
            Map.entry("§6Signal stick", "§6指挥棒"),
            Map.entry("§6Signal stick from menu", "§6菜单中获取指挥棒"),
            Map.entry("§6Skins", "§6外观列表"),
            Map.entry("§6Living pet features", "§6养成功能"),
            Map.entry("§6Level name", "§6等级名称"),
            Map.entry("§6Experience threshold", "§6经验阈值"),
            Map.entry("§6Maximum health", "§6最大生命"),
            Map.entry("§6Regeneration", "§6生命恢复"),
            Map.entry("§6Resistance modifier", "§6伤害减免"),
            Map.entry("§6Damage modifier", "§6伤害倍率"),
            Map.entry("§6Power modifier", "§6技能强度倍率"),
            Map.entry("§6Cooldown - Respawn", "§6冷却 - 复活"),
            Map.entry("§6Cooldown - Revoke", "§6冷却 - 收回"),
            Map.entry("§6Inventory extension", "§6背包扩展"),
            Map.entry("§6Announcement - Text", "§6进化公告 - 文本"),
            Map.entry("§6Announcement - Type", "§6进化公告 - 类型"),
            Map.entry("§6Announcement - Skill", "§6进化公告 - 技能"),
            Map.entry("§6Evolution - Pet ID", "§6进化 - 宠物 ID"),
            Map.entry("§6Evolution - Delay", "§6进化 - 延迟"),
            Map.entry("§6Evolution - Remove old access", "§6进化 - 移除旧权限"),
            Map.entry("§6Skin - MythicMob", "§6外观 - MythicMob"),
            Map.entry("§6Skin - Permission", "§6外观 - 权限"),
            Map.entry("§6Category ID", "§6分类 ID"),
            Map.entry("§6Default category", "§6默认分类"),
            Map.entry("§6Excluded categories", "§6排除分类"),
            Map.entry("§6Category Inventory title", "§6分类界面标题"),
            Map.entry("§6Category icon name", "§6分类图标名称"),
            Map.entry("§6Item ID", "§6物品 ID"),
            Map.entry("§6Petfood ID", "§6宠物食物 ID"),
            Map.entry("§6Food item", "§6食物物品"),
            Map.entry("§6Pet food type", "§6宠物食物类型"),
            Map.entry("§6Power value", "§6效果强度"),
            Map.entry("§6Duration of the buff", "§6Buff 持续时间"),
            Map.entry("§6Operator", "§6运算方式"),
            Map.entry("§6Signal", "§6信号"),
            Map.entry("§6Evolution", "§6进化"),
            Map.entry("§6Delay before evolution", "§6进化延迟"),
            Map.entry("§6Unlocked pet", "§6解锁宠物"),
            Map.entry("§cUnknown item", "§c未知物品"),
            Map.entry("§aPage selector", "§a翻页")
    );

    private static final Map<String, String> DESCRIPTIONS = Map.ofEntries(
            Map.entry("§6Edit configuration", "§7点击编辑插件全局配置。"),
            Map.entry("§6Edit pets", "§7点击管理宠物配置。"),
            Map.entry("§6Edit categories", "§7点击管理宠物分类。"),
            Map.entry("§6Edit items", "§7点击管理菜单物品。"),
            Map.entry("§6Edit pet food", "§7点击管理宠物食物。"),
            Map.entry("§6Prefix", "§7宠物名字前缀。"),
            Map.entry("§6Default pet name", "§7未设置名字时使用的默认名称，可用 %player%。"),
            Map.entry("§6Use default MythicMobs name", "§7是否直接使用 MythicMobs 的生物名称。"),
            Map.entry("§6Override default name", "§7是否用默认名称覆盖 MythicMobs 名称。"),
            Map.entry("§6Default mountable", "§7所有宠物默认是否可骑乘。"),
            Map.entry("§6Right click to open menu", "§7右键点击宠物时打开管理菜单。"),
            Map.entry("§6Left click to open menu", "§7左键点击宠物时打开管理菜单。"),
            Map.entry("§6Sneak mode to open menu", "§7潜行点击宠物时打开管理菜单。"),
            Map.entry("§6Nameable", "§7是否允许玩家给宠物改名。"),
            Map.entry("§6Distance before teleport", "§7宠物超过该距离后传送到玩家身边。"),
            Map.entry("§6Max name length", "§7宠物名字允许的最大字符数。"),
            Map.entry("§6Summoning Inventory size", "§7召唤界面的格数。"),
            Map.entry("§6Enable click back to menu (category)", "§7在分类菜单点击空白处返回上一级。"),
            Map.entry("§6Activate back menu icon", "§7是否在分类菜单显示返回按钮。"),
            Map.entry("§6Dismount on damaged", "§7宠物受伤时是否让玩家下坐骑。"),
            Map.entry("§6Percent health on respawn", "§7复活后恢复的生命百分比。"),
            Map.entry("§6Auto-save database delay", "§7自动保存玩家数据的间隔（秒）。"),
            Map.entry("§6Default respawn cooldown", "§7新宠物默认的复活冷却（秒）。"),
            Map.entry("§6Enable global respawn cooldown", "§7是否启用全局复活冷却计时。"),
            Map.entry("§6Autorespawn", "§7宠物死亡后是否自动复活。"),
            Map.entry("§6Disable inventory while signal stick", "§7手持指挥棒时是否禁止打开宠物背包。"),
            Map.entry("§6MythicMob", "§7对应的 MythicMobs 生物 ID。"),
            Map.entry("§6Permission", "§7解锁/使用该宠物所需的权限节点。"),
            Map.entry("§6Mountable", "§7该宠物是否可作为坐骑。"),
            Map.entry("§6Mount type", "§7坐骑类型：walking 或 flying。"),
            Map.entry("§6Despawn on Dismount", "§7玩家下坐骑时是否收回宠物。"),
            Map.entry("§6Auto-ride", "§7召唤后是否自动骑乘宠物。"),
            Map.entry("§6Mount permission", "§7骑乘该宠物所需的权限。"),
            Map.entry("§6Despawn skill", "§7收回宠物时释放的技能 ID。"),
            Map.entry("§6Come back distance", "§7宠物离玩家过远时开始返回的距离。"),
            Map.entry("§6Spawn range", "§7宠物允许生成的区域半径。"),
            Map.entry("§6Close up come back distance", "§7宠物靠近玩家多少格后停止返回。"),
            Map.entry("§6Inventory size", "§7宠物背包的总格数。"),
            Map.entry("§6Taming - Progress skill", "§7驯服过程中释放的技能。"),
            Map.entry("§6Taming - Finished skill", "§7驯服完成时释放的技能。"),
            Map.entry("§6Signals", "§7该宠物可用的指挥棒指令列表。"),
            Map.entry("§6Signal stick", "§7设置指挥棒物品。"),
            Map.entry("§6Signal stick from menu", "§7是否允许从菜单获取指挥棒。"),
            Map.entry("§6Skins", "§7该宠物的外观列表。"),
            Map.entry("§6Living pet features", "§7配置等级、生命、经验等养成属性。"),
            Map.entry("§6Level name", "§7等级显示名称。"),
            Map.entry("§6Experience threshold", "§7升到该等级所需经验。"),
            Map.entry("§6Maximum health", "§7宠物最大生命值。"),
            Map.entry("§6Regeneration", "§7每秒恢复的生命值。"),
            Map.entry("§6Resistance modifier", "§7受到伤害的减免百分比。"),
            Map.entry("§6Damage modifier", "§7宠物技能伤害倍率（百分比）。"),
            Map.entry("§6Power modifier", "§7技能强度倍率（百分比）。"),
            Map.entry("§6Cooldown - Respawn", "§7宠物死亡后复活所需冷却（秒）。"),
            Map.entry("§6Cooldown - Revoke", "§7收回宠物后的冷却时间（秒）。"),
            Map.entry("§6Inventory extension", "§7该等级为宠物背包增加的格数。"),
            Map.entry("§6Announcement - Text", "§7进化时显示的公告文本。"),
            Map.entry("§6Announcement - Type", "§7进化公告类型：CHAT、ACTION_BAR、TITLE 等。"),
            Map.entry("§6Announcement - Skill", "§7进化时播放的 MythicMobs 技能 ID。"),
            Map.entry("§6Evolution - Pet ID", "§7进化后的宠物 ID。"),
            Map.entry("§6Evolution - Delay", "§7进化动画/生效的延迟（秒）。"),
            Map.entry("§6Evolution - Remove old access", "§7进化后是否移除旧宠物权限。"),
            Map.entry("§6Skin - MythicMob", "§7该外观对应的 MythicMobs 生物 ID。"),
            Map.entry("§6Skin - Permission", "§7使用该外观所需的权限。"),
            Map.entry("§6Category ID", "§7分类的唯一标识，用于配置引用。"),
            Map.entry("§6Default category", "§7新宠物默认归入的分类 ID。"),
            Map.entry("§6Excluded categories", "§7不显示在宠物中心/坐骑中心的分类。"),
            Map.entry("§6Category Inventory title", "§7打开分类时显示的界面标题。"),
            Map.entry("§6Category icon name", "§7分类图标的显示名称。"),
            Map.entry("§6Item ID", "§7菜单物品的唯一键。"),
            Map.entry("§6Petfood ID", "§7宠物食物的唯一标识。"),
            Map.entry("§6Food item", "§7食物对应的物品。"),
            Map.entry("§6Pet food type", "§7食物效果类型：HEALTH、EXP、TAMING 等。"),
            Map.entry("§6Power value", "§7效果数值。"),
            Map.entry("§6Duration of the buff", "§7宠物食物 Buff 的持续时间（秒）。"),
            Map.entry("§6Operator", "§7属性运算方式：ADD、SET、MULTIPLY 等。"),
            Map.entry("§6Signal", "§7指挥棒指令名称。"),
            Map.entry("§6Evolution", "§7配置该等级的进化目标。"),
            Map.entry("§6Delay before evolution", "§7进化前的延迟（秒）。"),
            Map.entry("§6Unlocked pet", "§7使用该食物后可解锁的宠物 ID。")
    );

    private EditorText() {
    }

    /** Convert a raw editor icon name to its Chinese label; unknown names are returned unchanged. */
    public static String nameOf(final String raw) {
        return chineseName(raw);
    }

    private static String chineseName(final String raw) {
        if (raw == null) return "§c未知物品";
        final String mapped = NAMES.get(raw);
        if (mapped != null) return mapped;

        if (raw.startsWith("§cBack to ")) {
            return "§c返回" + target(raw.substring("§cBack to ".length()));
        }
        if (raw.startsWith("§aCreate a new ")) {
            return "§a新建" + target(raw.substring("§aCreate a new ".length()));
        }
        if (raw.startsWith("§cDelete the ")) {
            return "§c删除" + target(raw.substring("§cDelete the ".length()));
        }
        if (raw.startsWith("§6Category: §e")) {
            return "§6分类：§e" + raw.substring("§6Category: §e".length());
        }
        if (raw.startsWith("§6Skin: §e")) {
            return "§6外观：§e" + raw.substring("§6Skin: §e".length());
        }
        return raw;
    }

    private static String target(final String english) {
        return switch (english.toLowerCase()) {
            case "global menu" -> "主菜单";
            case "pet selection menu" -> "宠物选择";
            case "pet editor" -> "宠物编辑";
            case "pet levels" -> "宠物等级";
            case "pet skins" -> "宠物外观";
            case "categories" -> "分类";
            case "items" -> "物品";
            case "pet foods" -> "宠物食物";
            case "pet" -> "宠物";
            case "level" -> "等级";
            case "category" -> "分类";
            case "item" -> "物品";
            case "petfood" -> "宠物食物";
            case "skin" -> "外观";
            default -> english;
        };
    }

    /**
     * @return true when this icon is an editor-owned label and should be localized.
     */
    public static boolean shouldLocalize(final ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return false;
        final String name = stack.getItemMeta().getDisplayName();
        if (name == null) return false;
        return NAMES.containsKey(name)
                || (name.startsWith("§cBack to ") || name.startsWith("§aCreate a new ")
                || name.startsWith("§cDelete the ") || name.startsWith("§6Category: §e")
                || name.startsWith("§6Skin: §e"));
    }

    public static void localize(final ItemStack stack, final boolean hasValue,
                                final Object value, final EditorExpectationType type,
                                final boolean resetable) {
        if (stack == null || !stack.hasItemMeta()) return;
        final ItemMeta meta = stack.getItemMeta();
        final String rawName = meta.getDisplayName();
        meta.setDisplayName(chineseName(rawName));

        final List<String> lore = new ArrayList<>();
        final String special = specialDescription(rawName, type);
        if (special != null) {
            for (final String line : special.split("\n")) lore.add(line);
        }

        if (hasValue) {
            lore.add(" ");
            lore.add("§7当前值：");
            if (value instanceof List<?> list) {
                if (list.isEmpty()) {
                    lore.add("  §8（空）");
                } else {
                    for (final Object entry : list) {
                        lore.add("  §e- " + entry);
                    }
                }
            } else {
                lore.add("  " + formatValue(value));
            }
        }

        if (resetable) {
            lore.add(" ");
            lore.add("§cShift + 左键§7 重置为默认值。");
        }

        meta.setLore(lore.isEmpty() ? null : lore);
        stack.setItemMeta(meta);
    }

    private static String specialDescription(final String raw, final EditorExpectationType type) {
        if (raw == null) return "§7未知编辑器项目。";
        if (raw.startsWith("§cBack to ")) return "§7点击返回上一级。";
        if (raw.startsWith("§aCreate a new ")) return "§7点击创建新项目。";
        if (raw.startsWith("§cDelete the ")) return "§7Shift + 左键删除，§c操作不可撤销§7。";
        if (raw.startsWith("§6Category: ")) return "§7点击编辑分类。";
        if (raw.startsWith("§6Skin: ")) return "§7点击编辑外观。";
        if (raw.equals("§aPage selector")) return "§c左键§7 上一页\n§a右键§7 下一页";
        if (raw.equals("§cUnknown item")) return null;
        if (raw.equals("§6Signal stick")) return "§7手持物品点击图标可替换指挥棒。";
        final String described = DESCRIPTIONS.get(raw);
        if (described != null) return described;
        if (raw.startsWith("§6Edit ")) return "§7点击进入编辑界面。";

        if (type == EditorExpectationType.BOOLEAN) return "§7点击切换开启/关闭。";
        if (type == EditorExpectationType.ITEM || type == EditorExpectationType.ITEM_ID_OR_MATERIAL) {
            return "§7手持物品点击图标进行设置。";
        }
        return "§7点击后在聊天栏输入新值。";
    }

    /** Human readable Chinese name for an editor input type. */
    public static String typeName(final EditorExpectationType type) {
        if (type == null) return "文本";
        return switch (type) {
            case BOOLEAN -> "开关（true/false）";
            case INT -> "整数";
            case POSITIVE_INT -> "正整数";
            case INVENTORY_SIZE -> "背包格数";
            case FLOAT -> "小数";
            case POSITIVE_FLOAT -> "正小数";
            case STRING -> "文本";
            case STRING_LIST -> "文本列表";
            case MYTHICMOB -> "MythicMob ID";
            case SKILL -> "技能 ID";
            case PET_ID -> "宠物 ID";
            case PET_CREATE -> "新宠物 ID";
            case PET_DELETE, PET_LEVEL_DELETE, PET_SKIN_DELETE -> "要删除的项目 ID";
            case PET_LEVEL_EDIT, PET_LEVEL_CREATE -> "等级键名";
            case PET_SKIN_EDIT, PET_SKIN_CREATE -> "外观名称";
            case PET -> "宠物";
            case PAGE_SELECTOR -> "翻页";
            case ANNOUNCEMENT_TYPE -> "公告类型";
            case MOUNT_TYPE -> "坐骑类型";
            case CATEGORY_ID, CATEGORY_CREATE, CATEGORY_EDIT, CATEGORY_DELETE -> "分类 ID";
            case CATEGORY_PET_LIST_ADD, CATEGORY_PET_LIST_REMOVE -> "宠物 ID";
            case OPERATOR_TYPE -> "运算符";
            case ITEM_ID_OR_MATERIAL -> "物品 ID 或原版材料";
            case PETFOOD_TYPE -> "宠物食物类型";
            case PETFOOD_ID, PETFOOD_CREATE, PETFOOD_EDIT, PETFOOD_DELETE -> "宠物食物 ID";
            case PETFOOD_PET_LIST_ADD, PETFOOD_PET_LIST_REMOVE -> "宠物 ID";
            case ITEM -> "物品";
            case ITEM_CREATE, ITEM_EDIT, ITEM_DELETE -> "物品键名";
            case ITEM_SECTION_ID -> "配置节点";
            default -> "文本";
        };
    }

    private static String formatValue(final Object value) {
        if (value == null) return "§6默认（未设置）";
        if (value instanceof Boolean bool) return bool ? "§a开启" : "§c关闭";
        return "§e" + value;
    }
}