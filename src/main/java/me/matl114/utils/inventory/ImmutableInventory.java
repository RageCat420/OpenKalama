package me.matl114.utils.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ImmutableInventory implements Inventory {
   public void setStack(int slot, ItemStack stack) {
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return false;
   }

   public boolean isEmpty() {
      int size = this.size();

      for (int re = 0; re < size; re++) {
         ItemStack item = this.getStack(re);
         if (!item.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void markDirty() {
   }

   public void clear() {
   }

   public ItemStack removeStack(int slot, int amount) {
      return ItemStack.EMPTY;
   }

   public ItemStack removeStack(int slot) {
      return ItemStack.EMPTY;
   }
}
