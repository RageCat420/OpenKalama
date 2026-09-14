package me.matl114.gui;

import java.util.List;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.PlateElement;
import net.minecraft.text.Text;

public class GenericBackGroundScreen extends GenericScreen {
    protected static final int bO = 32;
    protected DrawableWidget bP;
    protected static final int bN = 20;
    protected static final int bM = 12;
    protected static final int aP = 12;
    protected DrawableWidget bQ;

    protected void l() {
        this.bP = DisplayWidget.instance(this.x, this.y, this.backgroundWidth, this.backgroundHeight)
                .<DrawableWidget>setRenderHandler(PlateElement.cg())
                .addTo(this);
        this.bQ = ExecutableWidget.instance(this.x + 5, this.y + 5, this.backgroundWidth - 10, 12)
                .<ExecutableWidget>eV(new LabelElement(this::getTitleLabel, -1, 0)
                        .cF(KalamaHelperHelperP.au(this::runClickTitle))
                        .aO(TooltipHandler.aq(this::provideTitleTooltips)))
                .addTo(this);
    }

    @Override
    protected void init() {
        super.init();
        this.l();
    }

    protected List<Text> provideTitleTooltips(DrawableWidget widget) {
        return null;
    }

    public GenericBackGroundScreen(Text title, int backgroundWidth, int backgroundDefaultHeight) {
        super(title, backgroundWidth, backgroundDefaultHeight);
    }

    protected void runClickTitle(boolean isLeft) {}
}
