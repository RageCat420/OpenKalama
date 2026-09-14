package me.matl114.accessors.moonrise;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.util.shape.VoxelShape;

public interface MoonriseBlockStateBaseAccess {
    VoxelShape moonrise$getConstantCollisionShape();

    default boolean isConstantCollisionShapeEmpty() {
        return this.moonrise$getConstantCollisionShape() == null
                || this.moonrise$getConstantCollisionShape().isEmpty();
    }

    static MoonriseBlockStateBaseAccess of(AbstractBlockState state) {
        return (MoonriseBlockStateBaseAccess) state;
    }
}
