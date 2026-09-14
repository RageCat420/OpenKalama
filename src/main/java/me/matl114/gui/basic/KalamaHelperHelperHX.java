package me.matl114.gui.basic;

import java.awt.Color;

class KalamaHelperHelperHX implements ColorSampler {
    final int val$color;
    final Color val$color1;

    KalamaHelperHelperHX(int var1, Color var2) {
        this.val$color = var1;
        this.val$color1 = var2;
    }

    @Override
    public Color getColor() {
        return this.val$color1;
    }

    @Override
    public int getColorInt() {
        return this.val$color;
    }
}
