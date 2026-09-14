package me.matl114.hacks;

import me.matl114.managers.Configs$LegalInteractMode;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

// $VF: synthetic class
class KalamaHelperHelperTX {
    static int[] c;
    static int[] b;
    static int[] a;

    static {
        try {
            c[Axis.X.ordinal()] = 1;
        } catch (NoSuchFieldError var13) {
        }

        try {
            c[Axis.Z.ordinal()] = 2;
        } catch (NoSuchFieldError var12) {
        }

        b = new int[Direction.values().length];

        try {
            b[Direction.DOWN.ordinal()] = 1;
        } catch (NoSuchFieldError var11) {
        }

        try {
            b[Direction.UP.ordinal()] = 2;
        } catch (NoSuchFieldError var10) {
        }

        try {
            b[Direction.NORTH.ordinal()] = 3;
        } catch (NoSuchFieldError var9) {
        }

        try {
            b[Direction.SOUTH.ordinal()] = 4;
        } catch (NoSuchFieldError var8) {
        }

        try {
            b[Direction.WEST.ordinal()] = 5;
        } catch (NoSuchFieldError var7) {
        }

        try {
            b[Direction.EAST.ordinal()] = 6;
        } catch (NoSuchFieldError var6) {
        }

        a = new int[Configs$LegalInteractMode.values().length];

        try {
            a[Configs$LegalInteractMode.USEITEM_PACKET.ordinal()] = 1;
        } catch (NoSuchFieldError var5) {
        }

        try {
            a[Configs$LegalInteractMode.DELAY_MOVEMENT.ordinal()] = 2;
        } catch (NoSuchFieldError var4) {
        }

        try {
            a[Configs$LegalInteractMode.MOVEMENT_POST.ordinal()] = 3;
        } catch (NoSuchFieldError var3) {
        }

        try {
            a[Configs$LegalInteractMode.LEGACY_SLIENT_ROT.ordinal()] = 4;
        } catch (NoSuchFieldError var2) {
        }

        try {
            a[Configs$LegalInteractMode.NONE.ordinal()] = 5;
        } catch (NoSuchFieldError var1) {
        }
    }
}
