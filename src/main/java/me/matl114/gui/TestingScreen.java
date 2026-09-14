package me.matl114.gui;

import me.matl114.gui.basic.DrawableWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TestingScreen extends Screen {
    protected int y;
    DrawableWidget cG;
    DrawableWidget cx;
    protected int backgroundHeight;
    DrawableWidget cB;
    DrawableWidget cD;
    DrawableWidget cE;
    DrawableWidget cF;
    DrawableWidget cH;
    protected int backgroundWidth = 220;
    DrawableWidget cw;
    protected int x;
    DrawableWidget cC;
    DrawableWidget cA;
    DrawableWidget cz;
    DrawableWidget cy;

    protected void init() {
        super.init();
        this.init0();
    }

    public TestingScreen(Text title) {
        super(title);
        this.backgroundHeight = 300;
    }

    protected void init0() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }
}
