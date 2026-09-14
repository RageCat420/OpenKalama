package me.matl114.utils.commands.params.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

enum KalamaHelperHelperD {
    mb,
    me,
    mc,
    md;

    @Nullable
    public static KalamaHelperHelperD tM(String raw) {
        return switch (raw) {
            case "arbitrary" -> mb;
            case "nearest" -> mc;
            case "furthest" -> md;
            case "random" -> me;
            default -> null;
        };
    }

    public void tN(Vec3d origin, List<Entity> entities) {
        switch (this) {
            case mb:
            default:
                break;
            case me:
                entities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(origin)));
                break;
            case mc:
                entities.sort(Comparator.<Entity>comparingDouble(entity -> entity.squaredDistanceTo(origin))
                        .reversed());
                break;
            case md:
                Collections.shuffle(entities);
        }
    }
}
