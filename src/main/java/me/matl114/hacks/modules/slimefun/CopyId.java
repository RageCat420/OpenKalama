package me.matl114.hacks.modules.slimefun;

import java.util.Locale;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.collections.Point;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class CopyId extends BaseModule {
    public KeyBindRef el;
    public final ModulePath fo = makePath(Configs.p, "slimefun-settings");

    public boolean copySfIdInHand() {
        MinecraftClient var1 = MinecraftClient.getInstance();
        ClientPlayerEntity var2 = MinecraftClient.getInstance().player;
        if (var2 != null && var1 != null) {
            ItemStack var3 = null;
            if (var1.currentScreen instanceof HandledScreen var5) {
                Point var7 = ScreenUtils.getMouseCoord(var1);
                Slot var6 = HandledScreenAccess.of(var5).reallyGetSlotAt(var7.a, var7.b);
                if (var6 != null) {
                    var3 = var6.getStack();
                }
            } else {
                var3 = var2.getStackInHand(Hand.MAIN_HAND);
            }

            if (var3 != null) {
                String var9 = ItemStackUtils.aa(var3);
                if (var9 != null) {
                    var1.keyboard.setClipboard(var9);
                    Debug.b(Text.literal("成功将Slimefun ID拷贝至你的剪切板和公共参数! 值: ")
                            .formatted(Formatting.GREEN)
                            .append(Text.literal(var9).formatted(Formatting.WHITE)));
                    return true;
                } else {
                    String var8 =
                            Registries.ITEM.getId(var3.getItem()).getPath().toUpperCase(Locale.ROOT);
                    var1.keyboard.setClipboard(var8);
                    Debug.b(Text.literal("该物品不是Slimefun物品,拷贝原版ID!")
                            .formatted(Formatting.GREEN)
                            .append(Text.literal(var8).formatted(Formatting.WHITE)));
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public CopyId() {
        super("CopyId");
        this.el = this.hotkey(Configs.p, this.fo.add("slimefunid-copy").toPath())
                .defaultValue(new MultiKeyBind(341, 67, -100))
                .registerHotkey(HotKeyUtils.e(this::copySfIdInHand))
                .build();
    }
}
