package me.matl114.hacks.modules.interact;

import me.matl114.hacks.api.ModulePreset;
import net.minecraft.util.math.Direction;

// $VF: synthetic class
class InteractSubHelperN {
    static int[] b;
    static int[] a;

    static {
        try {
            b[Direction.DOWN.ordinal()] = 1;
        } catch (NoSuchFieldError var4) {
        }

        try {
            b[Direction.UP.ordinal()] = 2;
        } catch (NoSuchFieldError var3) {
        }

        a = new int[ModulePreset.values().length];

        try {
            a[ModulePreset.fg.ordinal()] = 1;
        } catch (NoSuchFieldError var2) {
        }

        try {
            a[ModulePreset.fh.ordinal()] = 2;
        } catch (NoSuchFieldError var1) {
        }
    }
}
