package me.matl114.versioned.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.DataVersion;
import me.matl114.versioned.api.VItem;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

public class ItemUtils_v1_21_1 implements VItem {
    private static final Map<ComponentType<?>, Codec<?>> VERSIONED;

    @Override
    public boolean g(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    @Override
    public NbtCompound k(ItemStack tag, WrapperLookup lookup) {
        NbtCompound var3 = this.n(tag, lookup);
        var3.putInt("DataVersion", DataVersion.getDataVersion());
        return var3;
    }

    @Override
    public MutableText l(ItemStack stack) {
        MutableText var2 =
                Text.empty().append(stack.getName()).formatted(stack.getRarity().getFormatting());
        if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            var2.formatted(Formatting.ITALIC);
        }

        return var2;
    }

    @Override
    public Map<ComponentType<?>, Codec<?>> q() {
        return VERSIONED;
    }

    @Override
    public boolean c(ItemStack stack) {
        if (stack.getItem() instanceof MaceItem) {
            return true;
        } else if (stack.getItem() instanceof ToolItem var3) {
            return var3 instanceof AxeItem ? true : !(var3 instanceof MiningToolItem);
        } else {
            return false;
        }
    }

    @Override
    public CustomModelDataComponent createModelData(int cmd) {
        return new CustomModelDataComponent(cmd);
    }

    @Override
    public boolean b(ItemStack stack) {
        Item var2 = stack.getItem();
        if (var2.getRegistryEntry().isIn(ItemTags.SWORDS)) {
            Integer var3 = this.getOptionalViaItemId(stack);
            if (var3 != null && var3 >= 1296) {
                return true;
            }

            Text var4 = stack.getName();
            if (var4 != null) {
                String var5 = var4.getString();
                if (var5.contains("1.21.11") && var5.contains("Spear")) {
                    return true;
                }
            }
        }

        return false;
    }

    static {
        Builder var0 = ImmutableMap.builder();
        var0.put(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                Codec.withAlternative(
                        CustomModelDataComponent.CODEC, RecordCodecBuilder.create(instance -> instance.group(Codec.FLOAT
                                        .listOf()
                                        .optionalFieldOf("floats", List.of())
                                        .forGetter(s -> s.value() == 0 ? List.of() : List.of((float) s.value())))
                                .apply(
                                        instance,
                                        floats -> new CustomModelDataComponent(
                                                floats.isEmpty() ? 0 : (int) ((Float) floats.get(0)).floatValue())))));
        var0.put(
                DataComponentTypes.UNBREAKABLE,
                Codec.withAlternative(
                        UnbreakableComponent.CODEC,
                        Unit.CODEC.xmap(s -> new UnbreakableComponent(true), b -> Unit.INSTANCE)));
        VERSIONED = var0.build();
    }

    @Override
    public Integer i(ItemStack stack) {
        Item var2 = stack.getItem();
        if (var2 instanceof MiningToolItem) {
            return 2;
        } else {
            return !(var2 instanceof SwordItem) && !(var2 instanceof MaceItem) && !(var2 instanceof TridentItem)
                    ? null
                    : 1;
        }
    }

    @Override
    public ItemStack j(NbtCompound tag, WrapperLookup lookup) {
        return tag.isEmpty()
                ? ItemStack.EMPTY
                : (ItemStack) ((Pair) ItemStack.CODEC
                                .decode(lookup.getOps(NbtOps.INSTANCE), tag)
                                .getOrThrow())
                        .getFirst();
    }

    @Override
    public boolean d(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }

    @Override
    public boolean a(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem;
    }

    private NbtCompound toNbt0(ItemStack tag) {
        return tag.isEmpty()
                ? new NbtCompound()
                : (NbtCompound) ItemStack.CODEC
                        .encodeStart(ItemStackUtils.registry().getOps(NbtOps.INSTANCE), tag)
                        .getOrThrow();
    }

    @Override
    public boolean h(ItemStack stack) {
        return stack.contains(DataComponentTypes.FOOD) || stack.getItem() instanceof PotionItem;
    }

    private NbtCompound n(ItemStack tag, WrapperLookup lookup) {
        return tag.isEmpty()
                ? new NbtCompound()
                : (NbtCompound) ItemStack.CODEC
                        .encodeStart(lookup.getOps(NbtOps.INSTANCE), tag)
                        .getOrThrow();
    }

    public Integer getOptionalViaItemId(ItemStack stack) {
        NbtComponent var2 = (NbtComponent) stack.get(DataComponentTypes.CUSTOM_DATA);
        if (var2 != null) {
            NbtCompound var3 = var2.getNbt();
            if (var3 != null
                    && var3.get("VV|original_hashes") instanceof NbtCompound var5
                    && var5.get("id") instanceof NbtInt var6) {
                return var6.intValue();
            }

            if (var3 != null && var3.get("VB|Protocol1_21_11To1_21_9|id") instanceof NbtInt var7) {
                return var7.intValue();
            }
        }

        return null;
    }

    @Override
    public boolean f(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }

    @Override
    public boolean e(ItemStack stack) {
        return !this.c(stack);
    }
}
