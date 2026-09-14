package me.matl114.versioned.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import me.matl114.versioned.api.MatrixStack;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DrawContext_v1_21_1 implements VDrawContext {
   public static ThreadLocal<Boolean> colorOverride = ThreadLocal.withInitial(() -> false);
   private final DrawContext a;
   private static final int[] cachedShaderColor = new int[4];
   private final MatrixStack b;
   private Runnable c = null;

   @Override
   public void r(int rgba) {
      cachedShaderColor[0] = rgba >> 16 & 0xFF;
      cachedShaderColor[1] = rgba >> 8 & 0xFF;
      cachedShaderColor[2] = rgba & 0xFF;
      cachedShaderColor[3] = rgba >>> 24;
   }

   public DrawContext a() {
      return this.a;
   }

   @Override
   public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride) {
      this.a.drawItemInSlot(textRenderer, stack, x, y, countOverride);
   }

   public static int getShaderRGB() {
      return cachedShaderColor[3] << 24 | cachedShaderColor[0] << 16 | cachedShaderColor[1] << 8 | cachedShaderColor[2];
   }

   public static float n() {
      return cachedShaderColor[0] / 255.0F;
   }

   public static int j(int argb) {
      return argb >> 8 & 0xFF;
   }

   @Override
   public void setShaderAlpha(float alpha) {
      cachedShaderColor[3] = ColorHelper.channelFromFloat(alpha);
   }

   public static float o() {
      return cachedShaderColor[1] / 255.0F;
   }

   public void drawGuiTexture(Identifier texture, int x, int y, int z, int width, int height) {
      colorOverride.set(true);

      try {
         this.a.drawGuiTexture(texture, x, y, z, width, height);
      } finally {
         colorOverride.set(false);
      }
   }

   private void addInternal(Runnable runnable) {
      if (this.c != null) {
         Runnable var2 = this.c;
         this.c = () -> {
            var2.run();
            runnable.run();
         };
      } else {
         this.c = runnable;
      }
   }

   public void popLayer() {
      this.a.getMatrices().pop();
   }

   public void pushLayer(int depth) {
      this.a.getMatrices().push();
      this.a.getMatrices().translate(0.0F, 0.0F, depth);
   }

   @Override
   public void setShaderColor(float red, float green, float blue, float alpha) {
      cachedShaderColor[0] = ColorHelper.channelFromFloat(red);
      cachedShaderColor[1] = ColorHelper.channelFromFloat(green);
      cachedShaderColor[2] = ColorHelper.channelFromFloat(blue);
      cachedShaderColor[3] = ColorHelper.channelFromFloat(alpha);
   }

   public DrawContext popMatrix() {
      this.a.getMatrices().pop();
      return this.a;
   }

   public static float p() {
      return cachedShaderColor[2] / 255.0F;
   }

   @Override
   public void fillGuiGradient(int x1, int y1, int x2, int y2, int color1, int color2, int color3, int color4, int depth) {
      VertexConsumer var10 = this.a.getVertexConsumers().getBuffer(RenderLayer.getGui());
      Matrix4f var11 = this.a.getMatrices().peek().getPositionMatrix();
      var10.vertex(var11, x1, y1, depth).color(color1);
      var10.vertex(var11, x1, y2, depth).color(color2);
      var10.vertex(var11, x2, y2, depth).color(color3);
      var10.vertex(var11, x2, y1, depth).color(color4);
   }

   public void drawTexturedQuad(Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
      this.a.drawTexturedQuad(texture, x1, x2, y1, y2, z, u1, u2, v1, v2, n(), o(), p(), q());
   }

   public static int h(int argb) {
      return argb >>> 24;
   }

   public void drawItem(ItemStack stack, int x, int y, int seed, int z) {
      this.a.drawItem(stack, x, y, seed);
   }

   public static int i(int argb) {
      return argb >> 16 & 0xFF;
   }

   public void drawGuiTextureQuad(Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
      Sprite var11 = this.getGuiSprite(texture);
      float var12 = var11.getMinU();
      float var13 = var11.getMaxU();
      float var14 = var11.getMinV();
      float var15 = var11.getMaxV();
      float var16 = var12 + u1 * (var13 - var12);
      float var17 = var12 + u2 * (var13 - var12);
      float var18 = var14 + v1 * (var15 - var14);
      float var19 = var14 + v2 * (var15 - var14);
      this.drawTexturedQuad(var11.getAtlasId(), x1, x2, y1, y2, z, var16, var17, var18, var19);
   }

   public void drawText(TextRenderer textRenderer, @Nullable String text, int x, int y, int color, boolean shadow) {
      this.a.drawText(textRenderer, text, x, y, m(color), shadow);
   }

   public void tryDraw() {
      if (this.c != null) {
         this.c.run();
         this.c = null;
      }

      this.a.tryDraw();
   }

   @Override
   public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y) {
      Matrix4f var6 = this.b.peek3D();
      Vector4f var7 = new Vector4f(x, y, 0.0F, 1.0F).mul(var6);
      this.addInternal(() -> this.a.drawTooltip(textRenderer, text, data, (int)var7.x, (int)var7.y));
   }

   @Override
   public void z(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
      this.a.drawText(textRenderer, text, x, y, m(color), shadow);
   }

   public void lineGuiGradient(int x1, int y1, int x2, int y2, int color1, int color2, int depth) {
      VertexConsumer var8 = this.a.getVertexConsumers().getBuffer(RenderLayer.LINES);
      Entry var9 = this.a.getMatrices().peek();
      Vector3f var10 = new Vector3f(x2 - x1, y2 - y1, 0.0F).normalize();
      var8.vertex(var9, x1, y1, depth).color(color1).normal(var9, var10.x, var10.y, var10.z);
      var8.vertex(var9, x2, y2, depth).color(color2).normal(var9, var10.x, var10.y, var10.z);
      this.a.getVertexConsumers().draw(RenderLayer.LINES);
   }

   public static int getArgb(int alpha, int red, int green, int blue) {
      return alpha << 24 | red << 16 | green << 8 | blue;
   }

   public static int k(int argb) {
      return argb & 0xFF;
   }

   @Override
   public void enableScissor(int x, int y, int x2, int y2) {
      Matrix4f var5 = this.b.peek3D();
      Vector4f var6 = new Vector4f(x, y, 0.0F, 1.0F).mul(var5);
      Vector4f var7 = new Vector4f(x2, y2, 0.0F, 1.0F).mul(var5);
      this.a.enableScissor((int)var6.x, (int)var6.y, (int)var7.x, (int)var7.y);
   }

   public void disableScissor() {
      this.a.disableScissor();
   }

   public DrawContext pushMatrix() {
      this.a.getMatrices().push();
      return this.a;
   }

   @Override
   public MatrixStack f() {
      return this.b;
   }

   @Override
   public void drawGuiTexture(Identifier texture, int i, int j, int k, int l, int x, int y, int z, int width, int height) {
      colorOverride.set(true);

      try {
         this.a.drawGuiTexture(texture, i, j, k, l, x, y, z, width, height);
      } finally {
         colorOverride.set(false);
      }
   }

   public void fill(int x1, int y1, int x2, int y2, int z, int color) {
      this.a.fill(x1, y1, x2, y2, z, color);
   }

   public static int m(int a) {
      int var1 = getShaderRGB();
      if (var1 == -1) {
         return a;
      } else {
         return a == -1 ? var1 : getArgb(h(var1) * h(a) / 255, i(var1) * i(a) / 255, j(var1) * j(a) / 255, k(var1) * k(a) / 255);
      }
   }

   static {
      Arrays.fill(cachedShaderColor, 255);
   }

   public Sprite getGuiSprite(Identifier i) {
      return this.a.guiAtlasManager.getSprite(i);
   }

   public static float q() {
      return cachedShaderColor[3] / 255.0F;
   }

   @Override
   public void E(int x1, int y1, int x2, int y2, int color1, int color2, int depth) {
      this.a.fillGradient(RenderLayer.getGuiOverlay(), x1, y1, x2, y2, color1, color2, depth);
   }

   public DrawContext_v1_21_1(DrawContext context) {
      this.a = context;
      this.b = MatrixStack.of(context.getMatrices());
   }



   @Override
   public void e() { }


   public void A(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) { }

}
