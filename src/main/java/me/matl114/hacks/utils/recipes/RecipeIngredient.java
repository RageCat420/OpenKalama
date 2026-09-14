package me.matl114.hacks.utils.recipes;

import net.minecraft.item.ItemStack;

public record RecipeIngredient(ItemStack[] matchingStack) {
   public static RecipeIngredient EMPTY = new RecipeIngredient(new ItemStack[0]);
   public boolean testItemType(ItemStack itemStack) {
      if (itemStack == null) {
         return false;
      } else if (this.isEmpty()) {
         return itemStack.isEmpty();
      } else {
         for (ItemStack var5 : this.matchingStack()) {
            if (var5.isOf(itemStack.getItem())) {
               return true;
            }
         }

         return false;
      }
   }

   public ItemStack[] matchingStack() {
      return this.matchingStack;
   }

   public RecipeIngredient(ItemStack s) {
      this(new ItemStack[]{s});
   }

   public RecipeIngredient(ItemStack[] matchingStack) {
      this.matchingStack = matchingStack;
   }

   public boolean isEmpty() {
      return this.matchingStack.length == 0;
   }
}
