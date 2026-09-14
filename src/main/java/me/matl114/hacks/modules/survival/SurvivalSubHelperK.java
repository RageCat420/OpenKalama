package me.matl114.hacks.modules.survival;

import net.minecraft.util.math.BlockPos;

class SurvivalSubHelperK {
    public BlockPos b;
    private final BlockPos a;
    private int ticks;

    public void updateLastPosition(BlockPos pos) {
        this.b = pos.toImmutable();
    }

    public BlockPos d() {
        return this.a;
    }

    public boolean shouldRollback(BlockPos current) {
        return this.ticks > 20 && this.a.getSquaredDistance(current) <= 9.0;
    }

    public BlockPos e() {
        return this.b;
    }

    public SurvivalSubHelperK(BlockPos snapshotPos) {
        this.a = snapshotPos.toImmutable();
        this.b = this.a;
    }

    public void tick() {
        this.ticks++;
    }
}
