package me.matl114.hacks.modules.interact;

import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.item.ItemStack;

public record InteractSubHelperE() implements InteractSubHelperC {
   @Override
   public KalamaHelperHelperK<ItemStack> Uv() {
      return InteractManager.currentHandContext(InteractManager.preferredHand());
   }

   @Override
   public String toString() {
      return "Any";
   }
}
