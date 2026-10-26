package fr.nocsy.mcpets.data.editor;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorEditing {

    private static HashMap<UUID, EditorEditing> editing = new HashMap<>();

    @Getter
    @Setter
    private String petId;
    @Getter
    @Setter
    private String mappedId;

    @Getter
    private HashMap<Integer, String> editorMapping = new HashMap<>();

    public static EditorEditing get(Player p) {
        if (!editing.containsKey(p.getUniqueId()))
            editing.put(p.getUniqueId(), new EditorEditing());
        return editing.get(p.getUniqueId());
    }

    /** Forget the editing session of one player (quit). */
    public static void clear(java.util.UUID uuid) {
        editing.remove(uuid);
    }

    /** Forget every editing session (plugin disable / reload). */
    public static void clearAll() {
        editing.clear();
    }
}
