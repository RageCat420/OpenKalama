package me.matl114.utils;

import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

class KalamaHelperHelperZ implements KalamaHelperHelperHX {
   final BlockView val$world;
   KalamaHelperHelperZ(BlockView var1) {
      this.val$world = var1;
   }

   @KalamaHelperHelperA
   @Override
   public double getFluidResistance(BlockPos pos) {
      return this.val$world.getFluidState(pos).getBlastResistance();
   }

   @KalamaHelperHelperA
   public BlockState getBlockState(BlockPos pos) {
      return this.val$world.getBlockState(pos);
   }

   @KalamaHelperHelperA
   @Override
   public VoxelShape getCollisionShape(BlockPos pos) {
      return this.val$world.getBlockState(pos).getCollisionShape(this.val$world, pos);
   }



   @Override
   public BlockState a(Object arg0) { return null; }

}
