package me.matl114.hacks.utils.entity;

import net.minecraft.util.math.Vec3d;

public interface Predictor {
   Vec3d predict(int var1, int var2, int var3);

   Vec3d getKnownDeltaMovement();
}
