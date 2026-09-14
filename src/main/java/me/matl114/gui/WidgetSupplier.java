package me.matl114.gui;

import me.matl114.gui.basic.ElementHandler;

public interface WidgetSupplier {
    WidgetSupplier DEFAULT = DefaultWidgetSupplier.INSTANCE;

    static KalamaHelperHelperZ d() {
        return KalamaHelperHelperZ.builder();
    }

    ElementHandler f(KalamaHelperHelperL var1);

    ElementHandler h(KalamaHelperHelperZ var1);

    static KalamaHelperHelperS a() {
        return KalamaHelperHelperS.builder();
    }

    ElementHandler e(KalamaHelperHelperS var1);

    ElementHandler g(KalamaHelperHelperO var1);

    static KalamaHelperHelperL b() {
        return KalamaHelperHelperL.builder();
    }

    static KalamaHelperHelperO c() {
        return KalamaHelperHelperO.builder();
    }
}
