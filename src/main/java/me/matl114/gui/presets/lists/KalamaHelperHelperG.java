package me.matl114.gui.presets.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

class KalamaHelperHelperG<W> implements ListEntryWidgetController {
    boolean dirty;
    final List<W> b;
    final List<Element> widgets = new ArrayList<>();
    Supplier f;
    Function g;
    int d;
    int e;

    @Override
    public boolean g(int index) {
        if (index >= 0 && index < this.a()) {
            W var3 = (W) this.f.get();
            this.b.add(index + 1, var3);
            this.widgets.add(index + 1, (Element) this.g.apply(var3));
        } else {
            W var2 = (W) this.f.get();
            this.b.add(var2);
            this.widgets.add((Element) this.g.apply(var2));
        }

        this.dirty = true;
        return true;
    }

    @Override
    public boolean j() {
        if (this.a() > 0) {
            this.widgets.clear();
            this.b.clear();
            this.dirty = true;
            return true;
        } else {
            return false;
        }
    }

    public KalamaHelperHelperG(List var1, int var2, int var3, Supplier var4, Function var5) {
        this.b = var1;
        this.d = var2;
        this.e = var3;
        this.f = var4;
        this.g = var5;
        this.dirty = false;
        this.resync();
    }

    @Override
    public <T extends Element & Drawable & Selectable> T getEntryWidget(int index) {
        return (T) this.widgets.get(index);
    }

    @Override
    public void markDirty(boolean ma) {
        this.dirty = ma;
    }

    @Override
    public boolean i() {
        return this.dirty;
    }

    @Override
    public void resync() {
        this.widgets.clear();

        for (W var2 : this.b) {
            this.widgets.add((Element) this.g.apply(var2));
        }

        this.dirty = true;
    }

    @Override
    public boolean f(int index) {
        if (index >= 0 && index < this.a()) {
            this.b.remove(index);
            this.widgets.remove(index);
            this.dirty = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean h(int index) {
        if (index >= 0 && index < this.a()) {
            W var2 = this.b.get(index);
            if (index < this.widgets.size()) {
                this.widgets.set(index, (Element) this.g.apply(var2));
            } else {
                for (int var3 = this.widgets.size(); var3 < this.a(); var3++) {
                    this.widgets.add((Element) this.g.apply(this.b.get(var3)));
                }
            }

            this.dirty = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean d(int index) {
        if (index > 0 && index < this.a()) {
            W var2 = this.b.get(index - 1);
            W var3 = this.b.get(index);
            this.b.set(index - 1, var3);
            this.b.set(index, var2);
            Element var4 = this.widgets.get(index - 1);
            Element var5 = this.widgets.get(index);
            this.widgets.set(index - 1, var5);
            this.widgets.set(index, var4);
            this.dirty = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int a() {
        return this.b.size();
    }

    @Override
    public boolean e(int index) {
        if (index >= 0 && index < this.a() - 1) {
            W var2 = this.b.get(index + 1);
            W var3 = this.b.get(index);
            this.b.set(index + 1, var3);
            this.b.set(index, var2);
            Element var4 = this.widgets.get(index + 1);
            Element var5 = this.widgets.get(index);
            this.widgets.set(index + 1, var5);
            this.widgets.set(index, var4);
            this.dirty = true;
            return true;
        } else {
            return false;
        }
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
