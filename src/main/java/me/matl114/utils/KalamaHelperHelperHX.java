package me.matl114.utils;

import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;

@KalamaHelperHelperA
public interface KalamaHelperHelperHX {
    @KalamaHelperHelperA
    default double getFluidResistance(BlockPos pos) {
        return 0.0;
    }

    @KalamaHelperHelperA
    default double e(BlockPos pos, BlockState state) {
        return Math.max(this.getBlockResistance(pos, state), this.getFluidResistance(pos));
    }

    @KalamaHelperHelperA
    default double getBlockResistance(BlockPos pos, BlockState state) {
        return state == null ? 0.0 : state.getBlock().getBlastResistance();
    }

    @KalamaHelperHelperA
    default boolean hasBlastResistance(BlockPos pos, BlockState state) {
        return state != null && !state.isAir() || this.getFluidResistance(pos) > 0.0;
    }

    @KalamaHelperHelperA
    BlockState a(BlockPos var1);

    @KalamaHelperHelperA
    default VoxelShape getCollisionShape(BlockPos pos) {
        BlockState var2 = this.a(pos);
        return var2 == null ? VoxelShapes.empty() : var2.getCollisionShape(EmptyBlockView.INSTANCE, pos);
    }
}
