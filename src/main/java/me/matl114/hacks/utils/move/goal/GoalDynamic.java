package me.matl114.hacks.utils.move.goal;

import java.util.function.Supplier;
import me.matl114.utils.MathUtils;
import net.minecraft.util.math.Vec3d;

public record GoalDynamic(Supplier<Vec3d> supplier, double radius) implements IPathGoal {
   public double radius() {
      return this.radius;
   }

   public GoalDynamic(Supplier<Vec3d> supplier, double radius) {
      this.supplier = supplier;
      this.radius = radius;
   }

   public Supplier<Vec3d> supplier() {
      return this.supplier;
   }

   @Override
   public Vec3d sample() {
      return this.supplier.get();
   }

   @Override
   public boolean isInGoal(Vec3d playerPos) {
      return this.supplier.get().squaredDistanceTo(playerPos) <= MathUtils.a(this.radius);
   }
}
