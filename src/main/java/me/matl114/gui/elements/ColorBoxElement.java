package me.matl114.gui.elements;

import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.complex.BoxElement;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;

public class ColorBoxElement extends BoxElement {
   private final ColorSampler x;
   private final TextProvider e;
   private final KalamaHelperHelperIX y;
   private final ColorSampler w;

   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      OrderedText var8 = this.e.getLabel(element);
      context.G(0, 0, element.getTextureWidth(), element.getTextureHeight(), 0, this.w.getColorInt());
      if (var8 != null) {
         RenderHandler.drawScaledText0(context, mc.textRenderer, var8, 0, 0, element.getTextureWidth(), element.getTextureHeight(), this.x.getColorInt(), 0);
      }

      Integer var9 = this.y == null ? (shouldHighlight ? -1 : null) : this.y.toggle(element, shouldHighlight);
      if (var9 != null) {
         RenderHandler.K(context, 0, 0, element.getTextureWidth(), element.getTextureHeight(), var9);
      }
   }

   public ColorBoxElement(ButtonAction action, TextProvider provider, ColorSampler colorSampler, KalamaHelperHelperIX highLightProvider) {
      this(action, provider, colorSampler, ColorSampler.WHITE, highLightProvider);
   }

   public ColorBoxElement(
      ButtonAction action, TextProvider provider, ColorSampler colorSampler, ColorSampler textSampler, KalamaHelperHelperIX highLightProvider
   ) {
      super(action);
      this.e = provider;
      this.w = colorSampler;
      this.x = textSampler;
      this.y = highLightProvider;
   }
}
