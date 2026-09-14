package me.matl114.jsApi;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

class KalamaHelperHelperE implements Inventory {
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public void setStack(int slot, ItemStack stack) {
        this.val$helpers.set(slot, JsMacrosBridge.i().f(stack));
    }

    public void clear() {
        for (int i = 0; i < this.val$size; i++) {
            this.setStack(i, ItemStack.EMPTY);
        }
    }

    KalamaHelperHelperE(int var1, List var2) {
        this.val$size = var1;
        this.val$helpers = var2;
    }

    public ItemStack removeStack(int slot, int amount) {
        Object helper = this.val$helpers.get(slot);
        return !JsMacrosBridge.i().isItemEmpty(helper) && amount > 0
                ? JsMacrosBridge.i().e(helper).split(amount)
                : ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot) {
        ItemStack itemStack = JsMacrosBridge.i().e(this.val$helpers.get(slot));
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.val$helpers.set(slot, JsMacrosBridge.i().f(ItemStack.EMPTY));
            return itemStack;
        }
    }

    public ItemStack getStack(int slot) {
        return JsMacrosBridge.i().e(this.val$helpers.get(slot));
    }

    public int size() {
        return this.val$size;
    }

    public void markDirty() {}

    public boolean isEmpty() {
        return this.val$helpers.stream().allMatch(JsMacrosBridge.i()::isItemEmpty);
    }

    List val$helpers;
    int val$size;
}
