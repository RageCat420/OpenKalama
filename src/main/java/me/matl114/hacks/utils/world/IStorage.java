package me.matl114.hacks.utils.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.World;

public class IStorage {
    public boolean dirty = false;
    public final RegistryKey<World> a;
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    public final Map<String, NbtElement> b;

    public RegistryKey<World> getDimension() {
        return this.a;
    }

    public IStorage() {
        this(mc.world.getRegistryKey(), null);
    }

    public static <T> T resultOrNull(NbtElement re, Codec<T> codec, WrapperLookup lookup) {
        DataResult var3 = codec.decode(lookup.getOps(NbtOps.INSTANCE), re);
        return (T) (var3.isSuccess() ? ((Pair) var3.getOrThrow()).getFirst() : null);
    }

    public boolean k() {
        return !this.b.isEmpty();
    }

    public void f(String key, NbtElement value) {
        if (value == null) {
            if (this.b.remove(key) != null) {
                this.dirty = true;
            }
        } else {
            this.b.put(key, value);
            this.dirty = true;
        }
    }

    public static <T> T d(NbtElement re, Codec<T> codec) {
        DataResult var2 = codec.decode(NbtOps.INSTANCE, re);
        return (T) (var2.isSuccess() ? ((Pair) var2.getOrThrow()).getFirst() : null);
    }

    public boolean m() {
        return this.dirty;
    }

    public <T> T c(String key, Codec<T> codec, WrapperLookup lookup) {
        NbtElement var4 = this.get(key);
        return var4 == null ? null : resultOrNull(var4, codec, lookup);
    }

    public IStorage(RegistryKey<World> dimension) {
        this(dimension, null);
    }

    public boolean contains(String key) {
        return this.b.containsKey(key);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public IStorage(RegistryKey<World> dimension, Map<String, NbtElement> storage) {
        this.a = dimension;
        this.b = storage == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(storage);
    }

    public <T> T b(String key, Codec<T> codec) {
        NbtElement var3 = this.get(key);
        return var3 == null ? null : d(var3, codec);
    }

    public boolean j() {
        return this.b.isEmpty();
    }

    public <T> void put(String key, T val, Codec<T> codec, WrapperLookup lookup) {
        if (val == null) {
            this.f(key, null);
        } else {
            this.f(key, (NbtElement)
                    codec.encodeStart(lookup.getOps(NbtOps.INSTANCE), val).getOrThrow());
        }
    }

    public NbtElement get(String key) {
        return this.b.get(key);
    }

    public <T> void g(String key, T val, Codec<T> codec) {
        if (val == null) {
            this.f(key, null);
        } else {
            this.f(key, (NbtElement) codec.encodeStart(NbtOps.INSTANCE, val).getOrThrow());
        }
    }
}
