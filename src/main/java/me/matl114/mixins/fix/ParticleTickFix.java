package me.matl114.mixins.fix;

import me.matl114.hacks.RenderTasks;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Particle.class})
public class ParticleTickFix {
   @Shadow
   protected boolean field_3862;
   @Shadow
   @Final
   protected ClientWorld field_3851;

   @Inject(
      method = {"move(DDD)V"},
      at = {@At("HEAD")}
   )
   private void onMove(CallbackInfo ci) {
      if (this.field_3851.isClient() && RenderTasks.H().optimizeParticleTick.get()) {
         this.field_3862 = false;
      }
   }
}
