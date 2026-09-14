package me.matl114.utils;

import java.util.Map;
import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

class KalamaHelperHelperSX implements KalamaHelperHelperHX {
   KalamaHelperHelperSX(Map var1, BlockView var2) {
      this.a = var1;
      this.c = var2;
   }

   @KalamaHelperHelperA
   @Override
   public double getFluidResistance(BlockPos pos) {
      if (this.a.containsKey(pos)) {
         BlockState var2 = (BlockState)(Object)this.a.get(pos);
         return var2 == null ? 0.0 : var2.getFluidState().getBlastResistance();
      } else {
         return this.c.getFluidState(pos).getBlastResistance();
      }
   }

   @KalamaHelperHelperA
   public BlockState getBlockState(BlockPos pos) {
      return this.a.containsKey(pos) ? (BlockState)(Object)this.a.get(pos) : this.c.getBlockState(pos);
   }

   @KalamaHelperHelperA
   @Override
   public VoxelShape getCollisionShape(BlockPos pos) {
      BlockState var2 = (BlockState)(Object)this.a.get(pos);
      if (var2 == null && !this.a.containsKey(pos)) {
         return this.c.getBlockState(pos).getCollisionShape(this.c, pos);
      } else {
         return var2 == null ? VoxelShapes.empty() : var2.getCollisionShape(this.c, pos);
      }
   }
   Map a;
   BlockView c;



   @Override
   public BlockState a(Object arg0) { return null; }

}
