package me.matl114.versioned.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.List;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.ColorQuad;
import me.matl114.utils.render.Quad;
import me.matl114.utils.render.UV;
import me.matl114.versioned.api.KalamaHelperHelperA;
import me.matl114.versioned.api.KalamaHelperHelperJ;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class Render_v1_21_1 implements VRender, me.matl114.versioned.api.KalamaHelperHelperF {
   private static final float TEXT_HEIGHT = 9.0F;
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   @Override
   public void drawLines(MatrixStack matrixStack, VertexConsumer consumer, List<Vec3d> points, int color) {
      Entry var5 = matrixStack.peek();

      for (int var6 = 1; var6 < points.size(); var6++) {
         Vector3f var7 = ((Vec3d)points.get(var6 - 1)).toVector3f();
         Vector3f var8 = ((Vec3d)points.get(var6)).toVector3f();
         consumer.vertex(var5, var7).color(color);
         consumer.vertex(var5, var8).color(color);
      }
   }

   public void createSpriteTexturedLayer(Sprite sprite, KalamaHelperHelperJ callback) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableCull();
      Tessellator var3 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShaderTexture(0, sprite.getAtlasId());
      RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
      BufferBuilder var4 = var3.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      VertexConsumer var5 = RenderUtils.B(var4, sprite);
      callback.moonriseinitCache(this, var5);
      BuiltBuffer var6 = var4.endNullable();
      if (var6 != null) {
         BufferRenderer.drawWithGlobalProgram(var6);
      }

      RenderSystem.enableCull();
   }

   @Override
   public void drawQuad(MatrixStack matrixStack, VertexConsumer bufferBuilder, Quad uv, ColorQuad colorQuad) {
      Entry var5 = matrixStack.peek();

      for (int var6 = 0; var6 < 4; var6++) {
         Vec3d var7 = uv.get(var6);
         int var8 = colorQuad.get(var6);
         bufferBuilder.vertex(var5, (float)var7.x, (float)var7.y, (float)var7.z).color(var8);
      }
   }

   public void createGuiLayer(KalamaHelperHelperJ callback) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableCull();
      Tessellator var2 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder var3 = var2.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      callback.moonriseinitCache(this, var3);
      BuiltBuffer var4 = var3.endNullable();
      if (var4 != null) {
         BufferRenderer.drawWithGlobalProgram(var4);
      }

      RenderSystem.enableCull();
   }

   public void createGuiTexturedLayer(Identifier path, KalamaHelperHelperJ callback) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableCull();
      Tessellator var3 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShaderTexture(0, path);
      RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
      BufferBuilder var4 = var3.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      callback.moonriseinitCache(this, var4);
      BuiltBuffer var5 = var4.endNullable();
      if (var5 != null) {
         BufferRenderer.drawWithGlobalProgram(var5);
      }

      RenderSystem.enableCull();
   }

   public void drawLine(MatrixStack matrixStack, VertexConsumer consumer, Vec3d prevV, Vec3d nextV, int color) {
      Vector3f var6 = prevV.toVector3f();
      Vector3f var7 = nextV.toVector3f();
      Entry var8 = matrixStack.peek();
      consumer.vertex(var8, var6).color(color);
      consumer.vertex(var8, var7).color(color);
   }

   public void createTriangleStripLayer(KalamaHelperHelperJ callback, boolean hasCulling) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator var3 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder var4 = var3.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
      if (!hasCulling) {
         RenderSystem.disableCull();
      }

      callback.moonriseinitCache(this, var4);
      if (!hasCulling) {
         RenderSystem.enableCull();
      }

      BuiltBuffer var5 = var4.endNullable();
      if (var5 != null) {
         BufferRenderer.drawWithGlobalProgram(var5);
      }
   }

   public void createLinesLayer(KalamaHelperHelperJ callback) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator var2 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder var3 = var2.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      callback.moonriseinitCache(this, var3);
      BuiltBuffer var4 = var3.endNullable();
      if (var4 != null) {
         BufferRenderer.drawWithGlobalProgram(var4);
      }
   }

   public void drawOutlinedBox(MatrixStack matrix4f, VertexConsumer bufferBuilder, Vec3d from, Vec3d to, int cachedRenderColor) {
      Entry var6 = matrix4f.peek();
      float var7 = (float)from.getX();
      float var8 = (float)from.getY();
      float var9 = (float)from.getZ();
      float var10 = (float)to.getX();
      float var11 = (float)to.getY();
      float var12 = (float)to.getZ();
      bufferBuilder.vertex(var6, var7, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var9).color(cachedRenderColor);
   }

   @Override
   public void drawTexturedQuad(MatrixStack stack, VertexConsumer vertex, Quad quad, UV uv, ColorQuad colorQuad) {
      Entry var6 = stack.peek();

      for (int var7 = 0; var7 < 4; var7++) {
         Vec3d var8 = quad.get(var7);
         float var9 = uv.a(var7);
         float var10 = uv.b(var7);
         int var11 = colorQuad.get(var7);
         vertex.vertex(var6, (float)var8.x, (float)var8.y, (float)var8.z).texture(var9, var10).color(var11);
      }
   }

   public void createLineStripLayer(KalamaHelperHelperJ callback) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator var2 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder var3 = var2.begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      callback.moonriseinitCache(this, var3);
      BuiltBuffer var4 = var3.endNullable();
      if (var4 != null) {
         BufferRenderer.drawWithGlobalProgram(var4);
      }
   }

   public void createTrianglesLayer(KalamaHelperHelperJ callback, boolean hasCulling) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator var3 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder var4 = var3.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
      if (!hasCulling) {
         RenderSystem.disableCull();
      }

      callback.moonriseinitCache(this, var4);
      if (!hasCulling) {
         RenderSystem.enableCull();
      }

      BuiltBuffer var5 = var4.endNullable();
      if (var5 != null) {
         BufferRenderer.drawWithGlobalProgram(var5);
      }
   }

   public void drawSolidBoxQuad(MatrixStack matrixStack, VertexConsumer bufferBuilder, Vec3d from, Vec3d to, int cachedRenderColor) {
      Entry var6 = matrixStack.peek();
      float var7 = (float)from.x;
      float var8 = (float)from.y;
      float var9 = (float)from.z;
      float var10 = (float)to.x;
      float var11 = (float)to.y;
      float var12 = (float)to.z;
      bufferBuilder.vertex(var6, var7, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var10, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var9).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var8, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var12).color(cachedRenderColor);
      bufferBuilder.vertex(var6, var7, var11, var9).color(cachedRenderColor);
   }

   public void drawItemCameraCoord(
      ItemStack itemStack, MatrixStack stack, Vec3d vec3d, ItemDisplayContext context, me.matl114.versioned.api.KalamaHelperHelperD displayInfo
   ) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      boolean var6 = Vec3d.ZERO.equals(vec3d);
      if (!var6) {
         stack.translate(vec3d.x, vec3d.y, vec3d.z);
      }

      mc.getItemRenderer()
         .renderItem(itemStack, context, displayInfo.dM(), displayInfo.oG(), stack, mc.gameRenderer.buffers.getEntityVertexConsumers(), mc.world, -999);
      if (!var6) {
         stack.translate(-vec3d.x, -vec3d.y, -vec3d.z);
      }
   }

   public void createQuadsLayer(KalamaHelperHelperJ callback, boolean hasCulling) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator var3 = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder var4 = var3.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      if (!hasCulling) {
         RenderSystem.disableCull();
      }

      callback.moonriseinitCache(this, var4);
      if (!hasCulling) {
         RenderSystem.enableCull();
      }

      BuiltBuffer var5 = var4.endNullable();
      if (var5 != null) {
         BufferRenderer.drawWithGlobalProgram(var5);
      }
   }

   @Override
   public void drawTextCameraCoord(
      OrderedText orderedText, MatrixStack stack, Vec3d vec3d, int displayPositionFlag, Color color, KalamaHelperHelperA displayInfo
   ) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int var7 = displayPositionFlag % 3;
      int var8 = displayPositionFlag / 3;
      int var9 = mc.textRenderer.getWidth(orderedText);
      float var10 = -(var9 * var7 / 2.0F);
      float var11 = -(9.0F * var8 / 2.0F);
      stack.push();
      stack.translate(vec3d.x, vec3d.y, vec3d.z);
      stack.scale(1.0F, -1.0F, 1.0F);
      stack.translate(var10, var11, 0.0F);
      mc.textRenderer
         .draw(
            orderedText,
            0.0F,
            0.0F,
            color.getRGB(),
            displayInfo.shadow(),
            stack.peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            displayInfo.layerType(),
            displayInfo.backgroundColor(),
            displayInfo.backgroundColor()
         );
      mc.getBufferBuilders().getEntityVertexConsumers().draw();
      stack.pop();
   }



   @Override
   public void o(Object arg0) { }

}
