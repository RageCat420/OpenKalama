package me.matl114.utils.inventory;

import net.minecraft.item.ItemStack;

public record ItemStackSample(ItemStack sample) {
    public static final ItemStackSample EMPTY = new ItemStackSample(ItemStack.EMPTY);

    @Override
    public boolean equals(Object o) {
        return o instanceof ItemStackSample var2 && ItemStack.areItemsAndComponentsEqual(var2.sample, this.sample)
                || o instanceof ItemStack var3 && ItemStack.areItemsAndComponentsEqual(var3, this.sample);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashCode(this.sample);
    }

    public ItemStack fS() {
        return this.sample;
    }

    public static ItemStackSample of(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        } else {
            ItemStack var1 = stack.copy();
            var1.setCount(1);
            return new ItemStackSample(var1);
        }
    }
}
