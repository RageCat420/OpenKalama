package me.matl114.gui.elements;

import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;

public class ColorLabelTextElement extends RawTextElement {
   ColorSampler backgroundColor;

   @Override
   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      context.G(0, 0, element.getTextureWidth(), element.getTextureHeight(), 0, this.backgroundColor.getColorInt());
      OrderedText var8 = this.e.getLabel(element);
      if (var8 != null) {
         RenderHandler.drawScaledText0(
            context, mc.textRenderer, var8, 0, 0, element.getTextureWidth(), element.getTextureHeight(), this.f.getColorInt(), this.alignment
         );
      }
   }

   public ColorLabelTextElement(TextProvider text, int color, ColorSampler background) {
      super(text, color, 0);
      this.backgroundColor = background;
   }

   public ColorLabelTextElement(TextProvider text, ColorSampler color, ColorSampler background) {
      super(text, color, 0);
      this.backgroundColor = background;
   }
}
