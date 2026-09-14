package me.matl114.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.util.Identifier;

public class PlateElement extends AbstractElement {
    private boolean catchInteract;
    private static final int bd = 0;
    private int color = -1;
    private static final int be = 66;
    public static final Identifier TEXTURE = new Identifier("kalama", "textures/custom/recipecontainer.png");

    public PlateElement(boolean catchInteract) {
        this.catchInteract = catchInteract;
        this.showTooltips = false;
    }

    public void renderCentered0(
            DrawableWidget element,
            VDrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            float alpha,
            boolean shouldHighlight) {
        this.color = -1;
        float var8 = (this.color >> 24 & 0xFF) / 255.0F;
        float var9 = (this.color >> 16 & 0xFF) / 255.0F;
        float var10 = (this.color >> 8 & 0xFF) / 255.0F;
        float var11 = (this.color & 0xFF) / 255.0F;
        context.setShaderColor(var9, var10, var11, var8);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);
        int var12 = element.getTextureWidth();
        int var13 = element.getTextureHeight();
        context.Q(TEXTURE, 0, 0, 106, 190, 8, 8);
        context.Q(TEXTURE, var12 - 8, 0, 248, 190, 8, 8);
        context.Q(TEXTURE, 0, var13 - 8, 106, 248, 8, 8);
        context.Q(TEXTURE, var12 - 8, var13 - 8, 248, 248, 8, 8);
        context.w(TEXTURE, 8, var12 - 8, 0, 8, 0, 0.4453125F, 0.96875F, 0.7421875F, 0.7734375F);
        context.w(TEXTURE, 8, var12 - 8, var13 - 8, var13, 0, 0.4453125F, 0.96875F, 0.96875F, 1.0F);
        context.w(TEXTURE, 0, 8, 8, var13 - 8, 0, 0.4140625F, 0.4453125F, 0.7734375F, 0.96875F);
        context.w(TEXTURE, var12 - 8, var12, 8, var13 - 8, 0, 0.96875F, 1.0F, 0.7734375F, 0.96875F);
        context.w(TEXTURE, 8, var12 - 8, 8, var13 - 8, 0, 0.4453125F, 0.96875F, 0.7734375F, 0.96875F);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public PlateElement() {
        this(false);
    }

    @Override
    public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
        return this.catchInteract;
    }

    public static PlateElement cg() {
        return new PlateElement(false);
    }

    public static PlateElement ch() {
        return new PlateElement(true);
    }
}
