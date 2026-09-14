package me.matl114.hacks.modules.slimefun;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class SlimefunSubHelperF {
   Vec3i b;
   BlockPos a;

   public SlimefunSubHelperF(BlockPos leftDown, Vec3i left2Right) {
      this.a = leftDown;
      this.b = left2Right;
   }

   public BlockPos getComponentBlock(int x, int y) {
      return this.a.add(this.b.getX() * x, y, this.b.getZ() * x);
   }
}
