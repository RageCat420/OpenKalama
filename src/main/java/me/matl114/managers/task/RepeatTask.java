package me.matl114.managers.task;

import me.matl114.events.annotations.Cancelable;

public class RepeatTask implements Cancelable {
   int d;
   int c;
   boolean isCancelled;

   public boolean b() { }

   public void cancel() {
      this.isCancelled = true;
   }

   @Override
   public boolean optional() {
      if (--this.c <= 0) {
         if (this.isCancelled) {
            return true;
         } else if (this.b()) {
            this.cancel();
            return true;
         } else {
            this.c = this.d;
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean d() {
      return this.isCancelled;
   }

   public RepeatTask(int period) {
      this(0, period);
   }

   public RepeatTask(int delay, int period) {
      this.c = delay;
      this.d = period;
   }
}
