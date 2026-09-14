package me.matl114.mixins.versioned;

import java.util.function.BiConsumer;
import me.matl114.versioned.accessors.KalamaHelperHelperB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({LayeredDrawer.class})
public class LayeredDrawerMixin implements KalamaHelperHelperB {
   @Unique
   BiConsumer<DrawContext, RenderTickCounter> postRenderTask;

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/LayeredDrawer;renderInternal(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
         shift = Shift.AFTER
      )}
   )
   private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (this.postRenderTask != null) {
         this.postRenderTask.accept(context, tickCounter);
         context.getMatrices().translate(0.0F, 0.0F, 200.0F);
         this.postRenderTask = null;
      }
   }

   @Override
   public void setPos(BiConsumer<DrawContext, RenderTickCounter> postRenderTask) {
      this.postRenderTask = postRenderTask;
   }
}
