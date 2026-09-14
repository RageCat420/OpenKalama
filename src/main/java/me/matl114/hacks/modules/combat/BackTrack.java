package me.matl114.hacks.modules.combat;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.KalamaHelperHelperG;
import me.matl114.events.KalamaHelperHelperH;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.RenderListener;
import me.matl114.events.packets.PacketStorage;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BackTrack extends BaseModule {
    public final DoubleRef maxDistance;
    public final FlagRef playerOnly;
    public volatile boolean shouldDelay;
    public final KeyBindRef J;
    public Vec3d Js;
    public final FlagRef ae;
    public Entity Jr;
    Set<PacketType<?>> Jt;
    public final DoubleRef maxDelay;
    public final ModulePath pd = makePath(Configs.k, "lag-utils");
    public ModulePath Jo = this.pd.add("back-track");
    public final FlagRef renderOld;

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.XH();
    }

    public void XH() {
        this.XK();
        if (this.shouldDelay) {
            this.XI();
        }

        this.XL();
    }

    public void Dd(Event<Void> event) {
        this.XH();
    }

    public void XK() {
        this.Jr = null;
        this.Js = null;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(PacketManager.z().c(NetworkSide.CLIENTBOUND), this::onQueuePlayerPosition);
        this.registerListener(PacketManager.A(), this::Dd);
        this.registerListener(Listener.S(), this::aO);
        this.registerListener(Listener.V(), this::XQ);
        this.registerListener(RenderListener.q(), this::onRender);
    }

    public void refreshTarget() {
        if (this.ae.get()) {
            Entity var1 = TargetSelector.INSTANCE.akK(
                    this.maxDistance.get(),
                    true,
                    this.playerOnly.get() ? ev -> ev instanceof PlayerEntity : ev -> ev instanceof LivingEntity);
            if (var1 != this.Jr) {
                if (var1 != null) {
                    this.setTarget(var1);
                } else {
                    this.XK();
                }

                this.XL();
            } else if (this.Jr == null) {
                this.XL();
            } else if (this.Js == null) {
                this.Js = this.Jr.getPos();
            } else if (!TargetSelector.INSTANCE.akm(
                    mc.player.getPos(),
                    this.Jr.getBoundingBox(),
                    CombatExtra.INSTANCE.getAttackAtTargetRange(this.Jr))) {
                this.XL();
            }
        } else {
            this.XK();
            this.XL();
        }
    }

    public void onQueuePlayerPosition(Event<PacketStorage> event) {
        if (this.ae.get() && this.Jr != null) {
            PacketStorage var2 = (PacketStorage) event.b;
            if (var2 instanceof KalamaHelperHelperH var3) {
                Packet var4 = var3.aeU();
                if (PacketManager.s(var4)) {
                    return;
                }

                if (var4 instanceof EntityPositionS2CPacket var5 && var5.getEntityId() == this.Jr.getId()) {
                    this.XH();
                    return;
                }

                if (var4 instanceof EntityS2CPacket var9
                        && var9.getEntity(mc.world) == this.Jr
                        && var9.isPositionChanged()) {
                    TrackedPosition var6;
                    if (this.Js == null) {
                        var6 = this.Jr.getTrackedPosition();
                    } else {
                        var6 = new TrackedPosition();
                        var6.setPos(this.Js);
                    }

                    Vec3d var7 = var6.withDelta(var9.getDeltaX(), var9.getDeltaY(), var9.getDeltaZ());
                    boolean var8 = this.shouldDelay;
                    this.handleTrackEntityPosition(var7);
                    this.Js = var7;
                    if (var8 && !this.shouldDelay) {
                        this.XI();
                    }

                    if (this.shouldDelay) {
                        event.cancel();
                    }

                    return;
                }

                if (var4 instanceof EntityStatusS2CPacket var10
                        && var10.getStatus() == 35
                        && var10.getEntity(mc.world) == mc.player) {
                    this.XL();
                    this.XI();
                    return;
                }

                if (this.shouldDelay) {
                    event.cancel();
                }
            }
        }
    }

    public void XI() {
        PacketManager.k();
    }

    public void setTarget(Entity entity) {
        this.Jr = entity;
        this.Js = entity.getPos();
    }

    @Override
    public void onEnableModule() {
        super.onEnableModule();
        this.XH();
    }

    public void onRender(Event<MatrixStack> eventMatrixStack) {
        if (this.renderOld.get() && this.shouldDelay && this.Js != null && this.Jr != null) {
            Box var2 = this.Jr.dimensions.getBoxAt(this.Js);
            RenderUtils.startDrawVirtual((MatrixStack) eventMatrixStack.b);

            try {
                RenderUtils.drawOutlinedBox(
                        (MatrixStack) eventMatrixStack.b, var2.getMinPos(), var2.getMaxPos(), Color.ORANGE);
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) eventMatrixStack.b);
            }
        }
    }

    public void XQ(Event<ClientPlayerEntity> event) {
        if (this.shouldDelay) {
            this.XN();
        }
    }

    public void aO(Event<Void> eventTick) {
        if (checkNull()) {
            this.XH();
        } else {
            boolean var2 = this.shouldDelay;
            this.refreshTarget();
            if (var2 && !this.shouldDelay) {
                this.XI();
            }

            if (this.shouldDelay) {
                this.XN();
            }
        }
    }

    public void XL() {
        this.shouldDelay = false;
    }

    public void XM() {
        this.shouldDelay = true;
    }

    public BackTrack() {
        super("BackTrack");
        this.ae = this.flagBuilder(this.Jo.addEnable()).build();
        this.J = this.toggleHotkey(this.Jo.addHotkey(), new MultiKeyBind(), this.Jo.addEnable())
                .build();
        this.maxDelay =
                this.doubleBuilder(this.Jo.add("max-delay")).defaultValue(50.0).build();
        this.renderOld = this.flagBuilder(this.Jo.add("render-old")).build();
        this.maxDistance = this.doubleBuilder(this.Jo.add("max-distance"))
                .defaultValue(5.0)
                .build();
        this.playerOnly = this.flagBuilder(this.Jo.add("player-only")).build();
        this.Jt = new HashSet<>();
        this.Jt.add(PlayPackets.MOVE_ENTITY_POS);
        this.Jt.add(PlayPackets.MOVE_ENTITY_POS_ROT);
        this.bindFlag(this.ae);
    }

    public void XN() {
        long var1 = System.currentTimeMillis();
        PacketManager.l(ev -> {
            if (this.shouldDelay) {
                return ev.timestampMS() + this.maxDelay.get() < var1 ? KalamaHelperHelperG.NU : KalamaHelperHelperG.NV;
            } else {
                return KalamaHelperHelperG.NU;
            }
        });
    }

    public void handleTrackEntityPosition(Vec3d position) {
        if (this.Js != null) {
            double var2 = CombatExtra.INSTANCE.getAttackAtTargetRange(this.Jr) - 0.02;
            Box var4 = this.Jr.dimensions.getBoxAt(this.Js);
            Box var5 = this.Jr.dimensions.getBoxAt(position);
            boolean var6 = TargetSelector.INSTANCE.akm(mc.player.getPos(), var4, var2);
            boolean var7 = TargetSelector.INSTANCE.akm(mc.player.getPos(), var5, var2);
            if (var6 && !var7) {
                this.XM();
            } else if (var7) {
                this.XL();
            } else {
                Vec3d var8 = TargetSelector.INSTANCE.akn(mc.player.getPos(), var4);
                double var9 = var4.squaredMagnitude(var8);
                double var11 = var5.squaredMagnitude(var8);
                if (var11 > var9) {
                    this.XM();
                }
            }
        }
    }
}
