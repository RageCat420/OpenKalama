package me.matl114.gui.complex.slimefun;

import java.util.List;
import java.util.function.Function;
import me.matl114.gui.basic.DrawableWidget;

class KalamaHelperHelperE<T> extends SlimefunEntryListScreen<T> {
    Function<T, DrawableWidget> gson;

    @Override
    public DrawableWidget cz(T entry) {
        return this.gson.apply(entry);
    }

    KalamaHelperHelperE(List<T> var1, Function<T, DrawableWidget> var2) {
        super(var1);
        this.gson = var2;
    }
}
