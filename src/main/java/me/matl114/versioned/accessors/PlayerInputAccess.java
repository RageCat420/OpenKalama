package me.matl114.versioned.accessors;

import net.minecraft.client.input.Input;

public interface PlayerInputAccess {
   static PlayerInputAccess of(Input input) {
      return (PlayerInputAccess)input;
   }

   boolean isPressingSprint();

   void setPressingSprint(boolean var1);
}
