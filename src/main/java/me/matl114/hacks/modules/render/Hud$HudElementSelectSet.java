package me.matl114.hacks.modules.render;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.matl114.hacks.utils.config.BoundedPrimitiveFlagMap;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;

public class Hud$HudElementSelectSet extends BoundedPrimitiveFlagMap<Hud$HudElement>
        implements NBTParsable<Hud$HudElementSelectSet> {
    public static final NBTType<Hud$HudElementSelectSet> TYPE = createEnumMap(
            "HudElementSelectSet".toLowerCase(Locale.ROOT), Hud$HudElement.class, Hud$HudElementSelectSet::new);

    public Hud$HudElementSelectSet(List<Hud$HudElement> keys, Map<Hud$HudElement, Boolean> map, NBTType<Boolean> type) {
        super(keys, map, type);
    }

    public Hud$HudElementSelectSet() {
        super(Hud$HudElement.class);
    }

    @Override
    public NBTType<Hud$HudElementSelectSet> type() {
        return TYPE.cast();
    }
}
