package me.matl114.accessors.access;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;

public interface MerchantScreenAccess {
    int getSelectedIndex();

    void setSelectedIndex(int var1);

    static MerchantScreenAccess of(MerchantScreen screen) {
        return (MerchantScreenAccess) screen;
    }
}
