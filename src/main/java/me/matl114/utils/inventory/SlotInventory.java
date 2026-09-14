package me.matl114.utils.inventory;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotInventory implements Inventory {
   public List<Slot> slots;

   public void setStack(int slot, ItemStack stack) {
      this.slots.get(slot).setStack(stack);
      this.markDirty();
   }

   public void markDirty() {
   }

   public ItemStack getStack(int slot) {
      return this.slots.get(slot).getStack();
   }

   public void clear() {
      for (int i = 0; i < this.slots.size(); i++) {
         this.slots.get(i).setStack(ItemStack.EMPTY);
      }

      this.markDirty();
   }

   public boolean isEmpty() {
      return this.slots.stream().allMatch(s -> s.getStack().isEmpty());
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return true;
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack stack = this.slots.get(slot).getStack();
      ItemStack removed;
      if (!stack.isEmpty() && amount > 0) {
         removed = stack.split(amount);
      } else {
         removed = ItemStack.EMPTY;
      }

      if (!removed.isEmpty()) {
         this.markDirty();
      }

      return removed;
   }

   public SlotInventory(List<Slot> slots) {
      this.slots = slots;
   }

   public int size() {
      return this.slots.size();
   }

   public ItemStack removeStack(int slot) {
      ItemStack itemStack = this.slots.get(slot).getStack();
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.slots.get(slot).setStack(ItemStack.EMPTY);
         this.markDirty();
         return itemStack;
      }
   }
}
