package me.matl114.events.channels;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;

public class PacketEventChannel extends KalamaHelperHelperC<Packet<?>> {
   public EventChannel<Packet<?>> u() {
      return this.c(Boolean.FALSE);
   }

   public EventChannel<Packet<?>> v() {
      return this.c(Boolean.TRUE);
   }

   public PacketEventChannel() {
      super(v -> Listener.f(v.getClass()));
   }

   public <W extends Packet<?>> EventChannel<W> getChannel(Class<W> val) {
      return super.c(val);
   }

   public boolean handleValue(Event<Packet<?>> express) {
      if (((Packet)express.b).getPacketId() != null) {
         if (((Packet)express.b).getPacketId().side() == NetworkSide.CLIENTBOUND) {
            this.v().catchEvent(express);
         } else {
            this.u().catchEvent(express);
         }
      }

      return super.b(express);
   }
}
