package me.matl114.versioned.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import me.matl114.versioned.impl.ItemUtils_v1_21_1;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.util.Unit;

public interface VItem {
    Codec<ComponentChanges> b = Codec.dispatchedMap(KalamaHelperHelperH.CODEC, KalamaHelperHelperH::getValueCodec)
            .xmap(
                    changes -> {
                        if (changes.isEmpty()) {
                            return ComponentChanges.EMPTY;
                        } else {
                            Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(changes.size());

                            for (Entry var3 : changes.entrySet()) {
                                KalamaHelperHelperH var4 = (KalamaHelperHelperH) var3.getKey();
                                if (var4.AR() != null) {
                                    if (var4.removed()) {
                                        var1.put(var4.AR(), Optional.empty());
                                    } else {
                                        var1.put(var4.AR(), Optional.of(var3.getValue()));
                                    }
                                }
                            }

                            return new ComponentChanges(var1);
                        }
                    },
                    changes -> {
                        Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(changes.size());

                        for (Entry var3 : changes.entrySet()) {
                            ComponentType var4 = (ComponentType) var3.getKey();
                            if (!var4.shouldSkipSerialization()) {
                                Optional var5 = (Optional) var3.getValue();
                                if (var5.isPresent()) {
                                    var1.put(new KalamaHelperHelperH(var4, false), var5.get());
                                } else {
                                    var1.put(new KalamaHelperHelperH(var4, true), Unit.INSTANCE);
                                }
                            }
                        }

                        return var1;
                    });
    Codec<RegistryEntry<Item>> c = Registries.ITEM
            .getEntryCodec()
            .validate(entry -> entry.matches(Items.AIR.getRegistryEntry())
                    ? DataResult.error(() -> "Item must not be minecraft:air")
                    : DataResult.success(entry));
    MapCodec<ItemStack> d = MapCodec.recursive(
            "ItemStack",
            codec -> RecordCodecBuilder.mapCodec(instance -> instance.group(
                            VItem.c.fieldOf("id").forGetter(ItemStack::getRegistryEntry),
                            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                            VItem.b
                                    .optionalFieldOf("components", ComponentChanges.EMPTY)
                                    .forGetter(stack -> stack.components.getChanges()))
                    .apply(instance, ItemStack::new)));
    Codec<ItemStack> e = Codec.lazyInitialized(VItem.d::codec);
    VItem INSTANCE = new ItemUtils_v1_21_1();

    boolean g(ItemStack var1);

    NbtCompound k(ItemStack var1, WrapperLookup var2);

    MutableText l(ItemStack var1);

    boolean c(ItemStack var1);

    default Codec<ItemStack> x() {
        return e;
    }

    boolean b(ItemStack var1);

    Integer i(ItemStack var1);

    ItemStack j(NbtCompound var1, WrapperLookup var2);

    boolean d(ItemStack var1);

    boolean a(ItemStack var1);

    CustomModelDataComponent createModelData(int var1);

    boolean h(ItemStack var1);

    static VItem w() {
        return INSTANCE;
    }

    Map<ComponentType<?>, Codec<?>> q();

    boolean f(ItemStack var1);

    boolean e(ItemStack var1);
}
