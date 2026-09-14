package me.matl114.hacks.modules.combat;

import me.matl114.managers.config.ConfigEnum;

public enum Criticals$Mode implements ConfigEnum {
    PACKET,
    FREEZE,
    GRIM_GROUND_SIMULATION,
    GRIM_WALL,
    TEST;

    @Override
    public String getConfigEnumType() {
        return "critical_mode";
    }
}
