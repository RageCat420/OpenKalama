package me.matl114.mixins.events;

import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperD;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({WorldChunk.class})
public class WorldChunkEvents {
   @Shadow
   @Final
   private World field_12858;

   @Inject(
      method = {"setBlockState"},
      at = {@At("TAIL")}
   )
   private void onSetBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
      if (this.field_12858 instanceof ClientWorld world && MinecraftClient.getInstance().world == world) {
         BlockState oldState = (BlockState)cir.getReturnValue();
         if (oldState != null) {
            Listener.aW().broadcast(new KalamaHelperHelperD(world, pos, (BlockState)cir.getReturnValue(), state));
         }
      }
   }
}
