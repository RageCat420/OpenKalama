package me.matl114.events.packets;

import javax.annotation.Nullable;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.PacketType;

public interface PacketStorage {
    void send();

    void handle();

    @Nullable
    PacketType<?> packetType();

    long timestampMS();

    NetworkSide side();
}
