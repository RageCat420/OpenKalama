package me.matl114.utils;

import me.matl114.utils.inventory.ImmutableInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

class KalamaHelperHelperO extends ImmutableInventory {
    final Inventory val$view;

    public ItemStack getStack(int slot) {
        return this.val$view.getStack(slot + this.d);
    }

    KalamaHelperHelperO(Inventory var1, int var2, int var3) {
        this.val$view = var1;
        this.c = var2;
        this.d = var3;
    }

    public int size() {
        return Math.min(this.val$view.size(), this.c) - Math.min(this.val$view.size(), this.d);
    }

    int d;
    int c;
}
