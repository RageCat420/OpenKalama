package me.matl114.events.catchers;

import java.util.function.Predicate;
import me.matl114.events.Event;
import net.minecraft.network.packet.Packet;

public class PacketCatcherImpl<T extends Packet<?>> extends AbstractTypedPacketCatcher<T> {
   Predicate<Event<T>> predicate;

   public boolean onEvent(Event<T> packet) {
      return this.predicate.test(packet);
   }

   public PacketCatcherImpl(Class<T> packetClass, Predicate<Event<T>> predicate) {
      super(packetClass);
      this.predicate = predicate;
   }



   @Override
   public boolean a(Object arg0) { return null; }

}
