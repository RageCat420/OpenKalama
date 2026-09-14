package me.matl114.gui.presets.lists;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import me.matl114.gui.FilterService;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.KalamaHelperHelperK;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.ScrollableListWidget;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.ValueAccessor;

public class KalamaHelperHelperI<W> extends ScrollableListWidget {
    boolean W = true;
    ValueAccessor<String> aV;
    int aS;
    List<Entry<W, AttrKeyValue<Boolean>>> aT;
    Map<W, AttrKeyValue<Boolean>> dh;
    KalamaHelperHelperK<W> bh;
    boolean dj = false;
    BiFunction<W, AttrKeyValue<Boolean>, RenderHandler> di;

    public int bW() {
        return this.aS;
    }

    public ValueAccessor<String> cb() {
        return this.aV;
    }

    public KalamaHelperHelperI<W> er(BiFunction<W, AttrKeyValue<Boolean>, RenderHandler> renderFactory) {
        this.di = renderFactory;
        return this;
    }

    protected void af() {
        int var1 = Math.min(20, this.aS);
        this.am.Q(FilterService.d(
                this.aV,
                ValueAccessor.of(() -> this.dj, bl -> this.dj = bl),
                this::bT,
                1,
                -var1 + 1,
                this.dx - 1 - 2 * var1,
                var1 - 2));
        KalamaHelperHelperCX var2 = new KalamaHelperHelperCX(this.dx - 2 * var1, -var1 + 1, 2 * var1, var1 - 2);
        var2.Q(ExecutableWidget.instance(1, 0, var1 - 2, var1 - 2)
                .eV(IconElement.cm(KalamaHelperHelperB.f, ButtonAction.a(this::ei))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.list-multi-select-widget.select-all.tooltips", "")))));
        var2.Q(ExecutableWidget.instance(1 + var1, 0, var1 - 2, var1 - 2)
                .eV(IconElement.cm(KalamaHelperHelperB.h, ButtonAction.a(this::ej))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.list-multi-select-widget.unselect-all.tooltips", "")))));
        this.am.Q(var2);
        this.bT();
    }

    public KalamaHelperHelperI<W> ep(int entryHeight) {
        this.aS = entryHeight;
        return this;
    }

    public List<Entry<W, AttrKeyValue<Boolean>>> bX() {
        return this.aT;
    }

    protected void ei() {
        this.aT.forEach(s -> s.getValue().valueChangeInternal(null, true));
    }

    public boolean en() {
        return this.dj;
    }

    protected DrawableWidget bS(int index) {
        Entry var2 = this.aT.get(index);
        AttrKeyValue var3 = (AttrKeyValue) var2.getValue();
        ExecutableWidget var4 = ExecutableWidget.instance(0, this.aS * index, this.dx, this.aS);
        RenderHandler var5 = this.di.apply((W) var2.getKey(), var3);
        var5 = var5.l((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
            if (var3.getOriginValue() == Boolean.TRUE) {
                RenderHandler.L(context, 0, 0, this.dx, this.aS, -1);
            }
        });
        KalamaHelperHelperP var6 = KalamaHelperHelperP.aA(() -> {
            if (this.W) {
                if (var3.getOriginValue() == Boolean.TRUE) {
                    var3.valueChange(null, "false");
                } else {
                    var3.valueChange(null, "true");
                }
            }
        });
        var4.<ExecutableWidget>eT(var6).setRenderHandler(var5);
        return var4;
    }

    public KalamaHelperHelperI<W> es(KalamaHelperHelperK<W> filter) {
        this.bh = filter;
        return this;
    }

    public KalamaHelperHelperI<W> eu(boolean modifiable) {
        this.W = modifiable;
        return this;
    }

    public KalamaHelperHelperI<W> ev(boolean useRegexFilter) {
        this.dj = useRegexFilter;
        return this;
    }

    protected void bT() {
        Comparator<Map.Entry<W, AttrKeyValue<Boolean>>> var1 = (o1, o2) -> {
            if (o1.getValue().getOriginValue() && !o2.getValue().getOriginValue()) {
                return -1;
            } else {
                return !o1.getValue().getOriginValue() && o2.getValue().getOriginValue() ? 1 : 0;
            }
        };
        this.aT = this.dh.entrySet().stream()
                .filter(i -> this.bh == null || this.bh.isAccepted(i.getKey(), this.aV.getValue(), this.dj))
                .sorted(var1)
                .toList();
        this.bR();
    }

    public Map<W, AttrKeyValue<Boolean>> ek() {
        return this.dh;
    }

    public Set<W> eh() {
        return this.dh.entrySet().stream()
                .filter(i -> i.getValue().getOriginValue() == Boolean.TRUE)
                .map(Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public KalamaHelperHelperI<W> et(ValueAccessor<String> filterInput) {
        this.aV = filterInput;
        return this;
    }

    protected void bR() {
        this.aU();
        int var1 = this.aT.size();

        for (int var2 = 0; var2 < var1; var2++) {
            this.addScrollingWidget(this.bS(var2));
        }
    }

    public KalamaHelperHelperI<W> eo(Map<W, AttrKeyValue<Boolean>> list) {
        this.dh = list;
        return this;
    }

    public KalamaHelperHelperI<W> eq(List<Entry<W, AttrKeyValue<Boolean>>> filterList) {
        this.aT = filterList;
        return this;
    }

    public BiFunction<W, AttrKeyValue<Boolean>, RenderHandler> el() {
        return this.di;
    }

    public boolean ca() {
        return this.W;
    }

    public KalamaHelperHelperK<W> em() {
        return this.bh;
    }

    protected void ej() {
        this.aT.forEach(s -> s.getValue().valueChangeInternal(null, false));
    }

    public KalamaHelperHelperI(
            List<W> lst,
            Set<W> currentSelection,
            BiFunction<W, AttrKeyValue<Boolean>, RenderHandler> renderFactory,
            ValueAccessor<String> filterInput,
            KalamaHelperHelperK<W> filter,
            int x,
            int y,
            int dx,
            int dy,
            int height) {
        super(x, y, dx, dy);
        this.dh = new LinkedHashMap<>();

        for (Object var12 : lst) {
            this.dh.put(
                    (W) var12,
                    AttrKeyValue.bool(
                            "widget.gui.list-multi-select-widget.selected", currentSelection.contains(var12)));
            if (currentSelection.contains(var12)) {
                Debug.e("contains", var12);
            }
        }

        this.aS = height;
        this.bh = filter;
        this.aV = filterInput;
        this.di = renderFactory;
        this.af();
    }
}
