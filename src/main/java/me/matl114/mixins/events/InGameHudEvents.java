package me.matl114.mixins.events;

import me.matl114.events.RenderListener;
import me.matl114.versioned.accessors.KalamaHelperHelperB;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class InGameHudEvents {
   @Shadow
   @Final
   private MinecraftClient field_2035;
   @Shadow
   @Final
   private LayeredDrawer field_47847;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void renderPlayerList(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      KalamaHelperHelperB.of(this.field_47847).setPos((ctx, tc) -> {
         VDrawContext vdraw = VDrawContext.P(ctx);
         vdraw.b();

         try {
            RenderListener.r().h(vdraw, tc.getTickDelta(false), this.field_2035.options.hudHidden);
         } finally {
            vdraw.c();
         }
      });
   }
}
