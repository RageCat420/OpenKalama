package me.matl114.hacks.modules.inv;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.text.Text;

public class NbtTooltips extends BaseModule {
    public final KeyBindRef showHotkey;
    public final IntRef width;
    public final ModulePath Ta;
    public final ModulePath RB = makePath(Configs.l, "item-editor");
    public final FlagRef enable;
    public final IntRef formatIndent;

    private String als(String key) {
        return key.startsWith("minecraft:") ? "mc:" + key.substring("minecraft:".length()) : key;
    }

    private <T extends NbtElement> T replaceMcKey(T nbt) {
        if (nbt instanceof NbtCompound var2) {
            NbtCompound var10 = new NbtCompound();

            for (String var12 : var2.getKeys()) {
                NbtElement var6 = var2.get(var12);
                var10.put(this.als(var12), this.replaceMcKey(var6));
            }

            return (T) var10;
        } else if (!(nbt instanceof NbtList var7)) {
            if (nbt instanceof NbtString var8) {
                String var9 = var8.asString();
                return (T) NbtString.of(this.als(var9));
            } else {
                return (T) nbt;
            }
        } else {
            NbtList var3 = new NbtList();

            for (NbtElement var5 : var7) {
                var3.add(this.replaceMcKey(var5));
            }

            return (T) var3;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(RenderListener.x(), this::onTooltipsAppend);
    }

    public void onTooltipsAppend(Event<List<Text>> renderEvent) {
        if (this.enable.get() && this.showHotkey.get().d()) {
            ItemStack var2 = renderEvent.getArgs(0);
            ((List) renderEvent.b).addAll(this.getTooltipLines(var2));
        }
    }

    public NbtTooltips() {
        super("NbtTooltips");
        this.Ta = this.RB.add("nbt-tooltips");
        this.enable = this.flagBuilder(this.Ta.add("enable")).build();
        this.showHotkey = this.hotkey(this.Ta.add("show-hotkey"))
                .defaultValue(new MultiKeyBind(342))
                .build();
        this.width = this.intBuilder(this.Ta.add("width"))
                .defaultValue(360)
                .validator(Configs.d)
                .build();
        this.formatIndent = this.intBuilder(this.Ta.add("format-indent"))
                .defaultValue(0)
                .validator(Configs.d)
                .build();
        this.bindFlag(this.enable);
    }

    public NbtCompound getSimplifiedNbt(ItemStack stack) {
        NbtCompound var2 = VItem.w().k(stack, ItemStackUtils.registry());
        var2 = (NbtCompound) var2.get("components");
        var2 = var2 == null ? new NbtCompound() : var2;
        return this.replaceMcKey(var2);
    }

    public List<Text> getTooltipLines(ItemStack stack) {
        NbtCompound var2 = this.getSimplifiedNbt(stack);
        Text var3 = new NbtTextFormatter(" ".repeat(this.formatIndent.get())).apply(var2);
        return ChatUtils.splitToMultiLineText(var3, this.width.get());
    }
}
