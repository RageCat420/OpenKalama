package me.matl114.gui.basic;

public interface KalamaHelperHelperU extends KalamaHelperHelperP {
    boolean scroll(ExecutableWidget var1, double var2);

    @Override
    default boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
        return false;
    }

    @Override
    default boolean c(
            ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return widget.isMouseOver(mouseX, mouseY) && this.scroll(widget, verticalAmount);
    }

    @Override
    default boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
        return false;
    }
}
