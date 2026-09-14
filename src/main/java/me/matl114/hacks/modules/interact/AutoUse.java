package me.matl114.hacks.modules.interact;

import me.matl114.accessors.access.ClientAccess;
import me.matl114.accessors.hacks.KeyBindAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;

public class AutoUse extends BaseModule {
    public final FlagRef log;
    public final DoubleRef enemyNearDistance;
    public final NBTRef<EntrySet<Item>> cN;
    public ModulePath iE = makePath(Configs.n, "interaction-tweaks.auto-use");
    public final FlagRef onlyWhenEnemyNear;
    public final FlagRef useFood;
    public final FlagRef ae = this.flagBuilder(this.iE.addEnable()).build();
    public final KeyBindRef J = this.toggleHotkey(this.iE.addHotkey(), new MultiKeyBind(), this.iE.addEnable())
            .build();
    boolean lastAutoUsingSpear;

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        if (!checkNull() && this.lastAutoUsingSpear) {
            this.lastAutoUsingSpear = false;
            KeyBindAccess.of(mc.options.useKey).resetKeyState();
        }
    }

    public boolean isUsableNotFood(ItemStack stack) {
        return this.cN.get().test(stack.getItem())
                && (this.adQ(stack) || this.useFood.get() && VItem.w().h(stack));
    }

    public AutoUse() {
        super("AutoUse");
        this.log = this.flagBuilder(this.iE.add("log")).build();
        this.onlyWhenEnemyNear =
                this.flagBuilder(this.iE.add("only-when-enemy-near")).build();
        this.enemyNearDistance = this.doubleBuilder(this.iE.add("enemy-near-distance"))
                .defaultValue(12.0)
                .build();
        this.useFood = this.flagBuilder(this.iE.add("use-food")).build();
        this.cN = this.builder(this.iE.add("use-item-white-list"), EntrySet.<Item>parameter())
                .defaultValue(new EntrySet<Item>(new Regex("^(.*spear|.*sword|shield)$"), Registries.ITEM))
                .build();
        this.lastAutoUsingSpear = false;
        this.bindFlag(this.ae);
    }

    private boolean pass() {
        return !this.onlyWhenEnemyNear.get()
                || TargetSelector.INSTANCE.akK(this.enemyNearDistance.get(), true, pl -> pl instanceof PlayerEntity)
                        != null;
    }

    public void onInputEvent(Event<Void> event) {
        if (this.ae.get() && this.pass()) {
            if (!mc.player.isUsingItem()) {
                ItemStack var2 = mc.player.getMainHandStack();
                ItemStack var3 = mc.player.getOffHandStack();
                Hand var4;
                if (this.isUsableNotFood(var2)) {
                    var4 = Hand.MAIN_HAND;
                } else if (this.isUsableNotFood(var3)) {
                    var4 = Hand.OFF_HAND;
                } else {
                    var4 = null;
                }

                if (var4 != null) {
                    ClientAccess.of(mc).simulateUseItem(var4);
                    if (mc.player.isUsingItem() && mc.player.getActiveHand() == var4) {
                        mc.options.useKey.setPressed(true);
                        if (this.log.get()) {
                            Debug.chat(
                                    ChatUtils.textFromLegacyString("&c[Use] &fStart to use"),
                                    VItem.w().l(mc.player.getActiveItem()));
                        }
                    } else {
                        KeyBindAccess.of(mc.options.useKey).resetKeyState();
                    }
                } else if (this.lastAutoUsingSpear) {
                    this.lastAutoUsingSpear = false;
                    KeyBindAccess.of(mc.options.useKey).resetKeyState();
                }
            } else if (this.isUsableNotFood(mc.player.getActiveItem())) {
                mc.options.useKey.setPressed(true);
                this.lastAutoUsingSpear = true;
            } else if (this.lastAutoUsingSpear) {
                this.lastAutoUsingSpear = false;
                KeyBindAccess.of(mc.options.useKey).resetKeyState();
            }
        } else if (this.lastAutoUsingSpear) {
            this.lastAutoUsingSpear = false;
            KeyBindAccess.of(mc.options.useKey).resetKeyState();
        }
    }

    public boolean adQ(ItemStack stack) {
        if (VItem.w().b(stack)) {
            return true;
        } else {
            Item var2 = stack.getItem();
            return var2 instanceof ShieldItem
                    || var2 instanceof BowItem
                    || var2 instanceof TridentItem
                    || var2 instanceof CrossbowItem;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bd(), this::onInputEvent);
    }
}
