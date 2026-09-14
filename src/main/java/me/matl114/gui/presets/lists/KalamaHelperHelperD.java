package me.matl114.gui.presets.lists;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import me.matl114.gui.FilterService;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.ScrollableListWidget;
import me.matl114.utils.config.ValueAccessor;

public class KalamaHelperHelperD<W> extends ScrollableListWidget {
    boolean W = true;
    BiPredicate<W, String> aQ;
    List<W> b;
    int aS;
    protected W aR;
    Function<W, RenderHandler> aU;
    ValueAccessor<String> aV;
    List<W> aT;

    public KalamaHelperHelperD<W> cf(List<W> filterList) {
        this.aT = filterList;
        return this;
    }

    protected void af() {
        int var1 = Math.min(20, this.aS);
        this.am.Q(FilterService.b(this.aV, this::bT, 1, -var1 + 1, this.dx - 2, var1 - 2));
        this.bT();
    }

    public KalamaHelperHelperD<W> bQ(BiPredicate<W, String> fil) {
        if (this.aQ != fil) {
            this.aQ = fil;
            this.bT();
        }

        return this;
    }

    public KalamaHelperHelperD<W> cc(W selected) {
        this.aR = (W) selected;
        return this;
    }

    public W bU() {
        return this.aR;
    }

    public KalamaHelperHelperD<W> ci(ValueAccessor<String> filterInput) {
        this.aV = filterInput;
        return this;
    }

    public List<W> bV() {
        return this.b;
    }

    public BiPredicate<W, String> bZ() {
        return this.aQ;
    }

    public KalamaHelperHelperD<W> cg(Function<W, RenderHandler> renderFactory) {
        this.aU = renderFactory;
        return this;
    }

    public int bW() {
        return this.aS;
    }

    public KalamaHelperHelperD<W> ce(int entryHeight) {
        this.aS = entryHeight;
        return this;
    }

    protected void bR() {
        this.aU();
        int var1 = this.aT.size();
        boolean var2 = false;

        for (int var3 = 0; var3 < var1; var3++) {
            this.addScrollingWidget(this.bS(var3));
            if (this.aT.get(var3) == this.aR) {
                var2 = true;
            }
        }

        if (!var2) {
            this.aR = null;
        }
    }

    public KalamaHelperHelperD<W> cd(List<W> list) {
        this.b = list;
        return this;
    }

    public KalamaHelperHelperD(
            List<W> lst,
            Function<W, RenderHandler> renderFactory,
            ValueAccessor<String> filterInput,
            BiPredicate<W, String> filter,
            int x,
            int y,
            int dx,
            int dy,
            int height) {
        super(x, y, dx, dy);
        this.b = lst;
        this.aS = height;
        this.aQ = filter;
        this.aU = renderFactory;
        this.aV = filterInput;
        this.af();
    }

    public ValueAccessor<String> cb() {
        return this.aV;
    }

    public Function<W, RenderHandler> bY() {
        return this.aU;
    }

    public boolean ca() {
        return this.W;
    }

    public KalamaHelperHelperD<W> ch(boolean modifiable) {
        this.W = modifiable;
        return this;
    }

    protected DrawableWidget bS(int index) {
        Object var2 = this.aT.get(index);
        ExecutableWidget var3 = ExecutableWidget.instance(0, this.aS * index, this.dx, this.aS);
        RenderHandler var4 = this.aU.apply((W) var2);
        var4 = var4.l((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
            if (var2 == this.aR) {
                RenderHandler.L(context, 0, 0, this.dx, this.aS, -1);
            }
        });
        KalamaHelperHelperP var5 = KalamaHelperHelperP.aA(() -> {
            if (this.W) {
                this.aR = (W) var2;
            }
        });
        var3.<ExecutableWidget>eT(var5).setRenderHandler(var4);
        return var3;
    }

    public KalamaHelperHelperD<W> bP(Predicate<W> fil) {
        return this.bQ((s, b) -> fil.test(s));
    }

    public List<W> bX() {
        return this.aT;
    }

    public void bT() {
        String var1 = this.aV.getValue();
        this.aT = this.b.stream().filter(v -> this.aQ.test((W) v, var1)).toList();
        this.bR();
    }
}
