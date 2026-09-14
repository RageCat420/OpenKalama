package me.matl114.hacks.modules.survival;

import me.matl114.managers.config.ConfigEnum;

public enum SearchControl$Mode implements ConfigEnum {
    RECT,
    CIRCLE,
    SPIRAL;

    @Override
    public String getConfigEnumType() {
        return "search_control_look_mode";
    }
}
