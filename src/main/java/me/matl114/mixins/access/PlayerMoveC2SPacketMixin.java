package me.matl114.mixins.access;

import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin({PlayerMoveC2SPacket.class})
public abstract class PlayerMoveC2SPacketMixin implements PlayerMoveC2SPacketAccess {
   @Unique
   PlayerMoveC2SPacketAccess.Cause cause;

   @Mutable
   @Accessor("onGround")
   @Override
public abstract void setOnGround(boolean var1) ;

   @Mutable
   @Accessor("pitch")
   @Override
public abstract void setPitch(float var1) ;

   @Mutable
   @Accessor("yaw")
   @Override
public abstract void setYaw(float var1) ;

   @Override
   public PlayerMoveC2SPacketAccess.Cause getCause() {
      return this.cause;
   }

   @Override
   public void setCause(PlayerMoveC2SPacketAccess.Cause cause) {
      this.cause = cause;
   }
}
