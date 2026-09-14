package me.matl114.utils.itemdb;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.item.ItemStack;

class KalamaHelperHelperE implements ItemStackData {
    private static final int EMPTY_HASHCODE = ItemStack.hashCode(ItemStack.EMPTY);

    @Override
    public boolean isValid() {
        return true;
    }

    public ItemStack getItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public int hashCode() {
        return EMPTY_HASHCODE;
    }

    @Override
    public ItemStack ge() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
                || obj instanceof ItemStackData var2
                        && var2.isValid()
                        && var2.ge().isEmpty();
    }

    @Override
    public JsonElement gb() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void resolveItemStack() {}

    @Override
    public ItemStack gf() {
        return ItemStack.EMPTY;
    }
}
