package me.matl114.utils.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public record RegionPos(int x, int z) {
    public RegionPos xr() {
        return new RegionPos(-this.x, -this.z);
    }

    public static RegionPos of(ChunkPos pos) {
        return new RegionPos(pos.x >> 5 << 9, pos.z >> 5 << 9);
    }

    public Vec3d xs() {
        return new Vec3d(this.x, 0.0, this.z);
    }

    public static RegionPos xp(BlockPos pos) {
        return new RegionPos(pos.getX() >> 9 << 9, pos.getZ() >> 9 << 9);
    }

    public BlockPos xt() {
        return new BlockPos(this.x, 0, this.z);
    }
}
