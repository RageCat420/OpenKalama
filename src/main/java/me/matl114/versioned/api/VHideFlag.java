package me.matl114.versioned.api;

import net.minecraft.item.ItemStack;

public interface VHideFlag {
    @Deprecated
    String displayName();

    boolean isHide(ItemStack var1);

    void setHideFlag(ItemStack var1, boolean var2);
}
