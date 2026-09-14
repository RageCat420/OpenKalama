package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.accessors.hacks.EntityInternalAccess;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.modules.render.NoRender;
import me.matl114.hacks.utils.entity.Predictor;
import me.matl114.hacks.utils.entity.SimpleEntityPredictor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({Entity.class})
public abstract class EntityMixin<T extends Entity> implements EntityAccess<T>, EntityInternalAccess<T> {
   @Unique
   byte renderTracked = 0;
   @Unique
   boolean clientGlowEffect = false;
   @Unique
   Predictor predictorInstance;

   @Unique
   @Override
   public byte renderTrackedLevel() {
      return this.renderTracked;
   }

   @Unique
   @Override
   public void markRenderTracked(byte tracked) {
      this.renderTracked = tracked;
   }

   @Override
   public void setGlow0(boolean glow) {
      this.clientGlowEffect = glow;
   }

   @Inject(
      method = {"isGlowing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onGlowEffect(CallbackInfoReturnable<Boolean> cir) {
      if (this.clientGlowEffect) {
         cir.setReturnValue(true);
      }
   }

   @WrapWithCondition(
      method = {"pushAwayFrom"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"
      )}
   )
   public boolean onEntityNoPush(Entity instance, double deltaX, double deltaY, double deltaZ) {
      return !MovTasks.aC().noEntityPush.get();
   }

   @Unique
   @Override
   public Predictor getPositionPredictor() {
      if (this.predictorInstance == null) {
         this.predictorInstance = new SimpleEntityPredictor((Entity)(Object)this);
      }

      return this.predictorInstance;
   }

   @Inject(
      method = {"isInvisibleTo"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void noInvisiblity(CallbackInfoReturnable<Boolean> cir) {
      if (NoRender.INSTANCE.Da()) {
         cir.setReturnValue(false);
      }
   }

   public void getDataFlag(Object arg0) { }

}
