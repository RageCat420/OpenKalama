package me.matl114.hacks.modules.combat;

import java.awt.Color;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import me.matl114.utils.MathUtils;
import me.matl114.utils.algorithms.StateMachine;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class CombatSubHelperIX extends CombatSubHelperSX implements CombatSubHelperG {
    boolean h;
    int g;
    static final int c = 1;
    static final int d = 2;
    static final int b = 0;
    int f;
    StateMachine stateMachine = new StateMachine(
            0,
            this::onStateUpdate,
            this::onStateNone,
            this::onStateFollow,
            this::onStateNearFollow,
            this::onStatePullOver);
    static final int e = 3;

    @Override
    public void i() {
        this.stateMachine.c(0);
        this.g = 0;
        this.f = 0;
        this.h = false;
    }

    private boolean x(Vec3d targetPosition) {
        OptionalPrimitive var2 = this.t.spearNoEntryDistance.get();
        return var2.isPresent()
                && MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(targetPosition)
                        < MathUtils.a((Double) var2.getValue());
    }

    public void u(boolean isOn) {
        this.g = 0;
    }

    public int onStateNearFollow(StateMachine machine) {
        if (this.x(this.getTargetPosition())) {
            return 3;
        } else if ((this.t.uR && this.t.uQ == CombatSubHelperV.Rn
                        || this.t.uQ == CombatSubHelperV.Rm
                        || this.t.uQ == CombatSubHelperV.Rp)
                && !SpearEnhance.canSpearKineticAttack()) {
            return 3;
        } else {
            Vec3d var2 = this.getTargetPosition();
            if (MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(var2) > MathUtils.a(this.v())) {
                return 1;
            } else {
                machine.e();
                Vec3d var3 = var2.subtract(MinecraftClient.getInstance().player.getEyePos());
                if (var3.length() < 6.0) {
                    var3 = var3.normalize().multiply(6.0);
                }

                if (this.canAdjustMovement()
                        && this.adjustMovementForSpear((PlayerEntity) (Object) this.t.uI, var3, false)) {
                    return 2;
                } else {
                    Vec3d var4 = var3.withAxis(Axis.Y, 0.0);
                    Vec3d var5 = PlayerStateManager.INSTANCE.jr.withAxis(Axis.Y, 0.0);
                    double var6 = var4.dotProduct(var5);
                    if (var6 < 0.0) {
                        var3 = var3.negate();
                    }

                    this.u = var3;
                    return 2;
                }
            }
        }
    }

    public double w() {
        return 1.0;
    }

    public CombatSubHelperIX() {
        this.stateMachine.registerListener(2, this::t);
        this.stateMachine.registerListener(3, this::u);
    }

    private boolean q(Vec3d originalLook) {
        return false;
    }

    private boolean adjustMovementForSpear(PlayerEntity otherShit, Vec3d originalLook, boolean expand) {
        if (otherShit.squaredDistanceTo(MinecraftClient.getInstance().player.getPos())
                < MathUtils.a(this.v() * 2.0 + this.t.spearAntiSpearExtraDistance.get())) {
            return Tasks.b() % 5 < 2 ? this.moveAdjust(originalLook) : this.q(originalLook);
        } else {
            Box var4 = expand
                    ? MinecraftClient.getInstance().player.getBoundingBox().expand(0.85, 0.85, 0.85)
                    : MinecraftClient.getInstance().player.getBoundingBox();
            Vec3d var5 = PositionPredict.INSTANCE.acK(otherShit);
            Vec3d var6 = PositionPredict.INSTANCE.spearPredictArgument.get().predict(otherShit);
            Vec3d var7 = otherShit.getRotationVector();
            Debug.g(
                    "Spear judgement",
                    var5,
                    var6,
                    MinecraftClient.getInstance().player.getPos());
            double var8 = var5.dotProduct(var7);
            Vec3d var10 = var6.add(0.0, otherShit.getEyeHeight(otherShit.getPose()), 0.0);
            Vec3d var11 = var10.add(var7.multiply(this.w()));
            Vec3d var12 = var10.add(var7.multiply(this.v() + var8 + this.t.spearAntiSpearExtraDistance.get()));
            return var4.raycast(var11, var12).isPresent() ? this.moveAdjust(originalLook) : false;
        }
    }

    public int getCooldown() {
        return 6;
    }

    public int onStatePullOver(StateMachine machine) {
        OptionalPrimitive var2 = this.t.spearPullBackDistance.get();
        double var3 = MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(this.getTargetPosition());
        if (this.h && var2.isPresent()) {
            if (var3 >= MathUtils.a((Double) var2.getValue())) {
                this.h = false;
                return 1;
            }
        } else {
            if (++this.g > this.getCooldown()) {
                return 1;
            }

            Vec3d var5 = this.getTargetPosition();
            if (this.t.uQ != null) {
                if (this.t.uQ == CombatSubHelperV.Rp
                        || this.t.uQ == CombatSubHelperV.Ro
                        || this.t.uR && this.t.uQ != CombatSubHelperV.Rl) {
                    double var11 = this.y();
                    if (MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(var5) > MathUtils.a(var11)) {
                        return 1;
                    }
                } else if (this.t.uQ == CombatSubHelperV.Rl) {
                    double var6 = this.y();
                    if (MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(var5) > MathUtils.a(var6)) {
                        return 1;
                    }
                } else {
                    double var10 = this.y();
                    if (MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(var5) > MathUtils.a(var10)) {
                        return 1;
                    }
                }
            }
        }

        Vec3d var9 = this.getTargetPosition();
        machine.e();
        Vec3d var8 = var9.subtract(MinecraftClient.getInstance().player.getEyePos());
        if (var8.length() < 6.0) {
            var8 = var8.normalize().multiply(6.0);
        }

        if (this.canAdjustMovement() && this.adjustMovementForSpear((PlayerEntity) (Object) this.t.uI, var8, true)) {
            return 3;
        } else {
            if (this.t.uS) {
                if (var8.lengthSquared() < this.w()) {
                    this.u = var8.negate().add(0.0, 1.0, 0.0);
                } else {
                    this.u = var8.negate();
                }
            } else {
                this.u = var8.negate();
                this.u = this.u.withAxis(Axis.Y, Math.abs(this.u.y));
            }

            return 3;
        }
    }

    private boolean moveAdjust(Vec3d originalLook) {
        Debug.h("Judget may hit");
        Vec3d var2 = originalLook.withAxis(Axis.Y, 0.0);
        Vec3d var3 = new Vec3d(0.0, 1.0, 0.0);
        Vec3d var4 = var3.crossProduct(var2);
        Vec3d var5 = var4.normalize().multiply(var2.length()).add(0.0, var2.y, 0.0);
        Vec3d var6 = var5.normalize().multiply(0.5);
        Vec3d var7 = MovTasks.simulateMovement(
                MinecraftClient.getInstance().player,
                MinecraftClient.getInstance().player.getPos(),
                var6,
                false);
        if (var7.squaredDistanceTo(var6) < 0.1) {
            this.u = var5;
            Debug.g("JudgeA", this.u);
            RenderTasks.g(
                    MinecraftClient.getInstance().player.getBoundingBox(),
                    var5.normalize().multiply(1.7),
                    50,
                    Color.BLUE);
            return true;
        } else {
            var5 = var5.negate();
            var6 = var6.negate();
            var7 = MovTasks.simulateMovement(
                    MinecraftClient.getInstance().player,
                    MinecraftClient.getInstance().player.getPos(),
                    var6,
                    false);
            if (var7.squaredDistanceTo(var6) < 0.1) {
                this.u = var5;
                Debug.g("JudgeB", this.u);
                RenderTasks.g(
                        MinecraftClient.getInstance().player.getBoundingBox(),
                        var5.normalize().multiply(1.7),
                        50,
                        Color.BLUE);
                return true;
            } else {
                return false;
            }
        }
    }

    public void t(boolean isOn) {
        this.f = 0;
    }

    public synchronized void onUpdate() {
        super.h();
        if (!MinecraftClient.getInstance().player.isFallFlying()
                && !MinecraftClient.getInstance().player.getAbilities().flying) {
            this.u = Vec3d.ZERO;
            this.stateMachine.c(0);
        } else {
            this.stateMachine.f();
        }
    }

    public int onStateNone(StateMachine machine) {
        if (this.t.uI != null) {
            if (!VItem.w().b(MinecraftClient.getInstance().player.getMainHandStack())
                    && !VItem.w().b(MinecraftClient.getInstance().player.getOffHandStack())) {
                this.t.logI18N("message.module.elytra-bot.no-spear", new Object[0]);
            }

            return 1;
        } else {
            machine.e();
            this.u = Vec3d.ZERO;
            return 0;
        }
    }

    public double v() {
        return this.t.spearAttackDistance.get();
    }

    @Override
    public void j() {}

    private double y() {
        OptionalPrimitive var1 = this.t.spearNoEntryDistance.get();
        return var1.isPresent()
                ? Math.max(this.t.spearPullOverDistance.get(), (Double) var1.getValue())
                : this.t.spearPullOverDistance.get();
    }

    public int onStateFollow(StateMachine machine) {
        Vec3d var2 = this.getTargetPosition();
        if (this.x(var2)) {
            return 3;
        } else if (MinecraftClient.getInstance().player.getEyePos().squaredDistanceTo(var2) < MathUtils.a(this.v())) {
            return 2;
        } else {
            machine.e();
            Vec3d var3 = var2.subtract(MinecraftClient.getInstance().player.getEyePos());
            if (var3.length() < 6.0) {
                var3 = var3.normalize().multiply(6.0);
            }

            if (this.canAdjustMovement()
                    && this.adjustMovementForSpear((PlayerEntity) (Object) this.t.uI, var3, false)) {
                return 1;
            } else {
                this.u = var3;
                return 1;
            }
        }
    }

    private boolean canAdjustMovement() {
        return this.t.spearAntiSpear.get() && this.t.DC();
    }

    @Override
    public synchronized void onHit(int spear) {
        if (spear == 2) {
            this.h = true;
            this.f = 0;
            this.stateMachine.c(3);
            this.g = 0;
        }
    }

    public int onStateUpdate(StateMachine machine, int state) {
        return this.t.uI == null ? 0 : state;
    }

    private Vec3d getTargetPosition() {
        Vec3d var1 = this.t.spearUsePredictor.get()
                ? PositionPredict.INSTANCE.acJ(this.t.uI).predict(2, PositionPredict$Mode.PREDICTOR_NV.ordinal(), 10)
                : this.t.uI.getPos();
        Vec3d var2 = var1.subtract(this.t.uI.getPos());
        return this.t.uS
                ? this.t.uI.getEyePos().add(var2)
                : this.t.uI.getBoundingBox().getCenter().add(var2);
    }
}
