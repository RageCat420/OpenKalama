package me.matl114.utils.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;

public class KalamaHelperHelperC extends ImmutableInventory {
    List<ItemStack> val$itemStackSupplier;

    public KalamaHelperHelperC(List<ItemStack> itemStacks) {
        this.val$itemStackSupplier = itemStacks;
    }

    public int size() {
        return this.val$itemStackSupplier.size();
    }

    public ItemStack getStack(int slot) {
        return this.val$itemStackSupplier.get(slot);
    }
}
