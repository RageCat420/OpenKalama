package me.matl114.gui.presets.index;

import java.util.List;
import me.matl114.gui.GenericScreen;
import me.matl114.gui.basic.ElementHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

public abstract class IndexedScreen<T, W extends Element & Drawable & Selectable> extends GenericScreen {
    protected int au = 100;
    protected final List<T> bR;
    protected int ax = 20;
    protected IndexedSubScreen<T, W> bS;

    protected abstract W bd(T var1);

    @Override
    protected void init() {
        super.init();
        this.bc();
        this.dg();
        this.addDrawableChild(this.bS);
    }

    public void bc() {}

    public void setGlobal(T var1) {}

    public void close() {
        super.close();
        this.bc();
    }

    protected void bb() {
        if (this.bS != null) {
            this.bS.selectIndexToDisplay(this.getGlobal(), false);
        }
    }

    public void resize(MinecraftClient client, int width, int height) {
        this.bc();
        super.resize(client, width, height);
    }

    public abstract T getGlobal();

    public IndexedScreen(List<T> list, int backgroundWidth, int backgroundHeight) {
        super(Text.empty(), backgroundWidth, backgroundHeight);
        this.bR = list;
    }

    protected void dg() {
        this.bS = new KalamaHelperHelperB(this.bR, 10, 10, this.width - 20, this.height - 20, this.au, this.ax);
    }

    protected abstract ElementHandler be(T var1);

    class KalamaHelperHelperB extends IndexedSubScreen<T, W> {
        @Override
        protected W bd(T val) {
            return IndexedScreen.this.bd(val);
        }

        public T getGlobal() {
            return IndexedScreen.this.getGlobal();
        }

        @Override
        public void setGlobal(T config) {
            IndexedScreen.this.setGlobal(config);
        }

        @Override
        public void bc() {
            IndexedScreen.this.bc();
        }

        KalamaHelperHelperB(List<T> list, int x, int y, int dx, int dy, int indexDx, int indexDy) {
            super(list, x, y, dx, dy, indexDx, indexDy);
        }

        @Override
        public T bf() {
            return IndexedScreen.this.getGlobal();
        }

        @Override
        protected ElementHandler be(T val) {
            return IndexedScreen.this.be(val);
        }
    }
}
