package me.matl114.hooks.impl.xaeroplus.wrapper;

import java.util.function.Function;

public class ElementWrapper<T> {
    T element;
    Function<ElementWrapper<T>, T> elementGenerator;

    public T getElement() {
        if (this.element == null && this.elementGenerator != null) {
            this.element = this.elementGenerator.apply(this);
        }

        return this.element;
    }

    public <W, R extends ElementWrapper<W>> ElementWrapper<W> inject(Function<R, W> function) {
        this.elementGenerator = (Function<ElementWrapper<T>, T>) (Function<?, ?>) function;
        return (ElementWrapper<W>) (Object) this;
    }
}
