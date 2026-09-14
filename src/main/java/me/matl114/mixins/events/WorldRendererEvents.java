package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({WorldRenderer.class})
public class WorldRendererEvents {
   @WrapOperation(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Frustum;DDD)Z"
      )}
   )
   public boolean onEntityRenderEvent(
      EntityRenderDispatcher instance, Entity entity, Frustum frustum, double x, double y, double z, Operation<Boolean> original
   ) {
      Event<Entity> event = new Event<>(entity, true, false);
      RenderListener.y().catchEvent(event);
      return event.d() ? false : (Boolean)original.call(new Object[]{instance, entity, frustum, x, y, z});
   }
}
