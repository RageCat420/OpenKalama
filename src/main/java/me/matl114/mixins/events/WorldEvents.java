package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.Listener$ExceptionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({World.class})
public class WorldEvents {
   @Shadow
   @Final
   private boolean field_9236;

   @WrapOperation(
      method = {"tickBlockEntities"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V"
      )}
   )
   public void shouldTickBlockEntities(BlockEntityTickInvoker instance, Operation<Void> original) {
      if (this.field_9236) {
         Event<BlockEntityTickInvoker> event = new Event<>(instance, true, false);
         Listener.aV().catchEvent(event);
         if (!event.d()) {
            try {
               original.call(new Object[]{instance});
            } catch (Throwable var8) {
               if (Listener.handleException(var8, Listener$ExceptionType.rO, instance, this)) {
                  throw var8;
               }
            } finally {
               ;
            }
         }
      } else {
         original.call(new Object[]{instance});
      }
   }
}
