package me.matl114.hacks.modules.combat;

import me.matl114.managers.config.ConfigEnum;

public enum ElytraBot$Mode implements ConfigEnum {
    FOLLOW,
    MACE_ARUA,
    SPEAR_ARUA,
    MXS;

    @Override
    public String getConfigEnumType() {
        return "elytra_bot_mode";
    }
}
