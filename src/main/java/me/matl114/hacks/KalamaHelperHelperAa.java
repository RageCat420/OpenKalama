package me.matl114.hacks;

import java.awt.Color;
import java.util.List;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperAa implements KalamaHelperHelperRX {
    Color c;
    List<Vec3d> n;

    public KalamaHelperHelperAa N(List<Vec3d> multiLine) {
        this.n = multiLine;
        return this;
    }

    public KalamaHelperHelperAa O(Color color) {
        this.c = color;
        return this;
    }

    public Color d() {
        return this.c;
    }

    public KalamaHelperHelperAa(List<Vec3d> multiLine, Color color) {
        this.n = multiLine;
        this.c = color;
    }

    @Override
    public void render(MatrixStack stack, float partialTicks) {
        RenderUtils.n(stack, this.n, this.c);
    }

    public List<Vec3d> M() {
        return this.n;
    }
}
