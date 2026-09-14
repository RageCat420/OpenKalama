package me.matl114.hacks.modules.render;

import java.util.List;
import net.minecraft.item.ItemStack;

public class RenderSubHelperF {
    volatile List<ItemStack> itemStack;
    volatile long lastUpdated;

    public RenderSubHelperF(long lastUpdated, List<ItemStack> itemStack) {
        this.lastUpdated = lastUpdated;
        this.itemStack = itemStack;
    }
}
