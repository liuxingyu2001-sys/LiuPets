package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.commands.CCommand;
import fr.nocsy.mcpets.commands.tabcompleters.MCPetsCommandTabCompleter;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MCPetsCommand implements CCommand {

    @Override
    public String getName() {
        return "liupets";
    }

    @Override
    public String getPermission() {
        return PPermission.USE.getPermission();
    }

    @Override
    public TabCompleter getCompleter() {
        return new MCPetsCommandTabCompleter();
    }

    public String getAdminPermission() {
        return PPermission.ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(getPermission())) {

            if (args.length > 0 && args[0].equalsIgnoreCase("unlock")) {
                if (!sender.hasPermission(getAdminPermission())) { Language.NO_PERM.sendMessage(sender); return; }
                if (args.length != 3) { sender.sendMessage("§e/liupets unlock <在线玩家或UUID> <宠物ID>"); return; }
                org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(args[1]);
                java.util.UUID uuid;
                try { uuid = target != null ? target.getUniqueId() : java.util.UUID.fromString(args[1]); }
                catch (IllegalArgumentException ex) { sender.sendMessage("§c玩家不在线，请使用玩家 UUID。"); return; }
                fr.nocsy.mcpets.api.LiuPetsAPI.unlock(uuid, args[2]).whenComplete((result, failure) ->
                    org.bukkit.Bukkit.getScheduler().runTask(fr.nocsy.mcpets.MCPets.getInstance(), () -> {
                        if (failure != null) { sender.sendMessage("§c解锁失败：" + failure.getMessage()); return; }
                        sender.sendMessage("§a已永久解锁宠物：" + args[2]);
                        if (target != null && target.isOnline()) target.sendMessage("§a已获得新宠物！使用 /liupets 查看。");
                    }));
                return;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                Language.USAGE.sendMessage(sender);
                if (sender.hasPermission(getAdminPermission())) sender.sendMessage("§e/liupets reload §7重载\n§e/liupets editor §7配置编辑器\n§e/liupets unlock <玩家或UUID> <宠物ID> §7永久解锁（LuckPerms）");
                return;
            }
            if (sender instanceof Player player && args.length == 1 && args[0].equalsIgnoreCase("list")) {
                player.openInventory(new PetMenu(player, 0).getInventory());
                return;
            }
            if (sender instanceof Player && args.length == 0) {
                fr.nocsy.mcpets.data.inventories.PetHub.open((Player) sender);
                return;
            }

            AArgument[] outcomes = {
                    new ArgumentCategory(sender, args),
                    new ArgumentClearStats(sender, args),
                    new ArgumentDebug(sender, args),
                    new ArgumentItem(sender, args),
                    new ArgumentInventory(sender, args),
                    new ArgumentMount(sender, args),
                    new ArgumentMounts(sender, args),
                    new ArgumentMenu(sender, args),
                    new ArgumentName(sender, args),
                    new ArgumentOpen(sender, args),
                    new ArgumentPetFood(sender, args),
                    new ArgumentReload(sender, args),
                    new ArgumentRevoke(sender, args),
                    new ArgumentSignalStick(sender, args),
                    new ArgumentSpawn(sender, args),
                    new ArgumentEditor(sender, args),
            };

            for(AArgument argument : outcomes) {
                if(argument.conditionsVerified()) {
                    argument.commandEffect();
                    return;
                }
            }

            // Check if the argument name matched but with wrong number of args
            for(AArgument argument : outcomes) {
                if(argument.nameMatchesButIncomplete()) {
                    if (argument.getUsage() != null) {
                        sender.sendMessage("§c参数不完整，用法：§e" + argument.getUsage().replace("/mcpets", "/liupets"));
                    } else {
                        Language.USAGE.sendMessage(sender);
                    }
                    return;
                }
            }

            Language.USAGE.sendMessage(sender);
        }
        else {
            Language.NO_PERM.sendMessage(sender);
        }
    }
}
