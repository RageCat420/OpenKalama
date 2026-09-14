package me.matl114.utils.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class DirtyCollectionImpl<S extends Collection<V>, V> implements Collection<V>, Set<V> {
    private volatile boolean dirty = false;
    protected final S delegate;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Set var2 && var2.equals(this.delegate);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean add(V v) {
        if (this.delegate.add((V) v)) {
            this.d();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        if (!this.delegate.isEmpty()) {
            this.d();
            this.delegate.clear();
        }
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    public S f() {
        return this.delegate;
    }

    public void d() {
        this.setDirty(true);
    }

    public DirtyCollectionImpl(S value) {
        this.delegate = (S) value;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        if (this.delegate.addAll(c)) {
            this.d();
            return true;
        } else {
            return false;
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean remove(Object o) {
        if (this.delegate.remove(o)) {
            this.d();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if (this.delegate.removeAll(c)) {
            this.d();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return new KalamaHelperHelperC(this, this.delegate.iterator());
    }

    public boolean e() {
        return this.dirty;
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if (this.delegate.retainAll(c)) {
            this.d();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return (T[]) this.delegate.toArray(a);
    }
}
