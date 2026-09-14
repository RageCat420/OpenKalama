package me.matl114.hacks.utils.move.goal;

import me.matl114.utils.EntityUtils;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record GoalDirection(float yaw) implements IPathGoal {
   @Override
   public boolean isInGoal(Vec3d playerPos) {
      return false;
   }

   public float yaw() {
      return this.yaw;
   }

   public GoalDirection(Direction direction) {
      this(EntityUtils.directionToPitchYaw(direction).y);
   }

   public GoalDirection(float yaw) {
      this.yaw = yaw;
   }

   @Override
   public Vec3d sample() {
      return null;
   }
}
