package me.matl114.hooks.mixin.baritone;

import baritone.cache.ChunkPacker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin({ChunkPacker.class})
@Environment(EnvType.CLIENT)
public class BaritoneChunkPackerFixMixin {
    @Unique
    private static final BlockState a = Blocks.AIR.getDefaultState();

    @WrapOperation(
            method = {"a(Lnet/minecraft/world/chunk/WorldChunk;)Lbaritone/cache/CachedChunk;"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lbaritone/utils/BlockStateInterface;a(Lnet/minecraft/world/chunk/WorldChunk;III)Lnet/minecraft/block/BlockState;")
            },
            require = 0)
    private static BlockState fixWorldAccessIndexOutOfBound(
            WorldChunk chunk, int x, int y, int z, Operation<BlockState> original) {
        return y >= 0 && y < chunk.getSectionArray().length << 4
                ? (BlockState) original.call(new Object[] {chunk, x, y, z})
                : a;
    }
}
