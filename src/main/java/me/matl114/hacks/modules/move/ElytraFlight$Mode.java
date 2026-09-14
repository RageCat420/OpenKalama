package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum ElytraFlight$Mode implements ConfigEnum {
    CONTROL,
    ROTATION;

    @Override
    public String getConfigEnumType() {
        return "elytramode";
    }
}
