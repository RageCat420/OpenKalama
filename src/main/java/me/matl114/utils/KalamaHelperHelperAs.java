package me.matl114.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public class KalamaHelperHelperAs {
    private final Vec3d[] historyStack;
    private static final double MAX_TURN_ANGLE = Math.toRadians(60.0);
    private final IntSupplier currentIndex;

    public KalamaHelperHelperAs(Vec3d[] historyStack, IntSupplier currentIndex) {
        this.historyStack = historyStack;
        this.currentIndex = currentIndex;
    }

    private List<Vec3d> collectPoints() {
        int var1 = this.currentIndex.getAsInt();
        Vec3d var2 = this.historyStack[var1];
        if (var2 == null) {
            return List.of();
        } else {
            ArrayList var3 = new ArrayList();
            var3.add(var2);
            int var4 = this.historyStack.length;

            for (int var5 = 1; var5 < var4; var5++) {
                Vec3d var6 = this.historyStack[(var1 - var5 + var4) % var4];
                if (var6 == null) {
                    break;
                }

                var3.add(0, var6);
            }

            return var3;
        }
    }

    public Vec3d compute(int ticksLater) {
        List var2 = this.collectPoints();
        if (var2.isEmpty()) {
            return null;
        } else {
            Vec3d var3 = (Vec3d) var2.get(var2.size() - 1);
            if (ticksLater <= 0) {
                return var3;
            } else if (var2.size() < 2) {
                return var3;
            } else {
                Vec3d var4 = var3.subtract((Vec3d) var2.get(var2.size() - 2));
                if (var2.size() < 3) {
                    return var3.add(var4.multiply(ticksLater));
                } else {
                    ArrayList var5 = new ArrayList();
                    ArrayList var6 = new ArrayList();
                    ArrayList var7 = new ArrayList();

                    for (int var8 = 1; var8 < var2.size(); var8++) {
                        Vec3d var9 = ((Vec3d) var2.get(var8)).subtract((Vec3d) var2.get(var8 - 1));
                        var5.add(Math.hypot(var9.x, var9.z));
                        var6.add(var9.y);
                        var7.add(Math.atan2(var9.z, var9.x));
                    }

                    double var10 = 0.0;
                    int var12 = 0;

                    for (int var13 = 1; var13 < var7.size(); var13++) {
                        double var14 = (Double) var7.get(var13) - (Double) var7.get(var13 - 1);

                        while (var14 > Math.PI) {
                            var14 -= Math.PI * 2;
                        }

                        while (var14 < -Math.PI) {
                            var14 += Math.PI * 2;
                        }

                        if (Math.abs(var14) <= MAX_TURN_ANGLE) {
                            var10 += var14;
                        }

                        var12++;
                    }

                    if (var12 > 0) {
                        var10 /= var12;
                    }

                    double var16 = this.predictScalar(var5, ticksLater);
                    double var18 = this.predictScalar(var6, ticksLater);
                    if (var16 < 0.0) {
                        var16 = 0.0;
                    }

                    double var20 = (Double) var7.get(var7.size() - 1);
                    double var22 = var20 + var10;
                    double var24 = Math.cos(var22) * var16;
                    double var26 = Math.sin(var22) * var16;
                    return var3.add(var24, var18, var26);
                }
            }
        }
    }

    private double predictScalar(List<Double> samples, int ticksLater) {
        if (samples.isEmpty()) {
            return 0.0;
        } else {
            double var3 = (Double) samples.get(samples.size() - 1);
            if (ticksLater > 0 && samples.size() >= 2) {
                ArrayList<Double> var5 = new ArrayList();

                for (int var6 = 1; var6 < samples.size(); var6++) {
                    var5.add((Double) samples.get(var6) - (Double) samples.get(var6 - 1));
                }

                if (var5.isEmpty()) {
                    return var3;
                } else if (var5.size() == 1) {
                    return var3 + (Double) var5.get(0) * ticksLater;
                } else {
                    double var7 = 0.0;

                    for (double var10 : var5) {
                        var7 = (var7 + var10) * 0.5;
                    }

                    return var3 + var7 * ticksLater;
                }
            } else {
                return var3;
            }
        }
    }
}
