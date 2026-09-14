package me.matl114.managers;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.config.ConfigEnum;

public enum Configs$BypassMode implements ConfigEnum {
    NO_BYPASS,
    BYPASS_GRIM;

    public static Configs$BypassMode getFromPreset(ModulePreset preset) {
        return switch (preset) {
            case fh, fg -> BYPASS_GRIM;
            default -> NO_BYPASS;
        };
    }

    public boolean hasAc() {
        return this != NO_BYPASS;
    }

    @Override
    public String getConfigEnumType() {
        return "bypass_mode";
    }
}
