package me.matl114.accessors.gui;

import java.util.function.Consumer;
import me.matl114.accessors.interfaces.MetadataHolder;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenAccess extends MetadataHolder {
    <T extends Element & Drawable & Selectable> T addDrawableChildTo(T var1);

    void removeChildFrom(Element var1);

    static ScreenAccess of(Screen screen) {
        return (ScreenAccess) screen;
    }

    Screen getParent();

    void setParent(Screen var1);

    void open();

    void openFromCurrent();

    void openFrom(Screen var1);

    void switchToScreen(Screen var1);

    void switchFromCurrent();

    void addInitTask(Consumer<Screen> var1);

    void addCloseFuture(Runnable var1);
}
