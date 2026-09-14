package me.matl114.mixins.versioned;

import me.matl114.versioned.accessors.PlayerInputAccess;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Input.class})
public class PlayerInputMixin implements PlayerInputAccess {
   @Unique
   boolean pressingSprint;

   @Unique
   @Override
   public boolean isPressingSprint() {
      return this.pressingSprint;
   }

   @Unique
   @Override
   public void setPressingSprint(boolean pressingSprint) {
      this.pressingSprint = pressingSprint;
   }
}
