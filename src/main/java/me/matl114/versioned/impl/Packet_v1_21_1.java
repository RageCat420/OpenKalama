package me.matl114.versioned.impl;

import me.matl114.versioned.api.VPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

public class Packet_v1_21_1 implements VPacket {
    @Override
    public PlayerMoveC2SPacket c(float yaw, float pitch, boolean isOnGround, boolean collision) {
        return new LookAndOnGround(yaw, pitch, isOnGround);
    }

    @Override
    public PlayerMoveC2SPacket d(
            double x, double y, double z, float yaw, float pitch, boolean isOnGround, boolean collision) {
        return new Full(x, y, z, yaw, pitch, isOnGround);
    }

    @Override
    public PlayerMoveC2SPacket b(double x, double y, double z, boolean isOnGround, boolean collision) {
        return new PositionAndOnGround(x, y, z, isOnGround);
    }

    @Override
    public VehicleMoveC2SPacket e(Entity entity) {
        return new VehicleMoveC2SPacket(entity);
    }

    @Override
    public PlayerMoveC2SPacket a(boolean isOnGround, boolean collision) {
        return new OnGroundOnly(isOnGround);
    }
}
