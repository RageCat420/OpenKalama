package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.presets.single.WidgetPosSelectScreen;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.collections.KalamaHelperHelperM;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class WidgetPos implements NBTParsable<WidgetPos> {
    public final int type;
    public final double percentageX;
    public final double percentageY;
    public final int lengthX;
    public final int lengthY;
    public static final WidgetPos EMPTY = new WidgetPos(0, 0.0, 0.0, 0, 0);
    public static final MinecraftClient bj = MinecraftClient.getInstance();
    public static NBTType<WidgetPos> TYPE = new NBTType<>(
            "widgetpos",
            RecordCodecBuilder.create(oInstance -> oInstance
                    .group(
                            Codec.INT.fieldOf("type").forGetter(WidgetPos::getType),
                            Codec.DOUBLE.fieldOf("percentageX").forGetter(WidgetPos::getPercentageX),
                            Codec.DOUBLE.fieldOf("percentageY").forGetter(WidgetPos::getPercentageY),
                            Codec.INT.fieldOf("lengthX").forGetter(WidgetPos::getLengthX),
                            Codec.INT.fieldOf("lengthY").forGetter(WidgetPos::getLengthY))
                    .apply(oInstance, WidgetPos::new)),
            (w, x, y, dx, dy) -> {
                KalamaHelperHelperCX widget = new KalamaHelperHelperCX(x, y, dx, dy);
                widget.Q(ExecutableWidget.instance(0, 0, 2 * dy, dy)
                        .eV(new ButtonElement(
                                el -> {
                                    return switch (((WidgetPos) w.getOriginValue()).getType()) {
                                        case 0 -> Text.translatableWithFallback(
                                                "widget.nbt-parsable.widget-pos.percentage", "Per");
                                        case 1 -> Text.translatableWithFallback(
                                                "widget.nbt-parsable.widget-pos.absolute-length", "Abs");
                                        default -> Text.empty();
                                    };
                                },
                                ButtonAction.a(() -> {
                                    int total = 2;
                                    int type = ((WidgetPos) w.getOriginValue()).getType();
                                    w.valueChangeInternal(
                                            null, ((WidgetPos) w.getOriginValue()).withType((type + 1) % total));
                                }))));
                TypeConvertAttrKeyValue<WidgetPos, Vec2> percentageSel = new TypeConvertAttrKeyValue<>(
                        w,
                        WrapperFactory.of(
                                s -> ((WidgetPos) w.getOriginValue()).withPercentage(s), WidgetPos::toPercentage),
                        NBTTypes.p);
                KalamaHelperHelperCX percentage1 = new KalamaHelperHelperCX(0, 0, dx - 2 * dy, dy);
                percentage1.Q(percentageSel.generateValueWidget(0, 0, dx - 3 * dy, dy));
                percentage1.Q(generateWidgetPosSelectScreenButton(
                        dx - 3 * dy, 0, dy, dy, () -> ((WidgetPos) w.getOriginValue()).getFPoint(), el -> {
                            int width = bj.getWindow().getScaledWidth();
                            int height = bj.getWindow().getScaledHeight();
                            double mulWidth = el.a * 100.0 / width;
                            double mulHeight = el.b * 100.0 / height;
                            Vec2 percentage2x = new Vec2(
                                    MathHelper.clamp(Math.round(mulWidth) / 100.0, 0.0, 1.0),
                                    MathHelper.clamp(Math.round(mulHeight) / 100.0, 0.0, 1.0));
                            percentageSel.valueChangeInternal(null, percentage2x);
                        }));
                TypeConvertAttrKeyValue<WidgetPos, Vec2> absoluteSel = new TypeConvertAttrKeyValue<>(
                        w,
                        WrapperFactory.of(s -> ((WidgetPos) w.getOriginValue()).withLength(s), WidgetPos::toLength),
                        NBTTypes.p);
                KalamaHelperHelperCX percentage2 = new KalamaHelperHelperCX(0, 0, dx - 2 * dy, dy);
                percentage2.Q(absoluteSel.generateValueWidget(0, 0, dx - 3 * dy, dy));
                percentage2.Q(generateWidgetPosSelectScreenButton(
                        dx - 3 * dy,
                        0,
                        dy,
                        dy,
                        () -> ((WidgetPos) w.getOriginValue()).getFPoint(),
                        el -> absoluteSel.valueChangeInternal(null, new Vec2(el.a, el.b))));
                KalamaHelperHelperJ<DrawableWidget> showWidget = new KalamaHelperHelperJ<>(
                        () -> {
                            return switch (((WidgetPos) w.getOriginValue()).getType()) {
                                case 0 -> percentage1;
                                case 1 -> percentage2;
                                default -> null;
                            };
                        },
                        2 * dy,
                        0);
                widget.Q(showWidget);
                return widget;
            },
            EMPTY);

    public static DrawableWidget generateWidgetPosSelectScreenButton(
            int x,
            int y,
            int dx,
            int dy,
            Supplier<KalamaHelperHelperM> current,
            Consumer<KalamaHelperHelperM> consumer) {
        return ExecutableWidget.instance(x, y, dx, dy)
                .eV(IconElement.cm(KalamaHelperHelperB.g, ButtonAction.a(() -> ScreenAccess.of(
                                        new WidgetPosSelectScreen(320, current.get(), consumer))
                                .openFromCurrent()))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.nbt-parsable.widget-pos.open-select-screen.tooltips", ""))));
    }

    public WidgetPos withPercentage(Vec2 vec2) {
        return new WidgetPos(this.type, vec2.x(), vec2.y(), this.lengthX, this.lengthY);
    }

    public WidgetPos withLength(Vec2 vec2) {
        return new WidgetPos(this.type, this.percentageX, this.percentageY, (int) vec2.x(), (int) vec2.y());
    }

    public Vec2 toPercentage() {
        return new Vec2(this.percentageX, this.percentageY);
    }

    public Vec2 toLength() {
        return new Vec2(this.lengthX, this.lengthY);
    }

    public int getWindowX(Window window) {
        if (this.type == 0) {
            return (int) (window.getScaledWidth() * this.percentageX);
        } else {
            return this.type == 1 ? this.lengthX : 0;
        }
    }

    public double getWindowXFloat(Window window) {
        if (this.type == 0) {
            return window.getScaledWidth() * this.percentageX;
        } else {
            return this.type == 1 ? this.lengthX : 0.0;
        }
    }

    public KalamaHelperHelperM getFPoint() {
        return new KalamaHelperHelperM(this.getWindowXFloat(bj.getWindow()), this.getWindowYFloat(bj.getWindow()));
    }

    public int getWindowY(Window window) {
        if (this.type == 0) {
            return (int) (window.getScaledHeight() * this.percentageY);
        } else {
            return this.type == 1 ? this.lengthY : 0;
        }
    }

    public double getWindowYFloat(Window window) {
        if (this.type == 0) {
            return window.getScaledHeight() * this.percentageY;
        } else {
            return this.type == 1 ? this.lengthY : 0.0;
        }
    }

    @Override
    public <W> Optional<WidgetPos> tryTypeConvert(Ref<W> ref) {
        if (!(ref instanceof NBTRef<?> ref2 && ref2.get() instanceof Vec2 legacy)) {
            return Optional.empty();
        } else {
            return this.type == 0 ? Optional.of(this.withPercentage(legacy)) : Optional.of(this.withLength(legacy));
        }
    }

    @Override
    public NBTType<WidgetPos> type() {
        return TYPE.cast();
    }

    public int getType() {
        return this.type;
    }

    public double getPercentageX() {
        return this.percentageX;
    }

    public double getPercentageY() {
        return this.percentageY;
    }

    public int getLengthX() {
        return this.lengthX;
    }

    public int getLengthY() {
        return this.lengthY;
    }

    public WidgetPos(int type, double percentageX, double percentageY, int lengthX, int lengthY) {
        this.type = type;
        this.percentageX = percentageX;
        this.percentageY = percentageY;
        this.lengthX = lengthX;
        this.lengthY = lengthY;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WidgetPos other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.getType() != other.getType()) {
            return false;
        } else if (Double.compare(this.getPercentageX(), other.getPercentageX()) != 0) {
            return false;
        } else if (Double.compare(this.getPercentageY(), other.getPercentageY()) != 0) {
            return false;
        } else {
            return this.getLengthX() != other.getLengthX() ? false : this.getLengthY() == other.getLengthY();
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof WidgetPos;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getType();
        long $percentageX = Double.doubleToLongBits(this.getPercentageX());
        result = result * 59 + (int) ($percentageX >>> 32 ^ $percentageX);
        long $percentageY = Double.doubleToLongBits(this.getPercentageY());
        result = result * 59 + (int) ($percentageY >>> 32 ^ $percentageY);
        result = result * 59 + this.getLengthX();
        return result * 59 + this.getLengthY();
    }

    public WidgetPos withType(int type) {
        return this.type == type
                ? this
                : new WidgetPos(type, this.percentageX, this.percentageY, this.lengthX, this.lengthY);
    }
}
