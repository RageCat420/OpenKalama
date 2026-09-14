package me.matl114.hacks.modules.extra;

import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;

// $VF: synthetic class
class ExtraSubHelperQ {
    private static final int[] $SwitchMap$me$matl114$hacks$api$ModulePreset =
            new int[PlayerMoveC2SPacketAccess.Cause.values().length];

    static {
        try {
            $SwitchMap$me$matl114$hacks$api$ModulePreset[PlayerMoveC2SPacketAccess.Cause.SET_BACK.ordinal()] = 1;
        } catch (NoSuchFieldError var2) {
        }

        try {
            $SwitchMap$me$matl114$hacks$api$ModulePreset[PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP.ordinal()] = 2;
        } catch (NoSuchFieldError var1) {
        }
    }
}
