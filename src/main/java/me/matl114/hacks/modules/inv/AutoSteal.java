package me.matl114.hacks.modules.inv;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class AutoSteal extends BaseModule {
    public final KeyBindRef toggleKey;
    public final NBTRef<Regex> titleRegex;
    public final ModulePath zi;
    public final ModulePath wP = makePath(Configs.l, "auto-inv");
    public final FlagRef enable;
    public final NBTRef<EntrySet<Item>> zl;

    public void onInventoryTick(Event<ClientPlayerEntity> event) {
        if (mc.currentScreen instanceof HandledScreen var3 && this.enable.get()) {
            Text var10 = var3.getTitle();
            String var4 = var10 == null ? "" : ChatUtils.l(var10);
            if (this.titleRegex.get().test(var4)) {
                ScreenHandler var5 = var3.getScreenHandler();
                if (var5 != null && !(var5 instanceof CreativeScreenHandler)) {
                    for (Slot var7 : var5.slots) {
                        if (var7.inventory instanceof PlayerInventory var9) {
                            break;
                        }

                        ItemStack var11 = var7.getStack();
                        if (!var11.isEmpty() && this.zl.get().test(var11.getItem())) {
                            mc.interactionManager.clickSlot(
                                    var5.syncId, var7.getIndex(), 0, SlotActionType.QUICK_MOVE, mc.player);
                        }
                    }
                }
            }
        }
    }

    public AutoSteal() {
        super("AutoSteal");
        this.zi = this.wP.add("steal");
        this.enable = this.flagBuilder(this.zi.add("enable")).build();
        this.toggleKey = this.toggleHotkey(this.zi.add("toggle-key"), new MultiKeyBind(), this.zi.add("enable"))
                .build();
        this.titleRegex = this.builder(this.zi.add("title-regex"), Regex.class)
                .defaultValue(new Regex(".*"))
                .build();
        this.zl = this.builder(this.zi.add("item-filter"), EntrySet.<Item>parameter())
                .defaultValue(new EntrySet<Item>(new Regex(".*"), Registries.ITEM))
                .build();
        this.bindFlag(this.enable);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::onInventoryTick);
    }
}
