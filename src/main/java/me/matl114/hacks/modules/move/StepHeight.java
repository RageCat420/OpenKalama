package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.LivingEntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.EntityUtils;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class StepHeight extends BaseModule implements HackUtilHelperJ {
    public final FlagRef ae;
    boolean toggleRunning;
    double y;
    public final ModulePath gQ = makePath(Configs.m, "move-safety");
    private Runnable rx;
    int rz;
    public final ModulePath rw = this.gQ.add("enhance-stepheight");
    public static HackUtilHelperD instance;
    int rA;

    public void triggerJump() {
        if (this.isActive()) {
            double var1 = LivingEntityAccess.of(mc.player).getJumpUpwardSpeed(1.0F);
            double var3 = mc.player.getFinalGravity();
            int var5 = (int) ((var1 - 1.0E-5) / var3);
            this.rA = var5 + 1;
            this.toggleRunning = true;
            this.rz = 0;
        }
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.isActive() && this.toggleRunning && this.rz < 0) {
            this.rz = -114514;
            ((LegalMovementManager) movementManagerEvent.b).c.a.setOnGround(true);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aw(), this::AH);
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.isActive() && this.rz < -1145) {
            this.rz = 0;
            this.toggleRunning = false;
        }

        return true;
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.rx = null;
    }

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.isActive() && this.toggleRunning) {
            this.rz++;
            if (this.rz >= this.rA) {
                ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
                if (!var2.isOnGround()) {
                    Vec3d var3 = new Vec3d(var2.sidewaysSpeed, 0.0, var2.forwardSpeed);
                    Vec3d var4 = var2.getVelocity();
                    Vec3d var5 =
                            var4.add(EntityUtils.movementInputToVelocity(var3, var2.getMovementSpeed(), var2.getYaw()));
                    var2.setOnGround(true);
                    boolean var6 = MovTasks.M(var2, var5);
                    if (var6) {
                        this.rz = -200;
                        movementManagerEvent.cancel();
                        ((LegalMovementManager) movementManagerEvent.b).c.a.setOnGround(true);
                        mc.getNetworkHandler().sendPacket(VPacket.f(true, var2.horizontalCollision));
                        return;
                    }

                    var2.setOnGround(false);
                }

                this.rz = -11451;
            }
        }
    }

    private void AH(Event<Integer> jumpEvent) {
        if (this.rx != null) {
            this.rx.run();
        }
    }

    public StepHeight() {
        super("StepHeight");
        this.ae = this.flagBuilder(this.rw).build();
        this.rA = 6;
        if (instance == null) {
            instance = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> instance);
        }

        instance.mN(this::cast);
        this.bindFlag(this.ae);
    }

    @Override
    public void onEnableModule() {
        super.onEnableModule();
        this.rz = 0;
        this.toggleRunning = false;
        this.y = 0.0;
        this.rA = 6;
        this.rx = this::triggerJump;
    }

    @Override
    public int priority() {
        return 0;
    }
}
