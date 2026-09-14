package me.matl114.gui.basic;

import java.util.function.Predicate;

public interface ElementHandler extends KalamaHelperHelperP, RenderHandler {
    default ElementHandler ah(Predicate<ElementHandler> handlerPredicate) {
        return new KalamaHelperHelperX(this, handlerPredicate, this);
    }

    default ElementHandler af(TooltipHandler handler) {
        this.m(handler);
        return this;
    }

    default ElementHandler ag(Predicate<ElementHandler> handlerPredicate) {
        return new KalamaHelperHelperC(this, handlerPredicate, this);
    }
}
