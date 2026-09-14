package me.matl114.hacks.modules.render;

import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.utils.ClientUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

class RenderSubHelperC extends Screen implements RenderSubHelperWX {
    private final SleepMode this$0;

    protected RenderSubHelperC(final SleepMode param1) {
        super(Text.empty());
        this.this$0 = param1;
    }

    protected void init() {
        super.init();
        this.this$0.Ri = this;
        DisplayWidget.instance(40, 20, this.width - 80, this.height / 3 - 40)
                .<DrawableWidget>setRenderHandler(LabelElement.instance(Text.literal("您的游戏在待机中退出,目前已停止刷新")))
                .addTo(this);
        DisplayWidget.instance(40, this.height / 3 + 20, this.width - 80, this.height / 3 - 40)
                .<DrawableWidget>setRenderHandler(
                        LabelElement.instance(Text.literal("按 " + this.this$0.aiE() + " 键退出休眠模式")))
                .addTo(this);
        ExecutableWidget.instance(40, this.height * 2 / 3 + 20, this.width - 80, this.height / 3 - 40)
                .<ExecutableWidget>eV(
                        new ButtonElement(TextProvider.c(Text.literal("点击下方按钮以刷新屏幕")), ButtonAction.a(() -> {
                            if (this.this$0.aim() && ClientUtils.isPlayerOnline()) {
                                this.this$0.Ri = null;
                                this.this$0.setUpSleepingScreen(this.this$0.getDefaultDisplayText());
                            }
                        })))
                .addTo(this);
        this.this$0.Rk = true;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
}
