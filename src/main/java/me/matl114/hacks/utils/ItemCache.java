package me.matl114.hacks.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import javax.annotation.Nonnull;
import me.matl114.events.Listener;
import me.matl114.events.channels.EventChannel;
import me.matl114.managers.KalamaHelperHelperG;
import me.matl114.managers.config.ConfigLoader;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.codecs.NullCodec;
import me.matl114.utils.itemdb.ItemStackData;
import me.matl114.utils.itemdb.KalamaHelperHelperA;
import me.matl114.utils.itemdb.KalamaHelperHelperD;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemCache {
    ReentrantLock h;
    public static Codec<Map<String, ItemStackData>> k = Codec.unboundedMap(Codec.STRING, ItemStackData.dt);
    String a;
    public final EventChannel<Void> n;
    public final EventChannel<Void> m;
    Gson j;
    public static final String l = "customitems:";
    Map<ItemStackData, Pair<String, ItemStackData>> g;
    boolean i;
    Set<String> e;
    public final EventChannel<Void> o;
    volatile boolean b;
    Map<String, ItemStackData> f;
    boolean d;
    private volatile boolean c = false;

    public void j() {
        this.d = true;
    }

    public void load() {
        this.c = true;

        try {
            this.d();
            Debug.a("Start loading item cache");
            long var1 = System.currentTimeMillis();
            this.h.lock();

            try {
                if (!this.b) {
                    JsonObject var4;
                    try {
                        String var3 = ConfigLoader.loadExternalJson(this.a);
                        var4 = (JsonObject) (Object) this.j.fromJson(var3, JsonObject.class);
                    } catch (Throwable var16) {
                        Debug.a("Error while loading ItemDatabase");
                        Debug.f(var16);
                        this.b = false;
                        return;
                    }

                    this.f = new LinkedHashMap<>();
                    this.f = new LinkedHashMap<>((Map<? extends String, ? extends ItemStackData>)
                            ((Pair) k.decode(JsonOps.INSTANCE, var4).getOrThrow()).getFirst());
                    this.d = false;
                    this.g = new HashMap<>();
                    this.f.forEach((key, value) -> this.g.put(value, Pair.of(key, value)));
                    this.e.clear();
                    this.d();
                    Debug.e("Finish data loading of item cache, using", System.currentTimeMillis() - var1, "ms");

                    try {
                        this.m.broadcast(null);
                    } catch (Throwable var15) {
                        Debug.a("Error while loading ItemDatabase");
                        Debug.f(var15);
                    }

                    this.b = true;
                }
            } finally {
                this.h.unlock();
            }
        } finally {
            this.c = false;
        }
    }

    public Codec<ItemStackData> createStackDataCodec() {
        return Codec.STRING.xmap(this::getDataFromCodecId, this::q);
    }

    private Pair<String, ItemStackData> m(ItemStackData stackData) {
        int var2 = stackData.hashCode();
        String var3 = Integer.toHexString(var2);
        String var4 = "customitems:" + var3;
        String var5 = var4;
        int var6 = 0;

        while (this.f.containsKey(var5)) {
            var5 = var4 + "_" + ++var6;
        }

        return this.putInternal(var5, stackData);
    }

    @Nonnull
    public ItemStack getFromCodecId(String id) {
        if (!id.startsWith("customitems:")) {
            return new ItemStack((ItemConvertible) Registries.ITEM.get(Identifier.tryParse(id)));
        } else {
            ItemStackData var2 = this.i(id);
            return var2 != null && var2.isValid() ? var2.ge().copy() : ItemStackData.Iy;
        }
    }

    private void l(String s) {
        this.d = true;
        ItemStackData var2 = this.f.remove(s);
        if (var2 != null) {
            this.g.remove(var2);
        }
    }

    @Nonnull
    public String getOrRegisterCodecId(ItemStack item) {
        if (ItemStackUtils.hasInPatch(item)) {
            return (String) (Object) this.getOrRegisterItem(item).getFirst();
        } else {
            Identifier var2 = Registries.ITEM.getId(item.getItem());
            return var2 == null ? "minecraft:air" : var2.toString();
        }
    }

    public ItemCache(String saveJsonFile) {
        this.d = false;
        this.e = new LinkedHashSet<>();
        this.f = new LinkedHashMap<>();
        this.g = new HashMap<>();
        this.h = new ReentrantLock();
        this.i = false;
        this.j = new GsonBuilder().disableHtmlEscaping().create();
        this.m = new EventChannel<>();
        this.n = new EventChannel<>();
        this.o = new EventChannel<>();
        this.a = saveJsonFile;
        KalamaHelperHelperG.b(
                () -> {
                    if (this.b) {
                        this.save();
                    }
                },
                60000L,
                300000L);
        Listener.O().k(ev -> {
            if (this.b) {
                this.e();
            }
        });
        Listener.M().k(ev -> {
            if (this.b) {
                this.e();
            }

            KalamaHelperHelperG.c(this::b, 1000L);
        });
    }

    public EventChannel<Void> A() {
        return this.o;
    }

    public void f() {
        Iterator var1 = this.f.entrySet().iterator();

        while (var1.hasNext()) {
            Entry var2 = (Entry) var1.next();
            if (!this.e.contains(var2.getKey())) {
                this.d = true;
                var1.remove();
            }
        }
    }

    private Pair<String, ItemStackData> putInternal(String s, ItemStackData item) {
        this.d = true;
        this.f.put(s, item);
        Pair var3 = Pair.of(s, item);
        this.g.put(item, var3);
        this.h(s);
        return var3;
    }

    public String getItemIdOrNull(ItemStack stack) {
        if (ItemStackUtils.hasInPatch(stack)) {
            Pair var3 = this.g.get(ItemStackData.wrapRaw(stack));
            if (var3 == null) {
                return null;
            } else {
                this.h((String) var3.getFirst());
                return (String) var3.getFirst();
            }
        } else {
            Identifier var2 = Registries.ITEM.getId(stack.getItem());
            return var2 == null ? "minecraft:air" : var2.toString();
        }
    }

    public Codec<ItemStack> createStackCodec() {
        Codec<ItemStack> var1 = this.v();
        Codec<ItemStack> var2 = RecordCodecBuilder.create(instance -> instance.group(
                        var1.fieldOf("typeid").forGetter(Function.identity()),
                        Codec.INT.fieldOf("amount").forGetter(ItemStack::getCount))
                .apply(instance, ItemStack::copyWithCount));
        return new NullCodec<>(var2, ItemStack::isEmpty, ItemStack.EMPTY);
    }

    public EventChannel<Void> z() {
        return this.n;
    }

    public boolean x() {
        return this.b;
    }

    private synchronized ItemCache b() {
        if (this.b) {
            return this;
        } else {
            this.load();
            return this;
        }
    }

    public Pair<String, ItemStackData> getOrRegisterItem(ItemStack stack) {
        ItemStackData var2 = ItemStackData.wrapRaw(stack);
        Pair var3 = this.g.get(var2);
        if (var3 == null) {
            var2 = ItemStackData.wrapCopy(stack);
            return this.m(var2);
        } else {
            this.h((String) var3.getFirst());
            return var3;
        }
    }

    public void e() {
        try {
            if (this.b) {
                this.h.lock();

                try {
                    try {
                        this.o.broadcast(null);
                    } catch (Throwable var18) {
                        Debug.a("Error while unloading ItemDatabase");
                        Debug.f(var18);
                    }

                    if (this.i) {
                        this.f();
                    }
                } finally {
                    this.h.unlock();
                }
            }

            this.save();
            this.h.lock();

            try {
                this.f = new LinkedHashMap<>();
                this.g = new HashMap<>();
                this.b = false;
            } finally {
                this.h.unlock();
            }
        } finally {
            this.b = false;
        }
    }

    public boolean a() {
        if (this.b) {
            return true;
        } else if (this.c) {
            return false;
        } else {
            try {
                CompletableFuture.runAsync(this::b);
                return false;
            } catch (Exception var2) {
                return false;
            }
        }
    }

    public void save() {
        this.h.lock();

        try {
            try {
                this.n.broadcast(null);
            } catch (Throwable var5) {
                Debug.a("Error while saving ItemDatabase");
                Debug.f(var5);
            }

            if (this.d) {
                this.d = false;
                JsonElement var1 =
                        (JsonElement) k.encodeStart(JsonOps.INSTANCE, this.f).getOrThrow();
                CompletableFuture.runAsync(() -> {
                    String var2 = this.j.toJson(var1);

                    try {
                        ConfigLoader.saveToFile(this.a, var2);
                    } catch (IOException var4) {
                        Debug.f(var4);
                    }
                });
            }
        } finally {
            this.h.unlock();
        }
    }

    public void h(String id) {
        this.e.add(id);
    }

    @Nonnull
    public ItemStackData getDataFromCodecId(String id) {
        if (id.startsWith("customitems:")) {
            ItemStackData var2 = this.i(id);
            return (ItemStackData) (var2 != null ? var2 : new KalamaHelperHelperA(id));
        } else {
            return ItemStackData.wrapRaw(new ItemStack((ItemConvertible) Registries.ITEM.get(Identifier.tryParse(id))));
        }
    }

    public EventChannel<Void> y() {
        return this.m;
    }

    @Nonnull
    public String q(ItemStackData item) {
        if (item instanceof KalamaHelperHelperA var2) {
            return var2.gg();
        } else if (ItemStackUtils.hasInPatch(item.gf())) {
            return (String) (Object) this.n(item).getFirst();
        } else {
            Identifier var3 = Registries.ITEM.getId(item.gf().getItem());
            return var3 == null ? "minecraft:air" : var3.toString();
        }
    }

    private void d() {
        try {
            ItemStackUtils.registry();
        } catch (Throwable var2) {
            throw new IllegalStateException("Illegal access to registry!", var2);
        }
    }

    public Pair<String, ItemStackData> n(ItemStackData stackData) {
        Pair var2 = this.g.get(stackData);
        if (var2 == null) {
            return this.m((ItemStackData)
                    (stackData instanceof KalamaHelperHelperD var3 ? var3 : ItemStackData.wrapCopy(stackData.ge())));
        } else {
            this.h((String) var2.getFirst());
            return var2;
        }
    }

    private ItemStackData i(String id) {
        this.h(id);
        return this.f.get(id);
    }

    public Codec<ItemStack> v() {
        return Codec.STRING.xmap(this::getFromCodecId, this::getOrRegisterCodecId);
    }
}
