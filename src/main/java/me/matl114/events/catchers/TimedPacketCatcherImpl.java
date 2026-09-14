package me.matl114.events.catchers;

import java.util.function.Predicate;
import me.matl114.events.Event;
import me.matl114.managers.Tasks;
import net.minecraft.network.packet.Packet;

public class TimedPacketCatcherImpl<T extends Packet<?>> extends PacketCatcherImpl<T> {
   public int expireTick;

   public boolean count() {
      return Tasks.b() >= this.expireTick;
   }

   public TimedPacketCatcherImpl(Class<T> packetClass, int tick, Predicate<Event<T>> predicate) {
      super(packetClass, predicate);
      this.expireTick = tick + Tasks.b();
   }

   public boolean catchEvent(Event<?> packet) {
      return this.count() ? true : super.handleValue(packet);
   }
}
