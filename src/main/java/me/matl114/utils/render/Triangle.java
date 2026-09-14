package me.matl114.utils.render;

import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class Triangle implements Iterable<Vec3d> {
    Vec3d i;
    Vec3d h;
    Vec3d j;

    public Triangle(Vec3d vec3d1, Vec3d vec3d2, Vec3d vec3d3) {
        this.h = vec3d1;
        this.i = vec3d2;
        this.j = vec3d3;
    }

    @NotNull
    @Override
    public Iterator<Vec3d> iterator() {
        return Stream.of(this.h, this.i, this.j).iterator();
    }

    public Vec3d get(int index) {
        return switch (index % 3) {
            case 0 -> this.h;
            case 1 -> this.i;
            case 2 -> this.j;
            default -> throw new IndexOutOfBoundsException();
        };
    }
}
