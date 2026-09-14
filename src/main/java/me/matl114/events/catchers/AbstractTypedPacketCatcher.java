package me.matl114.events.catchers;

import me.matl114.events.Event;
import me.matl114.events.channels.ListenerPoint;
import net.minecraft.network.packet.Packet;

public abstract class AbstractTypedPacketCatcher<T extends Packet<?>> implements ListenerPoint {
   public Class<T> packetClass;

   public abstract boolean a(Event<T> var1);

   public AbstractTypedPacketCatcher(Class<T> packetClass) {
      this.packetClass = packetClass;
   }

   @Override
   public boolean handleValue(Event<?> packet) {
      return this.packetClass.isInstance(packet.e()) ? this.a((Event<T>)packet) : false;
   }
}
