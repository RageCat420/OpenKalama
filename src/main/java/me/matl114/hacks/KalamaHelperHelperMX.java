package me.matl114.hacks;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public interface KalamaHelperHelperMX {
   default List<Vec3d> generateTpSequence(Vec3d current, Vec3d target, boolean command, double farawayTp, boolean considerEnvironment) {
      return MovTasks.generateTpSequenceInternal(current, target, command, farawayTp, considerEnvironment);
   }

   default Vec3d simulateMovement(Entity entity, Vec3d currentPos, Vec3d currentTry) {
      return MovTasks.simulateMovement(entity, currentPos, currentTry, true);
   }

   default boolean checkEnvironmentCollision(Entity entity, Vec3d vec, boolean checkLiquid) {
      return MovTasks.checkEnvironmentCollision(entity, vec, checkLiquid, true);
   }
}
