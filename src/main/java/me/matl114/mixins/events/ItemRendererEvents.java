package me.matl114.mixins.events;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.GlobalEventVars;
import me.matl114.events.RenderListener;
import me.matl114.events.model.GuiModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ItemRenderer.class})
public class ItemRendererEvents {
   @ModifyVariable(
      method = {"getModel"},
      at = @At("HEAD"),
      index = 1,
      argsOnly = true
   )
   public ItemStack onItemModelLoad(ItemStack stack) {
      Event<ItemStack> itemStackEvent = new Event<>(stack, true, true);
      RenderListener.m().catchEvent(itemStackEvent);
      return itemStackEvent.d() ? stack : itemStackEvent.e();
   }

   @Inject(
      method = {"renderItem"},
      at = {@At("RETURN")}
   )
   public void onItemRenderDetached(
      ItemStack item,
      ItemDisplayContext renderMode,
      boolean leftHanded,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay,
      BakedModel model,
      CallbackInfo ci
   ) {
      List<GuiModel> stack = RenderListener.g(item);
      if (stack != null && !stack.isEmpty()) {
         this.renderItemContainerItemInfo((ItemRenderer)(Object)this, matrices, renderMode, GuiModel.b(stack), leftHanded, vertexConsumers, overlay);
      }
   }

   @Unique
   private void renderItemContainerItemInfo(
      ItemRenderer itemRenderer,
      MatrixStack matrices,
      ItemDisplayContext renderMode,
      GuiModel model,
      boolean leftHanded,
      VertexConsumerProvider vertexConsumers,
      int overlay
   ) {
      matrices.push();

      try {
         float scale = 0.54F;
         float scale_ground = 0.8F;
         boolean inGui = false;
         if (renderMode == ItemDisplayContext.GUI) {
            inGui = true;
            matrices.translate(0.26, -0.26, 1.0);
            matrices.scale(0.54F, 0.54F, 0.54F);
         } else if (renderMode == ItemDisplayContext.GROUND) {
            matrices.translate(0.15, -0.15, 0.0);
            matrices.scale(0.8F, 0.8F, 0.8F);
         } else {
            if (renderMode != ItemDisplayContext.FIXED) {
               if (renderMode == ItemDisplayContext.HEAD) {
                  return;
               }

               if (renderMode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                  return;
               }

               if (renderMode != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                  return;
               }

               return;
            }

            matrices.translate(-0.25, -0.25, -0.05);
            matrices.scale(0.8F, 0.8F, 0.8F);
         }

         if (inGui) {
            GlobalEventVars.lastRenderNeedDisableGuiLight = true;
         }

         model.render(itemRenderer, renderMode, leftHanded, matrices, vertexConsumers, 15728880, overlay);
      } finally {
         matrices.pop();
      }
   }
}
