package me.matl114.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;

public class KalamaHelperHelperB {
    public static final MinecraftClient a = MinecraftClient.getInstance();

    public static double g(PlayerEntity player, Entity target, ItemStack stack) {
        ItemEnchantmentsComponent var3 = (ItemEnchantmentsComponent) stack.get(DataComponentTypes.ENCHANTMENTS);
        float var4 = 0.0F;
        if (var3 != null && !var3.isEmpty()) {
            for (Entry var6 : var3.getEnchantmentEntries()) {
                RegistryEntry var7 = (RegistryEntry) var6.getKey();
                int var8 = var6.getIntValue();
                if (var7.matchesKey(Enchantments.SHARPNESS)) {
                    var4 += 1.0F + (var8 - 1) * 0.5F;
                } else if (var7.matchesKey(Enchantments.SMITE)
                        && target.getType().isIn(EntityTypeTags.SENSITIVE_TO_SMITE)) {
                    var4 += 2.5F * var8;
                } else if (var7.matchesKey(Enchantments.BANE_OF_ARTHROPODS)
                        && target.getType().isIn(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)) {
                    var4 += 2.5F * var8;
                } else if (var7.matchesKey(Enchantments.IMPALING)
                        && target.getType().isIn(EntityTypeTags.SENSITIVE_TO_IMPALING)) {
                    var4 += 2.5F * var8;
                }
            }
        }

        return var4;
    }

    public static float m(KalamaHelperHelperX context, float amount) {
        if (context != null && !(amount <= 0.0F)) {
            DamageSource var2 = context.l();
            if (var2 == null || !var2.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
                amount = n(context, amount);
            }

            if (var2 != null && var2.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
                return amount;
            } else {
                int var3 = context.q(StatusEffects.RESISTANCE);
                if (var3 >= 0 && (var2 == null || !var2.isIn(DamageTypeTags.BYPASSES_RESISTANCE))) {
                    int var4 = (var3 + 1) * 5;
                    amount = Math.max(amount * (25.0F - var4) / 25.0F, 0.0F);
                }

                if (!(amount <= 0.0F) && (var2 == null || !var2.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS))) {
                    float var5 = context.r();
                    if (var5 > 0.0F) {
                        amount = DamageUtil.getInflictedDamage(amount, var5);
                    }

                    return Math.max(amount, 0.0F);
                } else {
                    return Math.max(amount, 0.0F);
                }
            }
        } else {
            return Math.max(amount, 0.0F);
        }
    }

    public static double c(PlayerEntity player, ItemStack stack, EquipmentSlot slot) {
        return b(EntityAttributes.GENERIC_ARMOR, player, stack, slot);
    }

    public static double f(
            List<net.minecraft.component.type.AttributeModifiersComponent.Entry> modifiers,
            RegistryEntry<EntityAttribute> entityAttribute,
            double base,
            EquipmentSlot slot) {
        double var5 = base;

        for (net.minecraft.component.type.AttributeModifiersComponent.Entry var8 : modifiers) {
            if (var8.slot().matches(slot) && Objects.equals(entityAttribute, var8.attribute())) {
                double var9 = var8.modifier().value();

                var5 += switch (var8.modifier().operation()) {
                    case ADD_VALUE -> var9;
                    case ADD_MULTIPLIED_BASE -> var9 * base;
                    case ADD_MULTIPLIED_TOTAL -> var9 * var5;
                    default -> throw new MatchException((String) null, (Throwable) null);};
            }
        }

        return var5;
    }

    public static boolean a(RegistryKey<DamageType> key, String type) {
        return key != null && Objects.equals(key.getValue().getPath(), type);
    }

    public static float l(PlayerEntity player, DamageSource source, float amount) {
        return m(KalamaHelperHelperX.c(player, source), amount);
    }

    public static double h(ItemStack stack, float height) {
        if (height <= 1.5) {
            return 0.0;
        } else {
            double var2;
            if (height <= 3.0) {
                var2 = 4.0 * height;
            } else if (height <= 8.0) {
                var2 = 12.0 + 2.0 * (height - 3.0);
            } else {
                var2 = 22.0 + (height - 8.0);
            }

            ItemEnchantmentsComponent var4 = (ItemEnchantmentsComponent) stack.get(DataComponentTypes.ENCHANTMENTS);
            if (var4 != null) {
                int var5 = ItemStackUtils.getEnchantmentLevel(var4, Enchantments.DENSITY);
                var2 += var5 * 0.5 * height;
            }

            return var2;
        }
    }

    public static double b(
            RegistryEntry<EntityAttribute> entry, PlayerEntity player, ItemStack stack, EquipmentSlot slot) {
        double var4 = player.getAttributeBaseValue(entry);
        AttributeModifiersComponent var6 =
                (AttributeModifiersComponent) stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (var6 != null && !var6.modifiers().isEmpty()) {
            var4 = f(var6.modifiers(), entry, var4, slot);
        }

        return var4;
    }

    private static float o(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double k(LivingEntity livingEntity, ItemStack stack) {
        return i(a.player, livingEntity, stack);
    }

    private static float n(KalamaHelperHelperX context, float damageAmount) {
        float var2 = context.n();
        float var3 = context.o();
        float var4 = 2.0F + var3 / 4.0F;
        float var5 = o(var2 - damageAmount / var4, var2 * 0.2F, 20.0F);
        float var6 = var5 / 25.0F;
        return damageAmount * (1.0F - var6);
    }

    public static double d(PlayerEntity player, ItemStack stack, EquipmentSlot slot) {
        return b(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, player, stack, slot);
    }

    public static double i(PlayerEntity player, Entity livingEntity, ItemStack stack) {
        double var3 = player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        AttributeModifiersComponent var5 =
                (AttributeModifiersComponent) stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (var5 != null && !var5.modifiers().isEmpty()) {
            var3 = f(var5.modifiers(), EntityAttributes.GENERIC_ATTACK_DAMAGE, var3, EquipmentSlot.MAINHAND);
        }

        return var3 + g(player, livingEntity, stack);
    }

    public static double e(PlayerEntity player, ItemStack stack) {
        double var2 = player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
        AttributeModifiersComponent var4 =
                (AttributeModifiersComponent) stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (var4 != null && !var4.modifiers().isEmpty()) {
            var2 = f(var4.modifiers(), EntityAttributes.GENERIC_ATTACK_SPEED, var2, EquipmentSlot.MAINHAND);
        }

        return var2;
    }

    public static double j(PlayerEntity player, LivingEntity livingEntity, ItemStack stack, float cooldownProgress) {
        return i(player, livingEntity, stack);
    }
}
