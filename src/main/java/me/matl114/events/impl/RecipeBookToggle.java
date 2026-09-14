package me.matl114.events.impl;

import me.matl114.hacks.modules.interact.InteractSubHelperT;
import me.matl114.hacks.modules.interact.InteractSubHelperX;

public record RecipeBookToggle(String id, InteractSubHelperX countdown, InteractSubHelperT context) {
   public InteractSubHelperT context() {
      return this.context;
   }

   public InteractSubHelperX countdown() {
      return this.countdown;
   }

   public RecipeBookToggle(String id, InteractSubHelperX countdown, InteractSubHelperT context) {
      this.id = id;
      this.countdown = countdown;
      this.context = context;
   }
}
