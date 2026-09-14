package me.matl114.hacks.modules.move;

import java.util.ArrayDeque;
import java.util.Deque;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Velocity extends BaseModule implements HackUtilHelperJ {
    public final FlagRef pauseWhenWasd;
    public final KeyBindRef J;
    public int fL;
    public final FlagRef notInWater;
    public final FlagRef noClimbing;
    boolean fV;
    int fW;
    boolean fY;
    public final FlagRef onGroundOnly;
    public final FlagRef noLiquidFlowPush;
    public final ModulePath fu = makePath(Configs.m, "velocity-management");
    long fR;
    public final EnumRef<Velocity$Mode> mode;
    public final DoubleRef freezeTime;
    public final FlagRef noBlockPush;
    int fN;
    Deque<Packet> fX;
    long fQ;
    boolean fO;
    public final FlagRef ae;
    public final FlagRef executeInWall;
    public final DoubleRef verticalThreshold;
    public final ModulePath fv = this.fu.add("antikb");
    public final FlagRef bypassExplosions;
    Vec3d fT;
    public final FlagRef grimFreezeIfWalk;
    public static HackUtilHelperD instance;
    long fP;
    public final FlagRef noEntityPush;
    Vec3d fU;
    public final DoubleRef horizontalThreshold;
    public final FlagRef executeDuringFireworks;
    int fS;
    public final IntRef grimResetKbTick;
    public int fM;

    public void jA(Event<Vec3d> eventVc) {
        this.fW = Tasks.b();
        eventVc.cancel();
        this.ju();
    }

    public void x(Event<ClientPlayerEntity> eventPreTick) {
        if (this.ae.get()) {
            if (this.fV) {
                mc.interactionManager.sendSequencedPacket(
                        mc.world,
                        seq -> new PlayerActionC2SPacket(
                                Action.STOP_DESTROY_BLOCK,
                                mc.player.isCrawling()
                                        ? mc.player.getBlockPos()
                                        : mc.player.getBlockPos().up(),
                                Direction.DOWN,
                                seq));
                if (this.fS + this.grimResetKbTick.get() <= Tasks.b()) {
                    this.fV = false;
                } else if (!this.grimFreezeIfWalk.get()
                        && PlayerInputUtils.of(mc.options).rv()) {
                    ClientPlayerAccess.of(mc.player).resyncPos();
                    mc.player.setOnGround(true);
                } else {
                    FloatingUtils.INSTANCE.SB(true);
                    mc.player.setOnGround(true);
                }
            }

            if (this.mode.get() == Velocity$Mode.FREEZE
                    && this.fW + this.freezeTime.get() > Tasks.b()
                    && (!this.pauseWhenWasd.get()
                            || !PlayerInputUtils.of(mc.options).rv())) {
                FloatingUtils.INSTANCE.SB(true);
            }
        }
    }

    @Override
    public void gz(Event<LegalMovementManager> movementManagerEvent) {}

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {}

    public void jB(Event<Vec3d> eventVc) {
        if (this.fW + this.freezeTime.get() > Tasks.b()) {
            eventVc.cancel();
        }
    }

    public void jG(Event<PlayerMoveC2SPacket> event) {}

    public void jz(Event<Vec3d> eventVc) {
        eventVc.cancel();
        this.ju();
        this.fV = true;
    }

    public void onPlayerVelocity(Event<Vec3d> event) {
        if (this.ae.get() && mc.player != null) {
            if (this.executeDuringFireworks.get()
                    && mc.player.isFallFlying()
                    && MovTasks.ax().agj()) {
                this.ju();
                event.cancel();
                return;
            }

            if (this.executeInWall.get() && mc.player.isInsideWall()) {
                this.ju();
                event.cancel();
                return;
            }

            boolean var2 = this.fM > 0;
            if (var2) {
                this.fM = Math.max(this.fM - 1, 0);
                if (event.d()) {
                    return;
                }

                if (this.mode.get() == Velocity$Mode.NONE) {
                    this.ju();
                    event.cancel();
                    return;
                }

                if (this.fT.horizontalLength() >= this.horizontalThreshold.get()
                        || Math.abs(this.fT.y) >= this.verticalThreshold.get()) {
                    if ((mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava())
                            && this.notInWater.get()) {
                        return;
                    }

                    if (!mc.player.isFallFlying()
                            && (!this.onGroundOnly.get() || mc.player.isOnGround())
                            && this.mode.get() == Velocity$Mode.GRIM_LEGACY_GROUND) {
                        this.jz(event);
                        return;
                    }

                    if (!mc.player.isFallFlying()
                            && (!this.onGroundOnly.get() || mc.player.isOnGround())
                            && this.mode.get() == Velocity$Mode.GRIM_NEW_GROUND) {
                        this.jC(event);
                        return;
                    }

                    if (this.mode.get() == Velocity$Mode.FREEZE) {
                        this.jA(event);
                        return;
                    }

                    if (!mc.player.isOnGround() && this.onGroundOnly.get()) {}
                }
            }

            if (this.mode.get() == Velocity$Mode.FREEZE) {
                this.jB(event);
            }
        }
    }

    public void jC(Event<Vec3d> eventVc) {}

    public Velocity() {
        super("Velocity");
        this.mode = this.builder(this.fv.add("mode"), Velocity$Mode.class)
                .defaultValue(Velocity$Mode.NONE)
                .build();
        this.ae = this.flagBuilder(this.fv.addEnable()).build();
        this.J = this.moduleEntry(
                        this.fv.addHotkey(), new MultiKeyBind(), this.fv.addEnable(), moduleMeta(() -> this.mode))
                .build();
        this.horizontalThreshold = this.builder(this.fv.add("horizontal-threshold"), DoubleRef.TYPE)
                .defaultValue(0.0)
                .build();
        this.verticalThreshold = this.builder(this.fv.add("vertical-threshold"), DoubleRef.TYPE)
                .defaultValue(0.0)
                .build();
        this.bypassExplosions =
                this.flagBuilder(this.fv.add("bypass-explosions")).build();
        this.grimFreezeIfWalk = this.flagBuilder(this.fv.add("grim-freeze-if-walk"))
                .show(() -> this.mode.get().isIn(new ConfigEnum[] {Velocity$Mode.GRIM_LEGACY_GROUND}))
                .build();
        this.grimResetKbTick = this.intBuilder(this.fv.add("grim-reset-kb-tick"))
                .defaultValue(3)
                .show(() -> this.mode.get().isIn(new ConfigEnum[] {Velocity$Mode.GRIM_LEGACY_GROUND}))
                .build();
        this.pauseWhenWasd = this.flagBuilder(this.fv.add("pause-when-wasd"))
                .show(() -> this.mode.get().isIn(new ConfigEnum[] {Velocity$Mode.FREEZE}))
                .build();
        this.freezeTime = this.doubleBuilder(this.fv.add("freeze-time"))
                .show(() -> this.mode.get().isIn(new ConfigEnum[] {Velocity$Mode.FREEZE}))
                .defaultValue(5.0)
                .build();
        this.onGroundOnly = this.flagBuilder(this.fv.add("on-ground-only"))
                .show(() -> this.mode.get().isNotIn(new ConfigEnum[] {Velocity$Mode.NONE}))
                .build();
        this.notInWater = this.flagBuilder(this.fv.add("not-in-water"))
                .show(() -> this.mode.get().isNotIn(new ConfigEnum[] {Velocity$Mode.NONE}))
                .build();
        this.executeDuringFireworks = this.builder(this.fv.add("execute-during-fireworks"), Boolean.class)
                .defaultValue(true)
                .build();
        this.executeInWall = this.builder(this.fv.add("execute-in-wall"), Boolean.class)
                .defaultValue(true)
                .build();
        this.noBlockPush = this.flagBuilder(this.fv.add("no-block-push")).build();
        this.noEntityPush = this.flagBuilder(this.fv.add("no-entity-push")).build();
        this.noLiquidFlowPush =
                this.flagBuilder(this.fv.add("no-liquid-flow-push")).build();
        this.noClimbing = this.flagBuilder(this.fv.add("no-climbing")).build();
        this.fL = 0;
        this.fM = 0;
        this.fN = 0;
        this.fO = false;
        this.fP = 0L;
        this.fQ = 0L;
        this.fR = 0L;
        this.fS = 0;
        this.fT = Vec3d.ZERO;
        this.fU = Vec3d.ZERO;
        this.fV = false;
        this.fW = 0;
        this.fX = new ArrayDeque<>();
        this.fY = false;
        this.bindFlag(this.ae);
        if (instance == null) {
            instance = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> instance);
        }

        instance.mN(this::cast);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aN().c(EntityType.PLAYER), this::onVelocity);
        this.registerListener(Listener.aJ(), this::onExplosion);
        this.registerListener(Listener.aA(), this::jH);
        this.registerListener(Listener.ap().getChannel(EntityDamageS2CPacket.class), this::onEntityDamage);
        this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::jG);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
        this.registerListener(Listener.ap().getChannel(PlayerPositionLookS2CPacket.class), this::onSetPosition);
        this.registerListener(Listener.ap().getChannel(CommonPingS2CPacket.class), this::jE);
        this.registerListener(Listener.U(), this::x);
        this.registerListener(Listener.ar().getChannel(BlockUpdateS2CPacket.class), this::jD);
        this.registerListener(Listener.aq().getChannel(ExplosionS2CPacket.class), this::onExplosionPre);
        this.registerListener(Listener.az(), this::js);
    }

    public void ju() {
        this.fR = System.nanoTime();
        this.fS = Tasks.b();
        this.fU = this.fT;
    }

    @Override
    public void jJ(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
        if (this.fY) {
            this.sendFallFlying();
        }

        if (this.fO) {
            this.fY = true;
            this.sendFallFlying();
        }
    }

    public void onExplosionPre(Event<ExplosionS2CPacket> eventExplosion) {
        if (this.ae.get() && this.bypassExplosions.get() && mc.player != null) {
            ExplosionS2CPacket var2 = (ExplosionS2CPacket) eventExplosion.b;
            Vec3d var3 = new Vec3d(var2.getPlayerVelocityX(), var2.getPlayerVelocityY(), var2.getPlayerVelocityZ());
            if (var3.lengthSquared() > 1.0E-6) {
                this.fM++;
            }
        }
    }

    @Override
    public void jI(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {}

    public void jD(Event<BlockUpdateS2CPacket> eventBlockUpdate) {}

    public void onSetPosition(Event<PlayerPositionLookS2CPacket> event) {
        this.fP = System.nanoTime();
        if (this.fO) {
            for (Packet var3 : this.fX) {
                try {
                    var3.apply(mc.getNetworkHandler());
                } catch (OffThreadException var5) {
                }
            }

            this.fX.clear();
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.fN + 5 < Tasks.b()) {
            this.fO = false;
            this.fY = false;
        }

        return true;
    }

    private void sendFallFlying() {
        ClientCommandC2SPacket var1 = new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING);
        mc.getNetworkHandler().sendPacket(var1);
    }

    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        switch ((ModulePreset) ((KalamaHelperHelperI) event.b).b()) {
            case fh:
            case fg:
                this.mode.set(Velocity$Mode.GRIM_LEGACY_GROUND);
                break;
            case fi:
                this.mode.set(Velocity$Mode.GRIM_NEW_GROUND);
                break;
            default:
                this.mode.set(Velocity$Mode.NONE);
        }
    }

    public void jH(Event<MovTasks$MovInfo> event) {}

    public void onEntityDamage(Event<EntityDamageS2CPacket> damage) {
        if (this.ae.get() && mc.player != null && ((EntityDamageS2CPacket) damage.b).entityId() == mc.player.getId()) {
            RegistryEntry var2 = ((EntityDamageS2CPacket) damage.b).sourceType();
            if (!var2.isIn(DamageTypeTags.NO_KNOCKBACK)) {
                this.fM++;
                this.fL = Tasks.b();
            }
        }
    }

    public void onVelocity(Event<Vec3d> event) {
        if (!checkNull()) {
            if (event.getArgs(0) == mc.player) {
                this.fQ = System.nanoTime();
                this.fT = (Vec3d) event.b;
                this.onPlayerVelocity(event);
            }
        }
    }

    public void js(Event<Vec3d> velocity) {
        if (this.noLiquidFlowPush.get()) {
            velocity.cancel();
        }
    }

    public void jE(Event<CommonPingS2CPacket> pingEvent) {
        if (this.fO) {
            this.fX.add((Packet) pingEvent.b);
            pingEvent.cancel();
        }
    }

    public void onExplosion(Event<Vec3d> event) {
        if (!checkNull()) {
            this.fQ = System.nanoTime();
            this.fT = mc.player.getVelocity().add((Vec3d) event.e());
            this.onPlayerVelocity(event);
        }
    }

    @Override
    public void iC(Event<LegalMovementManager> movementManagerEvent) {}
}
