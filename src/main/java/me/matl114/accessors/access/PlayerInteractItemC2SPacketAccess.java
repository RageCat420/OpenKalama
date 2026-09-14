package me.matl114.accessors.access;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public interface PlayerInteractItemC2SPacketAccess {
    void setHand(Hand var1);

    void setYaw(float var1);

    void setPitch(float var1);

    void setItemStack(ItemStack var1);

    ItemStack getItemStack();

    static PlayerInteractItemC2SPacketAccess of(PlayerInteractItemC2SPacket packet) {
        return (PlayerInteractItemC2SPacketAccess) packet;
    }

    static PlayerInteractItemC2SPacket setContext(ClientPlayerEntity player, PlayerInteractItemC2SPacket packet) {
        of(packet).setItemStack(player.getStackInHand(packet.getHand()).copy());
        return packet;
    }
}
