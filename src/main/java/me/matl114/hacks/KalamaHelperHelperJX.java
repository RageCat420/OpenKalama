package me.matl114.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

class KalamaHelperHelperJX implements KalamaHelperHelperMX {
   @Override
   public boolean checkEnvironmentCollision(Entity entity, Vec3d vec, boolean checkLiquid) {
      return MovTasks.checkEnvironmentCollision(entity, vec, checkLiquid, false);
   }
}
