package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.Listener$ExceptionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({ClientWorld.class})
public class ClientWorldEvents {
   @WrapOperation(
      method = {"tickEntity"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;tick()V"
      )}
   )
   public void onEntityTick(Entity instance, Operation<Void> original) {
      Event<Entity> entityEvent = new Event<>(instance, true, false);
      Listener.aO().b(entityEvent);
      if (!entityEvent.d()) {
         try {
            original.call(new Object[]{instance});
         } catch (Throwable var9) {
            if (var9 instanceof CrashException crashException && crashException.getCause() instanceof OutOfMemoryError) {
               throw var9;
            }

            if (Listener.handleException(var9, Listener$ExceptionType.rN, instance)) {
               throw var9;
            }
         } finally {
            Listener.aQ().b(entityEvent);
         }
      }
   }
}
