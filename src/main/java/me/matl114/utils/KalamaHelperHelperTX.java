package me.matl114.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class KalamaHelperHelperTX implements Iterator<ItemStack> {
    private int u;
    private final Inventory inventory;
    private final int v;

    public KalamaHelperHelperTX(Inventory inventory) {
        this.inventory = inventory;
        this.v = inventory.size();
    }

    @Override
    public boolean hasNext() {
        return this.u < this.v;
    }

    public ItemStack next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            return this.inventory.getStack(this.u++);
        }
    }
}
