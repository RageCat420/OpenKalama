package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum Flight$Mode implements ConfigEnum {
    CREATIVE,
    MOTION,
    JETPACK;

    @Override
    public String getConfigEnumType() {
        return "flight_mode";
    }
}
