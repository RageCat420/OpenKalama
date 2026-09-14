package me.matl114.hacks.utils.tasks;

public class StateExecutor {
   boolean a = false;

   public void state(boolean state) {
      this.a = state;
   }

   public boolean a(boolean state, Runnable runnable) {
      if (state != this.a) {
         this.a = state;
         runnable.run();
         return true;
      } else {
         return false;
      }
   }

   public void stateOrElse(boolean state, Runnable runnable, Runnable orElse) {
      if (!this.a(state, runnable)) {
         orElse.run();
      }
   }
}
