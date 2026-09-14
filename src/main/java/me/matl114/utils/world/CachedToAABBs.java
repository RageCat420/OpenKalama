package me.matl114.utils.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Box;

public record CachedToAABBs(List<Box> aabbs, boolean isOffset, double offX, double offY, double offZ) {
   public List<Box> aabbs() {
      return this.aabbs;
   }

   public boolean isOffset() {
      return this.isOffset;
   }

   public double offX() {
      return this.offX;
   }

   public double offY() {
      return this.offY;
   }

   public static CachedToAABBs offset(CachedToAABBs cache, double offX, double offY, double offZ) {
      if (offX == 0.0 && offY == 0.0 && offZ == 0.0) {
         return cache;
      } else {
         double var7 = cache.offX + offX;
         double var9 = cache.offY + offY;
         double var11 = cache.offZ + offZ;
         return new CachedToAABBs(cache.aabbs, true, var7, var9, var11);
      }
   }

   public double offZ() {
      return this.offZ;
   }

   public CachedToAABBs(List<Box> aabbs, boolean isOffset, double offX, double offY, double offZ) {
      this.aabbs = aabbs;
      this.isOffset = isOffset;
      this.offX = offX;
      this.offY = offY;
      this.offZ = offZ;
   }

   public CachedToAABBs removeOffset() {
      List var1 = this.aabbs;
      double var2 = this.offX;
      double var4 = this.offY;
      double var6 = this.offZ;
      ArrayList var8 = new ArrayList(var1.size());
      int var9 = 0;

      for (int var10 = var1.size(); var9 < var10; var9++) {
         var8.add(((Box)var1.get(var9)).offset(var2, var4, var6));
      }

      return new CachedToAABBs(var8, false, 0.0, 0.0, 0.0);
   }
}
