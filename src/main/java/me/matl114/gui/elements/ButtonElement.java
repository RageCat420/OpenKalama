package me.matl114.gui.elements;

import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ButtonElement extends IconElement$SimpleIconElement {
   public static final Identifier bI = new Identifier("minecraft", "widget/button_highlighted");
   private final TextProvider provider;
   public static final Identifier bJ = new Identifier("minecraft", "widget/button_disabled");
   public static final Identifier bH = new Identifier("minecraft", "widget/button");

   @Override
   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      super.renderCentered0(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      int var8 = 16777215;
      OrderedText var9 = this.provider.getLabel(element);
      if (var9 != null) {
         RenderHandler.drawScaledText0(
            context, mc.textRenderer, var9, 0, 0, element.getTextureWidth(), element.getTextureHeight(), var8 | MathHelper.ceil(alpha * 255.0F) << 24, 0
         );
      }
   }

   public ButtonElement(TextProvider provider, ButtonAction action) {
      super(bJ, bH, true, action);
      this.provider = provider;
   }
}
