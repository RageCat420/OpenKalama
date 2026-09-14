package me.matl114.managers;

public class InputState {
   int status = -1;

   public synchronized boolean isPressed() {
      return this.status == 1;
   }

   public synchronized void setPressed(boolean pressed) {
      this.status = pressed ? 1 : 0;
   }
}
