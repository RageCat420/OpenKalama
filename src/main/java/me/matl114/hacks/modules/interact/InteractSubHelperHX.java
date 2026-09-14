package me.matl114.hacks.modules.interact;

public class InteractSubHelperHX implements InteractSubHelperX {
   int b;
   final int a;

   public final boolean countDownTimer() {
      if (++this.b >= this.a) {
         this.b = 0;
         return true;
      } else {
         return false;
      }
   }

   public InteractSubHelperHX(int delay) {
      this.a = delay;
      this.b = delay;
   }

   public boolean canRun() { return false; }


   public void cancel() { }

}
