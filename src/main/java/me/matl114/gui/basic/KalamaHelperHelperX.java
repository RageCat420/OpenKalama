package me.matl114.gui.basic;

import java.util.function.Predicate;
import me.matl114.versioned.api.VDrawContext;

class KalamaHelperHelperX implements ElementHandler {
   private final ElementHandler am;
   public final Predicate c;
   public boolean c(ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.c.test(this.am) && this.am.c(widget, mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean e(ExecutableWidget widget, char chr, int modifiers) {
      return this.c.test(this.am) && this.am.e(widget, chr, modifiers);
   }

   @Override
   public void f(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.am.f(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
   }

   KalamaHelperHelperX(final ElementHandler this$0, final Predicate param2, final ElementHandler nullx) {
      this.c = param2;
      this.am = nullx;
   }

   public boolean d(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      return this.c.test(this.am) && this.am.d(widget, keyCode, scanCode, modifiers, isPress);
   }

   @Override
   public void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.am.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
   }

   public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      throw new UnsupportedOperationException();
   }

   public boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      return this.c.test(this.am) && this.am.b(element, mouseX, mouseY, button, type);
   }
}
