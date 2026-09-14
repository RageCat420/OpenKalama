package me.matl114.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.accessors.access.LivingEntityAccess;
import me.matl114.hooks.ViaFabricPlusHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;

public class AttributeUtils {
   public static List<RegistryEntry<EntityAttribute>> ATTRIBUTES_1_21_1 = List.of(
      EntityAttributes.GENERIC_MOVEMENT_EFFICIENCY,
      EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY,
      EntityAttributes.PLAYER_MINING_EFFICIENCY,
      EntityAttributes.PLAYER_SNEAKING_SPEED,
      EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED,
      EntityAttributes.GENERIC_ATTACK_KNOCKBACK
   );

   private static int getEquipmentLevel(RegistryKey<Enchantment> enchantment, Map<EquipmentSlot, ItemStack> equipmentOverrides) {
      Reference var2 = ItemStackUtils.registry().getOptional(enchantment.getRegistryRef()).flatMap(s -> s.getEntry(enchantment)).orElseThrow();
      int var3 = 0;

      for (Entry var5 : equipmentOverrides.entrySet()) {
         if (((Enchantment)var2.value()).slotMatches((EquipmentSlot)var5.getKey())) {
            ItemStack var6 = (ItemStack)var5.getValue();
            int var7 = EnchantmentHelper.getLevel(var2, var6);
            if (var7 > var3) {
               var3 = var7;
            }
         }
      }

      return var3;
   }

   public static void overrideViaAttributes(Map<EquipmentSlot, ItemStack> equipmentMap, AttributeContainer container) {
      setAttributeVia(container, EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, getEquipmentLevel(Enchantments.DEPTH_STRIDER, equipmentMap) / 3.0);
      int var2 = getEquipmentLevel(Enchantments.EFFICIENCY, equipmentMap);
      setAttributeVia(container, EntityAttributes.PLAYER_MINING_EFFICIENCY, var2 > 0 ? var2 * var2 + 1.0 : 0.0);
      setAttributeVia(container, EntityAttributes.PLAYER_SNEAKING_SPEED, 0.3 + getEquipmentLevel(Enchantments.SWIFT_SNEAK, equipmentMap) * 0.15);
      setAttributeVia(container, EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED, getEquipmentLevel(Enchantments.AQUA_AFFINITY, equipmentMap) <= 0 ? 0.2 : 1.0);
      setAttributeVia(container, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, getEquipmentLevel(Enchantments.KNOCKBACK, equipmentMap));
   }

   public static void updateAttribute(LivingEntity living) {
      LivingEntityAccess.of(living).updateEquipmentAttributeChange();
   }

   public static AttributeContainer getAttributeWith(LivingEntity living, Map<EquipmentSlot, ItemStack> equipmentOverrides) {
      LinkedHashMap var2 = new LinkedHashMap();

      for (Entry var4 : equipmentOverrides.entrySet()) {
         ItemStack var5 = living.getEquippedStack((EquipmentSlot)var4.getKey());
         if (!ItemStack.areItemsAndComponentsEqual((ItemStack)var4.getValue(), var5)) {
            var2.put((EquipmentSlot)var4.getKey(), (ItemStack)var4.getValue());
         }
      }

      AttributeContainer var9 = new AttributeContainer(DefaultAttributeRegistry.get(living.getType()));
      var9.setFrom(living.getAttributes());

      for (Entry var12 : ((java.util.Set<Entry>)(var2).entrySet())) {
         EquipmentSlot var6 = (EquipmentSlot)var12.getKey();
         ItemStack var7 = (ItemStack)var12.getValue();
         ItemStack var8 = living.getEquippedStack(var6);
         if (!var8.isEmpty()) {
            var8.applyAttributeModifiers(var6, (attribute, modifier) -> {
               EntityAttributeInstance var3 = var9.getCustomInstance(attribute);
               if (var3 != null) {
                  var3.removeModifier(modifier);
               }
            });
         }

         if (!var7.isEmpty() && (!var7.isDamageable() || var7.getDamage() < var7.getMaxDamage())) {
            var7.applyAttributeModifiers(var6, (attribute, modifier) -> {
               EntityAttributeInstance var3 = var9.getCustomInstance(attribute);
               if (var3 != null) {
                  var3.removeModifier(modifier.id());
                  var3.addTemporaryModifier(modifier);
               }
            });
         }
      }

      if (ViaFabricPlusHooks.getInstance().getCurrentVersion().c(20, 8)) {
         HashMap var11 = new HashMap(var2);

         for (EquipmentSlot var16 : EquipmentSlot.values()) {
            if (!var11.containsKey(var16)) {
               var11.put(var16, living.getEquippedStack(var16));
            }
         }

         overrideViaAttributes(var11, var9);
      }

      return var9;
   }

   public static Map<EquipmentSlot, ItemStack> getEquipmentChanges() {
      return null;
   }

   public static double getPlayerBlockInteractionRange(PlayerEntity player) {
      return player.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
   }

   private static void setAttributeVia(AttributeContainer attributeContainer, RegistryEntry<EntityAttribute> attribute, double level) {
      EntityAttributeInstance var4 = attributeContainer.getCustomInstance(attribute);
      var4.clearModifiers();
      var4.setBaseValue(level);
   }
}
