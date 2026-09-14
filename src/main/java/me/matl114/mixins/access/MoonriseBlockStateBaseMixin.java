package me.matl114.mixins.access;

import me.matl114.accessors.moonrise.MoonriseBlockStateBaseAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({AbstractBlockState.class})
public abstract class MoonriseBlockStateBaseMixin implements MoonriseBlockStateBaseAccess {
    @Unique
    private VoxelShape constantCollisionShape;

    @Shadow
    public abstract VoxelShape method_26194(BlockView var1, BlockPos var2, ShapeContext var3);

    @Unique
    private void initCache0() {
        try {
            this.constantCollisionShape = this.method_26194(null, null, null);
        } catch (Throwable var2) {
            this.constantCollisionShape = null;
        }
    }

    @Inject(
            method = {"initShapeCache"},
            at = {@At("RETURN")})
    public void onInitCache(CallbackInfo ci) {
        this.initCache0();
    }

    @Unique
    @Override
    public VoxelShape moonrise$getConstantCollisionShape() {
        return this.constantCollisionShape;
    }
}
