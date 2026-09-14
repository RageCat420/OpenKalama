package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class KalamaHelperHelperW implements KalamaHelperHelperRX {
    Color c;
    Entity h;

    public KalamaHelperHelperW K(Entity entity) {
        this.h = entity;
        return this;
    }

    public KalamaHelperHelperW L(Color color) {
        this.c = color;
        return this;
    }

    public Color d() {
        return this.c;
    }

    public KalamaHelperHelperW(Entity entity, Color color) {
        this.h = entity;
        this.c = color;
    }

    @Override
    public void render(MatrixStack stack, float partialTicks) {
        RenderUtils.drawOutlinedBox(
                stack,
                this.h.getBoundingBox().getMinPos(),
                this.h.getBoundingBox().getMaxPos(),
                ColorUtils.k(this.c, 0.25F));
    }

    public Entity u() {
        return this.h;
    }
}
