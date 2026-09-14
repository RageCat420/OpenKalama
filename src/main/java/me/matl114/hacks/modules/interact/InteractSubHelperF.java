package me.matl114.hacks.modules.interact;

import me.matl114.utils.InventoryUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.item.ItemStack;

public record InteractSubHelperF(InteractSubHelperFX itemStack) implements InteractSubHelperC {
    @Override
    public String toString() {
        return "Item:" + this.itemStack.asString();
    }

    public InteractSubHelperFX aan() {
        return this.itemStack;
    }

    @Override
    public KalamaHelperHelperK<ItemStack> Uv() {
        return this.itemStack == null ? null : InventoryUtils.v(this.itemStack::matches, true, false);
    }
}
