package me.matl114.hacks.utils.render;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public record HackUtilHelperB(Text text, Vec3d position, int offSetFlag, float scale) {
    public int offSetFlag() {
        return this.offSetFlag;
    }

    public Text text() {
        return this.text;
    }

    public float scale() {
        return this.scale;
    }

    public HackUtilHelperB(Text text, Vec3d position) {
        this(text, position, RenderElements.POSITION_FLAG, 1.0F);
    }

    public HackUtilHelperB(Text text, Vec3d position, float scale) {
        this(text, position, RenderElements.POSITION_FLAG, scale);
    }

    public Vec3d position() {
        return this.position;
    }

    public HackUtilHelperB(Text text, Vec3d position, int offSetFlag, float scale) {
        this.text = text;
        this.position = position;
        this.offSetFlag = offSetFlag;
        this.scale = scale;
    }
}
