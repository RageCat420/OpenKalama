package me.matl114.utils.inventory;

import java.util.function.Supplier;
import net.minecraft.item.ItemStack;

public class ImmutableListInventory extends ImmutableInventory {
   private final Supplier<ItemStack> itemStacks;

   public ImmutableListInventory(Supplier<ItemStack> itemStackSupplier) {
      this.itemStacks = itemStackSupplier;
   }

   @Override
   public ItemStack getStack(int slot) {
      return this.itemStacks.get();
   }

   @Override
   public int size() {
      return 1;
   }
}
