package me.matl114.gui.elements;

import java.util.function.Predicate;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperM;
import me.matl114.versioned.api.VDrawContext;

class KalamaHelperHelperA implements ElementHandler {
   private final IconElement d;
   private final Predicate c;

   public boolean c(ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.c.test(this.d)) {
         this.d.setActive(true);
         return this.d.c(widget, mouseX, mouseY, horizontalAmount, verticalAmount);
      } else {
         this.d.setActive(false);
         return false;
      }
   }

   public boolean e(ExecutableWidget widget, char chr, int modifiers) {
      if (this.c.test(this.d)) {
         this.d.setActive(true);
         return this.d.e(widget, chr, modifiers);
      } else {
         this.d.setActive(false);
         return false;
      }
   }

   @Override
   public void f(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      if (this.c.test(this.d)) {
         this.d.setActive(true);
      } else {
         this.d.setActive(false);
      }

      this.d.f(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
   }

   KalamaHelperHelperA(final IconElement this$0, final Predicate param2, final IconElement nullx) {
      this.c = param2;
      this.d = nullx;
   }

   public boolean d(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      if (this.c.test(this.d)) {
         this.d.setActive(true);
         return this.d.d(widget, keyCode, scanCode, modifiers, isPress);
      } else {
         this.d.setActive(false);
         return false;
      }
   }

   @Override
   public void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      if (this.c.test(this.d)) {
         this.d.setActive(true);
      } else {
         this.d.setActive(false);
      }

      this.d.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
   }

   public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      throw new UnsupportedOperationException();
   }

   public boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      if (this.c.test(this.d)) {
         this.d.setActive(true);
         return this.d.b(element, mouseX, mouseY, button, type);
      } else {
         this.d.setActive(false);
         return false;
      }
   }
}
