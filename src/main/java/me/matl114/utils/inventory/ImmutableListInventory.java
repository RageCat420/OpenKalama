package me.matl114.utils.inventory;

import java.util.function.Supplier;
import net.minecraft.item.ItemStack;

public class ImmutableListInventory extends ImmutableInventory {
   public ItemStack getStack(int slot) {
      return (ItemStack)(Object)this.itemStacks.get();
   }

   ImmutableListInventory(Supplier var1) {
      this.itemStacks = var1;
   }

   public int size() {
      return 1;
   }
}
