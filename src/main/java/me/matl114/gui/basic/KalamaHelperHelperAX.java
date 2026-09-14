package me.matl114.gui.basic;

import java.util.function.Predicate;

class KalamaHelperHelperAX implements KalamaHelperHelperP {
   private final KalamaHelperHelperP b;
   private final Predicate a;

   public boolean c(ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.a.test(this.b) && this.b.c(widget, mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean d(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      return this.a.test(this.b) && this.b.d(widget, keyCode, scanCode, modifiers, isPress);
   }

   public boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      return this.a.test(this.b) && this.b.b(element, mouseX, mouseY, button, type);
   }

   public boolean e(ExecutableWidget widget, char chr, int modifiers) {
      return this.a.test(this.b) && this.b.e(widget, chr, modifiers);
   }

   KalamaHelperHelperAX(final KalamaHelperHelperP this$0, final Predicate param2, final KalamaHelperHelperP nullx) {
      this.a = param2;
      this.b = nullx;
   }

   public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      throw new UnsupportedOperationException();
   }
}
