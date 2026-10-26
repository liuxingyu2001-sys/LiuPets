package fr.nocsy.mcpets.data.config;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import fr.nocsy.mcpets.utils.Utils;

import net.kyori.adventure.text.Component;

public enum Language {

    INVENTORY_PETS_MENU("§0宠物中心"),
    INVENTORY_PETS_MENU_INTERACTIONS("§0宠物管理"),
    INVENTORY_MOUNTS_MENU("§0我的坐骑"),
    INVENTORY_MOUNTS_MENU_INTERACTIONS("§0坐骑管理"),
    MOUNT_ITEM_NAME("§6骑乘宠物"),
    MOUNT_ITEM_DESCRIPTION("§7点击骑上你的宠物"),
    RENAME_ITEM_NAME("§6宠物改名"),
    RENAME_ITEM_DESCRIPTION("§7点击后在聊天栏输入新名字，输入 取消 可退出"),
    BACK_TO_PETMENU_ITEM_NAME("§c返回列表"),
    BACK_TO_PETMENU_ITEM_DESCRIPTION("§7点击返回宠物中心"),
    INVENTORY_ITEM_NAME("§6宠物背包"),
    INVENTORY_ITEM_DESCRIPTION("§7点击打开宠物背包"),
    SKINS_ITEM_NAME("§6宠物外观"),
    SKINS_ITEM_DESCRIPTION("§7点击选择宠物皮肤"),
    EQUIPMENT_ITEM_NAME("§6宠物装备"),
    EQUIPMENT_DESCRIPTION("§7点击查看宠物装备"),
    NEXT_PAGE_ITEM_NAME("§6下一页 §7(§e%currentPage%§7/§e%maxPage%)"),
    NEXT_PAGE_ITEM_DESCRIPTION("§7点击查看下一页"),
    PREVIOUS_PAGE_ITEM_NAME("§6上一页 §7(§e%currentPage%§7/§e%maxPage%)"),
    PREVIOUS_PAGE_ITEM_DESCRIPTION("§7点击查看上一页"),
    NICKNAME("§9昵称：§7%nickname%"),
    NICKNAME_ITEM_LORE("§c点击收回这只宠物"),
    SUMMONED("§a宠物已召唤，点击宠物可以打开管理菜单。"),
    REVOKED("§7宠物已收回。"),
    REVOKED_FOR_NEW_ONE("§7已收回之前的宠物并召唤新宠物。"),
    REVOKED_UNKNOWN("§c宠物召唤失败。§7请检查 MythicMobs 生物 ID、世界难度、区域限制与生物生成保护，并确保宠物 ID 唯一。"),
    SPAWN_CANCELLED("§c召唤被其它插件或区域保护拦截了（PetSpawnEvent 被取消）。"),
    MYTHICMOB_NULL("§c宠物关联的 MythicMobs 生物配置不存在或已删除。"),
    NO_MOB_MATCH("§cMythicMobs 尚未注册这只宠物的生物 ID。"),
    NOT_ALLOWED("§c你还没有解锁这只宠物。"),
    OWNER_NOT_FOUND("§c找不到宠物主人，无法召唤。"),
    REVOKED_BEFORE_CHANGES("§c宠物已被收回，请重新召唤后操作。"),
    NOT_MOUNTABLE("§c这只宠物不支持骑乘。"),
    ALREADY_MOUNTING("§c你正在骑乘其他载具，请先下马。"),
    NOT_MOUNTABLE_HERE("§c当前区域禁止骑乘宠物。"),
    CANT_MOUNT_PET_YET("§c你还没有骑乘这只宠物的权限。"),
    CANT_FOLLOW_HERE("§c宠物无法在当前区域跟随。"),
    TYPE_NAME_IN_CHAT("§a请在聊天栏输入宠物的新名字，输入 §e取消 §a可退出。"),
    IF_WISH_TO_REMOVE_NAME("§7输入 §e%tag% §7可恢复默认名字。"),
    NICKNAME_CHANGED_SUCCESSFULY("§a宠物名字已更新！"),
    NICKNAME_NOT_CHANGED("§c名字不能为空或超过 32 个字符，请重新操作。"),
    TAG_TO_REMOVE_NAME("清除"),
    ALREADY_INSIDE_VEHICULE("§c请先离开当前载具，再骑乘宠物。"),
    PET_DOESNT_EXIST("§c这只宠物不存在，请检查宠物 ID。"),
    PLAYER_NOT_CONNECTED("§c玩家 §e%player% §c当前不在线。"),
    BLACKLISTED_WORD("§c名字包含不允许使用的词语：%word%，改名已取消。"),
    NO_ACTIVE_PET("§7你还没有召唤宠物，使用 §e/liupets §7打开宠物中心。"),
    SPECIFY_PET("§7你有多只正在跟随的宠物，请指定：§e%pets%"),
    SIGNAL_STICK_GIVEN("§a已获得宠物指挥棒：右键下达指令，左键切换指令。"),
    SIGNAL_STICK_SIGNAL("§6当前指令：§e%signal%"),
    LOOP_SPAWN("§c宠物频繁传送，已自动收回，请在空旷位置重新召唤。"),
    REQUIRES_ITEM_IN_HAND("§c请先在主手拿着要保存的物品。"),
    ITEM_UPDATED("§a物品已更新，键名：§e%key%"),
    ITEM_DOESNT_EXIST("§c物品键 §e%key% §c不存在，请使用 add 添加。"),
    KEY_DOESNT_EXIST("§c指定的物品键不存在。"),
    KEY_REMOVED("§a物品键已删除。"),
    KEY_ALREADY_EXISTS("§c这个物品键已存在，请使用更新操作。"),
    KEY_ADDED("§a物品与键名已保存。"),
    KEY_LIST("§a可用物品键："),
    RELOAD_SUCCESS("§aLiuPets 配置已重载。"),
    HOW_MANY_PETS_LOADED("§a成功加载 %numberofpets% 只宠物。"),
    REQUIRES_MODELENGINE("§c需要安装兼容的 ModelEngine 或 BetterModel 才能使用宠物模型。"),
    USAGE("§6LiuPets 帮助\n§e/liupets §7宠物中心\n§e/liupets list §7宠物列表\n§e/liupets menu [宠物ID] §7管理已召唤宠物\n§e/liupets name §7修改宠物名字\n§e/liupets mounts §7坐骑列表\n§e/liupets help §7查看帮助"),
    NO_PERM("§c你没有执行此操作的权限。"),
    BLACKLISTED_WORLD("§c当前世界禁用了 LiuPets。"),
    CATEGORY_MENU_TITLE("§0宠物分类"),
    CATEGORY_DOESNT_EXIST("§c指定的宠物分类不存在。"),
    PET_INVENTORY_TITLE("§0%pet% §8· 背包"),
    PET_INVENTORY_COULDNOT_OPEN("§c这只宠物没有可用的背包。"),
    PET_SKINS_TITLE("§0%pet% §8· 外观"),
    SKIN_COULD_NOT_APPLY("§c暂时无法切换这个外观。"),
    NO_SKIN_AVAILABLE("§7这只宠物没有可用的外观。"),
    SKIN_APPLIED("§a宠物外观已切换！"),
    GLOBAL_RESPAWN_TIMER_RUNNING("§c召唤冷却中，还需等待 %timeLeft% 秒。"),
    RESPAWN_TIMER_RUNNING("§c宠物正在恢复，还需等待 %timeLeft% 秒。"),
    REVOKE_TIMER_RUNNING("§c宠物正在休息，还需等待 %timeLeft% 秒。"),
    PLAYER_OR_PET_DOESNT_EXIST("§c宠物不存在，或玩家尚未进入过服务器。"),
    STATS_CLEARED("§a宠物养成数据已清除。"),
    STATS_CLEARED_FOR_PET_FOR_PLAYER("§a已清除玩家 %player% 的宠物 %petId% 的养成数据。"),
    STATS_CLEARED_FOR_PET("§a已清除宠物 %petId% 的养成数据。"),
    PET_TAMING_PROGRESS("§7驯服进度 §a%progress%% §7· %progressbar%"),
    PET_COULD_NOT_EVOLVE("§c你已拥有进化后的宠物，无法重复进化。"),
    PETFOOD_DOESNT_EXIST("§c指定的宠物食物不存在。"),
    PETUNLOCK_NOPERM("§c你没有使用该宠物解锁道具的权限。"),
    PETUNLOCKED("§a已解锁宠物 %petName%！使用 /liupets 查看。"),
    PETUNLOCKED_ALREADY("§7你已经拥有宠物 §e%petName%§7。"),
    PETUNLOCK_NO_LUCKPERMS("§c永久解锁需要安装 LuckPerms，本次未消耗道具。"),
    PETUNLOCK_NO_PERMISSION_CONFIGURED("§c该宠物没有配置解锁权限，无法使用解锁道具。"),
    INVALID_BOOLEAN("§c参数必须是 §etrue §c或 §efalse§c。"),
    PET_ALREADY_TAMED("§7这只宠物已经被驯服。"),
    PET_DOESNT_EAT("§c这只宠物不吃这种食物。"),
    PET_FOOD_ON_COOLDOWN("§7宠物暂时不饿，%timeleft% 秒后可再次喂食。"),
    PET_STATUS_ALIVE("§a可召唤"),
    PET_STATUS_REVOKED("§e休息中 §7（%timeleft% 秒）"),
    PET_STATUS_DEAD("§c恢复中 §7（%timeleft% 秒）"),
    PET_STATS("§6✦ 宠物信息 ✦\n§7状态：%status%\n§7等级：§6%levelname%\n \n§c❤ §f%health%§7/§f%maxhealth%\n§7生命恢复：%regeneration% ❤/秒\n§7伤害加成：§f%damagemodifier%%\n§7伤害抵抗：§f%resistancemodifier%%\n§7战斗力量：§f%power%%\n \n§7经验：§a%experience%/%threshold%\n%progressbar%"),
    PET_STATS_EVOLUTION_ALREADY_OWNED("§7已拥有进化形态。"),
    PET_STATS_MAX_LEVEL("§6已达到最高等级。"),
    MAX_ACTIVE_PETS_REACHED("§c同时召唤的宠物数量已达上限。"),
    PET_REPLACED_BY_NEW("§e已用 %newpet% 替换 %oldpet%。"),
    DEBUGGER_JOINING("§a已开启宠物调试信息。"),
    DEBUGGER_LEAVE("§7已关闭宠物调试信息。");

    private String message;

    Language(String message) {
        this.message = message;
    }

    public void reload() {
        if (LanguageConfig.getInstance().getMap().containsKey(name().toLowerCase())) {
            message = LanguageConfig.getInstance().getMap().get(name().toLowerCase());
        }
    }

    public String getMessage() {
        // PlaceholderAPI expansion is recipient-specific and is applied in sendMessage(),
        // never here with a fake offline player.
        return message;
    }

    /**
     * Raw text for legacy callers. PlaceholderAPI placeholders are intentionally not
     * expanded without a player context.
     */
    public String getMessagePAPI() {
        return message;
    }

    public Component getComponent() {
        return Utils.toComponent(getMessage());
    }

    public Component getComponentWithPrefix() {
        return Utils.toComponentWithPrefix(getMessage());
    }

    public void sendMessage(Player p) {
        if (message.isEmpty()) return;
        p.sendMessage(Utils.toComponentWithPrefix(Utils.applyPlaceholders(p.getUniqueId(), message)));
    }

    public void sendMessage(CommandSender sender) {
        if (message.isEmpty()) return;
        if (sender instanceof Player p) {
            sendMessage(p);
            return;
        }
        sender.sendMessage(getComponentWithPrefix());
    }

    public void sendMessageFormatted(CommandSender sender, FormatArg... args) {
        if (message.isEmpty()) return;

        String formatted = getMessageFormatted(args);
        if (sender instanceof Player p)
            formatted = Utils.applyPlaceholders(p.getUniqueId(), formatted);
        sender.sendMessage(Utils.toComponentWithPrefix(formatted));
    }

    public String getMessageFormatted(FormatArg... args) {
        String toSend = getMessage();
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }

        return toSend;
    }

    public Component getComponentFormatted(FormatArg... args) {
        return Utils.toComponent(getMessageFormatted(args));
    }

}
