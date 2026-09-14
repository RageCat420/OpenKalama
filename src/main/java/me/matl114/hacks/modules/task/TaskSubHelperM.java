package me.matl114.hacks.modules.task;

import me.matl114.managers.config.Ref;

record TaskSubHelperM(String[] path, Ref<?> value) {
   public String[] path() {
      return this.path;
   }

   public Ref<?> Xm() {
      return this.value;
   }

   public TaskSubHelperM(String[] path, Ref<?> value) {
      this.path = path;
      this.value = value;
   }
}
