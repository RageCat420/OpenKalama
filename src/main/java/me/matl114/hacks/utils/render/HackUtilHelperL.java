package me.matl114.hacks.utils.render;

import me.matl114.utils.render.Quad;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record HackUtilHelperL(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    public double maxY() {
        return this.maxY;
    }

    public HackUtilHelperL(Vec3d min, Vec3d max) {
        this(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public double maxZ() {
        return this.maxZ;
    }

    public double minY() {
        return this.minY;
    }

    public double minZ() {
        return this.minZ;
    }

    public double minX() {
        return this.minX;
    }

    public double maxX() {
        return this.maxX;
    }

    public Quad acm() {
        if (RenderElements.isFlat(this.minX, this.maxX)) {
            double x = this.minX;
            return new Quad(
                    new Vec3d(x, this.minY, this.minZ),
                    new Vec3d(x, this.maxY, this.minZ),
                    new Vec3d(x, this.maxY, this.maxZ),
                    new Vec3d(x, this.minY, this.maxZ));
        } else if (RenderElements.isFlat(this.minY, this.maxY)) {
            double y = this.minY;
            return new Quad(
                    new Vec3d(this.minX, y, this.minZ),
                    new Vec3d(this.maxX, y, this.minZ),
                    new Vec3d(this.maxX, y, this.maxZ),
                    new Vec3d(this.minX, y, this.maxZ));
        } else if (RenderElements.isFlat(this.minZ, this.maxZ)) {
            double z = this.minZ;
            return new Quad(
                    new Vec3d(this.minX, this.minY, z),
                    new Vec3d(this.maxX, this.minY, z),
                    new Vec3d(this.maxX, this.maxY, z),
                    new Vec3d(this.minX, this.maxY, z));
        } else {
            throw new IllegalStateException("Quad is not a face");
        }
    }

    public HackUtilHelperL(Box box, Direction direction) {
        this(
                switch (direction) {
                    case WEST -> box.getMinPos().x;
                    case EAST -> box.getMaxPos().x;
                    default -> box.getMinPos().x;
                },
                switch (direction) {
                    case DOWN -> box.getMinPos().y;
                    case UP -> box.getMaxPos().y;
                    default -> box.getMinPos().y;
                },
                switch (direction) {
                    case NORTH -> box.getMinPos().z;
                    case SOUTH -> box.getMaxPos().z;
                    default -> box.getMinPos().z;
                },
                switch (direction) {
                    case WEST -> box.getMinPos().x;
                    case EAST -> box.getMaxPos().x;
                    default -> box.getMaxPos().x;
                },
                switch (direction) {
                    case DOWN -> box.getMinPos().y;
                    case UP -> box.getMaxPos().y;
                    default -> box.getMaxPos().y;
                },
                switch (direction) {
                    case NORTH -> box.getMinPos().z;
                    case SOUTH -> box.getMaxPos().z;
                    default -> box.getMaxPos().z;
                });
    }

    public HackUtilHelperL offset(Vec3d delta) {
        return new HackUtilHelperL(
                this.minX + delta.x,
                this.minY + delta.y,
                this.minZ + delta.z,
                this.maxX + delta.x,
                this.maxY + delta.y,
                this.maxZ + delta.z);
    }

    public HackUtilHelperL(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double nMinX = RenderElements.normalizeZero(Math.min(minX, maxX));
        double nMinY = RenderElements.normalizeZero(Math.min(minY, maxY));
        double nMinZ = RenderElements.normalizeZero(Math.min(minZ, maxZ));
        double nMaxX = RenderElements.normalizeZero(Math.max(minX, maxX));
        double nMaxY = RenderElements.normalizeZero(Math.max(minY, maxY));
        double nMaxZ = RenderElements.normalizeZero(Math.max(minZ, maxZ));
        this.minX = nMinX;
        this.minY = nMinY;
        this.minZ = nMinZ;
        this.maxX = nMaxX;
        this.maxY = nMaxY;
        this.maxZ = nMaxZ;
    }
}
