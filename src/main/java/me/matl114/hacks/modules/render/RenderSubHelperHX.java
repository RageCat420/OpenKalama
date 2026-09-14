package me.matl114.hacks.modules.render;

import it.unimi.dsi.fastutil.Hash.Strategy;
import net.minecraft.item.ItemStack;

class RenderSubHelperHX implements Strategy<ItemStack> {
   public int hashCode(ItemStack o) {
      return o != null ? ItemStack.hashCode(o) : 0;
   }

   RenderSubHelperHX(final StorageDisplay this$0) {
   }

   public boolean equals(ItemStack a, ItemStack b) {
      return a != null && b != null ? ItemStack.areItemsAndComponentsEqual(a, b) : a == b;
   }
}
