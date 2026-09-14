package me.matl114.hooks.impl.baritone;

import baritone.api.pathing.goals.Goal;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;
import net.minecraft.util.math.BlockPos;

public class GoalYawDirection implements Goal {
    public final int x;
    public final int y;
    public final int z;
    public final float yaw;
    public final double dirX;
    public final double dirZ;

    public GoalYawDirection(BlockPos origin, float yaw) {
        this.x = origin.getX();
        this.y = origin.getY();
        this.z = origin.getZ();
        this.yaw = yaw;
        double rad = Math.toRadians(yaw);
        this.dirX = -Math.sin(rad);
        this.dirZ = Math.cos(rad);
        if (Math.abs(this.dirX) < 1.0E-9 && Math.abs(this.dirZ) < 1.0E-9) {
            throw new IllegalArgumentException("Invalid yaw " + yaw);
        }
    }

    public boolean isInGoal(int x, int y, int z) {
        return false;
    }

    public double heuristic(int x, int y, int z) {
        double relX = x - this.x;
        double relY = y - this.y;
        double relZ = z - this.z;
        double forward = relX * this.dirX + relZ * this.dirZ;
        double lateralX = relX - forward * this.dirX;
        double lateralZ = relZ - forward * this.dirZ;
        double lateral = Math.abs(lateralX) + Math.abs(lateralZ);
        double vertical = Math.abs(relY);
        double heuristic = 0.0;
        heuristic -= forward * 100.0;
        heuristic += lateral * 1000.0;
        return heuristic + vertical * 1000.0;
    }

    public double heuristic() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return !(o instanceof GoalYawDirection other)
                    ? false
                    : this.x == other.x
                            && this.y == other.y
                            && this.z == other.z
                            && Float.compare(other.yaw, this.yaw) == 0;
        }
    }

    @Override
    public int hashCode() {
        int hash = (int) BetterBlockPos.longHash(this.x, this.y, this.z);
        return 31 * hash + Float.hashCode(this.yaw);
    }

    @Override
    public String toString() {
        return String.format(
                "GoalYawDirection{x=%s,y=%s,z=%s,yaw=%s}",
                SettingsUtil.maybeCensor(this.x),
                SettingsUtil.maybeCensor(this.y),
                SettingsUtil.maybeCensor(this.z),
                this.yaw);
    }
}
