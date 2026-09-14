package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;
import org.jetbrains.annotations.ApiStatus.Experimental;

public enum NoFall$Mode implements ConfigEnum {
    NO_BYPASS,
    LAZY_MODE,
    BYPASS_GRIM,
    @Experimental
    LAZY_BYPASS_GRIM,
    LAZY_GRIM_PLUS,
    LAZY_GRIM_PLUS_2,
    DUP_FULL_FAKE_GROUND,
    TEST,
    TEST2;

    @Override
    public String getConfigEnumType() {
        return "no_fall_bypass_mode";
    }
}
