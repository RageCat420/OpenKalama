package me.matl114.hacks.modules.interact;

public class InteractSubHelperL extends InteractSubHelperHX {
   private int repeatLeft;

   @Override
   public boolean canRun() {
      return this.repeatLeft > 0;
   }

   @Override
   public int countDown() {
      if (this.a <= 0) {
         int var1 = Math.min(9, this.repeatLeft);
         this.repeatLeft -= var1;
         return var1;
      } else if (super.countDownTimer()) {
         this.repeatLeft--;
         return 1;
      } else {
         return 0;
      }
   }

   public InteractSubHelperL(int delay) {
      super(delay);
      this.repeatLeft = Integer.MAX_VALUE;
   }

   public InteractSubHelperL(int intervalTicks, int delay) {
      super(delay);
      this.repeatLeft = intervalTicks;
   }

   @Override
   public void cancel() {
      this.repeatLeft = 0;
   }
}
