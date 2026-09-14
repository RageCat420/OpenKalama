package me.matl114.hacks.modules.interact;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;

public record InteractSubHelperJX(Mutable startPos, BlockPos targetPos, int itemCount, ItemStack item, int selectedSlot, Hand hand, int way) {
   public int selectedSlot() {
      return this.selectedSlot;
   }

   public ItemStack item() {
      return this.item;
   }

   public int way() {
      return this.way;
   }

   public Hand hand() {
      return this.hand;
   }

   public int itemCount() {
      return this.itemCount;
   }

   public Mutable startPos() {
      return this.startPos;
   }

   public InteractSubHelperJX(Mutable startPos, BlockPos targetPos, int itemCount, ItemStack item, int selectedSlot, Hand hand, int way) {
      this.startPos = startPos;
      this.targetPos = targetPos;
      this.selectedSlot = itemCount;
      this.item = item;
      this.itemCount = selectedSlot;
      this.hand = hand;
      this.way = way;
   }

   public BlockPos targetPos() {
      return this.targetPos;
   }
}
