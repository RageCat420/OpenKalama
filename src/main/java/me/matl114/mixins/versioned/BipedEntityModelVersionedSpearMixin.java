package me.matl114.mixins.versioned;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.matl114.hacks.modules.combat.SpearEnhance;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.versioned.impl.LancingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({BipedEntityModel.class})
public class BipedEntityModelVersionedSpearMixin {
   @Shadow
   @Final
   public ModelPart field_27433;
   @Shadow
   @Final
   public ModelPart field_3398;
   @Shadow
   @Final
   public ModelPart field_3401;

   @Inject(
      method = {"positionRightArm"},
      at = {@At("RETURN")}
   )
   private void positionRightArm(LivingEntity entity, CallbackInfo ci) {
      if (entity instanceof PlayerEntity pl && SpearEnhance.INSTANCE.fixOldVersionSpear.get() && SpearEnhance.isUsingSpear(pl)) {
         Hand hand = pl.getActiveHand();
         Arm arm = hand == Hand.MAIN_HAND ? pl.getMainArm() : pl.getMainArm().getOpposite();
         if (arm == Arm.RIGHT) {
            LancingUtils.positionArmForSpear(this.field_3401, this.field_3398, true, pl.getActiveItem(), pl);
         }
      }
   }

   @Inject(
      method = {"positionLeftArm"},
      at = {@At("RETURN")}
   )
   private void positionLefgArm(LivingEntity entity, CallbackInfo ci) {
      if (entity instanceof PlayerEntity pl && SpearEnhance.INSTANCE.fixOldVersionSpear.get() && SpearEnhance.isUsingSpear(pl)) {
         Hand hand = pl.getActiveHand();
         Arm arm = hand == Hand.MAIN_HAND ? pl.getMainArm() : pl.getMainArm().getOpposite();
         if (arm == Arm.LEFT) {
            LancingUtils.positionArmForSpear(this.field_27433, this.field_3398, false, pl.getActiveItem(), pl);
         }
      }
   }

   @ModifyExpressionValue(
      method = {"setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getFallFlyingTicks()I"
      )}
   )
   private int setAnglesFallFlyingTicks(int original, @Local(argsOnly = true) LivingEntity p) {
      return original > 0 && p == MinecraftClient.getInstance().player && ElytraExtra.INSTANCE.afr() && ElytraExtra.INSTANCE.renderFix.get() ? 0 : original;
   }
}
