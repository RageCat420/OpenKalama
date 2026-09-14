package me.matl114.hacks;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public record MovTasks$MovInfo(Vec3d vec3d, Boolean oGroundOverride, boolean updatePlayer, Vec2f rotationOverride) {
   public MovTasks$MovInfo withUpdatePlayer(boolean updatePlayer) {
      return this.updatePlayer == updatePlayer ? this : new MovTasks$MovInfo(this.vec3d, this.oGroundOverride, updatePlayer, this.rotationOverride);
   }

   public Boolean oGroundOverride() {
      return this.oGroundOverride;
   }

   public static MovTasks$MovInfo adB(Vec3d to) {
      return new MovTasks$MovInfo(to, null, false, null);
   }

   public MovTasks$MovInfo adE(Boolean oGroundOverride) {
      return this.oGroundOverride == oGroundOverride ? this : new MovTasks$MovInfo(this.vec3d, oGroundOverride, this.updatePlayer, this.rotationOverride);
   }


   public static MovTasks$MovInfo adz(Vec3d to) {
      return new MovTasks$MovInfo(to, null, true, null);
   }

   public Vec3d vec3d() {
      return this.vec3d;
   }

   public MovTasks$MovInfo adG(Vec2f rotationOverride) {
      return this.rotationOverride == rotationOverride ? this : new MovTasks$MovInfo(this.vec3d, this.oGroundOverride, this.updatePlayer, rotationOverride);
   }

   public static MovTasks$MovInfo adA(Vec3d to) {
      return new MovTasks$MovInfo(to, Boolean.FALSE, true, null);
   }

   public Vec2f rotationOverride() {
      return this.rotationOverride;
   }

   public boolean updatePlayer() {
      return this.updatePlayer;
   }

   public boolean isEmptyTo(Vec3d currentPos) {
      return this.vec3d.squaredDistanceTo(currentPos) < 1.0E-4 && this.rotationOverride == null && this.oGroundOverride == null;
   }

   public MovTasks$MovInfo adD(Vec3d vec3d) {
      return this.vec3d == vec3d ? this : new MovTasks$MovInfo(vec3d, this.oGroundOverride, this.updatePlayer, this.rotationOverride);
   }
}
