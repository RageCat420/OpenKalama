package me.matl114.utils.config;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface PropertyTracker<OWNER, PROP> {
    PropertyTracker EMPTY = new PropertyTracker() {
        @Override
        public void valueChange(Object object, Object object2) {}
    };

    void valueChange(OWNER var1, PROP var2);

    static <T, W> PropertyTracker<T, W> empty() {
        return EMPTY;
    }

    static <T, W> PropertyTracker<T, W> event(Consumer<W> ob) {
        return new PropertyTracker<T, W>() {
            @Override
            public void valueChange(T t, W w) {
                ob.accept(w);
            }
        };
    }

    static <T, W> PropertyTracker<T, W> event(BiConsumer<T, W> ob) {
        return new PropertyTracker<T, W>() {
            @Override
            public void valueChange(T t, W w) {
                ob.accept(t, w);
            }
        };
    }
}
