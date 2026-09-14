package me.matl114.hacks.utils.render;

import net.minecraft.util.math.Direction;

// $VF: synthetic class
class HackUtilHelperI {
    private static final int[] $SwitchMap$net$minecraft$util$math$Direction = new int[Direction.values().length];

    static {
        try {
            $SwitchMap$net$minecraft$util$math$Direction[Direction.WEST.ordinal()] = 1;
        } catch (NoSuchFieldError var6) {
        }

        try {
            $SwitchMap$net$minecraft$util$math$Direction[Direction.EAST.ordinal()] = 2;
        } catch (NoSuchFieldError var5) {
        }

        try {
            $SwitchMap$net$minecraft$util$math$Direction[Direction.DOWN.ordinal()] = 3;
        } catch (NoSuchFieldError var4) {
        }

        try {
            $SwitchMap$net$minecraft$util$math$Direction[Direction.UP.ordinal()] = 4;
        } catch (NoSuchFieldError var3) {
        }

        try {
            $SwitchMap$net$minecraft$util$math$Direction[Direction.NORTH.ordinal()] = 5;
        } catch (NoSuchFieldError var2) {
        }

        try {
            $SwitchMap$net$minecraft$util$math$Direction[Direction.SOUTH.ordinal()] = 6;
        } catch (NoSuchFieldError var1) {
        }
    }
}
