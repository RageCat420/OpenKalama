package me.matl114.hacks.modules.interact;

import me.matl114.accessors.hacks.KeyBindAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.option.KeyBinding;

public class AutoClick extends BaseModule {
    public final FlagRef left;
    boolean dG;
    public final FlagRef onlyWhenPause;
    public final ModulePath aD = makePath(Configs.n, "interaction-tweaks.auto-click");
    public final IntRef cooldown;
    public final FlagRef right;
    public final KeyBindRef J;
    public final FlagRef ae = this.flagBuilder(this.aD.addEnable()).build();
    boolean dH;
    TimerExecutor dF;

    private void ac(KeyBinding keyBinding) {
        keyBinding.setPressed(true);
        if (keyBinding.timesPressed <= 0) {
            keyBinding.timesPressed = 1;
        }
    }

    public void hO(Event<Void> event) {
        if (this.dG) {
            KeyBindAccess.of(mc.options.leftKey).resetKeyState();
            this.dG = false;
        }

        if (this.dH) {
            KeyBindAccess.of(mc.options.rightKey).resetKeyState();
            this.dH = false;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bd(), this::bl);
        this.registerListener(Listener.be(), this::hO);
    }

    public AutoClick() {
        super("AutoClick");
        this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable())
                .build();
        this.onlyWhenPause = this.builder(this.aD.add("only-when-pause"), Boolean.class)
                .defaultValue(true)
                .build();
        this.cooldown = this.intBuilder(this.aD.add("cooldown")).defaultValue(0).build();
        this.left = this.flagBuilder(this.aD.add("left")).build();
        this.right = this.flagBuilder(this.aD.add("right")).build();
        this.dF = new TimerExecutor();
    }

    public void bl(Event<Void> event) {
        if (this.ae.get() && this.dF.c(this.cooldown.get())) {
            if (this.onlyWhenPause.get()) {
                if (this.left.get() && mc.options.leftKey.isPressed()) {
                    this.dG = true;
                }

                if (this.right.get() && mc.options.rightKey.isPressed()) {
                    this.dH = true;
                }
            } else {
                this.dG = this.left.get();
                this.dH = this.right.get();
            }

            if (this.dG) {
                this.ac(mc.options.leftKey);
            }

            if (this.dH) {
                this.ac(mc.options.rightKey);
            }
        }
    }
}
