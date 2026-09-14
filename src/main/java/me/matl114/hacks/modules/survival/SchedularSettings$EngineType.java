package me.matl114.hacks.modules.survival;

import me.matl114.managers.config.ConfigEnum;

public enum SchedularSettings$EngineType implements ConfigEnum {
    NONE,
    BARITONE;

    @Override
    public String getConfigEnumType() {
        return "schedular_engine_type";
    }
}
