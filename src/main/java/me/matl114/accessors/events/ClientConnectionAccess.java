package me.matl114.accessors.events;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public interface ClientConnectionAccess {
    void handlePacket(Packet<?> var1);

    NetworkState<?> getOutboundState();

    NetworkState<?> getInboundState();

    void sendByteBuf(ByteBuf var1);

    static ClientConnectionAccess of(ClientConnection connection) {
        return (ClientConnectionAccess) connection;
    }
}
