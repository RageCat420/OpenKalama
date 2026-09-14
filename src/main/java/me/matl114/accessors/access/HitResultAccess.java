package me.matl114.accessors.access;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public interface HitResultAccess {
    void setPos(Vec3d var1);

    static HitResultAccess of(HitResult hitResult) {
        return (HitResultAccess) hitResult;
    }
}
