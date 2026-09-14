package me.matl114.hacks.modules.combat;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs$LegalTargetingMode;

// $VF: synthetic class
class CombatSubHelperWX {
    static int[] b;
    static int[] a;

    static {
        try {
            b[ModulePreset.fd.ordinal()] = 1;
        } catch (NoSuchFieldError var5) {
        }

        try {
            b[ModulePreset.fe.ordinal()] = 2;
        } catch (NoSuchFieldError var4) {
        }

        a = new int[Configs$LegalTargetingMode.values().length];

        try {
            a[Configs$LegalTargetingMode.DELAY_MOVEMENT.ordinal()] = 1;
        } catch (NoSuchFieldError var3) {
        }

        try {
            a[Configs$LegalTargetingMode.LEGACY_SLIENT_ROT.ordinal()] = 2;
        } catch (NoSuchFieldError var2) {
        }

        try {
            a[Configs$LegalTargetingMode.NONE.ordinal()] = 3;
        } catch (NoSuchFieldError var1) {
        }
    }
}
