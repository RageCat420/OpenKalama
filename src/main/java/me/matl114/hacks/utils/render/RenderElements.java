package me.matl114.hacks.utils.render;

import java.util.List;
import me.matl114.versioned.api.VRender;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class RenderElements {
    public static final int POSITION_FLAG = VRender.createTextPositionFlag(0, 1);

    public static boolean isFlat(double min, double max) {
        return Double.compare(normalizeZero(min), normalizeZero(max)) == 0;
    }

    public static List<HackUtilHelperD> boxOutline(Box box) {
        Vec3d var1 = box.getMinPos();
        Vec3d var2 = box.getMaxPos();
        boolean var3 = isFlat(var1.x, var2.x);
        boolean var4 = isFlat(var1.y, var2.y);
        boolean var5 = isFlat(var1.z, var2.z);
        int var6 = (var3 ? 1 : 0) + (var4 ? 1 : 0) + (var5 ? 1 : 0);
        if (var6 >= 2) {
            if (!var3) {
                return List.of(new HackUtilHelperD(var1, var2));
            } else if (!var4) {
                return List.of(new HackUtilHelperD(var1, var2));
            } else {
                return !var5 ? List.of(new HackUtilHelperD(var1, var2)) : List.of();
            }
        } else if (var6 == 1) {
            if (var3) {
                return List.of(
                        new HackUtilHelperD(new Vec3d(var1.x, var1.y, var1.z), new Vec3d(var1.x, var2.y, var1.z)),
                        new HackUtilHelperD(new Vec3d(var1.x, var2.y, var1.z), new Vec3d(var1.x, var2.y, var2.z)),
                        new HackUtilHelperD(new Vec3d(var1.x, var2.y, var2.z), new Vec3d(var1.x, var1.y, var2.z)),
                        new HackUtilHelperD(new Vec3d(var1.x, var1.y, var2.z), new Vec3d(var1.x, var1.y, var1.z)));
            } else {
                return var4
                        ? List.of(
                                new HackUtilHelperD(
                                        new Vec3d(var1.x, var1.y, var1.z), new Vec3d(var2.x, var1.y, var1.z)),
                                new HackUtilHelperD(
                                        new Vec3d(var2.x, var1.y, var1.z), new Vec3d(var2.x, var1.y, var2.z)),
                                new HackUtilHelperD(
                                        new Vec3d(var2.x, var1.y, var2.z), new Vec3d(var1.x, var1.y, var2.z)),
                                new HackUtilHelperD(
                                        new Vec3d(var1.x, var1.y, var2.z), new Vec3d(var1.x, var1.y, var1.z)))
                        : List.of(
                                new HackUtilHelperD(
                                        new Vec3d(var1.x, var1.y, var1.z), new Vec3d(var2.x, var1.y, var1.z)),
                                new HackUtilHelperD(
                                        new Vec3d(var2.x, var1.y, var1.z), new Vec3d(var2.x, var2.y, var1.z)),
                                new HackUtilHelperD(
                                        new Vec3d(var2.x, var2.y, var1.z), new Vec3d(var1.x, var2.y, var1.z)),
                                new HackUtilHelperD(
                                        new Vec3d(var1.x, var2.y, var1.z), new Vec3d(var1.x, var1.y, var1.z)));
            }
        } else {
            return List.of(
                    new HackUtilHelperD(new Vec3d(var1.x, var1.y, var1.z), new Vec3d(var2.x, var1.y, var1.z)),
                    new HackUtilHelperD(new Vec3d(var2.x, var1.y, var1.z), new Vec3d(var2.x, var1.y, var2.z)),
                    new HackUtilHelperD(new Vec3d(var2.x, var1.y, var2.z), new Vec3d(var1.x, var1.y, var2.z)),
                    new HackUtilHelperD(new Vec3d(var1.x, var1.y, var2.z), new Vec3d(var1.x, var1.y, var1.z)),
                    new HackUtilHelperD(new Vec3d(var1.x, var2.y, var1.z), new Vec3d(var2.x, var2.y, var1.z)),
                    new HackUtilHelperD(new Vec3d(var2.x, var2.y, var1.z), new Vec3d(var2.x, var2.y, var2.z)),
                    new HackUtilHelperD(new Vec3d(var2.x, var2.y, var2.z), new Vec3d(var1.x, var2.y, var2.z)),
                    new HackUtilHelperD(new Vec3d(var1.x, var2.y, var2.z), new Vec3d(var1.x, var2.y, var1.z)),
                    new HackUtilHelperD(new Vec3d(var1.x, var1.y, var1.z), new Vec3d(var1.x, var2.y, var1.z)),
                    new HackUtilHelperD(new Vec3d(var2.x, var1.y, var1.z), new Vec3d(var2.x, var2.y, var1.z)),
                    new HackUtilHelperD(new Vec3d(var2.x, var1.y, var2.z), new Vec3d(var2.x, var2.y, var2.z)),
                    new HackUtilHelperD(new Vec3d(var1.x, var1.y, var2.z), new Vec3d(var1.x, var2.y, var2.z)));
        }
    }

    public static int comparePoint(double x0, double y0, double z0, double x1, double y1, double z1) {
        int var12 = Double.compare(x0, x1);
        if (var12 != 0) {
            return var12;
        } else {
            var12 = Double.compare(y0, y1);
            return var12 != 0 ? var12 : Double.compare(z0, z1);
        }
    }

    public static List<HackUtilHelperL> boxFaces(Box box) {
        Vec3d var1 = box.getMinPos();
        Vec3d var2 = box.getMaxPos();
        boolean var3 = isFlat(var1.x, var2.x);
        boolean var4 = isFlat(var1.y, var2.y);
        boolean var5 = isFlat(var1.z, var2.z);
        int var6 = (var3 ? 1 : 0) + (var4 ? 1 : 0) + (var5 ? 1 : 0);
        if (var6 >= 2) {
            return List.of();
        } else if (var6 == 1) {
            if (var3) {
                return List.of(new HackUtilHelperL(box, Direction.WEST));
            } else {
                return var4
                        ? List.of(new HackUtilHelperL(box, Direction.DOWN))
                        : List.of(new HackUtilHelperL(box, Direction.NORTH));
            }
        } else {
            return List.of(
                    new HackUtilHelperL(box, Direction.DOWN),
                    new HackUtilHelperL(box, Direction.UP),
                    new HackUtilHelperL(box, Direction.NORTH),
                    new HackUtilHelperL(box, Direction.SOUTH),
                    new HackUtilHelperL(box, Direction.WEST),
                    new HackUtilHelperL(box, Direction.EAST));
        }
    }

    public static double normalizeZero(double value) {
        return value == 0.0 ? 0.0 : value;
    }
}
