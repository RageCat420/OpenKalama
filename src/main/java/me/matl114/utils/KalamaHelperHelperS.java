package me.matl114.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public final class KalamaHelperHelperS {
    public float d;
    public final Map<RegistryKey<Enchantment>, Integer> e = new HashMap<>();

    @Nullable
    public RegistryEntry<DamageType> b;

    @Nullable
    public DamageSource a;

    public float c;
    public final Map<RegistryEntry<StatusEffect>, Integer> f = new HashMap<>();

    public KalamaHelperHelperS g(RegistryEntry<StatusEffect> effect, int amplifier) {
        return this.f(effect, amplifier);
    }

    public KalamaHelperHelperS e(RegistryKey<Enchantment> enchantment, int level) {
        if (enchantment != null && level > 0) {
            this.e.merge(enchantment, level, Integer::sum);
        }

        return this;
    }

    public KalamaHelperHelperX h() {
        return new KalamaHelperHelperX(this);
    }

    public KalamaHelperHelperS() {}

    public KalamaHelperHelperS c(float armor) {
        this.c = armor;
        return this;
    }

    public KalamaHelperHelperS d(float armorToughness) {
        this.d = armorToughness;
        return this;
    }

    public KalamaHelperHelperS b(@Nullable RegistryEntry<DamageType> damageType) {
        this.b = damageType;
        return this;
    }

    public KalamaHelperHelperS(KalamaHelperHelperX context) {
        this.a = context.a;
        this.b = context.b;
        this.c = context.c;
        this.d = context.d;
        this.e.putAll(context.e);
        this.f.putAll(context.f);
    }

    public KalamaHelperHelperS f(RegistryEntry<StatusEffect> effect, int amplifier) {
        if (effect != null && amplifier >= 0) {
            this.f.merge(effect, amplifier, Math::max);
        }

        return this;
    }

    public KalamaHelperHelperS a(@Nullable DamageSource source) {
        this.a = source;
        this.b = source == null ? null : source.getTypeRegistryEntry();
        return this;
    }
}
