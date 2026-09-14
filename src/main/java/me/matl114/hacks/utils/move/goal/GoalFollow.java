package me.matl114.hacks.utils.move.goal;

import me.matl114.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public record GoalFollow(Entity entity) implements IPathGoal {
    public Entity Pc() {
        return this.entity;
    }

    @Override
    public Vec3d sample() {
        return this.entity.getPos();
    }

    @Override
    public boolean isInGoal(Vec3d playerPos) {
        return this.entity.getPos().squaredDistanceTo(playerPos)
                <= MathUtils.a(
                        0.3 + this.entity.getDimensions(this.entity.getPose()).width() / 2.0F);
    }
}
