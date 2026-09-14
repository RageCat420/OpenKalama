package me.matl114.hacks.utils.move.goal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record GoalNearBlockPos(BlockPos pos) implements IPathGoal {
    public BlockPos qC() {
        return this.pos;
    }

    @Override
    public Vec3d sample() {
        return this.pos.toBottomCenterPos();
    }

    @Override
    public boolean isInGoal(Vec3d playerPos) {
        BlockPos var2 = BlockPos.ofFloored(playerPos);
        int var3 = var2.getX() - this.pos.getX();
        int var4 = var2.getY() - this.pos.getY();
        int var5 = var2.getZ() - this.pos.getZ();
        return Math.abs(var3) + Math.abs(var4 < 0 ? var4 + 1 : var4) + Math.abs(var5) <= 1;
    }
}
