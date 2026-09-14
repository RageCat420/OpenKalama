package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.utils.EntityUtils;
import me.matl114.versioned.api.VPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LegacySnapRotManager extends BaseModule {
    Vec2f lastSnapPitchYaw;
    public static LegacySnapRotManager INSTANCE;
    public boolean betweenViaPacket;

    public void snapAt(float pitch, float yaw, boolean force) {
        if (force || PlayerStateManager.INSTANCE.nQ(pitch, yaw)) {
            mc.getNetworkHandler().sendPacket(this.createSnapAt(pitch, yaw));
        }

        this.lastSnapPitchYaw = new Vec2f(pitch, yaw);
    }

    public PlayerMoveC2SPacket createSnapAt(float pitch, float yaw) {
        float var3 = PlayerStateManager.INSTANCE.jm;
        return PlayerMoveC2SPacketAccess.setCause(
                VPacket.j(
                        PlayerStateManager.INSTANCE.ji,
                        PlayerStateManager.INSTANCE.jk,
                        PlayerStateManager.INSTANCE.jj,
                        EntityUtils.i(var3, yaw),
                        EntityUtils.k(pitch),
                        PlayerStateManager.INSTANCE.jn,
                        mc.player.horizontalCollision),
                PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP);
    }

    public LegacySnapRotManager() {
        super("LegacySnapRotManager");
        INSTANCE = this;
    }

    public void sendAsSnap(PlayerMoveC2SPacket full) {
        PlayerMoveC2SPacket var2 = this.createAsSnap(full);
        mc.getNetworkHandler().sendPacket(var2);
    }

    public PlayerMoveC2SPacket createAsSnap(PlayerMoveC2SPacket full) {
        PlayerMoveC2SPacket var2 = VPacket.j(
                full.getX(PlayerStateManager.INSTANCE.ji),
                full.getY(PlayerStateManager.INSTANCE.jk),
                full.getZ(PlayerStateManager.INSTANCE.jj),
                PlayerStateManager.INSTANCE.jm,
                PlayerStateManager.INSTANCE.jl,
                full.isOnGround(),
                VPacket.getCollisionFlag(full));
        PlayerMoveC2SPacketAccess.of(var2).setCause(PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP);
        return var2;
    }

    public PlayerMoveC2SPacket ahv(Vec3d look, boolean onGroundOverride) {
        Vec2f var3 = EntityUtils.q(look.normalize());
        return this.ahx(var3.x, var3.y, onGroundOverride);
    }

    public PlayerMoveC2SPacket ahx(float pitch, float yaw, boolean onGroundOverride) {
        float var4 = PlayerStateManager.INSTANCE.jm;
        return PlayerMoveC2SPacketAccess.setCause(
                VPacket.j(
                        PlayerStateManager.INSTANCE.ji,
                        PlayerStateManager.INSTANCE.jk,
                        PlayerStateManager.INSTANCE.jj,
                        EntityUtils.i(var4, yaw),
                        EntityUtils.k(pitch),
                        onGroundOverride,
                        mc.player.horizontalCollision),
                PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP);
    }

    public void ahs(Vec3d look, boolean force) {
        Vec2f var3 = EntityUtils.q(look.normalize());
        this.snapAt(var3.x, var3.y, force);
    }

    public PlayerMoveC2SPacket ahu(Vec3d look) {
        Vec2f var2 = EntityUtils.q(look.normalize());
        return this.createSnapAt(var2.x, var2.y);
    }

    public void ahq(Event<PlayerInteractItemC2SPacket> eventInteract) {}

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aO().c(EntityType.PLAYER), this::onPrePlayerTick);
        this.registerListener(Listener.ap().getChannel(PlayerInteractItemC2SPacket.class), this::ahq);
        this.registerListener(
                Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::onSendPlayerPosRotPacket, Integer.MIN_VALUE);
    }

    public void resyncSnap() {
        if (this.lastSnapPitchYaw != null && PlayerStateManager.INSTANCE.isRotationDifferent()) {
            ClientPlayerAccess var1 = ClientPlayerAccess.of(mc.player);
            var1.resyncRot();
        }

        this.lastSnapPitchYaw = null;
    }

    public void onSendPlayerPosRotPacket(Event<PlayerMoveC2SPacket> event) {
        if (this.betweenViaPacket
                && ViaFabricPlusHooks.isSupportDupRot()
                && event.b instanceof Full var3
                && var3 instanceof PlayerMoveC2SPacketAccess var4) {
            var4.setCause(PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP);
        }

        if (((PlayerMoveC2SPacket) event.b).changesLook()) {
            this.lastSnapPitchYaw = null;
        }
    }

    public void onPrePlayerTick(Event<PlayerEntity> eventPre) {
        if (mc.player != null && eventPre.b == mc.player) {
            this.resyncSnap();
        }
    }
}
