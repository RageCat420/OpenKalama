package me.matl114.hacks.modules.render;

import me.matl114.hacks.utils.config.Direction2d;

// $VF: synthetic class
class RenderSubHelperPX {
    private static final int[] $SwitchMap$me$matl114$hacks$api$ModulePreset = new int[Direction2d.values().length];

    static {
        try {
            $SwitchMap$me$matl114$hacks$api$ModulePreset[Direction2d.UP.ordinal()] = 1;
        } catch (NoSuchFieldError var3) {
        }

        try {
            $SwitchMap$me$matl114$hacks$api$ModulePreset[Direction2d.LEFT.ordinal()] = 2;
        } catch (NoSuchFieldError var2) {
        }

        try {
            $SwitchMap$me$matl114$hacks$api$ModulePreset[Direction2d.RIGHT.ordinal()] = 3;
        } catch (NoSuchFieldError var1) {
        }
    }
}
