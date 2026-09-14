package me.matl114.gui.basic;

public interface KalamaHelperHelperK extends KalamaHelperHelperP {
   @Override
   boolean d(ExecutableWidget var1, int var2, int var3, int var4, boolean var5);

   @Override
   default boolean e(ExecutableWidget widget, char chr, int modifiers) {
      return false;
   }

   @Override
   default boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return false;
   }

   @Override
   default boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      return false;
   }
}
