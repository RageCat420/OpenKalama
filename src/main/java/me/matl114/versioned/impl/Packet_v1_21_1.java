package me.matl114.versioned.impl;

import me.matl114.versioned.api.VPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class Packet_v1_21_1 implements VPacket {
   public PlayerMoveC2SPacket createLookAndOnGround(float yaw, float pitch, boolean isOnGround, boolean collision) {
      return new LookAndOnGround(yaw, pitch, isOnGround);
   }

   public PlayerMoveC2SPacket createFull(double x, double y, double z, float yaw, float pitch, boolean isOnGround, boolean collision) {
      return new Full(x, y, z, yaw, pitch, isOnGround);
   }

   public PlayerMoveC2SPacket createPositionAndOnGround(double x, double y, double z, boolean isOnGround, boolean collision) {
      return new PositionAndOnGround(x, y, z, isOnGround);
   }

   public VehicleMoveC2SPacket createVehicleMove(Entity entity) {
      return new VehicleMoveC2SPacket(entity);
   }

   public PlayerMoveC2SPacket createOnGroundOnly(boolean isOnGround, boolean collision) {
      return new OnGroundOnly(isOnGround);
   }



   @Override
   public VehicleMoveC2SPacket e(Object arg0) { return null; }

}
