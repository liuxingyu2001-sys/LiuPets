package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorConversation {

    private static HashMap<UUID, EditorConversation> conversations = new HashMap<>();

    @Getter
    private Player player;

    @Getter
    private EditorItems editorItem;

    public EditorConversation(Player p, EditorItems editorItem) {
        this.player = p;
        this.editorItem = editorItem;
    }

    public static EditorConversation getConversation(Player p) {
        return conversations.get(p.getUniqueId());
    }

    public void start() {
        conversations.put(player.getUniqueId(), this);
        final String field = EditorText.nameOf(editorItem.getItem().getItemMeta().getDisplayName());
        Language.EDITOR_PROMPT_VALUE.sendMessageFormatted(player, new FormatArg("%field%", field));

        if (editorItem.getType().equals(EditorExpectationType.STRING_LIST)) {
            Language.EDITOR_PROMPT_LIST.sendMessage(player);
        }
        Language.EDITOR_PROMPT_QUIT.sendMessage(player);
    }

    public boolean entryMatch(String entry) {
        return editorItem.getType().matches(entry);
    }

    public Object output(String entry) {
        return editorItem.getType().parse(entry);
    }

    public void quit() {
        conversations.remove(player.getUniqueId());
        Language.EDITOR_CANCELLED.sendMessage(player);
    }

    public void end() {
        conversations.remove(player.getUniqueId());
        final String field = EditorText.nameOf(editorItem.getItem().getItemMeta().getDisplayName());
        Language.EDITOR_VALUE_CHANGED.sendMessageFormatted(player, new FormatArg("%field%", field));
        Language.EDITOR_RELOAD_HINT.sendMessage(player);
    }

    /**
     * Clear all active editor conversations.
     * Called on plugin disable to prevent {@link IllegalStateException} (zip file closed)
     * that occurs when a listener tries to load a class from an already unloaded JAR.
     */
    public static void clearAll() {
        for (EditorConversation conversation : conversations.values()) {
            if (conversation.getPlayer() != null && conversation.getPlayer().isOnline()) {
                Language.EDITOR_INTERRUPTED.sendMessage(conversation.getPlayer());
            }
        }
        conversations.clear();
    }
}
