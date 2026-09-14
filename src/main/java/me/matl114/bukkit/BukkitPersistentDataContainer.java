package me.matl114.bukkit;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class BukkitPersistentDataContainer {
    public Map<String, NbtElement> container = new HashMap<>();

    public NbtCompound toCompound() {
        NbtCompound var1 = new NbtCompound();

        for (String var3 : this.container.keySet()) {
            var1.put(var3, this.container.get(var3));
        }

        return var1;
    }

    public void a(Map<String, NbtElement> container) {
        this.container.putAll(container);
    }

    public void putData(NbtCompound compound) {
        for (String var3 : compound.getKeys()) {
            this.container.put(var3, compound.get(var3));
        }
    }
}
