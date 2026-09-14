package me.matl114.gui.basic;

public interface KalamaHelperHelperQ extends KalamaHelperHelperK {
   @Override
   default boolean d(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      return isPress && this.onPress(widget, keyCode);
   }

   boolean onPress(ExecutableWidget var1, int var2);
}
