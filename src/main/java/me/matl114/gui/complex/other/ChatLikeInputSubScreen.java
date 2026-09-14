package me.matl114.gui.complex.other;

import java.util.function.Consumer;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.utils.config.PropertyTracker;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;

public class ChatLikeInputSubScreen extends KalamaHelperHelperCX {
    String eK;
    ContentDelegateWidget<TextFieldWidget> eL;
    int messageHistoryIndex;
    ChatInputSuggestor eM;
    private Consumer<String> c;
    ContentDelegateWidget<DrawableWidget> eN;
    MinecraftClient bT = MinecraftClient.getInstance();

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void setChatFromHistory(int offset) {
        int var2 = this.messageHistoryIndex + offset;
        int var3 = this.bT.inGameHud.getChatHud().getMessageHistory().size();
        var2 = MathHelper.clamp(var2, 0, var3);
        if (var2 != this.messageHistoryIndex) {
            if (var2 == var3) {
                this.messageHistoryIndex = var3;
                ((TextFieldWidget) (Object) this.eL.ef()).setText(this.eK);
            } else {
                if (this.messageHistoryIndex == var3) {
                    this.eK = ((TextFieldWidget) (Object) this.eL.ef()).getText();
                }

                ((TextFieldWidget) (Object) this.eL.ef()).setText((String) (Object)
                        this.bT.inGameHud.getChatHud().getMessageHistory().get(var2));
                this.messageHistoryIndex = var2;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.isFocused()) {
            if (keyCode == 257 || keyCode == 335) {
                this.gc(((TextFieldWidget) (Object) this.eL.ef()).getText());
                ((TextFieldWidget) (Object) this.eL.ef()).setText("");
                return true;
            } else if (keyCode == 265) {
                this.setChatFromHistory(-1);
                return true;
            } else if (keyCode == 264) {
                this.setChatFromHistory(1);
                return true;
            } else {
                return false;
            }
        } else if (keyCode == 265) {
            this.setFocused(true);
            return true;
        } else {
            return false;
        }
    }

    public ChatLikeInputSubScreen(int x, int y, int dx, int dy, Consumer<String> callback) {
        super(x, y, dx, dy);
        this.eK = "";
        this.c = callback;
        this.init();
    }

    @Override
    public void renderInDefaultMatrix(
            VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
        context.fill(
                0,
                -2,
                this.eL.getWidth(),
                this.eL.getHeight() - 2,
                this.bT.options.getTextBackgroundColor(Integer.MIN_VALUE));
        RenderHandler.K(
                context, -1, -3, this.eL.getWidth() + 2, this.eL.getHeight() + 2, this.isFocused() ? -1 : -8355712);
        super.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
    }

    public void gc(String value) {
        if (this.c != null) {
            this.c.accept(value);
        }

        this.resetHistoryIndex();
    }

    protected void gb(String value) {}

    protected void init() {
        this.resetHistoryIndex();
        this.eL = McWidgetHelpers.c(0, 0, this.dx, this.dy, PropertyTracker.event(this::gb), "");
        ((TextFieldWidget) (Object) this.eL.ef()).setDrawsBackground(false);
        this.eL.addToSub(this);
    }

    public void resetHistoryIndex() {
        this.messageHistoryIndex =
                this.bT.inGameHud.getChatHud().getMessageHistory().size();
    }
}
