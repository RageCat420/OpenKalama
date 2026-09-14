package me.matl114.hacks.modules.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

class RenderSubHelperMX extends RenderSubHelperW {
   final net.minecraft.util.math.Vec3d val$offset;
   @Override
   public List<Vec3d> predictLine(int ticks) {
      List var2 = super.predictLine(ticks);
      int var3 = var2.size();
      if (var3 == 0) {
         return var2;
      } else {
         ArrayList var4 = new ArrayList();

         for (int var5 = 0; var5 < var3; var5++) {
            var4.add(((Vec3d)var2.get(var5)).subtract(this.val$offset.multiply((double)(var5 + 1) / var3)));
         }

         return var4;
      }
   }

   RenderSubHelperMX(Vec3d var1, Vec3d var2, RenderSubHelperAc var3, Entity var4, Vec3d var5) {
      super(var1, var2, var3, var4);
      this.val$offset = var5;
   }
}
