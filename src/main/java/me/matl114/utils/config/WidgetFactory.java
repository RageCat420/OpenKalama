package me.matl114.utils.config;

import me.matl114.gui.basic.DrawableWidget;

public interface WidgetFactory<T> {
   DrawableWidget generateWidget(T var1, int var2, int var3, int var4, int var5);
}
