package me.matl114.gui;

import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.complex.slimefun.SlimefunScreen;
import me.matl114.utils.config.PropertyTracker;
import net.minecraft.text.Text;

public class TestingScreen2 extends SlimefunScreen {
    DrawableWidget cC;
    DrawableWidget cD;
    DrawableWidget cG;
    DrawableWidget cz;
    DrawableWidget cF;
    DrawableWidget cy;
    DrawableWidget cE;
    DrawableWidget cH;
    DrawableWidget cA;
    DrawableWidget cw;
    DrawableWidget cx;
    DrawableWidget cB;

    public TestingScreen2(Text text) {
        super(text);
    }

    @Override
    protected void init() {
        this.init0();
        this.cy = McWidgetHelpers.a(
                this.x + 80,
                this.y + 110,
                80,
                90,
                PropertyTracker.event((val, str) -> {
                    int var2 = str.length();
                }),
                "byd");
        this.addDrawableChild((DrawableWidget) (Object) ((ContentDelegateWidget) this.cy).ef());
    }
}
