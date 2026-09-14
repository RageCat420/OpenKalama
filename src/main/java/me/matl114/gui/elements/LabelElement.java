package me.matl114.gui.elements;

import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LabelElement extends RawTextElement {
   protected static final float E = 0.296875F;
   protected static final Identifier BACKGROUND_RESOURCE = new Identifier("kalama", "textures/custom/recipecontainer.png");
   protected static final float D = 0.4921875F;
   protected static final float B = 0.4296875F;
   protected static final float C = 0.234375F;

   public LabelElement(Text text, int color, int alignment) {
      this(TextProvider.c(text), color, alignment);
   }

   public LabelElement(TextProvider text, int color, int alignment) {
      super(text, color, alignment);
   }

   public LabelElement(Text text, int color) {
      this(text, color, 0);
   }

   @Override
   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      OrderedText var8 = this.e.getLabel(element);
      if (var8 != null) {
         context.w(BACKGROUND_RESOURCE, 0, element.getTextureWidth(), 0, element.getTextureHeight(), 0, 0.4296875F, 0.4921875F, 0.234375F, 0.296875F);
         RenderHandler.drawScaledText0(
            context, mc.textRenderer, var8, 0, 0, element.getTextureWidth(), element.getTextureHeight(), this.f.getColorInt(), this.alignment
         );
      }
   }

   public static LabelElement instance(Text text) {
      return new LabelElement(text, -1);
   }

   public LabelElement(int color) {
      this(Text.empty(), color);
   }
}
