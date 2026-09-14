package me.matl114.hacks.modules.move;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs$BypassMode;

// $VF: synthetic class
class MoveSubHelperAa {
    static int[] b;
    static int[] a;

    static {
        try {
            b[Configs$BypassMode.BYPASS_GRIM.ordinal()] = 1;
        } catch (NoSuchFieldError var9) {
        }

        try {
            b[Configs$BypassMode.NO_BYPASS.ordinal()] = 2;
        } catch (NoSuchFieldError var8) {
        }

        a = new int[ModulePreset.values().length];

        try {
            a[ModulePreset.fd.ordinal()] = 1;
        } catch (NoSuchFieldError var7) {
        }

        try {
            a[ModulePreset.fg.ordinal()] = 2;
        } catch (NoSuchFieldError var6) {
        }

        try {
            a[ModulePreset.fh.ordinal()] = 3;
        } catch (NoSuchFieldError var5) {
        }

        try {
            a[ModulePreset.fe.ordinal()] = 4;
        } catch (NoSuchFieldError var4) {
        }

        try {
            a[ModulePreset.fj.ordinal()] = 5;
        } catch (NoSuchFieldError var3) {
        }

        try {
            a[ModulePreset.fi.ordinal()] = 6;
        } catch (NoSuchFieldError var2) {
        }

        try {
            a[ModulePreset.ff.ordinal()] = 7;
        } catch (NoSuchFieldError var1) {
        }
    }
}
