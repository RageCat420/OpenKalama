package me.matl114.mixins.events;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.accessors.access.LivingEntityAccess;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.utils.AttributeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({LivingEntity.class})
public class LivingEntityEvents extends Entity implements EntityAccess<LivingEntity>, LivingEntityAccess<LivingEntity> {
   @Unique
   private Map<EquipmentSlot, ItemStack> clientLastEquipmentSnapshot;
   @Unique
   Integer nextJumpCooldown;
   @Shadow
   private int field_6228;
   @Shadow
   protected int field_6239;
   @Shadow
   @Final
   private AttributeContainer field_6260;

   public LivingEntityEvents(EntityType<?> type, World world) {
      super(type, world);
   }

   @Unique
   @Override
   public Map<EquipmentSlot, ItemStack> getClientLastEquipmentSnapshot() {
      if (this.clientLastEquipmentSnapshot == null) {
         Map<EquipmentSlot, ItemStack> clientLastEquipmentSnapshotMap = new EnumMap<>(EquipmentSlot.class);

         for (EquipmentSlot re : EquipmentSlot.values()) {
            clientLastEquipmentSnapshotMap.put(re, ItemStack.EMPTY);
         }

         this.clientLastEquipmentSnapshot = clientLastEquipmentSnapshotMap;
      }

      return this.clientLastEquipmentSnapshot;
   }

   @Unique
   @Override
   public void tickEquipment() {
      Map<EquipmentSlot, ItemStack> map = this.getClientLastEquipmentSnapshot();

      for (EquipmentSlot re : EquipmentSlot.values()) {
         map.put(re, this.method_6118(re));
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;isRemoved()Z",
         shift = Shift.BEFORE
      )}
   )
   public void onTickEquipment(CallbackInfo ci) {
      if (this instanceof PlayerEntity) {
         this.tickEquipment();
      }
   }

   @Shadow
   public ItemStack method_6118(EquipmentSlot var1) { }

   @Shadow
   public boolean method_45324(ItemStack var1, ItemStack var2) { }

   @Shadow
   public AttributeContainer method_6127() { }

   @WrapOperation(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;jump()V"
      )}
   )
   private void onJump(LivingEntity instance, Operation<Void> original) {
      if (this == MinecraftClient.getInstance().player) {
         Event<Integer> jumpEvent = new Event<>(10, true, true);
         Listener.aw().catchEvent(jumpEvent);
         this.nextJumpCooldown = jumpEvent.e();
         if (!jumpEvent.d()) {
            original.call(new Object[]{instance});
         }
      } else {
         original.call(new Object[]{instance});
      }
   }

   @Inject(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getWorld()Lnet/minecraft/world/World;",
         ordinal = 5,
         shift = Shift.BEFORE
      )}
   )
   private void overrideJumpCooldown(CallbackInfo ci) {
      if (this.nextJumpCooldown != null) {
         this.field_6228 = this.nextJumpCooldown;
         this.nextJumpCooldown = null;
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z",
         shift = Shift.BEFORE
      )}
   )
   private void onWriteFlyingTicks(CallbackInfo ci) {
      if (this.checkClientPlayer()) {
         Event<Integer> fallFlyingEvent = new Event<>(this.field_6239 + 1, true, true);
         Listener.aF().catchEvent(fallFlyingEvent);
         if (fallFlyingEvent.d()) {
            this.field_6239--;
         } else {
            this.field_6239 = fallFlyingEvent.e() - 1;
         }
      }
   }

   @Unique
   @Override
   public final void updateEquipmentAttributeChange() {
      this.getClientLastEquipmentSnapshot();
      Map<EquipmentSlot, ItemStack> map = null;

      for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         ItemStack itemStack = this.clientLastEquipmentSnapshot.get(equipmentSlot);
         ItemStack itemStack2 = this.method_6118(equipmentSlot);
         if (this.method_45324(itemStack, itemStack2)) {
            if (map == null) {
               map = Maps.newEnumMap(EquipmentSlot.class);
            }

            map.put(equipmentSlot, itemStack2);
            AttributeContainer attributeContainer = this.method_6127();
            if (!itemStack.isEmpty()) {
               itemStack.applyAttributeModifiers(equipmentSlot, (attribute, modifier) -> {
                  EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(attribute);
                  if (entityAttributeInstance != null) {
                     entityAttributeInstance.removeModifier(modifier);
                  }

                  EnchantmentHelper.removeLocationBasedEffects(itemStack, (LivingEntity)(Object)this, equipmentSlot);
               });
            }
         }
      }

      if (map != null) {
         for (Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            EquipmentSlot equipmentSlot2 = entry.getKey();
            ItemStack itemStack3 = entry.getValue();
            if (!itemStack3.isEmpty()) {
               itemStack3.applyAttributeModifiers(equipmentSlot2, (registryEntry, entityAttributeModifier) -> {
                  EntityAttributeInstance entityAttributeInstance = this.field_6260.getCustomInstance(registryEntry);
                  if (entityAttributeInstance != null) {
                     entityAttributeInstance.removeModifier(entityAttributeModifier.id());
                     entityAttributeInstance.addTemporaryModifier(entityAttributeModifier);
                  }
               });
            }
         }

         this.tickEquipment();
         if (ViaFabricPlusHooks.getInstance().getCurrentVersion().c(20, 8)) {
            AttributeUtils.overrideViaAttributes(this.getClientLastEquipmentSnapshot(), this.method_6127());
         }
      }
   }

   public void writeCustomDataToNbt(Object arg0) { }

}
