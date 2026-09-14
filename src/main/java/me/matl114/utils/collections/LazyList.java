package me.matl114.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class LazyList<S extends List<V>, V> implements List<V> {
    protected volatile KalamaHelperHelperH<S> delegate = KalamaHelperHelperH.wD();

    @Override
    public void add(int index, V element) {
        this.b();
        this.delegate.value.add(index, (V) element);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return this.delegate.value.iterator();
    }

    protected void b() {
        if (this.delegate.state) {
            List var1 = this.delegate.value;
            this.delegate = KalamaHelperHelperH.wD();
            this.delegate.value = (S) (new ArrayList(var1));
        }
    }

    @Override
    public V remove(int index) {
        this.b();
        return this.delegate.value.remove(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.b();
        return this.delegate.value.retainAll(c);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Set var2 && var2.equals(this.delegate);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) this.delegate.value.toArray(a);
    }

    @Override
    public int size() {
        return this.delegate.value.size();
    }

    @Override
    public void clear() {
        if (!this.delegate.value.isEmpty()) {
            this.b();
            this.delegate.value.clear();
        }
    }

    @Override
    public boolean remove(Object o) {
        this.b();
        return this.delegate.value.remove(o);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        this.b();
        return this.delegate.value.addAll(c);
    }

    @Override
    public boolean add(V v) {
        this.b();
        return this.delegate.value.add((V) v);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.delegate.value.containsAll(c);
    }

    public LazyList(S value) {
        this.delegate.value = (S) value;
        this.delegate.state = true;
    }

    @Override
    public V get(int index) {
        return this.delegate.value.get(index);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.delegate.value.toArray();
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.value.lastIndexOf(o);
    }

    @Override
    public int hashCode() {
        return this.delegate.value.hashCode();
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.value.indexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<V> listIterator() {
        return new KalamaHelperHelperR<>(this);
    }

    @NotNull
    @Override
    public ListIterator<V> listIterator(int index) {
        return new KalamaHelperHelperR<>(this, index);
    }

    public S c() {
        return this.delegate.value;
    }

    @NotNull
    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        return new KalamaHelperHelperE<>(this, fromIndex, toIndex);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.value.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.b();
        return this.delegate.value.removeAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends V> c) {
        this.b();
        return this.delegate.value.addAll(index, c);
    }

    @Override
    public V set(int index, V element) {
        this.b();
        return this.delegate.value.set(index, (V) element);
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.value.contains(o);
    }
}
