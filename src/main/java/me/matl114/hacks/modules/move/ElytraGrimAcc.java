package me.matl114.hacks.modules.move;

import java.util.Random;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$SetBackTriggerType;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.versioned.api.VPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class ElytraGrimAcc extends BaseModule implements HackUtilHelperJ {
    public final DoubleRef maxAccelerateVelocity;
    int Kg;
    public final ModulePath Ka;
    public final EnumRef<Configs$SetBackTriggerType> setBackMode;
    static HackUtilHelperD cy;
    public final ModulePath JY = makePath(Configs.m, "elytra");
    int Kf;
    public final DoubleRef minAccelerateVelocity;
    public final ModulePath JZ = this.JY.add("elytra-flight-legit");
    boolean Ki;
    int Ke;
    boolean Kh;
    public final FlagRef enable;
    public final FlagRef fixKickFromLag;
    Random eU;
    public final KeyBindRef hotkey;
    public Packet<?> Ad;

    @Override
    public void onDisableModule() {
        super.onDisableModule();
    }

    private void Zp() {
        if (!this.fixKickFromLag.get() || !AntiChunkLag.INSTANCE.currentMayFaceLagChunk) {
            if (!this.fixKickFromLag.get() || Tasks.b() >= this.Kf + 20) {
                switch ((Configs$SetBackTriggerType) this.setBackMode.get()) {
                    case SIMULATION:
                        this.Ad = VPacket.j(
                                mc.player.getX(),
                                mc.player.getY() + 2.5 * (Tasks.b() % 3 + 1),
                                mc.player.getZ(),
                                mc.player.getYaw(),
                                mc.player.getPitch(),
                                mc.player.isOnGround(),
                                mc.player.horizontalCollision);
                        break;
                    case CRASH_PACKETS:
                        this.Ad = VPacket.j(
                                3.9999999E7,
                                mc.player.getY() + 2.5 * (Tasks.b() % 3 + 1),
                                3.9999999E7,
                                mc.player.getYaw(),
                                mc.player.getPitch(),
                                true,
                                mc.player.horizontalCollision);
                }

                PlayerMoveC2SPacketAccess.setCause(
                        (PlayerMoveC2SPacket) (Object) this.Ad, PlayerMoveC2SPacketAccess.Cause.TRIGGER_SIMULATION);
            }
        }
    }

    public void Zo() {
        this.Kh = true;
    }

    @Override
    public void iC(Event<LegalMovementManager> sendMovementPacketEvent) {
        if (this.Kh) {
            Vec3d var2 = mc.player.getVelocity();
            double var3 = var2.length();
            if (this.Ki) {
                if (var3 > this.maxAccelerateVelocity.get()) {
                    this.Ki = false;
                }
            } else if (var3 < this.minAccelerateVelocity.get()) {
                this.Ki = true;
            }

            if (this.Ki) {
                ((LegalMovementManager) sendMovementPacketEvent.e()).c.restorePos();
                sendMovementPacketEvent.cancel();
                if (this.Ad != null) {
                    this.Ad = null;
                    return;
                }

                this.Zp();
            }
        } else {
            this.Ki = false;
        }
    }

    @Override
    public void onEnableModule() {
        super.onEnableModule();
    }

    @Override
    public void bb(Event<LegalMovementManager> preTickEvent) {
        this.Kh = (this.enable.get() || this.Kh)
                && mc.player.isFallFlying()
                && !mc.player.isOnGround()
                && !MovTasks.ax().agj();
        if (this.Kh) {
            this.Kg = Tasks.b();
        }
    }

    public ElytraGrimAcc() {
        super("ElytraGrimAcc");
        this.Ka = this.JZ.add("grim-accelerate");
        this.enable = this.flagBuilder(this.Ka.add("enable")).build();
        this.hotkey = this.toggleHotkey(this.Ka.add("hotkey"), new MultiKeyBind(), this.Ka.add("enable"))
                .build();
        this.setBackMode = this.builder(this.Ka.add("set-back-mode"), Configs$SetBackTriggerType.class)
                .defaultValue(Configs$SetBackTriggerType.SIMULATION)
                .build();
        this.maxAccelerateVelocity = this.builder(this.Ka.add("max-accelerate-velocity"), Double.class)
                .defaultValue(6.0)
                .validator(Configs.doubleRange(0.0, 100.0))
                .build();
        this.minAccelerateVelocity = this.builder(this.Ka.add("min-accelerate-velocity"), Double.class)
                .defaultValue(3.6)
                .validator(Configs.doubleRange(0.0, 100.0))
                .build();
        this.fixKickFromLag = this.builder(this.Ka.add("fix-kick-from-lag"), Boolean.class)
                .defaultValue(true)
                .build();
        this.Ad = null;
        this.Ke = 0;
        this.Kf = 0;
        this.Kg = 0;
        this.Kh = false;
        this.Ki = false;
        this.eU = new Random();
        if (cy == null) {
            cy = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> cy);
        }

        cy.mN(this::cast);
        this.bindFlag(this.enable);
    }

    public void UT(Event<PlayerPositionLookS2CPacket> event) {}

    @Override
    public boolean postModify(Event<LegalMovementManager> postTickEvent, boolean enabledThisTick) {
        if (this.Ad != null) {
            mc.getNetworkHandler().sendPacket(this.Ad);
            this.Kf = Tasks.b();
            this.Ad = null;
        }

        this.Kh = false;
        return true;
    }

    public void Zm(Event<EntityVelocityUpdateS2CPacket> event) {
        Vec3d var2 = VPacket.getVelocity((EntityVelocityUpdateS2CPacket) event.b);
        if (mc.player != null
                && ((EntityVelocityUpdateS2CPacket) event.b).getEntityId() == mc.player.getId()
                && mc.player.isFallFlying()
                && (this.enable.get() || this.Kg + 10 > Tasks.b())) {
            if (var2.horizontalLengthSquared() < 0.01) {
                event.cancel();
            } else {
                Vec3d var3 = mc.player.getVelocity();
                if (var3.horizontalLengthSquared() > 0.01
                        && var3.withAxis(Axis.Y, 0.0).dotProduct(var2.withAxis(Axis.Y, 0.0)) < 0.0) {
                    event.cancel();
                }
            }
        }
    }

    public void Zn(Event<TeleportConfirmC2SPacket> packet) {
        this.Ke++;
        this.Kf = 0;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ao().getChannel(TeleportConfirmC2SPacket.class), this::Zn);
        this.registerListener(Listener.ap().getChannel(EntityVelocityUpdateS2CPacket.class), this::Zm);
        this.registerListener(Listener.ar().getChannel(PlayerPositionLookS2CPacket.class), this::UT);
    }
}
