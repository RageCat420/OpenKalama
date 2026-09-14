package me.matl114.gui.basic;

public interface KalamaHelperHelperK extends KalamaHelperHelperP {
   boolean onKey(ExecutableWidget var1, int var2, int var3, int var4, boolean var5);

   default boolean onTyped(ExecutableWidget widget, char chr, int modifiers) {
      return false;
   }

   default boolean onClick(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return false;
   }

   default boolean onAction(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      return false;
   }
}
