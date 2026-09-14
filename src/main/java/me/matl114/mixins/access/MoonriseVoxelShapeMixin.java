package me.matl114.mixins.access;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import me.matl114.accessors.moonrise.MoonriseVoxelShapeAccess;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.Debug;
import me.matl114.utils.collections.FlatBitsetUtil;
import me.matl114.utils.world.CachedShapeData;
import me.matl114.utils.world.CachedToAABBs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.OffsetDoubleList;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin({VoxelShape.class})
public abstract class MoonriseVoxelShapeMixin implements MoonriseVoxelShapeAccess {
    @Final
    @Shadow
    protected VoxelSet field_1401;

    @Unique
    private double offsetX;

    @Unique
    private double offsetY;

    @Unique
    private double offsetZ;

    @Unique
    private Box singleAABBRepresentation;

    @Unique
    private double[] rootCoordinatesX;

    @Unique
    private double[] rootCoordinatesY;

    @Unique
    private double[] rootCoordinatesZ;

    private CachedShapeData cachedShapeData;

    @Unique
    private boolean isEmpty;

    @Unique
    private CachedToAABBs cachedToAABBs;

    @Unique
    private Box cachedBounds;

    @Unique
    private Boolean isFullBlock;

    @Unique
    private Boolean occludesFullBlock;

    @Unique
    private static final int MERGED_CACHE_SIZE = 16;

    public boolean initialized = false;

    @Unique
    @Override
    public CachedToAABBs moonrise$cachedToAABBs() {
        return this.cachedToAABBs;
    }

    @Unique
    @Override
    public void moonriss$setCachedToAABBs(CachedToAABBs aabBs) {
        this.cachedToAABBs = aabBs;
    }

    @Unique
    @Override
    public final double moonrise$offsetX() {
        this.checkInitialize();
        return this.offsetX;
    }

    @Unique
    @Override
    public final double moonrise$offsetY() {
        this.checkInitialize();
        return this.offsetY;
    }

    @Unique
    @Override
    public final double moonrise$offsetZ() {
        this.checkInitialize();
        return this.offsetZ;
    }

    @Unique
    @Override
    public final Box moonrise$getSingleAABBRepresentation() {
        this.checkInitialize();
        return this.singleAABBRepresentation;
    }

    @Unique
    @Override
    public final double[] moonrise$rootCoordinatesX() {
        this.checkInitialize();
        return this.rootCoordinatesX;
    }

    @Unique
    @Override
    public final double[] moonrise$rootCoordinatesY() {
        this.checkInitialize();
        return this.rootCoordinatesY;
    }

    @Unique
    @Override
    public final double[] moonrise$rootCoordinatesZ() {
        this.checkInitialize();
        return this.rootCoordinatesZ;
    }

    @Override
    public CachedShapeData moonrise$getCachedVoxelData() {
        this.checkInitialize();
        return this.cachedShapeData;
    }

    @Shadow
    public abstract DoubleList method_1109(Axis var1);

    private static double[] extractRawArray(DoubleList list) {
        if (list == null) {
            Debug.stackTrace();
            return new double[0];
        } else if (list instanceof DoubleArrayList rawList) {
            double[] raw = rawList.elements();
            int expected = rawList.size();
            return raw.length == expected ? raw : Arrays.copyOf(raw, expected);
        } else {
            return list.toDoubleArray();
        }
    }

    private static final CachedShapeData moonrise$getOrCreateCachedShapeData(VoxelSet shape) {
        VoxelSet discreteVoxelShape = shape;
        int sizeX = shape.getXSize();
        int sizeY = shape.getYSize();
        int sizeZ = shape.getZSize();
        int maxIndex = sizeX * sizeY * sizeZ;
        int longsRequired = maxIndex + 63 >>> 6;
        boolean isEmpty = shape.isEmpty();
        long[] voxelSet;
        if (shape instanceof BitSetVoxelSet bitsetShape) {
            voxelSet = bitsetShape.storage.toLongArray();
            if (voxelSet.length < longsRequired) {
                voxelSet = Arrays.copyOf(voxelSet, longsRequired);
            }
        } else {
            voxelSet = new long[longsRequired];
            if (!isEmpty) {
                int mulX = sizeZ * sizeY;

                for (int x = 0; x < sizeX; x++) {
                    for (int y = 0; y < sizeY; y++) {
                        for (int z = 0; z < sizeZ; z++) {
                            if (discreteVoxelShape.contains(x, y, z)) {
                                int index = z + y * sizeZ + x * mulX;
                                voxelSet[index >>> 6] = voxelSet[index >>> 6] | 1L << index;
                            }
                        }
                    }
                }
            }
        }

        boolean hasSingleAABB =
                sizeX == 1 && sizeY == 1 && sizeZ == 1 && !isEmpty && discreteVoxelShape.contains(0, 0, 0);
        int minFullX = discreteVoxelShape.getMin(Axis.X);
        int minFullY = discreteVoxelShape.getMin(Axis.Y);
        int minFullZ = discreteVoxelShape.getMin(Axis.Z);
        int maxFullX = discreteVoxelShape.getMax(Axis.X);
        int maxFullY = discreteVoxelShape.getMax(Axis.Y);
        int maxFullZ = discreteVoxelShape.getMax(Axis.Z);
        return new CachedShapeData(
                sizeX,
                sizeY,
                sizeZ,
                voxelSet,
                minFullX,
                minFullY,
                minFullZ,
                maxFullX,
                maxFullY,
                maxFullZ,
                isEmpty,
                hasSingleAABB);
    }

    public void checkInitialize() {
        if (!this.initialized) {
            this.moonrise$initCache();
        }
    }

    @Override
    public final void moonrise$initCache() {
        this.initialized = true;
        this.cachedShapeData = moonrise$getOrCreateCachedShapeData(this.field_1401);
        this.isEmpty = this.cachedShapeData.isEmpty();
        DoubleList xList = this.method_1109(Axis.X);
        DoubleList yList = this.method_1109(Axis.Y);
        DoubleList zList = this.method_1109(Axis.Z);
        if (xList instanceof OffsetDoubleList offsetDoubleList) {
            if (offsetDoubleList.oldList == null) {
                Debug.e("check", offsetDoubleList, offsetDoubleList.getClass(), offsetDoubleList.offset);
            }

            this.offsetX = offsetDoubleList.offset;
            this.rootCoordinatesX = extractRawArray(offsetDoubleList.oldList);
        } else {
            this.rootCoordinatesX = extractRawArray(xList);
        }

        if (yList instanceof OffsetDoubleList offsetDoubleList) {
            if (offsetDoubleList.oldList == null) {
                Debug.e("check", offsetDoubleList, offsetDoubleList.getClass(), offsetDoubleList.offset);
            }

            this.offsetY = offsetDoubleList.offset;
            this.rootCoordinatesY = extractRawArray(offsetDoubleList.oldList);
        } else {
            this.rootCoordinatesY = extractRawArray(yList);
        }

        if (zList instanceof OffsetDoubleList offsetDoubleList) {
            if (offsetDoubleList.oldList == null) {
                Debug.e("check", offsetDoubleList, offsetDoubleList.getClass(), offsetDoubleList.offset);
            }

            this.offsetZ = offsetDoubleList.offset;
            this.rootCoordinatesZ = extractRawArray(offsetDoubleList.oldList);
        } else {
            this.rootCoordinatesZ = extractRawArray(zList);
        }

        if (this.cachedShapeData.hasSingleAABB()) {
            this.singleAABBRepresentation = new Box(
                    this.rootCoordinatesX[0] + this.offsetX,
                    this.rootCoordinatesY[0] + this.offsetY,
                    this.rootCoordinatesZ[0] + this.offsetZ,
                    this.rootCoordinatesX[1] + this.offsetX,
                    this.rootCoordinatesY[1] + this.offsetY,
                    this.rootCoordinatesZ[1] + this.offsetZ);
            this.cachedBounds = this.singleAABBRepresentation;
        }
    }

    private boolean computeFullBlock() {
        Boolean ret;
        if (this.isEmpty) {
            ret = Boolean.FALSE;
        } else if ((VoxelShape) (Object) this == VoxelShapes.fullCube()) {
            ret = Boolean.TRUE;
        } else {
            Box singleAABB = this.singleAABBRepresentation;
            if (singleAABB == null) {
                CachedShapeData shapeData = this.cachedShapeData;
                int sMinX = shapeData.sizeY();
                int sMinY = shapeData.minFullX();
                int sMinZ = shapeData.minFullY();
                int sMaxX = shapeData.sizeZ();
                int sMaxY = shapeData.sizeY();
                int sMaxZ = shapeData.sizeX();
                if (Math.abs(this.rootCoordinatesX[sMinX] + this.offsetX) <= 1.0E-7
                        && Math.abs(this.rootCoordinatesY[sMinY] + this.offsetY) <= 1.0E-7
                        && Math.abs(this.rootCoordinatesZ[sMinZ] + this.offsetZ) <= 1.0E-7
                        && Math.abs(1.0 - (this.rootCoordinatesX[sMaxX] + this.offsetX)) <= 1.0E-7
                        && Math.abs(1.0 - (this.rootCoordinatesY[sMaxY] + this.offsetY)) <= 1.0E-7
                        && Math.abs(1.0 - (this.rootCoordinatesZ[sMaxZ] + this.offsetZ)) <= 1.0E-7) {
                    int sizeY = shapeData.minFullY();
                    int sizeZ = shapeData.sizeX();
                    long[] bitset = shapeData.voxelSet();
                    ret = Boolean.TRUE;

                    label56:
                    for (int x = sMinX; x < sMaxX; x++) {
                        for (int y = sMinY; y < sMaxY; y++) {
                            int baseIndex = y * sizeZ + x * sizeZ * sizeY;
                            if (!FlatBitsetUtil.isRangeSet(bitset, baseIndex + sMinZ, baseIndex + sMaxZ)) {
                                ret = Boolean.FALSE;
                                break label56;
                            }
                        }
                    }
                } else {
                    ret = Boolean.FALSE;
                }
            } else {
                ret = Math.abs(singleAABB.minX) <= 1.0E-7
                        && Math.abs(singleAABB.minY) <= 1.0E-7
                        && Math.abs(singleAABB.minZ) <= 1.0E-7
                        && Math.abs(1.0 - singleAABB.maxX) <= 1.0E-7
                        && Math.abs(1.0 - singleAABB.maxY) <= 1.0E-7
                        && Math.abs(1.0 - singleAABB.maxZ) <= 1.0E-7;
            }
        }

        this.isFullBlock = ret;
        return ret;
    }

    @Override
    public final boolean moonrise$isFullBlock() {
        this.checkInitialize();
        Boolean ret = this.isFullBlock;
        return ret != null ? ret : this.computeFullBlock();
    }

    private List<Box> toAabbsUncached() {
        List<Box> ret = new ArrayList<>();
        if (this.singleAABBRepresentation != null) {
            ret.add(this.singleAABBRepresentation);
        } else {
            double[] coordsX = this.rootCoordinatesX;
            double[] coordsY = this.rootCoordinatesY;
            double[] coordsZ = this.rootCoordinatesZ;
            double offX = this.offsetX;
            double offY = this.offsetY;
            double offZ = this.offsetZ;
            this.field_1401.forEachBox(
                    (minX, minY, minZ, maxX, maxY, maxZ) -> ret.add(new Box(
                            coordsX[minX] + offX,
                            coordsY[minY] + offY,
                            coordsZ[minZ] + offZ,
                            coordsX[maxX] + offX,
                            coordsY[maxY] + offY,
                            coordsZ[maxZ] + offZ)),
                    true);
        }

        this.cachedToAABBs = new CachedToAABBs(ret, false, 0.0, 0.0, 0.0);
        return ret;
    }

    @Nullable
    private static Direction getDirection(
            Box box,
            Vec3d intersectingVector,
            double[] traceDistanceResult,
            @Nullable Direction approachDirection,
            double deltaX,
            double deltaY,
            double deltaZ) {
        if (deltaX > 1.0E-7) {
            approachDirection = clipPoint(
                    traceDistanceResult,
                    approachDirection,
                    deltaX,
                    deltaY,
                    deltaZ,
                    box.minX,
                    box.minY,
                    box.maxY,
                    box.minZ,
                    box.maxZ,
                    Direction.WEST,
                    intersectingVector.x,
                    intersectingVector.y,
                    intersectingVector.z);
        } else if (deltaX < -1.0E-7) {
            approachDirection = clipPoint(
                    traceDistanceResult,
                    approachDirection,
                    deltaX,
                    deltaY,
                    deltaZ,
                    box.maxX,
                    box.minY,
                    box.maxY,
                    box.minZ,
                    box.maxZ,
                    Direction.EAST,
                    intersectingVector.x,
                    intersectingVector.y,
                    intersectingVector.z);
        }

        if (deltaY > 1.0E-7) {
            approachDirection = clipPoint(
                    traceDistanceResult,
                    approachDirection,
                    deltaY,
                    deltaZ,
                    deltaX,
                    box.minY,
                    box.minZ,
                    box.maxZ,
                    box.minX,
                    box.maxX,
                    Direction.DOWN,
                    intersectingVector.y,
                    intersectingVector.z,
                    intersectingVector.x);
        } else if (deltaY < -1.0E-7) {
            approachDirection = clipPoint(
                    traceDistanceResult,
                    approachDirection,
                    deltaY,
                    deltaZ,
                    deltaX,
                    box.maxY,
                    box.minZ,
                    box.maxZ,
                    box.minX,
                    box.maxX,
                    Direction.UP,
                    intersectingVector.y,
                    intersectingVector.z,
                    intersectingVector.x);
        }

        if (deltaZ > 1.0E-7) {
            approachDirection = clipPoint(
                    traceDistanceResult,
                    approachDirection,
                    deltaZ,
                    deltaX,
                    deltaY,
                    box.minZ,
                    box.minX,
                    box.maxX,
                    box.minY,
                    box.maxY,
                    Direction.NORTH,
                    intersectingVector.z,
                    intersectingVector.x,
                    intersectingVector.y);
        } else if (deltaZ < -1.0E-7) {
            approachDirection = clipPoint(
                    traceDistanceResult,
                    approachDirection,
                    deltaZ,
                    deltaX,
                    deltaY,
                    box.maxZ,
                    box.minX,
                    box.maxX,
                    box.minY,
                    box.maxY,
                    Direction.SOUTH,
                    intersectingVector.z,
                    intersectingVector.x,
                    intersectingVector.y);
        }

        return approachDirection;
    }

    @Nullable
    private static Direction clipPoint(
            double[] traceDistanceResult,
            @Nullable Direction approachDirection,
            double deltaX,
            double deltaY,
            double deltaZ,
            double begin,
            double minX,
            double maxX,
            double minZ,
            double maxZ,
            Direction resultDirection,
            double startX,
            double startY,
            double startZ) {
        double d = (begin - startX) / deltaX;
        double e = startY + d * deltaY;
        double f = startZ + d * deltaZ;
        if (0.0 < d
                && d < traceDistanceResult[0]
                && minX - 1.0E-7 < e
                && e < maxX + 1.0E-7
                && minZ - 1.0E-7 < f
                && f < maxZ + 1.0E-7) {
            traceDistanceResult[0] = d;
            return resultDirection;
        } else {
            return approachDirection;
        }
    }

    private static BlockHitResult raycast(Box aabb, Vec3d from, Vec3d to, BlockPos offset) {
        double[] minDistanceArr = new double[] {1.0};
        double diffX = to.x - from.x;
        double diffY = to.y - from.y;
        double diffZ = to.z - from.z;
        Direction direction = getDirection(aabb.offset(offset), from, minDistanceArr, null, diffX, diffY, diffZ);
        if (direction == null) {
            return null;
        } else {
            double minDistance = minDistanceArr[0];
            return new BlockHitResult(
                    from.add(minDistance * diffX, minDistance * diffY, minDistance * diffZ), direction, offset, false);
        }
    }

    @Overwrite
    public boolean method_1110() {
        this.checkInitialize();
        return this.isEmpty;
    }

    @Overwrite
    public double method_1091(Axis axis) {
        this.checkInitialize();
        return CollisionUtil.aa(this, axis);
    }

    @Overwrite
    public double method_1105(Axis axis) {
        this.checkInitialize();
        return CollisionUtil.calculateAxisMin(this, axis);
    }

    @Overwrite
    public Box method_1107() {
        this.checkInitialize();
        if (this.isEmpty) {
            throw new UnsupportedOperationException("No bounds for empty shape.");
        } else {
            Box cached = this.cachedBounds;
            if (cached != null) {
                return cached;
            } else {
                CachedShapeData shapeData = this.cachedShapeData;
                double[] coordsX = this.rootCoordinatesX;
                double[] coordsY = this.rootCoordinatesY;
                double[] coordsZ = this.rootCoordinatesZ;
                double offX = this.offsetX;
                double offY = this.offsetY;
                double offZ = this.offsetZ;
                cached = new Box(
                        coordsX[shapeData.minFullX()] + offX,
                        coordsY[shapeData.minFullY()] + offY,
                        coordsZ[shapeData.minFullZ()] + offZ,
                        coordsX[shapeData.maxFullX()] + offX,
                        coordsY[shapeData.maxFullY()] + offY,
                        coordsZ[shapeData.maxFullZ()] + offZ);
                this.cachedBounds = cached;
                return cached;
            }
        }
    }

    private static DoubleList offsetList(DoubleList src, double by) {
        return src instanceof OffsetDoubleList offsetDoubleList
                ? new OffsetDoubleList(offsetDoubleList.oldList, by + offsetDoubleList.offset)
                : new OffsetDoubleList(src, by);
    }

    @Overwrite
    public VoxelShape method_1096(double x, double y, double z) {
        this.checkInitialize();
        if (this.isEmpty) {
            return VoxelShapes.empty();
        } else {
            ArrayVoxelShape ret = new ArrayVoxelShape(
                    this.field_1401,
                    offsetList(this.method_1109(Axis.X), x),
                    offsetList(this.method_1109(Axis.Y), y),
                    offsetList(this.method_1109(Axis.Z), z));
            CachedToAABBs cachedToAABBs = this.cachedToAABBs;
            if (cachedToAABBs != null) {
                MoonriseVoxelShapeAccess.of(ret)
                        .moonriss$setCachedToAABBs(CachedToAABBs.offset(cachedToAABBs, x, y, z));
            }

            return ret;
        }
    }

    @Overwrite
    public VoxelShape method_1097() {
        this.checkInitialize();
        if (this.isEmpty) {
            return VoxelShapes.empty();
        } else if (this.singleAABBRepresentation != null) {
            return this.moonrise$isFullBlock() ? VoxelShapes.fullCube() : (VoxelShape) (Object) this;
        } else {
            List<Box> aabbs = this.method_1090();
            if (aabbs.size() == 1) {
                Box singleAABB = aabbs.get(0);
                VoxelShape ret = VoxelShapes.cuboid(singleAABB);
                if (MoonriseVoxelShapeAccess.of(ret).moonrise$cachedToAABBs() == null) {
                    MoonriseVoxelShapeAccess.of(ret).moonriss$setCachedToAABBs(this.cachedToAABBs);
                }

                return ret;
            } else {
                VoxelShape[] tmp = new VoxelShape[aabbs.size()];
                int i = 0;

                for (int len = aabbs.size(); i < len; i++) {
                    tmp[i] = VoxelShapes.cuboid(aabbs.get(i));
                }

                i = aabbs.size();

                while (i > 1) {
                    int newSize = 0;

                    for (int ix = 0; ix < i; ix += 2) {
                        int next = ix + 1;
                        if (next >= i) {
                            tmp[newSize++] = tmp[ix];
                            break;
                        }

                        VoxelShape first = tmp[ix];
                        VoxelShape second = tmp[next];
                        tmp[newSize++] = VoxelShapes.combine(first, second, BooleanBiFunction.OR);
                    }

                    i = newSize;
                }

                VoxelShape ret = tmp[0];
                if (MoonriseVoxelShapeAccess.of(ret).moonrise$cachedToAABBs() == null) {
                    MoonriseVoxelShapeAccess.of(ret).moonriss$setCachedToAABBs(this.cachedToAABBs);
                }

                return ret;
            }
        }
    }

    @Overwrite
    public List<Box> method_1090() {
        this.checkInitialize();
        CachedToAABBs cachedToAABBs = this.cachedToAABBs;
        if (cachedToAABBs != null) {
            if (!cachedToAABBs.isOffset()) {
                return cachedToAABBs.aabbs();
            } else {
                cachedToAABBs = cachedToAABBs.removeOffset();
                this.cachedToAABBs = cachedToAABBs;
                return cachedToAABBs.aabbs();
            }
        } else {
            return this.toAabbsUncached();
        }
    }

    @Overwrite
    public BlockHitResult method_1092(Vec3d from, Vec3d to, BlockPos offset) {
        this.checkInitialize();
        if (this.isEmpty) {
            return null;
        } else {
            Vec3d directionOpposite = to.subtract(from);
            if (directionOpposite.lengthSquared() < 1.0E-7) {
                return null;
            } else {
                Vec3d fromBehind = from.add(directionOpposite.multiply(0.001));
                double fromBehindOffsetX = fromBehind.x - offset.getX();
                double fromBehindOffsetY = fromBehind.y - offset.getY();
                double fromBehindOffsetZ = fromBehind.z - offset.getZ();
                Box singleAABB = this.singleAABBRepresentation;
                if (singleAABB != null) {
                    return singleAABB.contains(fromBehindOffsetX, fromBehindOffsetY, fromBehindOffsetZ)
                            ? new BlockHitResult(
                                    fromBehind,
                                    Direction.getFacing(directionOpposite.x, directionOpposite.y, directionOpposite.z)
                                            .getOpposite(),
                                    offset,
                                    true)
                            : raycast(singleAABB, from, to, offset);
                } else {
                    return CollisionUtil.r(
                                    (VoxelShape) (Object) this, fromBehindOffsetX, fromBehindOffsetY, fromBehindOffsetZ)
                            ? new BlockHitResult(
                                    fromBehind,
                                    Direction.getFacing(directionOpposite.x, directionOpposite.y, directionOpposite.z)
                                            .getOpposite(),
                                    offset,
                                    true)
                            : Box.raycast(this.method_1090(), from, to, offset);
                }
            }
        }
    }

    @Overwrite
    public Optional<Vec3d> method_33661(Vec3d point) {
        this.checkInitialize();
        if (this.isEmpty) {
            return Optional.empty();
        } else {
            Vec3d ret = null;
            double retDistance = Double.MAX_VALUE;
            List<Box> aabbs = this.method_1090();
            int i = 0;

            for (int len = aabbs.size(); i < len; i++) {
                Box aabb = aabbs.get(i);
                double x = MathHelper.clamp(point.x, aabb.minX, aabb.maxX);
                double y = MathHelper.clamp(point.y, aabb.minY, aabb.maxY);
                double z = MathHelper.clamp(point.z, aabb.minZ, aabb.maxZ);
                double dist = point.squaredDistanceTo(x, y, z);
                if (dist < retDistance) {
                    ret = new Vec3d(x, y, z);
                    retDistance = dist;
                }
            }

            return Optional.ofNullable(ret);
        }
    }

    @Overwrite
    public double method_1108(Axis axis, Box source, double source_move) {
        this.checkInitialize();
        if (this.isEmpty) {
            return source_move;
        } else if (Math.abs(source_move) < 1.0E-7) {
            return 0.0;
        } else {
            VoxelShape voxelShape = (VoxelShape) (Object) this;
            return CollisionUtil.calculateAxisCollide(voxelShape, axis, source, source_move);
        }
    }
}
