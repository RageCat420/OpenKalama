package me.matl114.utils.inventory;

import java.util.Arrays;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class MutableArrayInventory implements Inventory {
   ItemStack[] stacks;

   public ItemStack removeStack(int slot) {
      ItemStack itemStack = this.stacks[slot];
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.stacks[slot] = ItemStack.EMPTY;
         this.markDirty();
         return itemStack;
      }
   }

   public void setStack(int slot, ItemStack stack) {
      this.stacks[slot] = stack;
      this.markDirty();
   }

   public void clear() {
      for (int i = 0; i < this.stacks.length; i++) {
         this.stacks[i] = ItemStack.EMPTY;
      }

      this.markDirty();
   }

   public ItemStack getStack(int slot) {
      return this.stacks[slot];
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return true;
   }

   public boolean isEmpty() {
      return Arrays.stream(this.stacks).allMatch(ItemStack::isEmpty);
   }

   public void markDirty() {
   }

   private ItemStack splitStack(int slot, int amount) {
      return slot >= 0 && slot < this.stacks.length && !this.stacks[slot].isEmpty() && amount > 0 ? this.stacks[slot].split(amount) : ItemStack.EMPTY;
   }

   public MutableArrayInventory(ItemStack[] stacks) {
      this.stacks = stacks;
   }

   public int size() {
      return this.stacks.length;
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack itemStack = this.splitStack(slot, amount);
      if (!itemStack.isEmpty()) {
         this.markDirty();
      }

      return itemStack;
   }
}
