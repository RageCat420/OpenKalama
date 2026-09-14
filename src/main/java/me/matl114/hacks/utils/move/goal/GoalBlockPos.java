package me.matl114.hacks.utils.move.goal;

import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record GoalBlockPos(BlockPos pos) implements IPathGoal {
   public BlockPos qC() {
      return this.pos;
   }

   @Override
   public Vec3d sample() {
      return this.pos.toBottomCenterPos();
   }

   @Override
   public boolean isInGoal(Vec3d playerPos) {
      return Objects.equals(BlockPos.ofFloored(playerPos), this.pos);
   }
}
