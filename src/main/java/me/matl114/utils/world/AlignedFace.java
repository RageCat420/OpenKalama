package me.matl114.utils.world;

import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AlignedFace {
    private final AtomicReference<Object> e;
    Vec3d a;
    private final AtomicReference<Object> d;
    Vec3d b;
    private final AtomicReference<Object> c = new AtomicReference<>();

    public Vec3d e() {
        return this.b;
    }

    public Vec3d getCenter() {
        Object var1 = this.e.get();
        if (var1 == null) {
            synchronized (this.e) {
                var1 = this.e.get();
                if (var1 == null) {
                    Vec3d var3 = this.a.add(this.b).multiply(0.5);
                    var1 = var3 == null ? this.e : var3;
                    this.e.set(var1);
                }
            }
        }

        return (Vec3d) (var1 == this.e ? null : var1);
    }

    public boolean isEmpty() {
        return MathHelper.approximatelyEquals(this.getArea(), 0.0);
    }

    private double calculateArea(Vec3d dims) {
        return (dims.x * dims.y + dims.y * dims.z + dims.x * dims.z) * 2.0;
    }

    public Vec3d d() {
        return this.a;
    }

    public Vec3d getDimensions() {
        Object var1 = this.c.get();
        if (var1 == null) {
            synchronized (this.c) {
                var1 = this.c.get();
                if (var1 == null) {
                    Vec3d var3 = new Vec3d(this.b.x - this.a.x, this.b.y - this.a.y, this.b.z - this.a.z);
                    var1 = var3 == null ? this.c : var3;
                    this.c.set(var1);
                }
            }
        }

        return (Vec3d) (var1 == this.c ? null : var1);
    }

    public AlignedFace truncateY(double minY) {
        return new AlignedFace(
                new Vec3d(this.a.x, Math.max(this.a.y, minY), this.a.z),
                new Vec3d(this.b.x, Math.max(this.b.y, minY), this.b.z));
    }

    public AlignedFace(Vec3d from, Vec3d to) {
        this.d = new AtomicReference<>();
        this.e = new AtomicReference<>();
        this.a = new Vec3d(Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z));
        this.b = new Vec3d(Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z));
    }

    public double getArea() {
        Object var1 = this.d.get();
        if (var1 == null) {
            synchronized (this.d) {
                var1 = this.d.get();
                if (var1 == null) {
                    double var3 = this.calculateArea(this.getDimensions());
                    var1 = var3;
                    this.d.set(var1);
                }
            }
        }

        return (Double) var1;
    }
}
