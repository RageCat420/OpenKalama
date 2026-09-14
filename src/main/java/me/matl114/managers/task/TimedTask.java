package me.matl114.managers.task;

import me.matl114.events.annotations.Cancelable;
import me.matl114.managers.Tasks;

public class TimedTask implements Cancelable {
   int expireTicks;

   @Override
   public boolean optional() {
      return Tasks.b() >= this.expireTicks ? this.b() : false;
   }

   abstract boolean b();

   public TimedTask(int delay) {
      this.expireTicks = Tasks.b() + delay;
   }
}
