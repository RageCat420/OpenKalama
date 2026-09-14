package me.matl114.hacks.modules.interact;

import me.matl114.hacks.api.ModulePreset;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

// $VF: synthetic class
class InteractSubHelperK {
    static int[] b;
    static int[] a;

    static {
        try {
            b[ModulePreset.fd.ordinal()] = 1;
        } catch (NoSuchFieldError var4) {
        }

        try {
            b[ModulePreset.fe.ordinal()] = 2;
        } catch (NoSuchFieldError var3) {
        }

        a = new int[Action.values().length];

        try {
            a[Action.START_DESTROY_BLOCK.ordinal()] = 1;
        } catch (NoSuchFieldError var2) {
        }

        try {
            a[Action.STOP_DESTROY_BLOCK.ordinal()] = 2;
        } catch (NoSuchFieldError var1) {
        }
    }
}
