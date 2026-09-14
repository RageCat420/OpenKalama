package me.matl114.mixins.fix;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class KeyboardDebugCrashFix {
   @Shadow
   private long field_1682;

   @Inject(
      method = {"pollDebugCrash"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/crash/CrashException;<init>(Lnet/minecraft/util/crash/CrashReport;)V",
         shift = Shift.BEFORE
      )}
   )
   private void onResetDebugCrashTime(CallbackInfo ci) {
      this.field_1682 = 0L;
   }
}
