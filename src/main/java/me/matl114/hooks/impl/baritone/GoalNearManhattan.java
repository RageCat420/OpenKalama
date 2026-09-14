package me.matl114.hooks.impl.baritone;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.interfaces.IGoalRenderPos;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class GoalNearManhattan implements Goal, IGoalRenderPos {
   private final double x;
   private final double y;
   private final double z;
   private final double threshold;

   public GoalNearManhattan(Vec3d pos, double threshold) {
      this(pos.getX(), pos.getY(), pos.getZ(), threshold);
   }

   public GoalNearManhattan(double x, double y, double z, double threshold) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.threshold = threshold;
   }

   public boolean isInGoal(int x, int y, int z) {
      double dx = Math.abs(x - this.x);
      double dy = Math.abs(y - this.y);
      double dz = Math.abs(z - this.z);
      return dx + dy + dz <= this.threshold;
   }

   public double heuristic(int x, int y, int z) {
      double dx = x - this.x;
      double dy = y - this.y;
      double dz = z - this.z;
      double manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
      return manhattan <= this.threshold ? 0.0 : GoalBlock.calculate(dx, (int)dy, dz);
   }

   public double heuristic() {
      return 0.0;
   }

   public BlockPos getGoalPos() {
      return new BlockPos((int)(Object)this.x, (int)(Object)this.y, (int)(Object)this.z);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof GoalNearManhattan other)
            ? false
            : this.x == other.x && this.y == other.y && this.z == other.z && this.threshold == other.threshold;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.z, this.threshold);
   }

   @Override
   public String toString() {
      return String.format("GoalNearManhattan{x=%.2f, y=%.2f, z=%.2f, threshold=%.2f}", this.x, this.y, this.z, this.threshold);
   }
}
