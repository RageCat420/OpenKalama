package me.matl114.gui.elements;

import java.util.List;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class MultiLineTextElement extends RawTextElement {
   public MultiLineTextElement(Text text, int color) {
      this(text, color, 0);
   }

   @Override
   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      Text var8 = this.e.a(element);
      if (var8 != null) {
         int var9 = element.getTextureWidth();
         List var10 = mc.textRenderer.wrapLines(var8, var9);
         int var11 = var10.size();
         if (var11 > 0) {
            int var12 = 9;
            int var13 = var12 * var11;
            int var14 = 0;
            if (var13 < element.getTextureHeight()) {
               var14 = (element.getTextureHeight() - var13) / 2;
            } else {
               var12 = Math.min(var12, element.getTextureHeight() / var11);
            }

            for (int var15 = 0; var15 < var11; var15++) {
               RenderHandler.drawScaledText0(
                  context,
                  mc.textRenderer,
                  (OrderedText)var10.get(var15),
                  0,
                  var15 * var12 + var14,
                  element.getTextureWidth(),
                  (var15 + 1) * var12 + var14,
                  this.f.getColorInt(),
                  this.alignment
               );
            }
         }
      }
   }

   public MultiLineTextElement(Text text, int color, int alignment) {
      this(TextProvider.c(text), color, alignment);
   }

   public MultiLineTextElement(TextProvider provider, int color, int alignment) {
      super(provider, color, alignment);
   }
}
