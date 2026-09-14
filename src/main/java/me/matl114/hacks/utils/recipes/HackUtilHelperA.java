package me.matl114.hacks.utils.recipes;

import java.util.Arrays;
import net.minecraft.item.ItemStack;

class HackUtilHelperA implements IRecipeEntry {
    private static final RecipeIngredient[] EMPTY = new RecipeIngredient[9];

    @Override
    public RecipeIngredient[] ingredient() {
        return (RecipeIngredient[]) EMPTY.clone();
    }

    public ItemStack output() {
        return ItemStack.EMPTY;
    }

    @Override
    public String Co() {
        return "";
    }

    @Override
    public String id() {
        return "";
    }

    static {
        Arrays.fill(EMPTY, RecipeIngredient.EMPTY);
    }

    @Override
    public ItemStack Ct() {
        return null;
    }
}
