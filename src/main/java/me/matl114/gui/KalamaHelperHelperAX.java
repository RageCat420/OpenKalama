package me.matl114.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.basic.TooltipHandler$TooltipProvider;
import net.minecraft.text.Text;

public abstract class KalamaHelperHelperAX<B extends KalamaHelperHelperAX<B>> {
    public Predicate<ElementHandler> g;
    public List<KalamaHelperHelperP> f;
    public boolean b;
    public TooltipHandler c;
    public WidgetSupplier a = WidgetSupplier.DEFAULT;
    public List<RenderHandler> e;
    public Predicate<ElementHandler> h;
    public List<RenderHandler> d;

    public ElementHandler c() {
        return this.d(this.a());
    }

    public KalamaHelperHelperAX<B> r(List<KalamaHelperHelperP> inputHandlers) {
        this.f = inputHandlers;
        return this;
    }

    public KalamaHelperHelperAX<B> t(Predicate<ElementHandler> activeActionCondition) {
        this.h = activeActionCondition;
        return this;
    }

    public abstract ElementHandler d(WidgetSupplier var1);

    public KalamaHelperHelperAX<B> showTooltips(boolean showTooltips) {
        this.b = showTooltips;
        return this;
    }

    public B g(TooltipHandler$TooltipProvider tooltips) {
        this.c = tooltips == null ? null : TooltipHandler.aq(tooltips);
        return this.b();
    }

    protected final WidgetSupplier a() {
        return this.a == null ? WidgetSupplier.DEFAULT : this.a;
    }

    public B k(Predicate<ElementHandler> presentCondition) {
        this.g = presentCondition;
        return this.b();
    }

    public B l(Predicate<ElementHandler> activeActionCondition) {
        this.h = activeActionCondition;
        return this.b();
    }

    public B e(List<Text> tooltips) {
        this.c = tooltips == null ? null : TooltipHandler.ap(tooltips);
        return this.b();
    }

    public B f(Supplier<List<Text>> tooltips) {
        this.c = tooltips == null ? null : TooltipHandler.ar(tooltips);
        return this.b();
    }

    protected final B b() {
        return (B) (Object) this;
    }

    public KalamaHelperHelperAX<B> q(List<RenderHandler> absoluteRenders) {
        this.e = absoluteRenders;
        return this;
    }

    public KalamaHelperHelperAX<B> p(List<RenderHandler> extraRenders) {
        this.d = extraRenders;
        return this;
    }

    public KalamaHelperHelperAX<B> s(Predicate<ElementHandler> presentCondition) {
        this.g = presentCondition;
        return this;
    }

    public KalamaHelperHelperAX<B> o(TooltipHandler tooltipHandler) {
        this.c = tooltipHandler;
        return this;
    }

    public B h(RenderHandler renderHandler) {
        if (renderHandler != null) {
            if (this.d == null) {
                this.d = new ArrayList<>();
            }

            this.d.add(renderHandler);
        }

        return this.b();
    }

    public B i(RenderHandler renderHandler) {
        if (renderHandler != null) {
            if (this.e == null) {
                this.e = new ArrayList<>();
            }

            this.e.add(renderHandler);
        }

        return this.b();
    }

    public KalamaHelperHelperAX() {
        this.b = true;
        this.d = new ArrayList<>();
        this.e = new ArrayList<>();
        this.f = new ArrayList<>();
    }

    public B j(KalamaHelperHelperP inputHandler) {
        if (inputHandler != null) {
            if (this.f == null) {
                this.f = new ArrayList<>();
            }

            this.f.add(inputHandler);
        }

        return this.b();
    }

    public KalamaHelperHelperAX<B> m(WidgetSupplier factory) {
        this.a = factory;
        return this;
    }
}
