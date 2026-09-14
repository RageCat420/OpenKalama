package me.matl114.hacks.utils.entity;

import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.GameMode;

public class CameraEntity extends AbstractClientPlayerEntity {
    boolean moveable;
    PlayerEntity H;
    PlayerListEntry K;
    GameMode I;

    public CameraEntity(ClientWorld clientWorld, @Nonnull ClientPlayerEntity player, GameMode mode, boolean moveable) {
        super(clientWorld, player.getGameProfile());
        this.setId(-this.getId());
        this.I = mode;
        this.moveable = moveable;
        this.setUuid(UUID.randomUUID());
        this.copyEquipments(player.getInventory());
        this.H = player;
        this.setPosition(player.getPos());
        this.setPitch(player.getPitch());
        this.setYaw(player.getYaw());
        this.resetPosition();
    }

    public void tickMovement() {
        super.tickMovement();
    }

    public boolean isCreative() {
        return this.I == GameMode.CREATIVE;
    }

    public boolean canMoveVoluntarily() {
        return this.moveable;
    }

    public boolean isSpectator() {
        return this.I == GameMode.SPECTATOR;
    }

    public boolean isMainPlayer() {
        return this.moveable;
    }

    public PlayerInventory getInventory() {
        return this.H != null ? this.H.getInventory() : super.getInventory();
    }

    public void tick() {
        if (this.H != null && this.H.getWorld().isPosLoaded(this.getBlockX(), this.getBlockZ())) {
            this.setHealth(this.H.getHealth());
            if (!this.moveable) {
                this.setPitch(this.H.getPitch());
                this.setYaw(this.H.getYaw());
                this.setHeadYaw(this.H.getHeadYaw());
                this.setBodyYaw(this.H.getBodyYaw());
                this.setPosition(this.H.getPos());
            }

            super.tick();
        }
    }

    protected PlayerListEntry getPlayerListEntry() {
        return this.H != null
                ? MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.H.getUuid())
                : null;
    }

    public float getPitch() {
        return !this.moveable && this.H != null ? this.H.getPitch() : super.getPitch();
    }

    public float getYaw() {
        return !this.moveable && this.H != null ? this.H.getYaw() : super.getYaw();
    }

    public void copyEquipments(PlayerInventory p) {
        this.getInventory().clone(p);
        super.isSpectator();
    }
}
