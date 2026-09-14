package me.matl114.gui.basic;

import javax.annotation.Nullable;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.Selectable.SelectionType;
import net.minecraft.client.gui.screen.Screen;

public class DelegateWidget extends DrawableWidget implements Draggable {
    @Nullable
    DrawableWidget delegate;

    @Override
    public void setY(int y) {
        if (this.delegate != null) {
            this.delegate.setY(y);
        }
    }

    @Nullable
    public DrawableWidget am() {
        return this.delegate;
    }

    @Override
    public SelectionType getType() {
        return this.delegate == null ? SelectionType.NONE : this.delegate.getType();
    }

    @Override
    public final int getHeight() {
        return this.delegate == null ? 0 : this.delegate.getHeight();
    }

    @Override
    public void setX(int x) {
        if (this.delegate != null) {
            this.delegate.setX(x);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.delegate != null && this.delegate.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isSubWidget() {
        return this.delegate != null && this.delegate.isSubWidget();
    }

    @Override
    public boolean startDrag(Screen screen, double mouseX, double mouseY) {
        return this.delegate != null && this.delegate.startDrag(screen, mouseX, mouseY);
    }

    @Override
    public void setWidth(int x) {
        if (this.delegate != null) {
            this.delegate.setWidth(x);
        }
    }

    public DelegateWidget() {
        super(0, 0, 0, 0);
    }

    @Override
    public void releaseDrag(Screen screen, double mouseX, double mouseY) {
        if (this.delegate != null) {
            this.delegate.releaseDrag(screen, mouseX, mouseY);
        }
    }

    @Override
    public int getExtraDepth() {
        return this.delegate == null ? 0 : this.delegate.getExtraDepth();
    }

    @Override
    public boolean isSelected() {
        return this.delegate != null && this.delegate.isSelected();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.delegate != null && this.delegate.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.delegate != null) {
            this.delegate.setFocused(focused);
        }
    }

    @Override
    public void renderAbsolute(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
        if (this.delegate != null) {
            this.delegate.renderAbsolute(context, mouseX, mouseY, delta, disableSelect);
        }
    }

    public DelegateWidget al(DrawableWidget delegate) {
        this.delegate = delegate;
        return this;
    }

    @Override
    public float getTextureScale() {
        return this.delegate == null ? 0.0F : this.delegate.getTextureScale();
    }

    @Override
    public <T extends DrawableWidget> T setRenderHandler(RenderHandler renderHandler) {
        if (this.delegate != null) {
            this.delegate.setRenderHandler(renderHandler);
        }

        return (T) (Object) this;
    }

    @Override
    public RenderHandler getRenderHandler() {
        return this.delegate == null ? null : this.delegate.getRenderHandler();
    }

    @Override
    public void setSubWidget(boolean s) {
        if (this.delegate != null) {
            this.delegate.setSubWidget(s);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.delegate != null && this.delegate.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean canSelect() {
        return this.delegate != null && this.delegate.canSelect();
    }

    @Override
    public boolean isDragging() {
        return this.delegate != null && this.delegate.isDragging();
    }

    @Override
    public final int getWidth() {
        return this.delegate == null ? 0 : this.delegate.getWidth();
    }

    @Override
    public void setHeight(int y) {
        if (this.delegate != null) {
            this.delegate.setHeight(y);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.delegate != null && this.delegate.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isFocused() {
        return this.delegate != null && this.delegate.focused;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.delegate != null && this.delegate.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public final int getX() {
        return this.delegate == null ? 0 : this.delegate.getX();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.delegate != null && this.delegate.charTyped(chr, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (this.delegate != null) {
            this.delegate.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public <T extends DrawableWidget> T setAlpha(float scale) {
        if (this.delegate != null) {
            this.delegate.setAlpha(scale);
        }

        return (T) (Object) this;
    }

    @Override
    public int getTextureWidth() {
        return this.delegate == null ? 0 : this.delegate.getTextureWidth();
    }

    @Override
    public <T extends DrawableWidget> T setTextureScale(float scale) {
        if (this.delegate != null) {
            this.delegate.setTextureScale(scale);
        }

        return (T) (Object) this;
    }

    @Override
    public void render0(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
        if (this.delegate != null) {
            this.delegate.render0(context, mouseX, mouseY, delta, disableSelect);
        }
    }

    @Override
    public float getAlpha() {
        return this.delegate == null ? 0.0F : this.delegate.getAlpha();
    }

    @Override
    public int getTextureHeight() {
        return this.delegate == null ? 0 : this.delegate.getTextureHeight();
    }

    @Override
    public final int getY() {
        return this.delegate == null ? 0 : this.delegate.getY();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.delegate != null && this.delegate.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public <T extends DrawableWidget> T addToSub(KalamaHelperHelperCX screen) {
        if (this.delegate != null) {
            this.delegate.setSubWidget(true);
        }

        screen.Q(this);
        return (T) (Object) this;
    }

    @Override
    public void setSelected(boolean s) {
        if (this.delegate != null) {
            this.delegate.setSelected(s);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.delegate != null && this.delegate.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void renderInDefaultMatrix(
            VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
        if (this.delegate != null) {
            this.delegate.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
        }
    }
}
