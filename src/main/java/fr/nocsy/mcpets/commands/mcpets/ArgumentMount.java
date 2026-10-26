package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.listeners.PetInteractionMenuListener;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentMount extends AArgument {

    public ArgumentMount(CommandSender sender, String[] args) {
        super("mount", new int[]{1}, sender, args);
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player;
    }

    @Override
    public void commandEffect() {
        Player p = (Player) sender;
        java.util.List<Pet> mounts = Pet.getActivePetsForOwner(p.getUniqueId()).stream()
                .filter(Pet::isMountable)
                .toList();
        if (mounts.isEmpty()) {
            Language.NO_ACTIVE_PET.sendMessage(p);
            return;
        }
        if (mounts.size() > 1) {
            Language.SPECIFY_PET.sendMessageFormatted(p, new fr.nocsy.mcpets.data.config.FormatArg("%pets%",
                    mounts.stream().map(Pet::getId).collect(java.util.stream.Collectors.joining(", "))));
            return;
        }

        PetInteractionMenuListener.mount(p, mounts.getFirst());
    }
}
