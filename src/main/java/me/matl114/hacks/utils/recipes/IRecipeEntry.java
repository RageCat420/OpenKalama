package me.matl114.hacks.utils.recipes;

import net.minecraft.item.ItemStack;

public interface IRecipeEntry {
   IRecipeEntry EMPTY = new HackUtilHelperA();

   String Co();

   ItemStack Ct();

   String id();

   RecipeIngredient[] ingredient();

}
