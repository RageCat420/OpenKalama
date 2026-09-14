package me.matl114.utils;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public final class KalamaHelperHelperAr extends EntityShapeContext {
   private boolean delegated;
   private ShapeContext delegate;

   public boolean canWalkOnFluid(FluidState stateAbove, FluidState state) {
      return this.getDelegate().canWalkOnFluid(stateAbove, state);
   }

   public boolean isHolding(Item item) {
      return this.getDelegate().isHolding(item);
   }

   public boolean a() {
      boolean var1 = this.delegated;
      this.delegated = false;
      return var1;
   }

   public ShapeContext getDelegate() {
      this.delegated = true;
      Entity var1 = this.getEntity();
      return this.delegate == null ? (this.delegate = var1 == null ? ShapeContext.absent() : ShapeContext.of(var1)) : this.delegate;
   }

   public KalamaHelperHelperAr(Entity entity) {
      super(false, 0.0, null, null, entity);
   }

   public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
      return this.getDelegate().isAbove(shape, pos, defaultValue);
   }

   public boolean isDescending() {
      return this.getDelegate().isDescending();
   }
}
