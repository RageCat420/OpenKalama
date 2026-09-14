package me.matl114.gui.basic;

public interface KalamaHelperHelperU extends KalamaHelperHelperP {
   boolean scroll(ExecutableWidget var1, double var2);

   default boolean onAction(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      return false;
   }

   default boolean onScroll(ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return widget.isMouseOver(mouseX, mouseY) && this.scroll(widget, verticalAmount);
   }

   default boolean onClick(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return false;
   }
}
