package me.matl114.hacks.modules.task;

import java.util.Locale;
import me.matl114.managers.config.ConfigEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus.Experimental;

public enum ConnectionProxy$Type implements ConfigEnum {
    SOCKS,
    HTTP,
    @Experimental
    HTTPS;

    @Override
    public Text resultAsString() {
        return Text.literal(this.name().toLowerCase(Locale.ROOT));
    }
}
