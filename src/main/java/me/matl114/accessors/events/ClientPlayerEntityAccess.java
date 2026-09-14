package me.matl114.accessors.events;

import javax.annotation.Nonnull;
import me.matl114.accessors.access.LivingEntityAccess;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public interface ClientPlayerEntityAccess extends LivingEntityAccess<ClientPlayerEntity> {
   LegalMovementManager getLegalMovementManager();

   void onPlayerInputPackets();

   default void resyncSprint() {
      this.setLastSprintFlag(!((Entity)(Object)this).isSprinting());
   }

   void setLastSprintFlag(boolean var1);

   default void resyncSneak() {
      this.setLastSneakFlag(!((Entity)(Object)this).isSneaking());
   }

   void setLastSneakFlag(boolean var1);

   default void resyncOnGround() {
      this.setLastOnGroundFlag(!((Entity)(Object)this).isOnGround());
   }

   void setLastOnGroundFlag(boolean var1);

   default void resyncPos() {
      this.setLastPos(Vec3d.ZERO);
   }

   void setLastPos(Vec3d var1);

   default void resyncRot() {
      this.setLastRot(0.0F, 0.0F);
   }

   void setLastRot(float var1, float var2);

   void resyncMovementPacket();

   void resyncInput();

   @Nonnull
   static ClientPlayerEntityAccess of(@Nonnull ClientPlayerEntity player) {
      return (ClientPlayerEntityAccess)player;
   }
}
