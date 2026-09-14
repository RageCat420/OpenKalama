package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperK implements KalamaHelperHelperRX {
    Color c;
    Entity h;

    public KalamaHelperHelperK x(Entity entity) {
        this.h = entity;
        return this;
    }

    public KalamaHelperHelperK y(Color color) {
        this.c = color;
        return this;
    }

    public Color d() {
        return this.c;
    }

    public KalamaHelperHelperK(Entity entity, Color color) {
        this.h = entity;
        this.c = color;
    }

    @Override
    public void render(MatrixStack stack, float partialTicks) {
        Vec3d var3 = RenderUtils.getCameraPos();
        Vec3d var4 = this.h.getBoundingBox().getCenter().subtract(var3);
        Vec3d var5 = RenderUtils.getTracerOrigin(1.0F);
        RenderUtils.m(stack, var5, var4, this.c);
    }

    public Entity u() {
        return this.h;
    }
}
