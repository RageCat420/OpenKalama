package me.matl114.gui.basic;

import java.util.function.Predicate;
import me.matl114.versioned.api.VDrawContext;

class KalamaHelperHelperE implements RenderHandler {
   private final RenderHandler bz;
   private final Predicate by;

   @Override
   public void f(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      if (this.by.test(this.bz)) {
         this.bz.f(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      }
   }

   KalamaHelperHelperE(final RenderHandler this$0, final Predicate param2, final RenderHandler nullx) {
      this.by = param2;
      this.bz = nullx;
   }

   @Override
   public void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      if (this.by.test(this.bz)) {
         this.bz.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      }
   }
}
