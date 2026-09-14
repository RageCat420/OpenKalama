package me.matl114.utils.inventory;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class MutableInventory implements Inventory {
    private final List<ItemStack> stacks;

    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
        this.markDirty();
    }

    public void clear() {
        for (int i = 0; i < this.stacks.size(); i++) {
            this.stacks.set(i, ItemStack.EMPTY);
        }

        this.markDirty();
    }

    public MutableInventory(int maxSize, List<ItemStack> stacks) {
        this.stacks = stacks;

        while (maxSize > stacks.size()) {
            stacks.add(ItemStack.EMPTY);
        }
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    public ItemStack removeStack(int slot) {
        ItemStack itemStack = this.stacks.get(slot);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(slot, ItemStack.EMPTY);
            this.markDirty();
            return itemStack;
        }
    }

    public ItemStack getStack(int slot) {
        return this.stacks.get(slot);
    }

    public int size() {
        return this.stacks.size();
    }

    public void markDirty() {}

    public boolean isEmpty() {
        return this.stacks.stream().allMatch(ItemStack::isEmpty);
    }
}
