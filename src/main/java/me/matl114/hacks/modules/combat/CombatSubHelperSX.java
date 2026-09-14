package me.matl114.hacks.modules.combat;

import me.matl114.events.Event;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.utils.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class CombatSubHelperSX {
    Vec3d u = Vec3d.ZERO;
    ElytraBot t;

    public void j() {}

    protected void antiSpear(OptionalPrimitive<Double> op) {
        Vec3d var2 = this.u.withAxis(Axis.Y, 0.0);
        if (var2.lengthSquared() < 0.01) {
            double var3 = 3.0;
            if (!(MinecraftClient.getInstance()
                            .player
                            .getPos()
                            .subtract(this.t.uI.getPos())
                            .horizontalLength()
                    < var3)) {
                return;
            }

            this.u = this.u.withAxis(Axis.X, 5.0);
            var2 = this.u.withAxis(Axis.Y, 0.0);
        }

        Vec3d var5 = new Vec3d(0.0, 1.0, 0.0);
        Vec3d var6 = var5.crossProduct(var2).normalize();
        Vec3d var7 = this.u.normalize();
        Vec3d var8 = var6.multiply((Double) op.getValue());
        this.u = var7.add(var8).normalize().multiply(10.0);
    }

    public CombatSubHelperSX S(Vec3d movementDirection) {
        this.u = movementDirection;
        return this;
    }

    protected boolean willUseAntiSpear(OptionalPrimitive<Double> op, Vec3d predictorPos) {
        return op.isPresent()
                && Math.abs((Double) op.getValue()) > 1.0E-6
                && this.t.DC()
                && MinecraftClient.getInstance().player.getPos().squaredDistanceTo(predictorPos)
                        < MathUtils.a(this.t.combatSpearRange.get());
    }

    public CombatSubHelperSX R(ElytraBot base) {
        this.t = base;
        return this;
    }

    public void i() {}

    public void L() {}

    public void O(Event<Void> eventInput) {}

    public void onElytra(Event<KalamaHelperHelperI<FlightVelocity>> event) {
        if (this.u != null && this.u.lengthSquared() > 1.0E-9) {
            Vec3d var2 = this.u;
            double var3 = var2.length();
            double var5 = Math.min(var3, ((FlightVelocity) ((KalamaHelperHelperI) event.b).b()).f() * this.t.uT);
            var2 = var2.normalize().multiply(var5);
            ((FlightVelocity) ((KalamaHelperHelperI) event.b).b()).velocity(var2);
        }
    }

    public synchronized void f(Entity entity) {}

    public synchronized void h() {
        this.t.uT = 1.0;
    }

    public Entity searchTarget() {
        return CombatTasks.l()
                .akK(this.t.range.get(), true, this.t.playerOnly.get() ? e -> e instanceof PlayerEntity : null);
    }
}
