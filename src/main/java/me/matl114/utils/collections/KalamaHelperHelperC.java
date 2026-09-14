package me.matl114.utils.collections;

import java.util.Iterator;

class KalamaHelperHelperC<V> implements Iterator<V> {
    final Iterator<V> a;
    final DirtyCollectionImpl b;

    KalamaHelperHelperC(final DirtyCollectionImpl var1, Iterator<V> delegate) {
        this.b = var1;
        this.a = delegate;
    }

    @Override
    public void remove() {
        this.b.d();
        this.a.remove();
    }

    @Override
    public boolean hasNext() {
        return this.a.hasNext();
    }

    @Override
    public V next() {
        return (V) (Object) this.a.next();
    }
}
