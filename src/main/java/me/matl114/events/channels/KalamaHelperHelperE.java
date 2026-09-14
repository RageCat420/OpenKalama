package me.matl114.events.channels;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class KalamaHelperHelperE<W> {
   protected List<KalamaHelperHelperA<W>> d = new ArrayList<>();

   public void p(Predicate<Consumer<W>> p) {
      this.d.removeIf(h -> h.value instanceof Consumer var3 && p.test(var3));
   }

   public boolean d() {
      return this.d.isEmpty();
   }

   public boolean a(W var1) { }

   public void o(Predicate<Predicate<W>> p) {
      this.d.removeIf(h -> h.value instanceof Predicate var3 && p.test(var3));
   }

   public void q(Predicate p) {
      this.d.removeIf(h -> p.test(h.value));
   }

   public void m(Predicate<W> val, int p) {
      this.n(val, p);
   }

   public void j(Predicate<W> val) {
      this.n(val, 0);
   }

   private void n(Object val, int p) {
      KalamaHelperHelperA var3 = new KalamaHelperHelperA<>(p, val);
      int var4 = this.d.size() - 1;

      while (var4 >= 0 && this.d.get(var4).priority > p) {
         var4--;
      }

      this.d.add(var4 + 1, var3);
   }

   public void l(Consumer<W> val, int p) {
      this.n(val, p);
   }

   public void k(Consumer<W> val) {
      this.n(val, 0);
   }
}
