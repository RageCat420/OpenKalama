package me.matl114.mixins.fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({HeightContext.class})
public class HeightContextNullFix {
    @WrapOperation(
            method = {"<init>"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getMinimumY()I")})
    private int onMinY(ChunkGenerator instance, Operation<Integer> original) {
        return instance == null ? -9999999 : (Integer) original.call(new Object[] {instance});
    }

    @WrapOperation(
            method = {"<init>"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getWorldHeight()I")})
    private int onHeight(ChunkGenerator instance, Operation<Integer> original) {
        return instance == null ? 100000000 : (Integer) original.call(new Object[] {instance});
    }
}
