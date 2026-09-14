package me.matl114.managers.task;

import java.util.function.BooleanSupplier;

public class KalamaHelperHelperI extends RepeatTask {
   int g;
   BooleanSupplier f;

   public KalamaHelperHelperI(BooleanSupplier runnable, int delay, int period, int repeat) {
      super(delay, period);
      this.f = runnable;
      this.g = repeat;
   }

   public KalamaHelperHelperI(Runnable runnable, int delay, int period, int repeat) {
      super(delay, period);
      this.f = () -> {
         runnable.run();
         return false;
      };
   }

   @Override
   public boolean b() {
      if (this.g > 0) {
         this.g--;
         return this.f.getAsBoolean();
      } else {
         return true;
      }
   }
}
