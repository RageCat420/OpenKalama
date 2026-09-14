package me.matl114.hacks.utils.move.goal;

import java.util.List;
import net.minecraft.util.math.Vec3d;

public record GoalList(List<IPathGoal> goals) implements IPathGoal {
    public List<IPathGoal> AF() {
        return this.goals;
    }

    @Override
    public Vec3d sample() {
        return this.goals.stream().map(IPathGoal::sample).findAny().orElse(null);
    }

    @Override
    public boolean isInGoal(Vec3d playerPos) {
        return this.goals.stream().anyMatch(goal -> goal.isInGoal(playerPos));
    }
}
