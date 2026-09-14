package me.matl114.events.channels;

import me.matl114.events.Event;
import me.matl114.events.catchers.PacketCatcher;

public class EventChannel<T> extends PacketCatcher<Event<T>> {
   private static final Object[] VALUES = new Object[0];

   public boolean fireEvent(T val) {
      if (this.d()) {
         return true;
      } else {
         Event var2 = new Event<>(val, true, false);
         this.catchEvent(var2);
         return !var2.d();
      }
   }

   public void broadcast(T val) {
      this.h((T)val, VALUES);
   }

   public void h(T val, Object... val2) {
      if (!this.d()) {
         this.catchEvent(new Event<>((T)val, false, false, val2));
      }
   }
}
