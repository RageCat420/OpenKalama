package me.matl114.utils.itemdb;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.item.ItemStack;

public final class KalamaHelperHelperC implements ItemStackData {
    Integer rs;
    ItemStack rr;
    JsonElement rt;

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public ItemStack gf() {
        return this.rr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ItemStackData var2) {
            return var2.isValid() ? ItemStack.areItemsAndComponentsEqual(this.rr, var2.ge()) : false;
        } else {
            return false;
        }
    }

    public KalamaHelperHelperC(ItemStack itemStack) {
        this.rr = itemStack;
    }

    @Override
    public ItemStack ge() {
        return this.rr;
    }

    @Override
    public int hashCode() {
        if (this.rs == null) {
            this.rs = ItemStack.hashCode(this.rr);
        }

        return this.rs;
    }

    @Override
    public JsonElement gb() {
        if (this.rt == null) {
            try {
                this.rt = ItemStackData.serialize(this.rr);
            } catch (Throwable var2) {
                this.rt = JsonNull.INSTANCE;
            }
        }

        return this.rt;
    }

    @Override
    public void resolveItemStack() {}
}
