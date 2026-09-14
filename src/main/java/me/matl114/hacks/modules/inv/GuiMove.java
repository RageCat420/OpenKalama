package me.matl114.hacks.modules.inv;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KeyboardAction;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.managers.input.SimpleInputManager;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;

public class GuiMove extends BaseModule {
    public final FlagRef allGuiMove;
    public final FlagRef noShiftInChest;
    public final KeyBindRef J;
    public KeyBinding[] qj;
    public ModulePath iE = makePath(Configs.l, "inventory.gui-move");
    public KeyBinding[] qk;
    public final FlagRef ae = this.flagBuilder(this.iE.addEnable()).build();

    public boolean handle(KeyBinding keyBinding, int keyCode, int action) {
        if (keyBinding.boundKey.getCode() != keyCode) {
            return false;
        } else if (action == 1) {
            keyBinding.setPressed(true);
            return true;
        } else if (action == 0) {
            keyBinding.setPressed(false);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkCustomWidget(DrawableWidget drawableWidget) {
        DrawableWidget var2 = WidgetUtils.a(drawableWidget);
        return var2 != null && WidgetUtils.isInputWidget(var2);
    }

    public KeyBinding[] getBindings() {
        this.initBinding();
        return this.noShiftInChest.get() && mc.currentScreen instanceof HandledScreen ? this.qk : this.qj;
    }

    public GuiMove() {
        super("GuiMove");
        this.J = this.toggleHotkey(this.iE.addHotkey(), new MultiKeyBind(), this.iE.addEnable())
                .build();
        this.allGuiMove = this.flagBuilder(this.iE.add("all-gui-move")).build();
        this.noShiftInChest = this.builder(this.iE.add("no-shift-in-chest"), FlagRef.TYPE)
                .defaultValue(true)
                .build();
        this.bindFlag(this.ae);
    }

    public void yF(Event<KeyboardAction> eventInput) {
        if (!checkNull()) {
            if (this.ae.get()) {
                if (this.skip()) {
                    return;
                }

                int var2 = ((KeyboardAction) eventInput.b).keyCode();
                int var3 = ((KeyboardAction) eventInput.b).action();

                for (KeyBinding var7 : this.getBindings()) {
                    if (this.handle(var7, var2, var3)) {}
                }
            }
        }
    }

    public void onPostSetScreen(Event<Screen> event) {
        if (!checkNull()) {
            if (this.ae.get() && event.b != null) {
                this.initBinding();

                for (KeyBinding var5 : this.getBindings()) {
                    var5.setPressed(SimpleInputManager.h().isKeyPressed(var5.boundKey.getCode()));
                }
            }
        }
    }

    private void initBinding() {
        if (this.qj == null || this.qk == null) {
            this.qj = new KeyBinding[] {
                mc.options.forwardKey,
                mc.options.backKey,
                mc.options.leftKey,
                mc.options.rightKey,
                mc.options.jumpKey,
                mc.options.sneakKey,
                mc.options.sprintKey
            };
            this.qk = new KeyBinding[] {
                mc.options.forwardKey,
                mc.options.backKey,
                mc.options.leftKey,
                mc.options.rightKey,
                mc.options.jumpKey,
                mc.options.sprintKey
            };
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bp(), this::yF);
        this.registerListener(Listener.ag(), this::onPostSetScreen);
    }

    public boolean skip() {
        if (mc.currentScreen == null
                || mc.currentScreen instanceof CreativeInventoryScreen
                || mc.currentScreen instanceof ChatScreen
                || mc.currentScreen instanceof SignEditScreen
                || mc.currentScreen instanceof AnvilScreen
                || mc.currentScreen instanceof CommandBlockScreen
                || mc.currentScreen instanceof StructureBlockScreen
                || mc.currentScreen.getFocused() instanceof TextFieldWidget
                || mc.currentScreen.getFocused() instanceof DrawableWidget var2 && this.checkCustomWidget(var2)) {
            return true;
        } else {
            return this.allGuiMove.get() ? false : !(mc.currentScreen instanceof HandledScreen);
        }
    }
}
