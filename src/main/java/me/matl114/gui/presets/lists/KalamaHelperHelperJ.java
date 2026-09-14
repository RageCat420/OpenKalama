package me.matl114.gui.presets.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

class KalamaHelperHelperJ<W, T extends Element & Drawable & Selectable> implements ListEntryWidgetController {
    final List<W> origin;
    final List<T> b = new ArrayList<>();
    final Function<W, T> g;
    int d;
    int e;

    KalamaHelperHelperJ(List<W> originData, Function<W, T> widgetFactory, int height, int width) {
        this.origin = originData;
        this.g = widgetFactory;
        this.d = height;
        this.e = width;

        for (W var6 : originData) {
            this.b.add(widgetFactory.apply(var6));
        }
    }

    @Override
    public boolean g(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean j() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Element & Drawable & Selectable> X getEntryWidget(int index) {
        return (X) (Object) this.b.get(index);
    }

    @Override
    public void markDirty(boolean mark) {}

    @Override
    public boolean i() {
        return false;
    }

    @Override
    public void resync() {}

    @Override
    public boolean f(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean h(int index) {
        if (index >= 0 && index < this.a()) {
            this.b.set(index, this.g.apply(this.origin.get(index)));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean d(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int a() {
        return this.b.size();
    }

    @Override
    public boolean e(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int c() {
        return this.e;
    }

    @Override
    public int b() {
        return this.d;
    }
}
