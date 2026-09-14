package me.matl114.events;

import me.matl114.accessors.events.ClientConnectionAccess;
import me.matl114.events.packets.PacketStorage;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record KalamaHelperHelperH(Packet<?> packet, long timestampMS, ClientConnection connection) implements PacketStorage {
   public ClientConnection connection() {
      return this.connection;
   }

   @Override
   public void send() {
      try {
         this.connection.send(this.packet);
      } catch (Throwable var2) {
      }
   }

   @Override
   public long timestampMS() {
      return this.timestampMS;
   }

   @Override
   public NetworkSide side() {
      return this.packet.getPacketId().side();
   }

   public Packet<?> aeU() {
      return this.packet;
   }

   @Override
   public void handle() {
      try {
         ClientConnectionAccess.of(this.connection).handlePacket(this.packet);
      } catch (Throwable var2) {
      }
   }


   @Override
   public PacketType<?> packetType() {
      return this.packet.getPacketId();
   }
}
