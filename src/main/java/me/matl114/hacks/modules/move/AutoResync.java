package me.matl114.hacks.modules.move;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class AutoResync extends BaseModule {
    public int Uf;
    public final DoubleRef autoResyncDistance;
    public final FlagRef autoResyncVelocity;
    int Uo;
    public final IntRef autoResyncRequestExpireTick;
    public Optional<Vec3d> Ue;
    public final FlagRef logAutoResyncRequest;
    public final ModulePath gQ = makePath(Configs.m, "move-safety");
    public final FlagRef autoResyncPos;
    public final FlagRef autoResyncRequestRecursively;
    public Vec2f Up;
    public final FlagRef Ug =
            this.flagBuilder(this.gQ.add("auto-resync-rotation")).build();
    public final FlagRef Uh =
            this.flagBuilder(this.gQ.add("auto-resync-rot-modify-packet")).build();
    public static AutoResync INSTANCE;

    public void onPreSetBack(Event<PlayerPositionLookS2CPacket> event) {
        if (this.Ug.get() && !this.Uh.get()) {
            this.Up = new Vec2f(mc.player.getPitch(), mc.player.getYaw());
            mc.player.setPitch(PlayerStateManager.INSTANCE.jl);
            mc.player.setYaw(PlayerStateManager.INSTANCE.jm);
        }
    }

    public void onPostSetBack(Event<PlayerPositionLookS2CPacket> event) {
        if (this.Up != null) {
            EntityUtils.setEntityPitchSafe(mc.player, this.Up.x);
            PlayerStateManager.nT(mc.player, this.Up.y);
            ClientPlayerAccess.of(mc.player).resyncRot();
            this.Up = null;
        }
    }

    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        switch ((ModulePreset) ((KalamaHelperHelperI) event.b).b()) {
            case fd:
            case fe:
            case fj:
                if (this.autoResyncDistance.get() < 0.0) {
                    this.autoResyncDistance.set(-this.autoResyncDistance.get());
                }
                break;
            default:
                if (this.autoResyncDistance.get() > 0.0) {
                    this.autoResyncDistance.set(-this.autoResyncDistance.get());
                }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(PlayerPositionLookS2CPacket.class), this::onSetBack);
        this.registerListener(Listener.aq().getChannel(PlayerPositionLookS2CPacket.class), this::onPreSetBack);
        this.registerListener(Listener.ar().getChannel(PlayerPositionLookS2CPacket.class), this::onPostSetBack);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
        this.registerListener(Listener.N(), this::y);
    }

    public Vec3d getPosition(PlayerPositionLookS2CPacket packet) {
        boolean var2 = packet.getFlags().contains(PositionFlag.X);
        boolean var3 = packet.getFlags().contains(PositionFlag.Y);
        boolean var4 = packet.getFlags().contains(PositionFlag.Z);
        ClientPlayerEntity var5 = mc.player;
        double var6;
        if (var2) {
            var6 = var5.getX() + packet.getX();
        } else {
            var6 = packet.getX();
        }

        double var8;
        if (var3) {
            var8 = var5.getY() + packet.getY();
        } else {
            var8 = packet.getY();
        }

        double var10;
        if (var4) {
            var10 = var5.getZ() + packet.getZ();
        } else {
            var10 = packet.getZ();
        }

        return new Vec3d(var6, var8, var10);
    }

    public void setAutoResyncSchedule(Optional<Vec3d> pos, int ticksExpire) {
        this.Ue = pos;
        this.Uf = ticksExpire + Tasks.b();
    }

    public void executeResyncTo(Vec3d resyncPos, Vec3d resyncToPos, boolean currentOnGround) {
        mc.player.setPosition(resyncPos);
        mc.player.setOnGround(false);
        if (currentOnGround) {
            resyncToPos = resyncToPos.add(0.0, 1.0E-6, 0.0);
        }

        MovTasks.scheduleTpInternal(MovTasks.createPlayerMovContext(), resyncToPos, 200.0, false, true, true);
        this.Uf = -1;
        this.Ue = null;
    }

    public void y(Event<World> event) {
        this.Uo = Tasks.b();
    }

    public AutoResync() {
        super("AutoResync");
        this.autoResyncVelocity =
                this.flagBuilder(this.gQ.add("auto-resync-velocity")).build();
        this.autoResyncPos = this.flagBuilder(this.gQ.add("auto-resync-pos")).build();
        this.autoResyncDistance = this.builder(this.gQ.add("auto-resync-distance"), DoubleRef.TYPE)
                .defaultValue(10.0)
                .build();
        this.logAutoResyncRequest =
                this.flagBuilder(this.gQ.add("log-auto-resync-request")).build();
        this.autoResyncRequestExpireTick = this.builder(this.gQ.add("auto-resync-request-expire-tick"), IntRef.TYPE)
                .defaultValue(10)
                .validator(Configs.e)
                .build();
        this.autoResyncRequestRecursively =
                this.flagBuilder(this.gQ.add("auto-resync-request-recursively")).build();
        this.Uo = 0;
        this.Up = null;
        INSTANCE = this;
    }

    public void alV(Optional<Vec3d> pos) {
        this.setAutoResyncSchedule(pos, this.autoResyncRequestExpireTick.get());
    }

    public void onSetBack(Event<PlayerPositionLookS2CPacket> event) {
        if (!event.d()) {
            if (mc.player != null) {
                if (this.Uo + 100 <= Tasks.b()) {
                    if (!mc.player.getPos().equals(Vec3d.ZERO)) {
                        if (mc.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
                            boolean var2 = mc.player.isOnGround();
                            if (this.Uf > Tasks.b() && this.Ue != null) {
                                Vec3d var3 = this.Ue.orElseGet(mc.player::getPos);
                                PlayerPositionLookS2CPacket var4 = (PlayerPositionLookS2CPacket) event.b;
                                Vec3d var5 = this.getPosition(var4);
                                double var6 = var5.squaredDistanceTo(mc.player.getPos());
                                double var8 = var5.squaredDistanceTo(var3);
                                if (var6 > 1.0E-4
                                        && var6 < MathUtils.b(128)
                                        && var8 > 1.0E-4
                                        && var8 < MathUtils.b(128)) {
                                    if (this.logAutoResyncRequest.get()) {
                                        Debug.b("Auto Resync triggered!");
                                    }

                                    mc.getNetworkHandler()
                                            .sendPacket(new TeleportConfirmC2SPacket(var4.getTeleportId()));
                                    this.executeResyncTo(var5, var3, var2);
                                    if (this.autoResyncRequestRecursively.get()) {
                                        mc.player.setPosition(var3);
                                        this.alV(Optional.empty());
                                    }

                                    event.cancel();
                                    return;
                                }
                            }

                            if (this.autoResyncPos.get()) {
                                Vec3d var15 = mc.player.getPos();
                                BlockPos var17 = BlockPos.ofFloored(var15);
                                if (mc.world.getChunkManager().isChunkLoaded(var17.getX() >> 4, var17.getZ() >> 4)) {
                                    PlayerPositionLookS2CPacket var19 = (PlayerPositionLookS2CPacket) event.b;
                                    Vec3d var10 = this.getPosition(var19);
                                    double var11 = var15.squaredDistanceTo(var10);
                                    if (this.autoResyncDistance.get() > 0.0
                                            && var11 < MathUtils.a(this.autoResyncDistance.get())) {
                                        mc.getNetworkHandler()
                                                .sendPacket(new TeleportConfirmC2SPacket(var19.getTeleportId()));
                                        this.executeResyncTo(var10, var15, var2);
                                        event.cancel();
                                        return;
                                    }
                                }
                            }

                            boolean var16 = false;
                            PlayerPositionLookS2CPacket var18 = (PlayerPositionLookS2CPacket) event.e();
                            Set var20 = var18.getFlags();
                            HashSet var21 = null;
                            float var13 = var18.getYaw();
                            float var14 = var18.getPitch();
                            if (this.Ug.get() && this.Uh.get()) {
                                var16 = true;
                                if (var21 == null) {
                                    var21 = new HashSet(var20);
                                }

                                var21.add(PositionFlag.X_ROT);
                                var21.add(PositionFlag.Y_ROT);
                                var13 = 0.0F;
                                var14 = 0.0F;
                            }

                            if (this.autoResyncVelocity.get()) {}

                            if (var16 && var21 != null) {
                                event.context(new PlayerPositionLookS2CPacket(
                                        var18.getX(),
                                        var18.getY(),
                                        var18.getZ(),
                                        var13,
                                        var14,
                                        var21,
                                        var18.getTeleportId()));
                            }
                        }
                    }
                }
            }
        }
    }
}
