package me.matl114.gui.basic;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.Text;

public class TooltipHandler implements RenderHandler {
   final TooltipHandler$TooltipProvider provider;

   public void renderExtraAbsoluteCoord(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      if (shouldHighlight && this.provider != null) {
         List var8 = this.provider.a(element);
         if (var8 != null && !var8.isEmpty()) {
            context.drawTooltip(mc.textRenderer, var8, Optional.empty(), mouseX, mouseY);
         }
      }
   }

   @Override
   public final void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
   }

   public static TooltipHandler aq(TooltipHandler$TooltipProvider provider) {
      return new TooltipHandler(provider);
   }

   public static TooltipHandler ar(Supplier<List<Text>> listSupplier) {
      return new TooltipHandler(TooltipHandler$TooltipProvider.c(listSupplier));
   }

   public static TooltipHandler ap(List<Text> list) {
      return new TooltipHandler(list);
   }

   public TooltipHandler(TooltipHandler$TooltipProvider provider) {
      this.provider = provider;
   }

   public TooltipHandler(List<Text> provider) {
      this(TooltipHandler$TooltipProvider.b(provider));
   }
}
