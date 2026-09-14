package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperBX implements KalamaHelperHelperRX {
    Vec3d b;
    Vec3d a;
    Color c = Color.GREEN;

    public Vec3d c() {
        return this.b;
    }

    public KalamaHelperHelperBX e(Vec3d start) {
        this.a = start;
        return this;
    }

    public KalamaHelperHelperBX g(Color color) {
        this.c = color;
        return this;
    }

    public KalamaHelperHelperBX(Vec3d start, Vec3d movement) {
        this.a = start;
        this.b = movement;
    }

    public Color d() {
        return this.c;
    }

    public KalamaHelperHelperBX f(Vec3d movement) {
        this.b = movement;
        return this;
    }

    @Override
    public void render(MatrixStack stack, float partialTicks) {
        RenderUtils.l(stack, this.a, this.a.add(this.b), this.c);
    }

    public Vec3d b() {
        return this.a;
    }
}
