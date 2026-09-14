package me.matl114.events.impl;

import net.minecraft.util.math.Direction;

public record CombatPlayer(int dy, Direction direction) {
    public int dy() {
        return this.dy;
    }

    public Direction direction() {
        return this.direction;
    }
}
