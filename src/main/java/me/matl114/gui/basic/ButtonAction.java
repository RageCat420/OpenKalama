package me.matl114.gui.basic;

import java.util.function.Consumer;

public interface ButtonAction {
   ButtonAction a = (element, widget, mouseButton) -> true;

   static ButtonAction b(Consumer<Boolean> isLeft) {
      return (element, widget, mouseButton) -> {
         isLeft.accept(mouseButton == 0);
         return true;
      };
   }

   static ButtonAction c() {
      return a;
   }

   static ButtonAction a(Runnable task) {
      return (element, widget, mouseButton) -> {
         task.run();
         return true;
      };
   }

   boolean d(AbstractElement var1, ExecutableWidget var2, int var3);
}
