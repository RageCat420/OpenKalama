package me.matl114.hacks.utils.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public record SimpleEntityPredictor(Entity entity) implements Predictor {
    public Entity Pc() {
        return this.entity;
    }

    @Override
    public Vec3d getKnownDeltaMovement() {
        return new Vec3d(
                this.entity.getX() - this.entity.prevX,
                this.entity.getY() - this.entity.prevY,
                this.entity.getZ() - this.entity.prevZ);
    }

    @Override
    public Vec3d predict(int ticksLater, int method, int a) {
        return this.entity.getLerpedPos(ticksLater);
    }
}
