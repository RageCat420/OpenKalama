package me.matl114.hacks.utils.entity;

import net.minecraft.util.math.Vec3d;

public record HackUtilHelperE(Vec3d vec3d, int tick) {
    public Vec3d vec3d() {
        return this.vec3d;
    }

    public int tick() {
        return this.tick;
    }
}
