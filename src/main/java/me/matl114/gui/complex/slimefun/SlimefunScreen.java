package me.matl114.gui.complex.slimefun;

import java.util.List;
import me.matl114.gui.GenericBackGroundScreen;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.PlateElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.utils.ChatUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimefunScreen extends GenericBackGroundScreen {
    protected ExecutableWidget eD;
    protected ExecutableWidget eB;
    private static final Identifier eE = new Identifier("kalama", "gui/search");
    protected static Identifier ai = new Identifier("minecraft", "container/beacon/cancel");
    protected DrawableWidget ev;
    protected ExecutableWidget ez;
    protected ExecutableWidget eA;
    protected ExecutableWidget ey;
    protected DrawableWidget ew;
    protected ExecutableWidget eC;
    protected DrawableWidget ex;

    public SlimefunScreen(Text title) {
        super(title, 240, 320);
        this.titleLabel = title;
    }

    @Override
    protected void init() {
        super.init();
        this.ey = ExecutableWidget.instance(this.x - 19, this.y + 16, 18, 18)
                .<DrawableWidget>setRenderHandler(SlotElement.aI(SlimefunTasks.b)
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.slimefun-screen.all-item.tooltips", ""))))
                .addTo(this);
        this.ez = ExecutableWidget.instance(this.x - 19, this.y + 42, 18, 18)
                .<DrawableWidget>setRenderHandler(SlotElement.aI(SlimefunTasks.c)
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.slimefun-screen.all-type.tooltips", ""))))
                .addTo(this);
        this.eC = ExecutableWidget.instance(this.x - 19, this.y + 68, 18, 18)
                .<DrawableWidget>setRenderHandler(SlotElement.aI(SlimefunTasks.d)
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.slimefun-screen.all-vanilla.tooltips", ""))))
                .addTo(this);
        this.eD = ExecutableWidget.instance(this.x - 19, this.y + 94, 18, 18)
                .<DrawableWidget>setRenderHandler(SlotElement.aI(SlimefunTasks.e)
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.slimefun-screen.all-custom.tooltips", ""))))
                .addTo(this);
        this.ey.eT(KalamaHelperHelperP.aA(SlimefunTasks.w()::adZ));
        this.ez.eT(KalamaHelperHelperP.aA(SlimefunTasks.w()::aeb));
        this.eC.eT(KalamaHelperHelperP.aA(SlimefunTasks.w()::aec));
        this.eD.eT(KalamaHelperHelperP.aA(SlimefunTasks.w()::aea));
    }

    protected List<Text> getSearchButtonTooltips() {
        return ChatUtils.parseTranslation("widget.gui.slimefun-screen.search-default.tooltips", "");
    }

    @Override
    protected void l() {
        this.ev = DisplayWidget.instance(this.x - 23, this.y + 12, 26, 26)
                .<DrawableWidget>setRenderHandler(PlateElement.cg())
                .addTo(this);
        this.ew = DisplayWidget.instance(this.x - 23, this.y + 38, 26, 26)
                .<DrawableWidget>setRenderHandler(PlateElement.cg())
                .addTo(this);
        this.ex = DisplayWidget.instance(this.x - 23, this.y + 64, 26, 26)
                .<DrawableWidget>setRenderHandler(PlateElement.cg())
                .addTo(this);
        DisplayWidget.instance(this.x - 23, this.y + 90, 26, 26)
                .<DrawableWidget>setRenderHandler(PlateElement.cg())
                .addTo(this);
        this.eA = ExecutableWidget.instance(this.x + this.backgroundWidth - 3, this.y + 12, 26, 26)
                .<ExecutableWidget>eT(KalamaHelperHelperP.aA(this::close))
                .<DrawableWidget>setRenderHandler(PlateElement.cg()
                        .cD(RenderHandler.z(ai, 4, 4, 18, 18))
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.slimefun-screen.close-screen.tooltips", ""))))
                .addTo(this);
        this.eB = ExecutableWidget.instance(this.x + this.backgroundWidth - 3, this.y + 38, 26, 26)
                .<ExecutableWidget>eT(KalamaHelperHelperP.aA(this::close))
                .<DrawableWidget>setRenderHandler(PlateElement.cg()
                        .cD(RenderHandler.z(eE, 4, 4, 18, 18))
                        .aO(TooltipHandler.ar(this::getSearchButtonTooltips)))
                .addTo(this);
        super.l();
    }
}
