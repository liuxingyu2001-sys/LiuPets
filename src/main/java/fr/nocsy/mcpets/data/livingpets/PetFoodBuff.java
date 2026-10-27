package fr.nocsy.mcpets.data.livingpets;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import lombok.Getter;

import org.bukkit.Bukkit;

import org.jetbrains.annotations.NotNull;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.utils.debug.Debugger;

public class PetFoodBuff {

    public static Map<Pet, List<PetFoodBuff>> runningBuffs = new java.util.concurrent.ConcurrentHashMap<>();

    @Getter
    private PetFoodType type;
    @Getter
    private float power;
    @Getter
    private Pet pet;
    @Getter
    private PetMath operator;

    @Getter
    private long duration;

    public PetFoodBuff(@NotNull Pet pet, @NotNull PetFoodType type, float power, PetMath operator, long duration) {
        this.pet = pet;
        this.type = type;
        this.power = power;
        this.duration = duration;
        this.operator = operator;
    }

    public boolean apply() {
        if (pet.getPetStats() == null) {
            Debugger.send("§c无法对该宠物触发 Buff：它没有养成属性。");
            return false;
        }

        for (PetFoodBuff buff : new ArrayList<>(getBuffs(pet))) {
            if (buff.getType() != this.getType()) continue;
            buff.stop();
        }

        runTask();
        return true;
    }

    private void runTask() {
        // computeIfAbsent: getBuffs() intentionally returns an immutable empty list,
        // so buffs.add() used to throw UnsupportedOperationException on the first buff.
        final List<PetFoodBuff> buffs = runningBuffs.computeIfAbsent(pet, k -> new ArrayList<>());
        buffs.add(this);

        Debugger.send("§7正在对 §6" + pet.getId() + "§7 施加 Buff §a" + type.name() + "§7，持续 §a" + duration + "§7 tick。");
        Debugger.send("§7Buff 信息：" +
                "  \n宠物主人：§b" + pet.getOwner() +
                "  \n§a类型：§7" + type.name() +
                "  \n§a强度：§7" + power +
                "  \n§a运算方式：§7" + operator.name() +
                "  \n§a持续时间：§7" + duration + "§7 tick。");

        PetFoodBuff instance = this;

        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), instance::stop, duration);
    }

    public void stop() {
        final List<PetFoodBuff> buffs = runningBuffs.get(pet);
        if (buffs == null) return;

        buffs.remove(this);
        if (buffs.isEmpty()) runningBuffs.remove(pet);
        Debugger.send("§7Buff §a" + type.name() + "§7 已在 §6" + pet.getId() + "§7 上 §c过期§7，持续 §a" + duration + "§7 tick。");
    }

    public static List<PetFoodBuff> getBuffs(Pet pet) {
        return runningBuffs.getOrDefault(pet, List.of());
    }

    /**
     * Drop all running buff references (plugin disable / reload).
     */
    public static void clearAll() {
        runningBuffs.clear();
    }

}
