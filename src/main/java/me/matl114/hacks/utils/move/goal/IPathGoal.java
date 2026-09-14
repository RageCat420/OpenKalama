package me.matl114.hacks.utils.move.goal;

import net.minecraft.util.math.Vec3d;

public sealed interface IPathGoal
        permits GoalBlockPos, GoalDirection, GoalDynamic, GoalFollow, GoalList, GoalNear, GoalNearBlockPos {
    boolean isInGoal(Vec3d var1);

    Vec3d sample();
}
