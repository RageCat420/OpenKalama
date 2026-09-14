package me.matl114.versioned.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import me.matl114.versioned.impl.DrawContext_v1_21_1;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface VDrawContext {
    void drawTooltip(TextRenderer var1, List<Text> var2, Optional<TooltipData> var3, int var4, int var5);

    void H(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

    default void drawSprite(Sprite sprite, int x, int y, int z, int width, int height) {
        if (width != 0 && height != 0) {
            this.w(
                    sprite.getAtlasId(),
                    x,
                    x + width,
                    y,
                    y + height,
                    z,
                    sprite.getMinU(),
                    sprite.getMaxU(),
                    sprite.getMinV(),
                    sprite.getMaxV());
        }
    }

    void enableScissor(int var1, int var2, int var3, int var4);

    void x(
            Identifier var1,
            int var2,
            int var3,
            int var4,
            int var5,
            int var6,
            float var7,
            float var8,
            float var9,
            float var10);

    public MatrixStack f();

    void drawGuiTexture(
            Identifier var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

    default void drawTexture(
            Identifier texture,
            int x,
            int y,
            int z,
            float u,
            float v,
            int width,
            int height,
            int textureWidth,
            int textureHeight) {
        this.U(texture, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    default void aa(int x1, int y1, int x2, int y2, int color2, int depth) {
        this.H(x1, y1, x2, y2, color2, color2, depth);
    }

    void u(Identifier var1, int var2, int var3, int var4, int var5, int var6);

    default void Y(TextRenderer textRenderer, @Nullable String text, int x, int y, int color) {
        this.A(textRenderer, text, x, y, color, true);
    }

    void d(int var1);

    void K(ItemStack var1, int var2, int var3, int var4, int var5);

    void fillGuiGradient(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

    void setShaderAlpha(float var1);

    default void V(Identifier texture, int x, int y, int width, int height) {
        this.u(texture, x, y, 0, width, height);
    }

    void z(TextRenderer var1, OrderedText var2, int var3, int var4, int var5, boolean var6);

    default void T(
            Identifier texture,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            int textureWidth,
            int textureHeight) {
        this.S(texture, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    public void C();

    public void D();

    Sprite y(Identifier var1);

    void G(int var1, int var2, int var3, int var4, int var5, int var6);

    @Nonnull
    public static VDrawContext P(DrawContext drawContext) {
        return new DrawContext_v1_21_1(drawContext);
    }

    default void S(
            Identifier texture,
            int x,
            int y,
            int width,
            int height,
            float u,
            float v,
            int regionWidth,
            int regionHeight,
            int textureWidth,
            int textureHeight) {
        this.U(texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    default void drawCenteredTextWithShadow(
            TextRenderer textRenderer, OrderedText text, int centerX, int y, int color) {
        this.z(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color, true);
    }

    void setShaderColor(float var1, float var2, float var3, float var4);

    void w(
            Identifier var1,
            int var2,
            int var3,
            int var4,
            int var5,
            int var6,
            float var7,
            float var8,
            float var9,
            float var10);

    default void U(
            Identifier texture,
            int x1,
            int x2,
            int y1,
            int y2,
            int z,
            int regionWidth,
            int regionHeight,
            float u,
            float v,
            int textureWidth,
            int textureHeight) {
        this.w(
                texture,
                x1,
                x2,
                y1,
                y2,
                z,
                (u + 0.0F) / textureWidth,
                (u + regionWidth) / textureWidth,
                (v + 0.0F) / textureHeight,
                (v + regionHeight) / textureHeight);
    }

    public DrawContext c();

    void A(TextRenderer var1, @Nullable String var2, int var3, int var4, int var5, boolean var6);

    public DrawContext b();

    public void e();

    void r(int var1);

    default void fill(int x1, int y1, int x2, int y2, int color) {
        this.G(x1, y1, x2, y2, 0, color);
    }

    void E(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

    void drawItemInSlot(TextRenderer var1, ItemStack var2, int var3, int var4, @Nullable String var5);

    default void Q(Identifier texture, int x, int y, int u, int v, int width, int height) {
        this.drawTexture(texture, x, y, 0, u, v, width, height, 256, 256);
    }
}
