package me.matl114.hacks.modules.inv;

import java.util.Locale;
import me.matl114.utils.commands.params.impl.StringArgumentResult;
import net.minecraft.text.Text;

public enum InvSubHelperS implements StringArgumentResult {
    Is,
    Iv,
    It,
    Iu;
    // $VF: synthetic field

    @Override
    public Text resultAsString() {
        return Text.translatable(
                "widget.kit-manager.open-kit-list.rule.type." + this.name().toLowerCase(Locale.ROOT));
    }
}
