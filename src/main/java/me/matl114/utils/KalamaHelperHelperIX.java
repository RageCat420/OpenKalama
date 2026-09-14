package me.matl114.utils;

import java.util.Map;
import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;

class KalamaHelperHelperIX implements KalamaHelperHelperHX {
   @KalamaHelperHelperA
   @Override
   public double getBlockResistance(BlockPos pos, BlockState state) {
      if (this.a.containsKey(pos)) {
         return state == null ? 0.0 : state.getBlock().getBlastResistance();
      } else {
         return this.b.getBlockResistance(pos, state);
      }
   }

   @KalamaHelperHelperA
   @Override
   public double getFluidResistance(BlockPos pos) {
      if (this.a.containsKey(pos)) {
         BlockState var2 = (BlockState)(Object)this.a.get(pos);
         return var2 == null ? 0.0 : var2.getFluidState().getBlastResistance();
      } else {
         return this.b.getFluidResistance(pos);
      }
   }

   @KalamaHelperHelperA
   @Override
   public BlockState a(BlockPos pos) {
      return this.a.containsKey(pos) ? (BlockState)(Object)this.a.get(pos) : this.b.a(pos);
   }

   KalamaHelperHelperIX(Map var1, KalamaHelperHelperHX var2) {
      this.a = var1;
      this.b = var2;
   }

   @KalamaHelperHelperA
   @Override
   public VoxelShape getCollisionShape(BlockPos pos) {
      if (this.a.containsKey(pos)) {
         BlockState var2 = (BlockState)(Object)this.a.get(pos);
         return var2 == null ? VoxelShapes.empty() : var2.getCollisionShape(EmptyBlockView.INSTANCE, pos);
      } else {
         return this.b.getCollisionShape(pos);
      }
   }
   Map a;
   KalamaHelperHelperHX b;
}
