package me.matl114.gui.complex;

import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class RawTextElement extends AbstractElement {
    protected final ColorSampler f;
    protected final TextProvider e;
    protected int alignment;

    public RawTextElement(Text text, int color, int alignment) {
        this(TextProvider.c(text), color, alignment);
    }

    public RawTextElement(TextProvider provider, ColorSampler color, int alignment) {
        this.e = provider;
        this.f = color;
        this.alignment = alignment;
    }

    public RawTextElement setAlignment(int alignment) {
        this.alignment = alignment;
        return this;
    }

    public static RawTextElement g(Text text) {
        return new RawTextElement(text, -1);
    }

    public RawTextElement(TextProvider provider, int color, int alignment) {
        this(provider, ColorSampler.of(color), alignment);
    }

    public void renderCentered0(
            DrawableWidget element,
            VDrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            float alpha,
            boolean shouldHighlight) {
        OrderedText var8 = this.e.getLabel(element);
        if (var8 != null) {
            RenderHandler.drawScaledText0(
                    context,
                    mc.textRenderer,
                    var8,
                    0,
                    0,
                    element.getTextureWidth(),
                    element.getTextureHeight(),
                    this.f.getColorInt(),
                    this.alignment);
        }
    }

    public static RawTextElement h(TextProvider text) {
        return new RawTextElement(text, -1, 0);
    }

    public RawTextElement(Text text, int color) {
        this(text, color, 0);
    }
}
