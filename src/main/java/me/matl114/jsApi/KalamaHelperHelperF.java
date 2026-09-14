package me.matl114.jsApi;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

// $VF: synthetic class
class KalamaHelperHelperF {
    private static final int[] $SwitchMap$net$minecraft$network$packet$c2s$play$PlayerActionC2SPacket$Action =
            new int[Action.values().length];

    static {
        try {
            $SwitchMap$net$minecraft$network$packet$c2s$play$PlayerActionC2SPacket$Action[
                    Action.SWAP_ITEM_WITH_OFFHAND.ordinal()] = 1;
        } catch (NoSuchFieldError var4) {
        }

        try {
            $SwitchMap$net$minecraft$network$packet$c2s$play$PlayerActionC2SPacket$Action[Action.DROP_ITEM.ordinal()] =
                    2;
        } catch (NoSuchFieldError var3) {
        }

        try {
            $SwitchMap$net$minecraft$network$packet$c2s$play$PlayerActionC2SPacket$Action[
                    Action.DROP_ALL_ITEMS.ordinal()] = 3;
        } catch (NoSuchFieldError var2) {
        }

        try {
            $SwitchMap$net$minecraft$network$packet$c2s$play$PlayerActionC2SPacket$Action[
                    Action.RELEASE_USE_ITEM.ordinal()] = 4;
        } catch (NoSuchFieldError var1) {
        }
    }
}
