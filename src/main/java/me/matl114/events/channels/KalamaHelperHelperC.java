package me.matl114.events.channels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import me.matl114.events.Event;

public class KalamaHelperHelperC<T> extends EventChannel<T> {
   public Map<?, EventChannel<? extends T>> b;
   public Function<Event<T>, ?> a;

   public <W extends T> EventChannel<W> c(Object val) {
      return (EventChannel<W>)((Map<Object, EventChannel<? extends T>>)this.b).computeIfAbsent(val, v -> new EventChannel());
   }

   public KalamaHelperHelperC(Function<T, ?> dispatcher) {
      this.a = event -> dispatcher.apply(event.e());
      this.b = new ConcurrentHashMap<>();
   }

   @Override
   public boolean d() {
      return super.d() && this.b.isEmpty();
   }

   public KalamaHelperHelperC(Function<Event<T>, ?> dispatcher, boolean second) {
      this.a = dispatcher;
      this.b = new ConcurrentHashMap<>();
   }

   public boolean b(Event<T> express) {
      boolean var2 = super.catchEvent(express);
      Object var3 = this.a.apply(express);
      EventChannel var4 = this.b.get(var3);
      if (var4 != null && !var4.d()) {
         var4.catchEvent(express);
      }

      return var2;
   }
}
