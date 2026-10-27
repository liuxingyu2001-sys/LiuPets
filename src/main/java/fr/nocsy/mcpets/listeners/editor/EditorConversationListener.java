package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.editor.*;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.utils.PetMath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditorConversationListener implements Listener {

    public void syncOpenEditor(Player p, EditorState newState) {
        final UUID uuid = p.getUniqueId();
        // Run it sync otherwise it will not open
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null)
                    return;
                Editor editor = Editor.getEditor(player);
                if (newState != null)
                    editor.setState(newState);
                editor.openEditor();
            }
        }.runTask(MCPets.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listen_conversation(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        EditorConversation conversation = EditorConversation.getConversation(p);

        if (conversation == null)
            return;

        e.setCancelled(true);

        String entry = e.getMessage();

        // If they wanna abandon the process
        if (entry.equalsIgnoreCase("quit")) {
            conversation.quit();
            syncOpenEditor(p, null);
            return;
        }

        // If the output type isn't respected, ask for another one.
        if (!conversation.entryMatch(entry)) {
            if (conversation.getEditorItem().getType().equals(EditorExpectationType.MYTHICMOB)) {
                Language.EDITOR_MYTHICMOB_INVALID.sendMessageFormatted(p, new FormatArg("%value%", entry));
            }
            else if (conversation.getEditorItem().getType().equals(EditorExpectationType.SKILL)) {
                Language.EDITOR_SKILL_INVALID.sendMessageFormatted(p, new FormatArg("%value%", entry));
            }
            else if (conversation.getEditorItem().getType().equals(EditorExpectationType.PET_CREATE)) {
                Language.EDITOR_PET_ALREADY_EXISTS.sendMessageFormatted(p, new FormatArg("%value%", entry));
            }
            else {
                Language.EDITOR_EXPECTED_TYPE.sendMessageFormatted(p,
                        new FormatArg("%type%", EditorText.typeName(conversation.getEditorItem().getType())));
                Language.EDITOR_RETRY.sendMessage(p);
                if (conversation.getEditorItem().getType().equals(EditorExpectationType.ITEM_ID_OR_MATERIAL)) {
                    Language.EDITOR_POSSIBLE_ITEMS.sendMessage(p);
                    p.sendMessage("§7" + ItemsListConfig.getInstance().getItems().keySet());
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.PET_ID) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE)) {
                    Language.EDITOR_POSSIBLE_PETS.sendMessage(p);
                    p.sendMessage("§7" + Pet.getObjectPets().stream().map(Pet::getId).toList());
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.OPERATOR_TYPE) ) {
                    Language.EDITOR_POSSIBLE_OPERATORS.sendMessage(p);
                    p.sendMessage("§7" + Arrays.toString(PetMath.values()));
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.MOUNT_TYPE) ) {
                    Language.EDITOR_POSSIBLE_MOUNT_TYPES.sendMessage(p);
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.ANNOUNCEMENT_TYPE)) {
                    Language.EDITOR_POSSIBLE_ANNOUNCEMENTS.sendMessage(p);
                    p.sendMessage("§7" + Arrays.toString(PetAnnouncement.values()));
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_TYPE)) {
                    Language.EDITOR_POSSIBLE_FOOD_TYPES.sendMessage(p);
                    p.sendMessage("§7" + Arrays.toString(PetFoodType.values()));
                }
            }
            return;
        }

        // This time we have the right value, let's parse it
        Object value = conversation.output(entry);

        // Fetch the edited field and save it
        EditorItems editorItem = conversation.getEditorItem();
        editorItem.setValue(value);
        editorItem.save(p);

        conversation.end();

        // If we just created a pet, we reload MCPets
        if (conversation.getEditorItem().getType().equals(EditorExpectationType.PET_CREATE)) {
            Pet pet = Pet.getFromId(value.toString());
            if (pet == null)
                return;
            EditorEditing editing = EditorEditing.get(p);
            editing.setPetId(pet.getId());
            syncOpenEditor(p, EditorState.PET_EDITOR_EDIT);
            return;
        }

        // Open back the editor
        syncOpenEditor(p, null);
    }
}
