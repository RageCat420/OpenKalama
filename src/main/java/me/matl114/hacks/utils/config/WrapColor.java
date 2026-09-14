package me.matl114.hacks.utils.config;

import java.awt.Color;
import java.util.Optional;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.config.WrapperFactory;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public record WrapColor(TextColor color) implements NBTParsable<WrapColor> {
    public static WrapColor WHITE = new WrapColor(TextColor.fromFormatting(Formatting.WHITE));
    public static NBTType<WrapColor> TYPE =
            NBTTypes.createXMap("wrapcolor", NBTTypes.h, WrapperFactory.of(WrapColor::new, WrapColor::color));

    public WrapColor(Formatting formatting) {
        this(ColorUtils.color(formatting));
    }

    public WrapColor(Color color) {
        this(ColorUtils.o(color));
    }

    public WrapColor(String string) {
        this(ColorUtils.p(string));
    }

    @Override
    public NBTType<WrapColor> type() {
        return TYPE;
    }

    public int asRGB() {
        return this.color.getRgb();
    }

    public int withAlpha(int alpha) {
        return ColorUtils.j(this.color.getRgb(), alpha);
    }

    @Override
    public <W> Optional<WrapColor> tryTypeConvert(Ref<W> ref) {
        return ref instanceof IntRef intRef
                ? Optional.of(new WrapColor(TextColor.fromRgb(intRef.get())))
                : Optional.empty();
    }
}
