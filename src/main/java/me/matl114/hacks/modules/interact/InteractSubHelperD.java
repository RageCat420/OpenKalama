package me.matl114.hacks.modules.interact;

import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public record InteractSubHelperD(Hand hand) implements InteractSubHelperC {
    @Override
    public String toString() {
        return "Fixed:" + this.hand;
    }

    public Hand lr() {
        return this.hand;
    }

    @Override
    public KalamaHelperHelperK<ItemStack> Uv() {
        return InteractManager.currentHandContext(InteractManager.normalizedHand(this.hand));
    }
}
