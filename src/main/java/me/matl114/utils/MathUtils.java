package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class MathUtils {
    public static final Direction[] HORIZONTALS = Arrays.stream(Direction.values())
            .filter(direction -> direction.getAxis().isHorizontal())
            .sorted(Comparator.comparingInt(Direction::getHorizontal))
            .toArray(Direction[]::new);

    public static double a(double x) {
        return x * x;
    }

    public static boolean isInBox(Vec3d a, double range) {
        return Math.abs(a.x) < range && Math.abs(a.y) < range && Math.abs(a.z) < range;
    }

    public static Vec3d lerp(double delta, Vec3d start, Vec3d end) {
        return new Vec3d(
                MathHelper.lerp(delta, start.x, end.x),
                MathHelper.lerp(delta, start.y, end.y),
                MathHelper.lerp(delta, start.z, end.z));
    }

    public static Pair<Vec3d, Vec3d> getTangentWithSameXZ(Vec3d center, double range, Vec3d point) {
        return F(range, point.subtract(center));
    }

    public static Direction getHorizontalFacing(Vec3d vec3d) {
        float var1 = EntityUtils.s(vec3d.normalize());
        return fromHorizontalDegrees(var1);
    }

    public static double squaredMagnitude(Box thi, Box other) {
        double var2 = Math.max(Math.max(thi.minX - other.maxX, other.minX - thi.maxX), 0.0);
        double var4 = Math.max(Math.max(thi.minY - other.maxY, other.minY - thi.maxY), 0.0);
        double var6 = Math.max(Math.max(thi.minZ - other.maxZ, other.minZ - thi.maxZ), 0.0);
        return MathHelper.squaredMagnitude(var2, var4, var6);
    }

    public static List<Vec3i> create2DPointListInRange(double i, int x) {
        ArrayList<Vec3i> var3 = new ArrayList<>();
        int var4 = (int) i;

        for (int var5 = -var4; var5 <= var4; var5++) {
            for (int var6 = -var4; var6 <= var4; var6++) {
                var3.add(new Vec3i(var5, x, var6));
            }
        }

        var3.sort(Comparator.comparingDouble(v -> v.getX() * v.getX() + v.getZ() * v.getZ()));
        return var3;
    }

    public static double squareSum(double... x) {
        double var1 = 0.0;

        for (double var6 : x) {
            var1 += var6 * var6;
        }

        return var1;
    }

    public static Vec3d quadraticPolynomialFit(Vec3d[] positions, int ticksLater) {
        if (ticksLater <= 0 || positions.length < 3) {
            return positions[positions.length - 1];
        } else if (positions[0] != null && positions[1] != null && positions[2] != null) {
            double[] var2 = new double[] {-2.0, -1.0, 0.0};
            double[] var3 = new double[3];
            double[] var4 = new double[3];
            double[] var5 = new double[3];

            for (int var6 = 0; var6 < 3; var6++) {
                var3[var6] = positions[var6].x;
                var4[var6] = positions[var6].y;
                var5[var6] = positions[var6].z;
            }

            double[] var13 = solveQuadratic(var2, var3);
            double[] var7 = solveQuadratic(var2, var4);
            double[] var8 = solveQuadratic(var2, var5);
            double var9 = ticksLater;
            double var11 = var9 * var9;
            return new Vec3d(
                    var13[0] * var11 + var13[1] * var9 + var13[2],
                    var7[0] * var11 + var7[1] * var9 + var7[2],
                    var8[0] * var11 + var8[1] * var9 + var8[2]);
        } else {
            return positions[2];
        }
    }

    public static Pair<Vec3d, Vec3d> D(double range, Vec3d point) {
        double var3 = a(range);
        double var5 = point.length();
        if (a(var5) <= var3) {
            Vec3d var7 = getVerticalWithSameXZ(point);
            return Pair.of(var7, var7.negate());
        } else {
            double var8 = var3 / var5;
            double var10 = Math.sqrt(var3 - a(var8));
            Vec3d var12 = getVerticalWithSameXZ(point);
            Vec3d var13 = point.normalize().multiply(var8);
            return Pair.of(
                    var13.add(var12.multiply(var10)).subtract(point),
                    var13.subtract(var12.multiply(var10)).subtract(point));
        }
    }

    public static Direction fromHorizontalDegrees(double angle) {
        return fromHorizontalQuarterTurns(MathHelper.floor(angle / 90.0 + 0.5) & 3);
    }

    public static List<BlockPos> z(Vec3d min, Vec3d max) {
        return getOccupiedBlockPositions(new Box(min, max));
    }

    public static int e(long packed) {
        return (int) (packed >> 32);
    }

    public static int getManhattanDistance(BlockPos pos1, BlockPos pos2) {
        return Math.abs(pos1.getX() - pos2.getX())
                + Math.abs(pos1.getY() - pos2.getY())
                + Math.abs(pos1.getZ() - pos2.getZ());
    }

    private static double[] solveQuadratic(double[] t, double[] p) {
        double var2 = t[0];
        double var4 = t[1];
        double var6 = t[2];
        double var8 = p[0];
        double var10 = p[1];
        double var12 = p[2];
        double var14 = var2 * var2 * (var4 - var6)
                + var2 * (var6 * var6 - var4 * var4)
                + (var4 * var4 * var6 - var4 * var6 * var6);
        double var16 = var8 * (var4 - var6) + var2 * (var12 - var10) + (var10 * var6 - var12 * var4);
        double var18 = var2 * var2 * (var10 - var12)
                + var8 * (var6 * var6 - var4 * var4)
                + (var4 * var4 * var12 - var6 * var6 * var10);
        double var20 = var2 * var2 * (var4 * var12 - var6 * var10)
                + var2 * (var6 * var6 * var10 - var4 * var4 * var12)
                + var8 * (var4 * var4 * var6 - var4 * var6 * var6);
        double var22 = var16 / var14;
        double var24 = var18 / var14;
        double var26 = var20 / var14;
        return new double[] {var22, var24, var26};
    }

    public static List<BlockPos> getOccupiedBlockPositions(Box box) {
        int var1 = (int) Math.floor(box.minX);
        int var2 = (int) Math.ceil(box.maxX) - 1;
        int var3 = (int) Math.floor(box.minY);
        int var4 = (int) Math.ceil(box.maxY) - 1;
        int var5 = (int) Math.floor(box.minZ);
        int var6 = (int) Math.ceil(box.maxZ) - 1;
        ArrayList var7 = new ArrayList((var2 - var1 + 1) * (var4 - var3 + 1) * (var6 - var5 + 1));

        for (int var8 = var1; var8 <= var2; var8++) {
            for (int var9 = var3; var9 <= var4; var9++) {
                for (int var10 = var5; var10 <= var6; var10++) {
                    var7.add(new BlockPos(var8, var9, var10));
                }
            }
        }

        return var7;
    }

    public static Box r(BlockPos pos) {
        return new Box(pos);
    }

    public static Direction fromHorizontalQuarterTurns(int quarterTurns) {
        return HORIZONTALS[MathHelper.abs(quarterTurns % HORIZONTALS.length)];
    }

    public static double getBoxDistance(double x, double minX, double maxX) {
        return Math.max(Math.max(minX - x, x - maxX), 0.0);
    }

    public static boolean o(Vec3d a, Vec3d b, double range) {
        return isInBox(a.subtract(b), range);
    }

    public static Vec3d M(Vec3d[] vec3ds, int ticksLater) {
        if (ticksLater <= 0) {
            return vec3ds[vec3ds.length - 1];
        } else {
            int var2 = 0;

            for (int var3 = vec3ds.length - 1; var3 >= 0 && vec3ds[var3] != null; var3--) {
                var2++;
            }

            if (var2 < 2) {
                return vec3ds[vec3ds.length - 1];
            } else {
                Vec3d[] var12 = new Vec3d[var2];
                System.arraycopy(vec3ds, vec3ds.length - var2, var12, 0, var2);
                vec3ds = var12;
                double[] var4 = new double[var12.length];
                double[] var5 = new double[var12.length];
                double[] var6 = new double[var12.length];
                double[] var7 = new double[var12.length];

                for (int var8 = 0; var8 < vec3ds.length; var8++) {
                    var4[var8] = vec3ds[var8].x;
                    var5[var8] = vec3ds[var8].y;
                    var6[var8] = vec3ds[var8].z;
                    var7[var8] = -vec3ds.length + 1 + var8;
                }

                KalamaHelperHelperLX var13 = O(var7, var4);
                KalamaHelperHelperLX var9 = O(var7, var5);
                KalamaHelperHelperLX var10 = O(var7, var6);
                return new Vec3d(var13.f(ticksLater), var9.f(ticksLater), var10.f(ticksLater));
            }
        }
    }

    public static boolean m(Vec3d a, Vec3d b, double range) {
        return isInBox(a.subtract(b), range);
    }

    public static Box createBox(Vec3d vec3d, double ra) {
        return new Box(vec3d.subtract(ra, ra, ra), vec3d.add(ra, ra, ra));
    }

    public static Vec3d getVerticalWithSameY(Vec3d vec3d) {
        Vec3d var1 = vec3d.normalize();
        double var2 = var1.x;
        double var4 = var1.z;
        return Math.abs(var2) < 1.0E-8 && Math.abs(var4) < 1.0E-8
                ? new Vec3d(1.0, 0.0, 0.0)
                : new Vec3d(var4, 0.0, -var2).normalize();
    }

    public static Pair<Vec3d, Vec3d> F(double range, Vec3d point) {
        double var3 = a(range);
        double var5 = point.length();
        if (a(var5) <= var3) {
            Vec3d var7 = getVerticalWithSameY(point);
            return Pair.of(var7, var7.negate());
        } else {
            double var8 = var3 / var5;
            double var10 = Math.sqrt(var3 - a(var8));
            Vec3d var12 = getVerticalWithSameY(point);
            Vec3d var13 = point.normalize().multiply(var8);
            return Pair.of(
                    var13.add(var12.multiply(var10)).subtract(point),
                    var13.subtract(var12.multiply(var10)).subtract(point));
        }
    }

    public static int f(long packed) {
        return (int) packed;
    }

    public static Vec3d getVerticalWithSameXZ(Vec3d vec3d) {
        Vec3d var1 = vec3d.normalize();
        return (var1.y != 0.0 ? new Vec3d(var1.x, -(a(var1.x) + a(var1.z)) / var1.y, var1.z) : new Vec3d(0.0, 1.0, 0.0))
                .normalize();
    }

    public static KalamaHelperHelperAl P(double[] x, double[] y) {
        int var2 = x.length;
        double var3 = 0.0;
        double var5 = 0.0;
        double var7 = 0.0;
        double var9 = 0.0;
        double var11 = 0.0;
        double var13 = 0.0;
        double var15 = 0.0;

        for (int var17 = 0; var17 < var2; var17++) {
            double var18 = x[var17];
            double var20 = var18 * var18;
            double var22 = var20 * var18;
            double var24 = var22 * var18;
            var3 += var18;
            var5 += var20;
            var7 += var22;
            var9 += var24;
            var11 += y[var17];
            var13 += var18 * y[var17];
            var15 += var20 * y[var17];
        }

        double[][] var10000 = new double[][] {{var9, var7, var5}, {var7, var5, var3}, {var5, var3, var2}};
        double[] var31 = new double[] {var15, var13, var11};
        Matrix3d var27 = new Matrix3d(var9, var7, var5, var7, var5, var3, var5, var3, var2);
        Vector3d var28 = new Vector3d(var15, var13, var11);
        if (Math.abs(var27.determinant()) > 1.0E-6) {
            Matrix3d var29 = var27.invert();
            Vector3d var30 = var29.transform(var28);
            return new KalamaHelperHelperRX(var30.x, var30.y, var30.z);
        } else {
            return O(x, y);
        }
    }

    public static Vec3d linearInterpolation(Vec3d[] vec3ds, int ticksLater) {
        if (ticksLater <= 0 || vec3ds.length < 3) {
            return vec3ds[vec3ds.length - 1];
        } else if (vec3ds[0] != null && vec3ds[1] != null && vec3ds[2] != null) {
            Vec3d var2 = vec3ds[2].subtract(vec3ds[1]);
            Vec3d var3 = vec3ds[1].subtract(vec3ds[0]);
            Vec3d var4 = var2.subtract(var3);
            double var5 = ticksLater;
            return vec3ds[2].add(var2.multiply(var5)).add(var4.multiply(0.5 * var5 * var5));
        } else {
            return vec3ds[2];
        }
    }

    public static long packInt(int high, int low) {
        return (long) high << 32 | low & 4294967295L;
    }

    public static double sgn(double t, double threshold) {
        return Math.abs(t) > threshold ? j(t) : 0.0;
    }

    public static int i(int t) {
        return Integer.compare(t, 0);
    }

    public static KalamaHelperHelperLX O(double[] x, double[] y) {
        int var2 = x.length;
        double var3 = 0.0;
        double var5 = 0.0;
        double var7 = 0.0;
        double var9 = 0.0;

        for (int var11 = 0; var11 < var2; var11++) {
            var3 += x[var11];
            var5 += y[var11];
            var7 += x[var11] * y[var11];
            var9 += x[var11] * x[var11];
        }

        double var12 = var2 * var9 - var3 * var3;
        if (Math.abs(var12) < 1.0E-10) {
            return new KalamaHelperHelperLX(0.0, var5 / var2);
        } else {
            double var14 = (var2 * var7 - var3 * var5) / var12;
            double var16 = (var5 - var14 * var3) / var2;
            return new KalamaHelperHelperLX(var14, var16);
        }
    }

    public static List<Vec3i> H(double i) {
        ArrayList<Vec3i> var2 = new ArrayList<>();
        int var3 = (int) i;

        for (int var4 = -var3; var4 <= var3; var4++) {
            for (int var5 = -var3; var5 <= var3; var5++) {
                for (int var6 = -var3; var6 <= var3; var6++) {
                    var2.add(new Vec3i(var4, var5, var6));
                }
            }
        }

        var2.sort(Comparator.comparingDouble(s -> s.getX() * s.getX() + s.getZ() * s.getZ() + s.getY() * s.getY()));
        return var2;
    }

    public static ChunkPos toChunkPos(Vec3d vec3d) {
        return new ChunkPos(BlockPos.ofFloored(vec3d));
    }

    public static String getDirectionName(int xSgn, int zSgn) {
        if (xSgn == 0 && zSgn == 0) {
            return "Center";
        } else if (xSgn == 0 && zSgn == 1) {
            return "South";
        } else if (xSgn == 0 && zSgn == -1) {
            return "North";
        } else if (xSgn == 1 && zSgn == 0) {
            return "East";
        } else if (xSgn == -1 && zSgn == 0) {
            return "West";
        } else if (xSgn == 1 && zSgn == 1) {
            return "Southeast";
        } else if (xSgn == 1 && zSgn == -1) {
            return "Northeast";
        } else if (xSgn == -1 && zSgn == 1) {
            return "Southwest";
        } else {
            return xSgn == -1 && zSgn == -1 ? "Northwest" : "Unknown";
        }
    }

    public static Vec3d magnitudePoint(Box shrinkedBox, Vec3d bestEyePos) {
        double var2 = Math.clamp(bestEyePos.x, shrinkedBox.minX, shrinkedBox.maxX);
        double var4 = Math.clamp(bestEyePos.y, shrinkedBox.minY, shrinkedBox.maxY);
        double var6 = Math.clamp(bestEyePos.z, shrinkedBox.minZ, shrinkedBox.maxZ);
        return new Vec3d(var2, var4, var6);
    }

    public static int b(int x) {
        return x * x;
    }

    public static Vec3d N(Vec3d[] vec3ds, int ticksLater) {
        if (ticksLater <= 0) {
            return vec3ds[vec3ds.length - 1];
        } else {
            int var2 = 0;

            for (int var3 = vec3ds.length - 1; var3 >= 0 && vec3ds[var3] != null; var3--) {
                var2++;
            }

            if (var2 < 4) {
                return vec3ds[vec3ds.length - 1];
            } else {
                Vec3d[] var12 = new Vec3d[var2];
                System.arraycopy(vec3ds, vec3ds.length - var2, var12, 0, var2);
                vec3ds = var12;
                double[] var4 = new double[var12.length];
                double[] var5 = new double[var12.length];
                double[] var6 = new double[var12.length];
                double[] var7 = new double[var12.length];

                for (int var8 = 0; var8 < vec3ds.length; var8++) {
                    var4[var8] = vec3ds[var8].x;
                    var5[var8] = vec3ds[var8].y;
                    var6[var8] = vec3ds[var8].z;
                    var7[var8] = -vec3ds.length + 1 + var8;
                }

                KalamaHelperHelperAl var13 = P(var7, var4);
                KalamaHelperHelperAl var9 = P(var7, var5);
                KalamaHelperHelperAl var10 = P(var7, var6);
                return new Vec3d(var13.f(ticksLater), var9.f(ticksLater), var10.f(ticksLater));
            }
        }
    }

    public static double j(double t) {
        return Double.compare(t, 0.0);
    }

    public static boolean isInXZRange(Vec3d a, double range) {
        return Math.abs(a.x) < range && Math.abs(a.z) < range;
    }

    public static Pair<Vec3d, Vec3d> C(Vec3d center, double range, Vec3d point) {
        return D(range, point.subtract(center));
    }
}
