package me.matl114.utils;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ColorUtils {
    public static TextColor color(Formatting formatting) {
        return Objects.requireNonNull(TextColor.fromFormatting(formatting));
    }

    public static Color withAlpha(TextColor color, int alpha) {
        return new Color(color.getRgb() & 16777215 | alpha << 24, true);
    }

    public static int m(int color, int alpha) {
        return (color & 0xFF000000) == 0 ? j(color, alpha) : color;
    }

    public static Color h(TextColor color, float alpha) {
        return withAlpha(color, (int) (alpha * 255.0F));
    }

    public static Color f(Color color, int alpha) {
        return new Color(color.getRGB() & 16777215 | alpha << 24, true);
    }

    public static TextColor o(Color color) {
        return TextColor.fromRgb(color.getRGB());
    }

    public static int getColorInt(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static Color getColor(int r, int g, int b, int a) {
        return new Color((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF, true);
    }

    public static Color k(Color color, float alpha) {
        return f(color, (int) (alpha * 255.0F));
    }

    public static TextColor p(String str) {
        return (TextColor) TextColor.parse(str).getOrThrow();
    }

    public static int b(float r, float g, float b) {
        return c((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F));
    }

    public static Color d(int r, int g, int b) {
        return new Color(0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF, true);
    }

    public static int withAlphaInt(int color, float alpha) {
        return j(color, (int) (alpha * 255.0F));
    }

    public static int j(int color, int alpha) {
        return color & 16777215 | alpha << 24;
    }

    public static int c(int r, int g, int b) {
        return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static int i(Color color, int alpha) {
        return j(color.getRGB(), alpha);
    }
}
