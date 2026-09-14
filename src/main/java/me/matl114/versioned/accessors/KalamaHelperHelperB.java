package me.matl114.versioned.accessors;

import java.util.function.BiConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.render.RenderTickCounter;

public interface KalamaHelperHelperB {
    static KalamaHelperHelperB of(LayeredDrawer drawer) {
        return (KalamaHelperHelperB) drawer;
    }

    void setPos(BiConsumer<DrawContext, RenderTickCounter> var1);
}
