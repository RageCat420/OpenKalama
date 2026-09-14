package me.matl114.utils.inventory;

import java.util.Arrays;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import me.matl114.managers.Tasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class MyIngredientImmutableInventory implements Inventory {
    RecipeIngredient[] ingredients;

    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY;
    }

    public void setStack(int slot, ItemStack stack) {}

    public void clear() {}

    public ItemStack getCurrentItemStack(RecipeIngredient ingredient) {
        ItemStack[] itemStacks = ingredient.matchingStack();
        return itemStacks.length == 0
                ? ItemStack.EMPTY
                : itemStacks[MathHelper.floor(Tasks.b() / 30.0F) % itemStacks.length];
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    public boolean isEmpty() {
        return Arrays.stream(this.ingredients).allMatch(RecipeIngredient::isEmpty);
    }

    public void markDirty() {}

    public ItemStack getStack(int slot) {
        return this.getCurrentItemStack(this.ingredients[slot]);
    }

    public MyIngredientImmutableInventory(RecipeIngredient[] val) {
        this.ingredients = val;
    }

    public int size() {
        return this.ingredients.length;
    }

    public ItemStack removeStack(int slot, int amount) {
        return ItemStack.EMPTY;
    }
}
