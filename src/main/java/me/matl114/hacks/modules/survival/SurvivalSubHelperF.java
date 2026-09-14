package me.matl114.hacks.modules.survival;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

record SurvivalSubHelperF(int minX, int maxX, int minZ, int maxZ, int y) {
   BlockPos selectPerimeterStop(Vec3d playerPos) {
      int var2 = this.y + 1;
      int var3 = BlockPos.ofFloored(playerPos).getX();
      int var4 = BlockPos.ofFloored(playerPos).getZ();
      BlockPos var5 = new BlockPos(this.minX - 1, var2, this.clamp(var4, this.maxZ, this.maxX));
      BlockPos var6 = new BlockPos(this.minZ + 1, var2, this.clamp(var4, this.maxZ, this.maxX));
      BlockPos var7 = new BlockPos(this.clamp(var3, this.minX, this.minZ), var2, this.maxZ - 1);
      BlockPos var8 = new BlockPos(this.clamp(var3, this.minX, this.minZ), var2, this.maxX + 1);
      BlockPos var9 = var5;
      double var10 = var5.toCenterPos().squaredDistanceTo(playerPos);
      double var12 = var6.toCenterPos().squaredDistanceTo(playerPos);
      if (var12 < var10) {
         var9 = var6;
         var10 = var12;
      }

      double var14 = var7.toCenterPos().squaredDistanceTo(playerPos);
      if (var14 < var10) {
         var9 = var7;
         var10 = var14;
      }

      double var16 = var8.toCenterPos().squaredDistanceTo(playerPos);
      if (var16 < var10) {
         var9 = var8;
      }

      return var9;
   }

   public int minX() {
      return this.minX;
   }

   public int maxZ() {
      return this.maxZ;
   }

   Box sP() {
      return new Box(this.minX + 1.0, this.y, this.maxZ + 1.0, this.minZ, this.y + 2.0, this.maxX);
   }

   public int minZ() {
      return this.minZ;
   }

   public SurvivalSubHelperF(int minX, int maxX, int minZ, int maxZ, int y) {
      this.minX = minX;
      this.minZ = maxX;
      this.maxZ = minZ;
      this.maxX = maxZ;
      this.y = y;
   }

   public int maxX() {
      return this.maxX;
   }

   private int clamp(int value, int min, int max) {
      return Math.max(min, Math.min(max, value));
   }

   Box sO() {
      return new Box(this.minX, this.y, this.maxZ, this.minZ + 1.0, this.y + 2.0, this.maxX + 1.0);
   }
}
