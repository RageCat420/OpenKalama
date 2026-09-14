package me.matl114.hacks.modules.inv;

import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.collections.Point;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FastInv extends BaseModule {
    public final KeyBindRef mk;
    public static final String mn = "take-all";
    public final FlagRef applyDrop;
    public final ModulePath mg = makePath(Configs.l, "fastinv");
    public final FlagRef applyShift;
    public final KeyBindRef ml;
    public final FlagRef leftOne;
    public final FlagRef ae = this.flagBuilder(this.mg.add("fast-inv")).build();
    public static final String mo = "save-all";
    public final KeyBindRef mm;

    public void tV() {
        InvTasks.takeAllContainerItem();
    }

    public void tU() {
        InvTasks.d();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        TaskManagers.c().register("button-toggle.fast-inv", this.ae);
        TaskManagers.c().register("button-toggle.left-one", this.leftOne);
        TaskManagers.b().register("button-task.take-all", this::tU);
        TaskManagers.b().register("button-task.save-all", this::tV);
    }

    public boolean onShiftAction() {
        ClientPlayerEntity var1 = mc.player;
        if (var1 == null) {
            return false;
        } else {
            Screen var2 = InvTasks.getCurrentServerScreen(var1);
            if (var2 instanceof HandledScreen var3 && !(var2 instanceof InventoryScreen)) {
                ScreenHandler var4 = var3.getScreenHandler();
                Point var5 = ScreenUtils.getMouseCoord(mc);
                Slot var6 = HandledScreenAccess.of(var3).reallyGetSlotAt(var5.a, var5.b);
                if (this.ae.get() && this.applyShift.get()) {
                    InvTasks.h(var3, var6);
                } else if (this.leftOne.get() && var6 != null) {
                    int var7 = var4.slots.indexOf(var6);
                    if (var7 >= 0) {
                        InvTasks.quickMoveSlot(var3, var7);
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public FastInv() {
        super("FastInv");
        this.leftOne = this.flagBuilder(this.mg.add("left-one")).build();
        this.applyDrop = this.flagBuilder(this.mg.add("apply-drop")).build();
        this.applyShift = this.flagBuilder(this.mg.add("apply-shift")).build();
        this.mk = this.hotkey(Configs.l, this.mg.add("fast-mov").toPath(), new MultiKeyBind(340, -100))
                .registerHotkey(HotKeyUtils.e(this::onShiftAction))
                .build();
        this.ml = this.hotkey(Configs.l, this.mg.add("fast-drop").toPath(), new MultiKeyBind(340, 81))
                .registerHotkey(HotKeyUtils.e(this::onDropAction))
                .build();
        this.mm = this.hotkey(Configs.l, this.mg.add("quick-drop").toPath(), new MultiKeyBind(340, 81, -100))
                .registerHotkey(HotKeyUtils.e(this::onQuickDropAction))
                .build();
        this.bindFlag(this.ae);
    }

    public boolean onQuickDropAction() {
        if (mc.player == null) {
            return false;
        } else {
            return this.ae.get() && this.applyDrop.get() ? InvTasks.dropAllCursorStack() : false;
        }
    }

    public boolean onDropAction() {
        if (mc.player == null) {
            return false;
        } else {
            if (this.ae.get() && this.applyDrop.get()) {
                ClientPlayerEntity var1 = mc.player;
                if (InvTasks.getCurrentServerScreen(var1) instanceof HandledScreen var3) {
                    Point var4 = ScreenUtils.getMouseCoord(mc);
                    Slot var5 = HandledScreenAccess.of(var3).reallyGetSlotAt(var4.a, var4.b);
                    if (InvTasks.quickDropSlotItem(var3, var5)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
