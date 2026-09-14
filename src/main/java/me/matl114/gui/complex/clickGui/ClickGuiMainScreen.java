package me.matl114.gui.complex.clickGui;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import me.matl114.gui.GenericScreen;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.utils.ChatUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiMainScreen extends GenericScreen {
    public static final int dG = 12;
    public static final int dH = 60;
    DrawableWidget be;
    Map<String, Function<Screen, DrawableWidget>> dE;
    String dF;
    ContentDelegateWidget<DrawableWidget> dI;

    @Override
    protected void init() {
        super.init();
        int var1 = this.dE.size();
        int var2;
        int var3;
        if (var1 * 60 > this.width) {
            var2 = 0;
            var3 = this.width / var1;
        } else {
            var2 = (this.width - var1 * 60) / 2;
            var3 = 60;
        }

        int var4 = 0;

        for (String var6 : this.dE.keySet()) {
            AbstractElement var8 = new ButtonElement(
                            TextProvider.c(Text.translatableWithFallback("widget.click-gui.selection." + var6, var6)),
                            ButtonAction.a(() -> this.setGlobal(var6)))
                    .cA(ButtonElement.bH)
                    .cC(ButtonElement.bI)
                    .cw(el -> Objects.equals(this.dF, var6))
                    .aO(TooltipHandler.ap(
                            ChatUtils.parseTranslation("widget.click-gui.selection." + var6 + ".tooltips", "暂无介绍")));
            ExecutableWidget.instance(var2 + var4 * var3, 0, var3, 12)
                    .<ExecutableWidget>eV(var8)
                    .addTo(this);
            var4++;
        }

        String var9 = this.dF;
        this.dF = null;
        this.setGlobal(var9);
        this.dI = new ContentDelegateWidget<DrawableWidget>(0, 12, this.width, this.height - 12)
                .setContentDelegate(this.be)
                .addTo(this);
    }

    protected void setGlobal(String string) {
        if (!Objects.equals(string, this.dF)) {
            this.dF = string;
            this.be = this.dE.get(this.dF).apply(this);
            if (this.dI != null) {
                this.dI.setContentDelegate(this.be);
            }
        }
    }

    public ClickGuiMainScreen(Map<String, Function<Screen, DrawableWidget>> widgets) {
        super(Text.empty(), 0, 0);
        this.dE = widgets;
        String var2 = this.dE.keySet().iterator().next();
        this.setGlobal(var2);
    }

    @Override
    protected void init0() {
        super.init0();
        this.x = 0;
        this.y = 0;
    }
}
