package me.matl114.hacks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class KalamaHelperHelperGX {
   Runnable c;
   int a = -1;
   List<KalamaHelperHelperRX> d = new ArrayList<>();
   BooleanSupplier b;

   public KalamaHelperHelperGX c(BooleanSupplier autoStop) {
      this.b = autoStop;
      return this;
   }

   public KalamaHelperHelperCX e() {
      KalamaHelperHelperCX var1;
      if (this.a <= 0) {
         var1 = new KalamaHelperHelperCX(this.d.toArray(KalamaHelperHelperRX[]::new));
      } else {
         var1 = new KalamaHelperHelperCX(this.a, this.d.toArray(KalamaHelperHelperRX[]::new));
      }

      if (this.b != null) {
         var1.d(this.b);
      }

      if (this.c != null) {
         var1.e(this.c);
      }

      return var1;
   }

   public KalamaHelperHelperGX d(Runnable runnable) {
      this.c = runnable;
      return this;
   }

   public KalamaHelperHelperGX a(int tickLeft) {
      this.a = tickLeft;
      return this;
   }

   public KalamaHelperHelperGX b(KalamaHelperHelperRX renderObject) {
      this.d.add(renderObject);
      return this;
   }
}
