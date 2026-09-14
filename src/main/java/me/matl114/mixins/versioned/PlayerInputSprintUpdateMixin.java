package me.matl114.mixins.versioned;

import me.matl114.versioned.accessors.PlayerInputAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerEntity.class})
public class PlayerInputSprintUpdateMixin {
   @Shadow
   public Input field_3913;
   @Shadow
   @Final
   protected MinecraftClient field_3937;

   @Inject(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/input/Input;tick(ZF)V",
         shift = Shift.BEFORE
      )}
   )
   private void onTickMovement(CallbackInfo ci) {
      PlayerInputAccess.of(this.field_3913).setPressingSprint(this.field_3937.options.sprintKey.isPressed());
   }
}
