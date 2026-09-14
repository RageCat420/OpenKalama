package me.matl114.events.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

public class KalamaHelperHelperF implements GuiModel {
   List<GuiModel> guiModelList;

   @Override
   public KalamaHelperHelperE a(ItemRenderer itemRenderer) {
      throw new UnsupportedOperationException("DO NOT CALL");
   }

   @Override
   public void render(
      ItemRenderer itemRenderer,
      ItemDisplayContext renderMode,
      boolean leftHanded,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay
   ) {
      List var8 = this.g(itemRenderer);
      if (var8 != null && !var8.isEmpty()) {
         for (KalamaHelperHelperE var11 : this.h(var8)) {
            if (var11.stackTransformer() != null) {
               matrices.push();
               var11.stackTransformer().apply(matrices);
               itemRenderer.renderItem(var11.stack(), renderMode, leftHanded, matrices, vertexConsumers, light, overlay, var11.state());
               matrices.pop();
            } else {
               itemRenderer.renderItem(var11.stack(), renderMode, leftHanded, matrices, vertexConsumers, light, overlay, var11.state());
            }
         }
      }
   }

   public KalamaHelperHelperF(List<GuiModel> guiModelList) {
      this.guiModelList = guiModelList;
   }

   public List<KalamaHelperHelperE> g(ItemRenderer renderer) {
      return this.guiModelList.stream().flatMap(s -> s instanceof KalamaHelperHelperF var2 ? var2.g(renderer).stream() : Stream.of(s.a(renderer))).toList();
   }

   private List<KalamaHelperHelperE> h(List<KalamaHelperHelperE> originalEntries) {
      ArrayList var2 = new ArrayList();
      int var3 = Math.min(originalEntries.size(), 4);
      float[][] var4 = new float[][]{{0.0F, 0.0F}, {-1.0F, 0.0F}, {0.0F, -1.0F}, {-1.0F, -1.0F}};

      for (int var5 = 0; var5 < var3; var5++) {
         KalamaHelperHelperE var6 = (KalamaHelperHelperE)originalEntries.get(var5);
         float var7 = var4[var5][0];
         float var8 = -var4[var5][1];
         UnaryOperator var9 = matrices -> {
            matrices.translate(var7, var8, 0.0F);
            return matrices;
         };
         UnaryOperator var10 = var6.stackTransformer() != null ? matrices -> var6.stackTransformer().apply(var9.apply(matrices)) : var9;
         var2.add(var6.Uz(var10));
      }

      return var2;
   }
}
