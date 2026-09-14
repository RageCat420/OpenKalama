package me.matl114.accessors.access;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public interface PlayerMoveC2SPacketAccess {
   void setOnGround(boolean var1);

   void setPitch(float var1);

   void setYaw(float var1);

   void setCause(PlayerMoveC2SPacketAccess.Cause var1);

   PlayerMoveC2SPacketAccess.Cause getCause();

   static PlayerMoveC2SPacket setCause(PlayerMoveC2SPacket packet, PlayerMoveC2SPacketAccess.Cause cause) {
      of(packet).setCause(cause);
      return packet;
   }

   static PlayerMoveC2SPacket setCauseFrom(PlayerMoveC2SPacket packet, PlayerMoveC2SPacket packet2) {
      return setCause(packet, of(packet2).getCause());
   }

   static PlayerMoveC2SPacketAccess of(PlayerMoveC2SPacket packet) {
      return (PlayerMoveC2SPacketAccess)packet;
   }

   public static enum Cause {
      SET_BACK,
      PLAYER_MOVEMENT,
      HACKING_PACKETS,
      LEGACY_SNAP,
      TRIGGER_SIMULATION;
   }
}
