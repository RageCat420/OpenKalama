package me.matl114.hacks.modules.interact;

import me.matl114.managers.config.ConfigEnum;

public enum Airplace$Mode implements ConfigEnum {
    VANILLA,
    GRIM_GHOST_BLOCK_WALL,
    GRIM_FAST_GHOST_BLOCK_WALL;

    @Override
    public String getConfigEnumType() {
        return "air_place_mode";
    }
}
