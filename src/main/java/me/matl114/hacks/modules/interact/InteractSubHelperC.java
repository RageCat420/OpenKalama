package me.matl114.hacks.modules.interact;

import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface InteractSubHelperC {
   KalamaHelperHelperK<ItemStack> Uv();

   static InteractSubHelperC Uy(InteractSubHelperFX itemStack) {
      return itemStack == null ? null : new InteractSubHelperF(itemStack);
   }

   static InteractSubHelperC Uw() {
      return new InteractSubHelperE();
   }

   static InteractSubHelperC Ux(Hand hand) {
      return new InteractSubHelperD(hand);
   }
}
