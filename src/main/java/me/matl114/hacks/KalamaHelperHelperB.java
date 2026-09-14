package me.matl114.hacks;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class KalamaHelperHelperB {
   public int count = 0;
   public ItemStack a;
   public IntList c = new IntArrayList();

   public IntList f() {
      return this.c;
   }

   public int getCount() {
      return this.count;
   }

   public void setItemSample(ItemStack stack) {
      this.a = stack.copy();
   }

   public void addMatchingSlot(int idx, Slot slot) {
      this.c.add(idx);
      this.count = this.count + slot.getStack().getCount();
   }

   public KalamaHelperHelperB() {
      this.a = null;
   }

   public ItemStack d() {
      return this.a;
   }

   public int[] toIntArray() {
      return this.c.toIntArray();
   }
}
