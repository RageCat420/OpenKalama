package me.matl114.gui.basic;

import java.util.function.Predicate;
import java.util.function.Supplier;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public interface RenderHandler {
   MinecraftClient mc = MinecraftClient.getInstance();

   static RenderHandler z(Identifier guiTexture, int startX, int startY, int sizeX, int sizeY) {
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> context.V(guiTexture, startX, startY, sizeX, sizeY);
   }

   static RenderHandler p(Identifier identifier) {
      return o(identifier, 0, 0, 256, 256);
   }

   static RenderHandler ofSingleItem(Supplier<ItemStack> item, int x, int y, boolean inSlot) {
      return new KalamaHelperHelperW(item, x, y, inSlot);
   }

   static RenderHandler y(Identifier guiTexture) {
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> context.V(
         guiTexture, 0, 0, element.getTextureWidth(), element.getTextureHeight()
      );
   }

   static RenderHandler u(Identifier identifier, float scaler) {
      return ofResource(identifier, 0, 0, 256, 256, scaler);
   }

   static RenderHandler s(Identifier identifier) {
      return u(identifier, 1.0F);
   }

   static void drawScrollableText(VDrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color) {
      drawScrollableText0(context, textRenderer, text, (startX + endX) / 2, startX, startY, endX, endY, color);
   }

   default RenderHandler n(Predicate<RenderHandler> renderPredicate) {
      return new KalamaHelperHelperE(this, renderPredicate, this);
   }

   default RenderHandler m(RenderHandler handlerAbsolute) {
      return new KalamaHelperHelperT(this, handlerAbsolute);
   }

   default RenderHandler l(RenderHandler handler) {
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
         this.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
         handler.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      };
   }

   default boolean canBeSelected(DrawableWidget element) {
      return true;
   }

   static RenderHandler w(Identifier identifier, int x, int y, int xheight, int yheight) {
      return ofPositionResource(identifier, x, y, xheight, yheight, 0, 0, 256, 256);
   }

   static RenderHandler ofResource(Identifier identifier, int u0, int v0, int uheight, int vheight, float scaler) {
      float var6 = u0 / 256.0F;
      float var7 = v0 / 256.0F;
      float var8 = (u0 + uheight) / 256.0F;
      float var9 = (v0 + vheight) / 256.0F;
      int var10 = (int)(uheight * scaler);
      int var11 = (int)(vheight * scaler);
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
         context.setShaderAlpha(alpha);
         context.w(identifier, 0, var10, 0, var11, 0, var6, var8, var7, var9);
         context.setShaderAlpha(1.0F);
      };
   }

   static RenderHandler ofPositionResource(Identifier identifier, int x, int y, int xheight, int yheight, int u0, int v0, int uheight, int vheight) {
      float var9 = u0 / 256.0F;
      float var10 = v0 / 256.0F;
      float var11 = (u0 + uheight) / 256.0F;
      float var12 = (v0 + vheight) / 256.0F;
      int var13 = x + xheight;
      int var14 = y + yheight;
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
         context.setShaderAlpha(alpha);
         context.w(identifier, x, var13, y, var14, 0, var9, var11, var10, var12);
         context.setShaderAlpha(1.0F);
      };
   }

   static RenderHandler ofColorQuad(int color) {
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> context.E(
         0, 0, element.getTextureWidth(), element.getTextureHeight(), color, color, 0
      );
   }

   static void drawScrollableText0(
      VDrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color
   ) {
      F(context, textRenderer, text.asOrderedText(), centerX, startX, startY, endX, endY, color);
   }

   static void L(VDrawContext context, int x, int y, int width, int height, int color) {
      context.fill(x, y, x + 1, y + height, color);
      context.fill(x, y, x + width, y + 1, color);
      context.fill(x + width - 1, y, x + width, y + height, color);
      context.fill(x, y + height - 1, x + width, y + height, color);
   }

   void renderAtCentered(DrawableWidget var1, VDrawContext var2, int var3, int var4, float var5, float var6, boolean var7);

   static RenderHandler o(Identifier identifier, int u0, int v0, int uheight, int vheight) {
      float var5 = u0 / 256.0F;
      float var6 = v0 / 256.0F;
      float var7 = (u0 + uheight) / 256.0F;
      float var8 = (v0 + vheight) / 256.0F;
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
         context.setShaderAlpha(alpha);
         context.w(identifier, 0, element.getTextureWidth(), 0, element.getTextureHeight(), 0, var5, var7, var6, var8);
         context.setShaderAlpha(1.0F);
      };
   }

   static void G(VDrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color, int alignment) {
      drawScaledText0(context, textRenderer, text.asOrderedText(), startX, startY, endX, endY, color, alignment);
   }

   static RenderHandler t(Identifier identifier, int u0, int v0, int uheight, int vheight) {
      return ofResource(identifier, u0, v0, uheight, vheight, 1.0F);
   }

   default void f(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
   }

   static void drawScaledText0(
      VDrawContext context, TextRenderer textRenderer, OrderedText text, int startX, int startY, int endX, int endY, int color, int alignment
   ) {
      float var9 = textRenderer.getTextHandler().getWidth(text);
      int var10 = endX - startX;
      float var11 = (startY + endY - 9) / 2.0F;
      if (var10 > var9) {
         float var12 = switch (alignment) {
            case -1 -> startX;
            case 1 -> endX - var9;
            default -> (startX + endX - var9 + 1.0F) / 2.0F;
         };
         context.f().pushMatrix();
         context.f().translate(var12, var11);
         context.z(textRenderer, text, 0, 0, color, true);
         context.f().popMatrix();
      } else {
         float var15 = var10 / var9;
         float var13 = 9.0F * var15;
         float var14 = (startY + endY - var13) / 2.0F;
         context.f().pushMatrix();
         context.f().translate(startX, var14);
         context.f().scale(var15, var15);
         context.z(textRenderer, text, 0, 0, color, true);
         context.f().popMatrix();
      }
   }

   static RenderHandler B(Text text, int color) {
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> drawScrollableText(
         context, mc.textRenderer, text, 0, 0, element.getTextureWidth(), element.getTextureHeight(), color
      );
   }

   static RenderHandler q(Identifier identifier) {
      return r(identifier, 1.0F);
   }

   static void F(VDrawContext context, TextRenderer textRenderer, OrderedText text, int centerX, int startX, int startY, int endX, int endY, int color) {
      int var9 = textRenderer.getWidth(text);
      int var10 = startY + endY;
      int var11 = (var10 - 9) / 2 + 1;
      int var12 = endX - startX;
      if (var9 > var12) {
         int var13 = var9 - var12;
         double var14 = Util.getMeasuringTimeMs() / 1000.0;
         double var16 = Math.max(var13 * 0.5, 3.0);
         double var18 = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * var14 / var16)) / 2.0 + 0.5;
         double var20 = MathHelper.lerp(var18, 0.0, var13);
         context.enableScissor(startX, startY, endX, endY);
         context.z(textRenderer, text, startX - (int)var20, var11, color, true);
         context.C();
      } else {
         int var22 = MathHelper.clamp(centerX, startX + var9 / 2, endX - var9 / 2);
         context.drawCenteredTextWithShadow(textRenderer, text, var22, var11, color);
      }
   }

   static RenderHandler C(Text text, int color) {
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> G(
         context, mc.textRenderer, text, 0, 0, element.getTextureWidth(), element.getTextureHeight(), color, 0
      );
   }

   static void K(VDrawContext context, int x, int y, int dx, int dy, int color) {
      context.E(x, y, x + dx, y + 1, color, color, 0);
      context.E(x, y, x + 1, y + dy, color, color, 0);
      context.E(x + dx - 1, y + 1, x + dx, y + dy, color, color, 0);
      context.E(x + 1, y + dy - 1, x + dx, y + dy, color, color, 0);
   }

   static void drawSingleItem(VDrawContext context, ItemStack stack, int x, int y, boolean inSlot) {
      if (!stack.isEmpty()) {
         context.f().pushMatrix();
         context.K(stack, x, y, 114514, 0);
         if (inSlot) {
            context.drawItemInSlot(mc.textRenderer, stack, x, y, null);
         }

         context.f().popMatrix();
      }
   }

   static RenderHandler r(Identifier identifier, float scaler) {
      float var2 = scaler * 256.0F;
      return (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
         context.setShaderAlpha(alpha);
         context.w(
            identifier,
            0,
            element.getTextureWidth(),
            0,
            element.getTextureHeight(),
            0,
            0.0F,
            element.getTextureWidth() / var2,
            0.0F,
            element.getTextureHeight() / var2
         );
         context.setShaderAlpha(1.0F);
      };
   }
}
