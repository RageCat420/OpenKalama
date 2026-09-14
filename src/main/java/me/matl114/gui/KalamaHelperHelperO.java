package me.matl114.gui;

import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.TextProvider;
import net.minecraft.text.Text;

public final class KalamaHelperHelperO extends KalamaHelperHelperAX<KalamaHelperHelperO> {
    public ColorSampler r;
    public TextProvider q = TextProvider.c(Text.empty());
    public int alignment;

    public KalamaHelperHelperO J(int alignment) {
        this.alignment = alignment;
        return this;
    }

    public KalamaHelperHelperO I(TextProvider textProvider) {
        this.q = textProvider;
        return this;
    }

    public static KalamaHelperHelperO builder() {
        return new KalamaHelperHelperO();
    }

    public KalamaHelperHelperO G(Text text) {
        this.q = text == null ? null : TextProvider.c(text);
        return this;
    }

    public KalamaHelperHelperO() {
        this.r = ColorSampler.WHITE;
    }

    @Override
    public ElementHandler d(WidgetSupplier factory) {
        return factory.g(this);
    }

    public KalamaHelperHelperO H(int color) {
        this.r = ColorSampler.of(color);
        return this;
    }
}
