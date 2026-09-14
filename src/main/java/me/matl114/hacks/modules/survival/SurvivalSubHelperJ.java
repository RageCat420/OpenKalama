package me.matl114.hacks.modules.survival;

import java.util.ArrayList;
import java.util.List;
import me.matl114.utils.MathUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

class SurvivalSubHelperJ {
   public int stuckTicks;
   public boolean f;
   private int h;
   public final List<BlockPos> a;
   private boolean e;
   public BlockPos g;
   public final BlockPos b;
   public final BlockPos c;
   public final boolean i;

   public BlockPos c() {
      return this.c;
   }

   public void n() {
      this.e = false;
      this.f = false;
      this.stuckTicks = 0;
      this.i();
   }

   public int currentIndex() {
      return this.stuckTicks;
   }

   public List<BlockPos> a() {
      return this.a;
   }

   public void i() {
      this.g = null;
      this.h = 0;
   }

   public boolean updateGoalIndex(BlockPos playerPos, double goalDistance) {
      if (playerPos != null && this.stuckTicks < this.a.size() - 1) {
         while (this.stuckTicks < this.a.size() - 1) {
            double var4 = this.a.get(this.stuckTicks).getSquaredDistance(playerPos);
            if (var4 > MathUtils.a(goalDistance)) {
               break;
            }

            this.stuckTicks++;
         }

         return true;
      } else {
         return false;
      }
   }

   public void l() {
      this.e = true;
   }

   public void m() {
      this.e = true;
      this.f = true;
      this.i();
   }

   public SurvivalSubHelperJ(List<BlockPos> points, boolean cut) {
      this.i = cut;
      ArrayList var3 = new ArrayList();
      BlockPos var4 = null;

      for (BlockPos var6 : points) {
         if (var4 != null && cut) {
            double var7 = var4.getSquaredDistance(var6);
            if (var7 > 25.0) {
               Vec3d var9 = var4.toCenterPos();
               Vec3d var10 = var6.toCenterPos();
               Vec3d var11 = var10.subtract(var9);
               double var12 = var11.length();
               var11 = var11.normalize();
               int var14 = ((int)var12 - 1) / 5 + 1;

               for (int var15 = 1; var15 < var14; var15++) {
                  var3.add(BlockPos.ofFloored(var9.add(var11.multiply(var12 * var15 / var14))));
               }
            }
         }

         var3.add(var6.toImmutable());
         var4 = var6;
      }

      this.a = List.copyOf(var3);
      this.b = this.a.getFirst();
      this.c = this.a.getLast();
   }

   public boolean started() {
      return this.f;
   }

   public BlockPos b() {
      return this.b;
   }

   public boolean updateIndex(BlockPos playerPos) {
      if (playerPos != null && this.stuckTicks < this.a.size() - 1) {
         int var2 = this.stuckTicks;
         int var3 = Math.min(this.a.size() - 1, this.stuckTicks + 50);
         int var4 = var3;
         double var5 = Double.MAX_VALUE;

         for (int var7 = this.stuckTicks; var7 <= var3; var7++) {
            double var8 = this.a.get(var7).getSquaredDistance(playerPos);
            if (var8 < var5) {
               var5 = var8;
               var4 = var7;
            }
         }

         this.stuckTicks = var4;
         return this.stuckTicks != var2;
      } else {
         return false;
      }
   }

   public boolean tickStuck(BlockPos playerPos) {
      if (playerPos == null) {
         return false;
      } else if (this.g == null) {
         this.g = playerPos.toImmutable();
         this.h = 0;
         return false;
      } else if (this.g.getSquaredDistance(playerPos) > 36.0) {
         this.g = playerPos.toImmutable();
         this.h = 0;
         return false;
      } else {
         return ++this.h > 200;
      }
   }

   public BlockPos g() {
      return this.a.get(this.stuckTicks);
   }

   public List<BlockPos> f() {
      return List.copyOf(this.a.subList(Math.min(this.a.size() - 1, this.stuckTicks + 1), this.a.size()));
   }
}
