package me.matl114.gui.basic;

public interface KalamaHelperHelperY extends KalamaHelperHelperP {
   boolean onAction(ExecutableWidget var1, double var2, double var4, int var6, KalamaHelperHelperM var7);

   default boolean onClick(ExecutableWidget element, double mouseX, double mouseY, int button) {
      throw new IllegalStateException();
   }
}
