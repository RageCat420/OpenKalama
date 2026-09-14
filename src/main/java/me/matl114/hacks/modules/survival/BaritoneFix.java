package me.matl114.hacks.modules.survival;

import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.hooks.impl.baritone.BaritoneFuture;
import me.matl114.hooks.impl.baritone.BaritoneLanding;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BaritoneFix extends BaseModule {
    public final FlagRef enableFireworkSwap;
    public final DoubleRef exp2MaxHeight;
    public final FlagRef changeLandingToLog;
    public final FlagRef baritoneConditionalPause;
    public final DoubleRef baritoneExperimentHeight1;
    public final FlagRef autoImportSeed;
    public final FlagRef fixWhenFailCalculate;
    public final FlagRef dimensionFix;
    public final DoubleRef exp2MinHeight;
    public ValueAccessor<Integer> oY;
    public ValueAccessor<String> oZ;
    public final FlagRef changeLandingToElytraFlight;
    public final FlagRef autoJumpFix;
    public final FlagRef emergencyLandingFix;
    public final ModulePath oD = makePath(Configs.o, "baritone.fix");
    public final KeyBindRef baritonePauseHotkey;
    public ValueAccessor<Integer> oX;
    public final FlagRef enableBaritoneCommandProtect;
    public static BaritoneFix INSTANCE;
    public final FlagRef fixBaritoneSimulateError;
    public final FlagRef baritoneExperiment1;
    public final FlagRef enableInventoryFireworks;
    public final FlagRef baritoneExperiment2;
    public final FlagRef disableInventoryCheck;

    public void xf(Event<String> eventCommandSay) {
        if (BaritoneHooks.getInstance().isEnabled() && ((String) eventCommandSay.b).startsWith("/")) {
            this.xa();
            String var2 = this.oZ.getValue();
            if (((String) eventCommandSay.b).startsWith(var2)
                    && BaritoneHooks.getInstance().handleCommand((String) eventCommandSay.b)) {
                eventCommandSay.cancel();
            }
        }
    }

    public void xk(Event<BaritoneFuture> event) {
        if (((BaritoneFuture) event.b).getOnCompleteFutures().isEmpty()) {
            BaritoneLanding var2 = event.getArgs(0);
            switch (var2) {
                case EMERGENCY:
                    if (this.xg("Emergency Landing")) {
                        event.cancel();
                        return;
                    }

                    if (this.xh("Emergency Landing")) {
                        event.cancel();
                        return;
                    }
                    break;
                case PATH_COMPLETE:
                    if (this.xg("Path Complete")) {
                        event.cancel();
                        return;
                    }

                    if (this.xh("Path Complete")) {
                        event.cancel();
                        return;
                    }
            }
        }
    }

    private boolean xb() {
        ElytraExtra var1 = ElytraExtra.INSTANCE;
        ItemStack var2 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (var1.afr()) {
            return true;
        } else {
            return var1.enable.get()
                    ? true
                    : var2.getItem() == Items.ELYTRA && var2.getMaxDamage() - var2.getDamage() >= this.oX.getValue();
        }
    }

    public void xe(Event<String> chatEvent) {
        if (this.enableBaritoneCommandProtect.get() && ((String) chatEvent.e()).startsWith("#")) {
            if (!BaritoneHooks.getInstance().isEnabled()) {
                this.logI18N("message.module.baritone-fix.command-without-baritone", new Object[0]);
                chatEvent.cancel();
            } else {
                this.xa();
                if (!((String) chatEvent.e()).startsWith(this.oZ.getValue())) {
                    this.logI18N(
                            "message.module.baritone-fix.command-prefix-mismatch", new Object[] {this.oZ.getValue()});
                    chatEvent.cancel();
                }
            }
        }
    }

    public BaritoneFix() {
        super("BaritoneFix");
        portConfigs(makePath(Configs.m, "baritone.fix"), this.oD);
        this.dimensionFix = this.flagBuilder(this.oD.add("dimension-fix")).build();
        this.autoImportSeed = this.flagBuilder(this.oD.add("auto-import-seed")).build();
        this.emergencyLandingFix =
                this.flagBuilder(this.oD.add("emergency-landing-fix")).build();
        this.disableInventoryCheck =
                this.flagBuilder(this.oD.add("disable-inventory-check")).build();
        this.enableInventoryFireworks =
                this.flagBuilder(this.oD.add("enable-inventory-fireworks")).build();
        this.enableFireworkSwap =
                this.flagBuilder(this.oD.add("enable-firework-swap")).build();
        this.enableBaritoneCommandProtect =
                this.flagBuilder(this.oD.add("enable-baritone-command-protect")).build();
        this.changeLandingToElytraFlight =
                this.flagBuilder(this.oD.add("change-landing-to-elytra-flight")).build();
        this.autoJumpFix = this.flagBuilder(this.oD.add("auto-jump-fix")).build();
        this.changeLandingToLog =
                this.flagBuilder(this.oD.add("change-landing-to-log")).build();
        this.baritoneConditionalPause =
                this.flagBuilder(this.oD.add("baritone-conditional-pause")).build();
        this.baritonePauseHotkey = this.builder(this.oD.add("baritone-pause-hotkey"), KeyBindRef.TYPE)
                .defaultValue(new MultiKeyBind())
                .build();
        this.fixBaritoneSimulateError =
                this.flagBuilder(this.oD.add("fix-baritone-simulate-error")).build();
        this.fixWhenFailCalculate =
                this.flagBuilder(this.oD.add("fix-when-fail-calculate")).build();
        this.baritoneExperiment1 =
                this.flagBuilder(this.oD.add("baritone-experiment-1")).build();
        this.baritoneExperimentHeight1 = this.doubleBuilder(this.oD.add("baritone-experiment-height-1"))
                .defaultValue(36.0)
                .build();
        this.baritoneExperiment2 =
                this.flagBuilder(this.oD.add("baritone-experiment-2")).build();
        this.exp2MinHeight = this.doubleBuilder(this.oD.add("exp-2-min-height"))
                .defaultValue(38.0)
                .build();
        this.exp2MaxHeight = this.doubleBuilder(this.oD.add("exp-2-max-height"))
                .defaultValue(42.0)
                .build();
        INSTANCE = this;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.Z(), this::xe);
        this.registerListener(Listener.Z(), this::xf);
        this.registerListener(Listener.aQ().c(EntityType.PLAYER), this::hZ);
        this.registerListener(BaritoneHooks.getLandingEvent(), this::xk);
    }

    public void hZ(Event<Entity> event) {
        if (event.b == mc.player
                && this.baritoneExperiment2.get()
                && mc.player.isFallFlying()
                && BaritoneHooks.getInstance().isBaritoneElytraProcessing()
                && ElytraExtra.INSTANCE.enable2.get()) {
            boolean var2 = ElytraExtra.INSTANCE.afr();
            if (var2 && mc.player.getY() < this.exp2MinHeight.get()) {
                ElytraExtra.INSTANCE.afW(true);
            } else if (!var2 && mc.player.getY() > this.exp2MaxHeight.get()) {
                ElytraExtra.INSTANCE.afY(-1);
            }
        }
    }

    public boolean xg(String situation) {
        if (this.changeLandingToLog.getValue()) {
            Debug.e("Disconnect because of Emergency situation:", situation);
            MainTasks.q();
            return true;
        } else {
            return false;
        }
    }

    private void xa() {
        if (this.oX == null || this.oY == null || this.oZ == null) {
            this.oX = BaritoneHooks.getInstance().getSetting("elytraMinimumDurability");
            this.oY = BaritoneHooks.getInstance().getSetting("elytraMinFireworksBeforeLanding");
            this.oZ = BaritoneHooks.getInstance().getSetting("prefix");
        }
    }

    public boolean xj() {
        if (this.baritoneConditionalPause.get()) {
            if (FloatingUtils.INSTANCE.DY.get()) {
                return true;
            }

            if (MovTasks.ay().enableControl.get()) {
                return true;
            }

            if (this.baritonePauseHotkey.get().d()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        acceptor.accept(this.createTitleLabel(
                BaritoneHooks.getInstance().isBaritoneAPISupported()
                        ? "widget.baritone-fix.baritone-api-support"
                        : "widget.baritone-fix.baritone-api-not-support",
                0,
                dblank,
                dx,
                dy));
        acceptor.accept(this.createTitleLabel(
                BaritoneHooks.getInstance().isBaritoneVersionSupported()
                        ? "widget.baritone-fix.baritone-support"
                        : "widget.baritone-fix.baritone-not-support",
                0,
                dblank,
                dx,
                dy));
    }

    private boolean xc() {
        int var1 = this.oY.getValue();
        ElytraExtra var2 = ElytraExtra.INSTANCE;
        return InventoryUtils.computePlayerInventory(stack -> var2.afG(stack) ? (double) stack.getCount() : null, false)
                >= var1;
    }

    public boolean xh(String situation) {
        if (this.changeLandingToElytraFlight.get()) {
            if (!MovTasks.ay().enableControl.get()) {
                this.logI18N("message.module.baritone-fix.landing-cancelled", new Object[] {situation});
                FloatingUtils.INSTANCE.SB(true);
                MovTasks.ay().enableControl.set(true);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean xd() {
        this.xa();
        return this.xb() && this.xc();
    }

    public boolean xi() {
        if (this.autoJumpFix.get() && !mc.player.isFallFlying()) {
            this.logI18N("message.module.baritone-fix.autojump-takeoff", new Object[0]);
            ElytraExtra.INSTANCE.afo();
            return true;
        } else {
            return false;
        }
    }
}
