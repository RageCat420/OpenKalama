package me.matl114.utils.collections;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class UnmodifiableListMappingIterator<T, W> implements Iterator<W> {
    public final List<T> w;
    public int u = 0;
    public final Function<T, W> x;
    public final int v;

    @Override
    public W next() {
        return this.x.apply(this.w.get(this.u++));
    }

    public UnmodifiableListMappingIterator(List<T> val, Function<T, W> func) {
        this.v = val.size();
        this.x = func;
        this.w = val;
    }

    @Override
    public boolean hasNext() {
        return this.u < this.v;
    }
}
