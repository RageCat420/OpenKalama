package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum NoSlowDown$PacketSneakMode implements ConfigEnum {
    BAD_PACKET,
    INTERACT,
    GRIM_FALLFLYING;

    @Override
    public String getConfigEnumType() {
        return "packet_sneak_bypass_mode";
    }
}
