package me.matl114.utils;

import java.util.ArrayList;
import java.util.function.IntSupplier;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperUX {
   private final IntSupplier supplier;
   private final Vec3d[] pointList;

   public Vec3d compute(int ticksLater) {
      int var2 = this.supplier.getAsInt();
      Vec3d var3 = this.pointList[var2];
      if (var3 == null) {
         return null;
      } else {
         int var4 = this.pointList.length;
         int var5 = 1;
         ArrayList<Vec3d> var6 = new ArrayList();
         var6.add(var3);

         while (var5 < var4) {
            Vec3d var7 = this.pointList[(var2 - var5 + var4) % var4];
            if (var7 == null) {
               break;
            }

            var6.add(0, var7);
            var5++;
         }

         Vec3d var13 = null;
         if (!var6.isEmpty()) {
            var13 = var3;
         }

         if (var6.size() < 2) {
            return var13;
         } else {
            ArrayList<Vec3d> var8 = new ArrayList();
            Vec3d var9 = null;

            for (Vec3d var11 : var6) {
               if (var9 == null) {
                  var9 = var11;
               } else {
                  var8.add(var11.subtract(var9));
                  var9 = var11;
               }
            }

            if (var8.size() < 2) {
               return var8.size() == 1 ? var3.add(((Vec3d)var8.get(0)).multiply(ticksLater)) : var13;
            } else {
               Vec3d var14 = new Vec3d(0.0, 0.0, 0.0);

               for (Vec3d var12 : var8) {
                  var14 = var14.add(var12).multiply(0.5);
               }

               return var13.add(var14.multiply(ticksLater));
            }
         }
      }
   }

   public KalamaHelperHelperUX(Vec3d[] historyStack, IntSupplier currentIndex) {
      this.pointList = historyStack;
      this.supplier = currentIndex;
   }
}
