package me.matl114.gui;

import java.util.function.BooleanSupplier;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.PropertyTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class McWidgetHelpers {
    private static final KalamaHelperHelperIX b = (el, fo) -> fo ? -1 : -6250336;
    private static final MinecraftClient a = MinecraftClient.getInstance();

    public static KalamaHelperHelperIX getWrongRedTextBoxColorProvider(BooleanSupplier supplier) {
        return (el, fo) -> supplier.getAsBoolean() ? (fo ? -1 : -6250336) : -65536;
    }

    public static void drawTextWidgetBox(
            Drawable drawable,
            DrawContext context,
            int x,
            int y,
            int width,
            int height,
            boolean focus,
            KalamaHelperHelperIX borderColor) {
        Integer var8 = borderColor.toggle(drawable, focus);
        if (var8 != null) {
            context.fill(x, y, x + width, y + height, var8);
        }

        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, -16777216);
    }

    public static KalamaHelperHelperIX getDefaultTextBoxColorProvider() {
        return b;
    }

    public static ContentDelegateWidget<TextFieldWidget> createAttrValueEditBox(
            AttrKeyValue<?> attrKeyValue, int x, int y, int dx, int dy) {
        return new KalamaHelperHelperE(x, y, new KalamaHelperHelperU(attrKeyValue, a.textRenderer, 0, 0, dx, dy));
    }

    public static ContentDelegateWidget<EditBoxWidget> a(
            int x, int y, int dx, int dy, PropertyTracker<EditBoxWidget, String> valueTracker, String origin) {
        return createEnhancedMultiLine(x, y, dx, dy, valueTracker, origin, null);
    }

    public static ContentDelegateWidget<EditBoxWidget> b(
            int x,
            int y,
            int dx,
            int dy,
            PropertyTracker<EditBoxWidget, String> valueTracker,
            String origin,
            KalamaHelperHelperIX boxColorProvider) {
        return createEnhancedMultiLine(x, y, dx, dy, valueTracker, origin, boxColorProvider);
    }

    public static <T> ContentDelegateWidget<TextFieldWidget> d(
            int x,
            int y,
            int dx,
            int dy,
            PropertyTracker<T, String> valueTracker,
            String origin,
            KalamaHelperHelperIX boxColorProvider) {
        return createEnhancedTextBox(x, y, dx, dy, valueTracker, origin, boxColorProvider);
    }

    public static <T> ContentDelegateWidget<EditBoxWidget> createEnhancedMultiLine(
            int x,
            int y,
            int dx,
            int dy,
            PropertyTracker<EditBoxWidget, String> valueTracker,
            String origin,
            KalamaHelperHelperIX boxColorProvider) {
        EditBoxWidget var7 = new EditBoxWidget(a.textRenderer, x, y, dx, dy, Text.empty(), Text.empty());
        var7.setText(origin);
        var7.setChangeListener(str -> valueTracker.valueChange(var7, str));
        if (boxColorProvider != null) {
            TextFieldAccess.of(var7).setBorderColorProvider(boxColorProvider);
        }

        return new KalamaHelperHelperE(0, 0, var7);
    }

    public static <T> ContentDelegateWidget<TextFieldWidget> c(
            int x, int y, int dx, int dy, PropertyTracker<T, String> valueTracker, String origin) {
        return createEnhancedTextBox(x, y, dx, dy, valueTracker, origin, null);
    }

    public static <T> ContentDelegateWidget<TextFieldWidget> createEnhancedTextBox(
            int x,
            int y,
            int dx,
            int dy,
            PropertyTracker<T, String> valueTracker,
            String origin,
            KalamaHelperHelperIX boxColorProvider) {
        TextFieldWidget var7 = new TextFieldWidget(a.textRenderer, 0, 0, dx, dy, Text.empty());
        var7.setMaxLength(32768);
        var7.setText(origin);
        var7.setChangedListener(str -> valueTracker.valueChange((T) var7, str));
        if (boxColorProvider != null) {
            TextFieldAccess.of(var7).setBorderColorProvider(boxColorProvider);
        }

        return new KalamaHelperHelperE(x, y, var7);
    }
}
