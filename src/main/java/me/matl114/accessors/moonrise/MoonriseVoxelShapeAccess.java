package me.matl114.accessors.moonrise;

import me.matl114.utils.world.CachedShapeData;
import me.matl114.utils.world.CachedToAABBs;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public interface MoonriseVoxelShapeAccess {
    double moonrise$offsetX();

    double moonrise$offsetY();

    double moonrise$offsetZ();

    double[] moonrise$rootCoordinatesX();

    double[] moonrise$rootCoordinatesY();

    double[] moonrise$rootCoordinatesZ();

    Box moonrise$getSingleAABBRepresentation();

    CachedToAABBs moonrise$cachedToAABBs();

    void moonriss$setCachedToAABBs(CachedToAABBs var1);

    boolean moonrise$isFullBlock();

    CachedShapeData moonrise$getCachedVoxelData();

    void moonrise$initCache();

    static MoonriseVoxelShapeAccess of(VoxelShape voxel) {
        return (MoonriseVoxelShapeAccess) voxel;
    }
}
