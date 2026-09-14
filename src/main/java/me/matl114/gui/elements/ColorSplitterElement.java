package me.matl114.gui.elements;

import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;

public class ColorSplitterElement extends ColorLabelTextElement {
   @Override
   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      context.G(0, 0, element.getTextureWidth(), element.getTextureHeight(), 0, this.backgroundColor.getColorInt());
      OrderedText var8 = this.e.getLabel(element);
      float var9 = 0.0F;
      int var10 = this.f.getColorInt();
      if (var8 != null) {
         var9 = mc.textRenderer.getTextHandler().getWidth(var8);
         RenderHandler.drawScaledText0(context, mc.textRenderer, var8, 0, 0, element.getTextureWidth(), element.getTextureHeight(), var10, this.alignment);
      }

      float var11 = (element.getTextureHeight() - 1) / 2.0F;
      context.b();
      context.f().translate(0.0F, var11);
      if (var9 == 0.0F) {
         context.G(0, 0, element.getTextureWidth(), 1, 0, var10);
      } else {
         int var12 = element.getTextureWidth();
         int var13 = (int)((var12 - var9 - 2.0F) / 2.0F);
         if (var13 > 0) {
            context.G(0, 0, var13, 1, 0, var10);
            context.G(var12 - var13, 0, var12, 1, 0, var10);
         }
      }

      context.c();
   }

   public ColorSplitterElement(TextProvider text, int color, ColorSampler background) {
      super(text, color, background);
   }
}
