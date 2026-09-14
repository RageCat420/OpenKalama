package me.matl114.mixins.gui;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.PropertyTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({EditBoxWidget.class})
public abstract class EditBoxWidgetMixin extends ScrollableWidget implements TextFieldAccess {
    @Unique
    private static final KalamaHelperHelperIX ORIGIN_PROVIDER = McWidgetHelpers.getDefaultTextBoxColorProvider();

    @Shadow
    @Final
    private EditBox field_39509;

    @Unique
    @Nonnull
    private KalamaHelperHelperIX boxColorProvider = ORIGIN_PROVIDER;

    @Unique
    @Override
    public void setBorderColorProvider(KalamaHelperHelperIX provider) {
        this.boxColorProvider = provider == null ? ORIGIN_PROVIDER : provider;
    }

    @Shadow
    public void method_44401(Consumer<String> var1) {}

    @Shadow
    protected abstract void method_44404(double var1, double var3);

    @Shadow
    protected abstract double getDeltaYPerScroll();

    @Unique
    @Override
    public void setListener(PropertyTracker<TextFieldAccess, String> tracker) {
        this.method_44401(str -> tracker.valueChange(this, str));
    }

    public EditBoxWidgetMixin(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    protected void drawBox(DrawContext context, int x, int y, int width, int height) {
        McWidgetHelpers.drawTextWidgetBox(this, context, x, y, width, height, this.isFocused(), this.boxColorProvider);
    }

    @Inject(
            method = {"setFocused"},
            at = {@At("HEAD")})
    private void resetSelectOnRelease(boolean focused, CallbackInfo ci) {
        if (!focused) {
            this.resetSelect();
        }
    }

    @Inject(
            method = {"keyPressed"},
            at = {@At("RETURN")},
            cancellable = true)
    public void fixInventoryKeyPressedWhenFocused(
            int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.isFocused()
                && MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    @Override
    public boolean canStartDrag(double mouseX, double mouseY) {
        return this.isWithinBounds(mouseX, mouseY) || super.scrollbarDragged;
    }

    @Unique
    @Override
    public void dragSelect(int deltaX, int deltaY, boolean shiftDownAction) {
        if (!super.scrollbarDragged) {
            this.field_39509.setSelecting(true);
            this.method_44404(this.getX() + deltaX, this.getY() + deltaY);
            this.field_39509.setSelecting(ScreenUtils.hasShiftDown());
        }
    }

    @Unique
    @Override
    public void resetSelect() {
        if (this.field_39509.hasSelection()) {
            this.field_39509.setSelecting(false);
            this.field_39509.selectionEnd = this.field_39509.getCursor();
        }
    }
}
