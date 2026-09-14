package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.text.Text;

public record TracingOption(boolean box, boolean line) implements NBTParsable<TracingOption> {
    public static final PairLikeFactory<Boolean, Boolean, TracingOption> PAIR_FACTORY =
            PairLikeFactory.of(TracingOption::new, TracingOption::box, TracingOption::line);
    public static final NBTType<TracingOption> TYPE = new NBTType<>(
            "tracingoption",
            RecordCodecBuilder.create(s -> s.group(
                            Codec.BOOL.fieldOf("box").forGetter(TracingOption::box),
                            Codec.BOOL.fieldOf("line").forGetter(TracingOption::line))
                    .apply(s, TracingOption::new)),
            (s, x, y, dx, dy) -> {
                KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
                return subScreenWidget
                        .Q(DisplayWidget.instance(0, 0, 2 * dy, dy)
                                .setRenderHandler(new ButtonElement(
                                                TextProvider.c(Text.translatableWithFallback(
                                                        "widget.nbt-parsable.tracing-option.box", "Box:")),
                                                ButtonAction.c())
                                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                                "widget.nbt-parsable.tracing-option.box.tooltips", "")))))
                        .Q(new TypeConvertAttrKeyValue<>(s, PAIR_FACTORY.asFirstWrapper(s::getOriginValue), NBTTypes.f)
                                .generateValueWidget(2 * dy, 0, dy, dy))
                        .Q(DisplayWidget.instance(3 * dy, 0, 2 * dy, dy)
                                .setRenderHandler(new ButtonElement(
                                                TextProvider.c(Text.translatableWithFallback(
                                                        "widget.nbt-parsable.tracing-option.line", "Line:")),
                                                ButtonAction.c())
                                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                                "widget.nbt-parsable.tracing-option.line.tooltips", "")))))
                        .Q(new TypeConvertAttrKeyValue<>(s, PAIR_FACTORY.asSecondWrapper(s::getOriginValue), NBTTypes.f)
                                .generateValueWidget(5 * dy, 0, dy, dy));
            },
            new TracingOption(false, false));

    @Override
    public NBTType<TracingOption> type() {
        return TYPE;
    }

    public boolean isEmpty() {
        return !this.box && !this.line;
    }
}
