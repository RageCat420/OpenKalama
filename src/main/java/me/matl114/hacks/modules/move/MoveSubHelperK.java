package me.matl114.hacks.modules.move;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

// $VF: synthetic class
class MoveSubHelperK {
    private static final int[] $SwitchMap$net$minecraft$network$packet$c2s$play$ClientCommandC2SPacket$Mode =
            new int[Mode.values().length];

    static {
        try {
            $SwitchMap$net$minecraft$network$packet$c2s$play$ClientCommandC2SPacket$Mode[
                    Mode.START_SPRINTING.ordinal()] = 1;
        } catch (NoSuchFieldError var2) {
        }

        try {
            $SwitchMap$net$minecraft$network$packet$c2s$play$ClientCommandC2SPacket$Mode[
                    Mode.STOP_SPRINTING.ordinal()] = 2;
        } catch (NoSuchFieldError var1) {
        }
    }
}
