package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({Entity.class})
public class EntityEvents<T extends Entity> implements EntityAccess<T> {
   @ModifyExpressionValue(
      method = {"updateVelocity"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"
      )}
   )
   private Vec3d onModifyVelocity(Vec3d original) {
      if (!this.checkClientPlayer()) {
         return original;
      } else {
         Event<Vec3d> vec3d = new Event<>(original, true, true);
         Listener.aC().catchEvent(vec3d);
         return vec3d.d() ? Vec3d.ZERO : vec3d.e();
      }
   }

   @Shadow
   protected abstract void method_5729(int var1, boolean var2);

   @Shadow
   protected abstract boolean method_5795(int var1);

   @Shadow
   protected abstract void method_5623(double var1, boolean var3, BlockState var4, BlockPos var5);

   @Shadow
   public ActionResult method_5688(PlayerEntity var1, Hand var2) { }

   @Unique
   @Override
   public void setDataFlag(int index, boolean val) {
      this.method_5729(index, val);
   }

   @Unique
   @Override
   public boolean getDataFlag(int index) {
      return this.method_5795(index);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   public void onEntityTickUpdate(CallbackInfo ci) {
      Entity entity = (Entity)(Object)this;
      Listener.aP().broadcast(entity);
   }

   @Inject(
      method = {"onDataTrackerUpdate"},
      at = {@At("HEAD")}
   )
   public void onEntityDataUpdate(List<SerializedEntry<?>> dataEntries, CallbackInfo ci) {
      Entity entity = (Entity)(Object)this;
      Listener.aR().h(entity, new Object[]{dataEntries});
   }

   @Inject(
      method = {"setRemoved"},
      at = {@At("RETURN")}
   )
   public void onEntityRemoved(RemovalReason reason, CallbackInfo ci) {
      Listener.aU().h((Entity)(Object)this, new Object[]{reason});
   }

   @WrapOperation(
      method = {"updateMovementInFluid"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
         ordinal = 1
      )}
   )
   public Vec3d onEntityUpdateVelocity(Vec3d instance, Vec3d vec, Operation<Vec3d> original, @Local(argsOnly = true) TagKey<Fluid> tagKey) {
      if (this.checkClientPlayer()) {
         Event<Vec3d> eventVec3d = new Event<>(vec, true, true, tagKey);
         Listener.az().catchEvent(eventVec3d);
         return eventVec3d.d() ? instance : (Vec3d)original.call(new Object[]{instance, eventVec3d.b});
      } else {
         return (Vec3d)original.call(new Object[]{instance, vec});
      }
   }
}
