package me.matl114.utils;

import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

@KalamaHelperHelperA
public interface KalamaHelperHelperQ {
    boolean mayHitIgnoreShape(BlockPos var1, BlockState var2);

    default boolean mayHit(Vec3d start, Vec3d end, BlockPos pos, BlockState state, boolean strictCheck) {
        if (state.isAir() || state.isLiquid() || !this.mayHitIgnoreShape(pos, state)) {
            return false;
        } else if (!strictCheck && state.isFullCube(ExplosionUtils.b.world, pos)) {
            return true;
        } else {
            VoxelShape var6 = state.getCollisionShape(ExplosionUtils.b.world, pos);
            if (var6.isEmpty()) {
                return false;
            } else {
                BlockHitResult var7 = var6.raycast(start, end, pos);
                return var7 != null && var7.getType() == Type.BLOCK;
            }
        }
    }
}
