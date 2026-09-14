package me.matl114.accessors.access;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractTypeHandler;

public interface PlayerInteractEntityC2SPacketAccess {
    void setEntityId(int var1);

    int getEntityId();

    void setType(InteractTypeHandler var1);

    void setPlayerSneaking(boolean var1);

    boolean isAttack();

    static PlayerInteractEntityC2SPacketAccess of(PlayerInteractEntityC2SPacket packet) {
        return (PlayerInteractEntityC2SPacketAccess) packet;
    }
}
