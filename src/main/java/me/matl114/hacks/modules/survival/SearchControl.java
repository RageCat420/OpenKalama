package me.matl114.hacks.modules.survival;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.Pos3;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class SearchControl extends BaseModule {
    public final KeyBindRef J;
    public final DoubleRef rangeCircle;
    public final DoubleRef rangeSpiral;
    public final FlagRef outMaxLog;
    public final FlagRef flyOnly;
    public final FlagRef ae;
    public final FlagRef abortWasd;
    public final ModulePath iN = makePath(Configs.o, "travelling-control");
    public final DoubleRef rangeRect;
    public final DoubleRef maxDistance;
    Runnable CD;
    public final EnumRef<SearchControl$Mode> searchMode;
    public final NBTRef<Pos3> centerPos;
    public final ModulePath Cu = this.iN.add("search-control");

    public void tickLookRect() {
        Vec3d var1 = mc.player.getPos();
        Vec3d var2 = this.centerPos.get().to().toCenterPos();
        Vec3d var3 = var1.subtract(var2);
        double var4 = var3.z - var3.x;
        double var6 = var3.x + var3.z;
        float var8;
        if (var6 <= 0.0) {
            if (var4 <= 0.0) {
                var8 = -90.0F;
            } else {
                var8 = 180.0F;
            }
        } else if (var4 <= this.rangeRect.get()) {
            var8 = 0.0F;
        } else {
            var8 = 90.0F;
        }

        mc.player.setYaw(var8);
    }

    @Override
    public void onEnableModule() {
        super.onEnableModule();
        this.Qx();
    }

    public void onPreTick(Event<ClientPlayerEntity> event) {
        if (this.ae.get()) {
            if (this.CD == null) {
                this.Qx();
            }

            if (this.CD != null) {
                if (this.abortWasd.get() && PlayerInputUtils.of(mc.options).rv()) {
                    return;
                }

                if (this.flyOnly.get() && !mc.player.isFallFlying()) {
                    return;
                }

                if (mc.player
                                .getPos()
                                .squaredDistanceTo(this.centerPos.get().to().toCenterPos())
                        < MathUtils.a(this.maxDistance.get())) {
                    this.CD.run();
                } else if (this.outMaxLog.get()) {
                    MainTasks.q();
                }
            }
        }
    }

    public SearchControl() {
        super("SearchControl");
        portConfigs(makePath(Configs.m, "travelling-control.search-control"), this.iN);
        this.ae = this.flagBuilder(this.Cu.addEnable()).build();
        this.J = this.toggleHotkey(this.Cu.addHotkey(), new MultiKeyBind(), this.Cu.addEnable())
                .build();
        this.searchMode = this.builder(this.Cu.add("search-mode"), SearchControl$Mode.class)
                .defaultValue(SearchControl$Mode.SPIRAL)
                .updateListener(s -> this.Qx())
                .build();
        this.centerPos = this.builder(this.Cu.add("center-pos"), Pos3.class)
                .defaultValue(new Pos3(0, 0, 0))
                .build();
        this.rangeSpiral = this.doubleBuilder(this.Cu.add("range-spiral"))
                .defaultValue(32.0)
                .show(() -> this.searchMode.get().isIn(new ConfigEnum[] {SearchControl$Mode.SPIRAL}))
                .build();
        this.rangeRect = this.doubleBuilder(this.Cu.add("range-rect"))
                .defaultValue(192.0)
                .show(() -> this.searchMode.get().isIn(new ConfigEnum[] {SearchControl$Mode.RECT}))
                .build();
        this.rangeCircle = this.doubleBuilder(this.Cu.add("range-circle"))
                .defaultValue(192.0)
                .show(() -> this.searchMode.get().isIn(new ConfigEnum[] {SearchControl$Mode.CIRCLE}))
                .build();
        this.abortWasd = this.flagBuilder(this.Cu.add("abort-wasd")).build();
        this.flyOnly = this.flagBuilder(this.Cu.add("fly-only")).build();
        this.maxDistance = this.doubleBuilder(this.Cu.add("max-distance"))
                .defaultValue(3.0E8)
                .build();
        this.outMaxLog = this.flagBuilder(this.Cu.add("out-max-log")).build();
        this.bindFlag(this.ae);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::onPreTick);
    }

    public Runnable QA() {
        return this::tickLookCircle;
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.CD = null;
    }

    public void tickLookSpiral() {
        Vec3d var1 = mc.player.getPos();
        Vec3d var2 = this.centerPos.get().to().toCenterPos();
        Vec3d var3 = var1.subtract(var2);
        double var4 = this.rangeSpiral.get();
        double var6 = var4 / (Math.PI * 2);
        double var8 = var3.horizontalLength();
        float var10 = EntityUtils.s(var3.normalize());
        double var11 = Math.atan2(var8, var6);
        float var13 = (float) Math.toDegrees(var11);
        float var14 = (float) (mc.player.getVelocity().horizontalLength() / (2.0 * var3.horizontalLength()));
        float var15 = var10 + var13 - var14;
        PlayerStateManager.nT(mc.player, var15);
    }

    public void Qx() {
        this.CD = null;
        if (!checkNull()) {
            this.CD = switch ((SearchControl$Mode) this.searchMode.get()) {
                case RECT -> this.Qy();
                case CIRCLE -> this.QA();
                case SPIRAL -> this.QC();};
        }
    }

    public void tickLookCircle() {
        Vec3d var1 = mc.player.getPos();
        Vec3d var2 = this.centerPos.get().to().toCenterPos();
        Vec3d var3 = var1.subtract(var2);
        double var4 = this.rangeCircle.get();
        Vec3d var6 = new Vec3d(0.0, 0.0, var4 / 2.0);
        Vec3d var7 = new Vec3d(0.0, 0.0, -var4 / 2.0);
        Vec3d var8;
        if (var3.x > 0.0) {
            var8 = var6;
        } else {
            var8 = var7;
        }

        Vec3d var9 = var3.subtract(var8);
        float var10 = EntityUtils.s(var9.normalize());
        float var11 = (float) (mc.player.getVelocity().horizontalLength() / (2.0 * var9.horizontalLength()));
        float var12 = var10 + 90.0F - var11;
        PlayerStateManager.nT(mc.player, var12);
    }

    public Runnable Qy() {
        return this::tickLookRect;
    }

    public Runnable QC() {
        return this::tickLookSpiral;
    }
}
