package me.matl114.hooks.mixin.baritone;

import com.mojang.authlib.GameProfile;
import java.util.Objects;
import me.matl114.accessors.events.ClientPlayerEntityAccess;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hooks.BaritoneHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerEntity.class})
public class BaritoneClientPlayerEntityRotFixMixin extends AbstractClientPlayerEntity implements ClientPlayerEntityAccess {
   @Unique
   Vec2f storedPreBaritonePitchYaw;

   @Shadow
   public float getPitch(float tickDelta) { }

   @Shadow
   public float getYaw(float tickDelta) { }

   public BaritoneClientPlayerEntityRotFixMixin(ClientWorld world, GameProfile profile) {
      setLastRot(world, profile);
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
         shift = Shift.AFTER
      )},
      order = 999
   )
   private void onPreBaritonePlayerUpdateEvent(CallbackInfo ci) {
      if (this.checkClientPlayer()
         && BaritoneHooks.getInstance().isBaritoneAPISupported()
         && (BaritoneHooks.getInstance().isBaritonePathing() || BaritoneHooks.getInstance().isBaritoneElytraProcessing())) {
         LegalMovementManager manager = this.getLegalMovementManager();
         Vec2f rotModify = BaritoneHooks.getInstance().getBaritoneCurrentMoveRot(MinecraftClient.getInstance().player);
         Vec2f currentPY = new Vec2f(this.getPitch(), this.getYaw());
         if (rotModify != null && !Objects.equals(rotModify, currentPY) && manager.v()) {
            this.storedPreBaritonePitchYaw = currentPY;
            manager.c.d();
         }
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
         shift = Shift.AFTER
      )},
      order = 1111
   )
   private void onPostBaritonePlayerUpdateEvent(CallbackInfo ci) {
      if (this.storedPreBaritonePitchYaw != null) {
         float pitch = this.getPitch();
         float yaw = this.getYaw();
         LegalMovementManager manager = this.getLegalMovementManager();
         if (pitch == manager.c.h && yaw == manager.c.i) {
            this.setPitch(this.storedPreBaritonePitchYaw.x);
            this.setYaw(this.storedPreBaritonePitchYaw.y);
         }

         this.storedPreBaritonePitchYaw = null;
      }
   }

   public void resyncInput() { }


   public void resyncMovementPacket() { }

}
