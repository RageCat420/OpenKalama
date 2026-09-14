package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperS implements KalamaHelperHelperRX {
    Color k;
    Box i;
    Color l;
    Vec3d j;

    public KalamaHelperHelperS D(Box startBox) {
        this.i = startBox;
        return this;
    }

    public KalamaHelperHelperS E(Vec3d delta) {
        this.j = delta;
        return this;
    }

    public KalamaHelperHelperS(Box startBox, Vec3d delta, Color color1, Color color2) {
        this.i = startBox;
        this.j = delta;
        this.k = color1;
        this.l = color2;
    }

    public Vec3d A() {
        return this.j;
    }

    public KalamaHelperHelperS G(Color color2) {
        this.l = color2;
        return this;
    }

    public Box z() {
        return this.i;
    }

    public KalamaHelperHelperS F(Color color1) {
        this.k = color1;
        return this;
    }

    public Color B() {
        return this.k;
    }

    public KalamaHelperHelperS(Box startBox, Vec3d delta) {
        this(startBox, delta, Color.GREEN, Color.RED);
    }

    @Override
    public void render(MatrixStack stack, float partialTicks) {
        Color var3 = ColorUtils.k(this.k, 0.25F);
        RenderUtils.r(stack, this.i.getMinPos(), this.i.getMaxPos(), var3);
        RenderUtils.r(stack, this.i.getMinPos().add(this.j), this.i.getMaxPos().add(this.j), var3);

        for (Vec3d var5 : CollisionUtil.getBoxVertices(this.i)) {
            RenderUtils.l(stack, var5, var5.add(this.j), this.l);
        }
    }

    public Color C() {
        return this.l;
    }
}
