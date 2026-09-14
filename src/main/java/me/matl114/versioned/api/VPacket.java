package me.matl114.versioned.api;

import me.matl114.versioned.impl.Packet_v1_21_1;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public interface VPacket {
   VPacket instance = new Packet_v1_21_1();

   static VehicleMoveC2SPacket i(Entity entity) {
      return getInstance().e(entity);
   }

   PlayerMoveC2SPacket b(double var1, double var3, double var5, boolean var7, boolean var8);

   static Vec3d getVelocity(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket) {
      return new Vec3d(entityVelocityUpdateS2CPacket.getVelocityX(), entityVelocityUpdateS2CPacket.getVelocityY(), entityVelocityUpdateS2CPacket.getVelocityZ());
   }

   private static VPacket getInstance() {
      return instance;
   }

   PlayerMoveC2SPacket d(double var1, double var3, double var5, float var7, float var8, boolean var9, boolean var10);

   static PlayerMoveC2SPacket h(float yaw, float pitch, boolean isOnGround, boolean collision) {
      return getInstance().c(yaw, pitch, isOnGround, collision);
   }

   PlayerMoveC2SPacket c(float var1, float var2, boolean var3, boolean var4);

   static PlayerMoveC2SPacket f(boolean isOnGround, boolean collision) {
      return getInstance().a(isOnGround, collision);
   }

   PlayerMoveC2SPacket a(boolean var1, boolean var2);

   VehicleMoveC2SPacket e(Entity var1);

   static PlayerMoveC2SPacket j(double x, double y, double z, float yaw, float pitch, boolean isOnGround, boolean collision) {
      return getInstance().d(x, y, z, yaw, pitch, isOnGround, collision);
   }

   static boolean getCollisionFlag(PlayerMoveC2SPacket packet) {
      return false;
   }

   static PlayerMoveC2SPacket g(double x, double y, double z, boolean isOnGround, boolean collision) {
      return getInstance().b(x, y, z, isOnGround, collision);
   }

}
