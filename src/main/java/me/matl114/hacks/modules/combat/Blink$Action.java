package me.matl114.hacks.modules.combat;

import me.matl114.managers.config.ConfigEnum;

public enum Blink$Action implements ConfigEnum {
    NONE,
    FLUSH,
    CLOSE;

    @Override
    public String getConfigEnumType() {
        return "blink_event_action";
    }
}
