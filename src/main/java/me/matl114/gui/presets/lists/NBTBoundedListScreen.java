package me.matl114.gui.presets.lists;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.presets.choices.ConfirmingBigScreen;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WidgetFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NBTBoundedListScreen<W, T> extends ConfirmingBigScreen {
    int g;
    int f;
    List<Pair<W, AttrKeyValue<T>>> b;
    Consumer<Map<W, T>> c;
    final Predicate<Map<W, T>> a;
    int e;
    ListEntryWidgetController h;
    final BiFunction<String, T, AttrKeyValue<T>> d;

    private Map<W, T> listMap() {
        LinkedHashMap var1 = new LinkedHashMap();

        for (Pair var3 : this.b) {
            var1.put(var3.getFirst(), ((AttrKeyValue) var3.getSecond()).getOriginValue());
        }

        return var1;
    }

    @Override
    protected void c() {
        Map var1 = this.listMap();
        if (this.a.test(var1)) {
            this.c.accept(var1);
            this.close();
        }
    }

    public NBTBoundedListScreen(
            AttrKeyValue<Map<W, T>> attrKeyValue,
            NBTType<T> type,
            WidgetFactory<W> keyWidgetFactory,
            Consumer<Map<W, T>> callback,
            int dkey,
            int dx,
            int dy) {
        this(
                (Map<W, T>) attrKeyValue.getOriginValue(),
                attrKeyValue::isValueValid,
                type::createAttrKeyValue,
                keyWidgetFactory,
                type::generateValueWidget,
                callback,
                dkey,
                dx,
                dy);
    }

    @Override
    protected boolean canConfirm(ElementHandler elementHandler) {
        LinkedHashMap var2 = new LinkedHashMap();

        for (Pair var4 : this.b) {
            if (!((AttrKeyValue) var4.getSecond()).isValidate()) {
                return false;
            }

            var2.put(var4.getFirst(), ((AttrKeyValue) var4.getSecond()).getOriginValue());
        }

        return this.a.test(var2);
    }

    public NBTBoundedListScreen(
            Map<W, T> list,
            Predicate<Map<W, T>> listValidator,
            BiFunction<String, T, AttrKeyValue<T>> attrElementFactory,
            WidgetFactory<W> keyWidgetFactory,
            WidgetFactory<AttrKeyValue<T>> valueWidgetFactory,
            Consumer<Map<W, T>> callback,
            int dkey,
            int dx,
            int dy) {
        super(Text.translatable("widget.gui.nbt-bounded-list-screen.title").formatted(Formatting.GREEN));
        this.a = listValidator;
        this.d = attrElementFactory;
        this.b = list.entrySet().stream()
                .map(s -> Pair.of(s.getKey(), this.d.apply("", (T) s.getValue())))
                .collect(Collectors.toCollection(ArrayList::new));
        this.c = callback;
        this.e = dkey;
        this.f = dx;
        this.g = dy;
        this.h = ListEntryWidgetController.immutable(
                this.b,
                w -> new KalamaHelperHelperCX(0, 0, this.f, this.g)
                        .Q(keyWidgetFactory.generateWidget(w.getFirst(), 0, 0, this.e, this.g))
                        .Q(valueWidgetFactory.generateWidget(
                                (AttrKeyValue) w.getSecond(), this.e, 0, this.f - this.e, this.g)),
                this.g,
                this.f);
    }

    @Override
    protected void init() {
        super.init();
        int var1 = this.f;
        new ListUnmodifiableWidget(
                        this.h,
                        this.x + (this.backgroundWidth - var1) / 2,
                        this.y + CONTENT_START_Y,
                        var1,
                        this.content_end_y - CONTENT_START_Y)
                .addTo(this);
    }
}
