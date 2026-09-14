package me.matl114.utils.collections;

import java.util.List;
import java.util.ListIterator;

public class KalamaHelperHelperR<T> extends KalamaHelperHelperQ<T> implements ListIterator<T> {
    int lastVisit = -1;

    @Override
    public void remove() {
        if (this.lastVisit == -1) {
            throw new IllegalStateException("No element to remove");
        } else {
            this.delegate.remove(this.lastVisit);
            if (this.lastVisit < this.index) {
                this.index--;
            }

            this.lastVisit = -1;
        }
    }

    @Override
    public int previousIndex() {
        return this.index - 1;
    }

    @Override
    public void set(T t) {
        if (this.lastVisit == -1) {
            throw new IllegalStateException("No element to set");
        } else {
            this.delegate.set(this.lastVisit, (T) t);
        }
    }

    public KalamaHelperHelperR(List<T> delegate) {
        super(delegate);
    }

    @Override
    public int nextIndex() {
        return this.index;
    }

    @Override
    public T next() {
        this.lastVisit = this.index;
        return super.next();
    }

    @Override
    public T previous() {
        this.lastVisit = --this.index;
        return this.delegate.get(this.lastVisit);
    }

    public KalamaHelperHelperR(List<T> delegate, int index) {
        super(delegate);
        this.index = index;
    }

    @Override
    public void add(T t) {
        this.delegate.add(this.index, (T) t);
        this.index++;
        this.lastVisit = -1;
    }

    @Override
    public boolean hasPrevious() {
        return this.index >= 0;
    }
}
