package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import java.util.Map;
import me.matl114.managers.config.MapRef;
import net.minecraft.util.Identifier;

public record TaskSubHelperA(Map<Identifier, MapRef> snapSnot) {
    public static final Codec<TaskSubHelperA> CODEC =
            Codec.unboundedMap(Identifier.CODEC, ConfigManager.yq).xmap(TaskSubHelperA::new, TaskSubHelperA::jr);

    public Map<Identifier, MapRef> jr() {
        return this.snapSnot;
    }
}
