package me.matl114.utils;

import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import org.jetbrains.annotations.Nullable;

public final class KalamaHelperHelperX {
   public final float d;
   public final Map<RegistryKey<Enchantment>, Integer> e;
   @Nullable
   public final RegistryEntry<DamageType> b;
   @Nullable
   public final DamageSource a;
   public final float c;
   public final Map<RegistryEntry<StatusEffect>, Integer> f;

   @Nullable
   public RegistryEntry<DamageType> m() {
      return this.b;
   }

   public KalamaHelperHelperX e(@Nullable RegistryEntry<DamageType> damageType) {
      return k(this).b(damageType).h();
   }

   public static KalamaHelperHelperS a() {
      return new KalamaHelperHelperS();
   }

   public KalamaHelperHelperX h(RegistryKey<Enchantment> enchantment, int level) {
      return k(this).e(enchantment, level).h();
   }

   public KalamaHelperHelperX(KalamaHelperHelperS builder) {
      this.a = builder.a;
      this.b = builder.b;
      this.c = builder.c;
      this.d = builder.d;
      this.e = Map.copyOf(builder.e);
      this.f = Map.copyOf(builder.f);
   }

   public KalamaHelperHelperX i(RegistryEntry<StatusEffect> effect, int amplifier) {
      return k(this).f(effect, amplifier).h();
   }

   public float r() {
      DamageSource var1 = this.a;
      float var2 = this.p(Enchantments.PROTECTION);
      if (var1 == null) {
         return var2;
      } else if (var1.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return 0.0F;
      } else {
         boolean var3 = var1.isIn(DamageTypeTags.IS_EXPLOSION);
         boolean var4 = var1.isIn(DamageTypeTags.IS_FIRE);
         boolean var5 = var1.isIn(DamageTypeTags.IS_PROJECTILE);
         boolean var6 = var1.isIn(DamageTypeTags.IS_FALL);
         if (var3) {
            var2 += this.p(Enchantments.BLAST_PROTECTION) * 2.0F;
         }

         if (var4) {
            var2 += this.p(Enchantments.FIRE_PROTECTION) * 2.0F;
         }

         if (var5) {
            var2 += this.p(Enchantments.PROJECTILE_PROTECTION) * 2.0F;
         }

         if (var6) {
            var2 += this.p(Enchantments.FEATHER_FALLING) * 3.0F;
         }

         return var2;
      }
   }

   @Nullable
   public DamageSource l() {
      return this.a;
   }

   public float n() {
      return this.c;
   }

   public static KalamaHelperHelperX c(@Nullable PlayerEntity player, @Nullable DamageSource source) {
      KalamaHelperHelperS var2 = b(source);
      if (player == null) {
         return var2.h();
      } else {
         var2.c(player.getArmor());
         var2.d((float)player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));

         for (EquipmentSlot var6 : EquipmentSlot.values()) {
            ItemStack var7 = player.getEquippedStack(var6);
            if (var7 != null && !var7.isEmpty()) {
               ItemEnchantmentsComponent var8 = (ItemEnchantmentsComponent)var7.get(DataComponentTypes.ENCHANTMENTS);
               if (var8 != null && !var8.isEmpty()) {
                  var2.e(Enchantments.PROTECTION, ItemStackUtils.getEnchantmentLevel(var8, Enchantments.PROTECTION));
                  var2.e(Enchantments.BLAST_PROTECTION, ItemStackUtils.getEnchantmentLevel(var8, Enchantments.BLAST_PROTECTION));
                  var2.e(Enchantments.FIRE_PROTECTION, ItemStackUtils.getEnchantmentLevel(var8, Enchantments.FIRE_PROTECTION));
                  var2.e(Enchantments.PROJECTILE_PROTECTION, ItemStackUtils.getEnchantmentLevel(var8, Enchantments.PROJECTILE_PROTECTION));
                  var2.e(Enchantments.FEATHER_FALLING, ItemStackUtils.getEnchantmentLevel(var8, Enchantments.FEATHER_FALLING));
               }
            }
         }

         if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            var2.f(StatusEffects.RESISTANCE, player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier());
         }

         return var2.h();
      }
   }

   public static KalamaHelperHelperS b(@Nullable DamageSource source) {
      return new KalamaHelperHelperS().a(source);
   }

   public KalamaHelperHelperX g(float armorToughness) {
      return k(this).d(armorToughness).h();
   }

   public KalamaHelperHelperX f(float armor) {
      return k(this).c(armor).h();
   }

   public KalamaHelperHelperX d(@Nullable DamageSource source) {
      return k(this).a(source).h();
   }

   public static KalamaHelperHelperS k(KalamaHelperHelperX context) {
      return new KalamaHelperHelperS(context);
   }

   public float o() {
      return this.d;
   }

   public KalamaHelperHelperX j(RegistryEntry<StatusEffect> effect, int amplifier) {
      return this.i(effect, amplifier);
   }

   public int p(RegistryKey<Enchantment> enchantment) {
      return this.e.getOrDefault(enchantment, 0);
   }

   public int q(RegistryEntry<StatusEffect> effect) {
      return this.f.getOrDefault(effect, -1);
   }
}
