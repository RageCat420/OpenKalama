package me.matl114.hacks.utils.move.goal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record GoalNear(Vec3d center, double radius) implements IPathGoal {
   public double radius() {
      return this.radius;
   }

   @Override
   public boolean isInGoal(Vec3d playerPos) {
      Vec3d var2 = playerPos.subtract(this.center);
      return Math.abs(var2.x) + Math.abs(var2.y) + Math.abs(var2.z) <= this.radius;
   }

   public GoalNear(BlockPos center, double radius) {
      this(center.toBottomCenterPos(), radius + 0.5);
   }

   public GoalNear(Vec3d center, double radius) {
      this.center = center;
      this.radius = radius;
   }



    @Override
    public Vec3d sample() { return center; }

}
