package me.matl114.versioned;

import net.minecraft.SharedConstants;

public class DataVersion {
    public static final String DATA_VERSION_FLAG = "DataVersion";

    public static int b() {
        return 1;
    }

    public static int getDataVersion() {
        return SharedConstants.getGameVersion().getSaveVersion().getId();
    }
}
