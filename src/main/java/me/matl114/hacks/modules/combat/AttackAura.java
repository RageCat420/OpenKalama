package me.matl114.hacks.modules.combat;

import java.util.List;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

public class AttackAura extends BaseModule {
    public final FlagRef hand;
    public final IntRef autoAttRate;
    public final FlagRef airKill;
    public final KeyBindRef autoAttHotkey;
    public final FlagRef stopAttackWhenSpear;
    public final FlagRef autoAtt;
    public final FlagRef stopAttackWhenEat;
    private int BO;
    public final IntRef maxAtOnce;
    public final ModulePath KE = makePath(Configs.k, "att-bot");
    public final ModulePath KF = this.KE.add("respect-cooldown");
    public final FlagRef weapon;

    public void aO(Event<ClientPlayerEntity> tickEvent) {
        if (mc.player != null) {
            if (this.autoAtt.get()) {
                Attack var2 = CombatTasks.n();
                boolean var3 = CombatTasks.isHoldingWeapon(mc.player);
                boolean var4 = this.airKill.get() && !mc.player.isOnGround();
                this.BO++;
                int var5 = this.autoAttRate.get();
                if (var4 || var5 <= this.BO) {
                    if (var4
                            || !var2.legalTargeting.get().isLegal()
                                    && (!var3 || !this.weapon.get())
                                    && (var3 || !this.hand.get())) {
                        List<Entity> var6 = var2.XX();
                        int var7 = this.maxAtOnce.get();
                        if (!var6.isEmpty()) {
                            this.BO = 0;

                            for (Entity var9 : var6) {
                                if (var2.Yi(var9, var2.createAttackSettings())) {
                                    break;
                                }

                                if (--var7 <= 0) {
                                    return;
                                }
                            }
                        }
                    } else {
                        if (this.aaq()) {
                            return;
                        }

                        if (mc.player.getAttackCooldownProgress(0.5F) > 0.98) {
                            this.BO = 0;
                            var2.tryAttack(true);
                        }
                    }
                }
            }
        }
    }

    public boolean aaq() {
        return this.aao() || this.aap();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::aO);
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        acceptor.accept(this.createTitleLabel("widget.attack.attack.use-argument", 0, dblank, dx, dy));
    }

    public AttackAura() {
        super("AttackAura");
        this.autoAtt = this.flagBuilder(this.KE.add("auto-att")).build();
        this.autoAttHotkey = this.toggleHotkey(
                        this.KE.add("auto-att-hotkey"), new MultiKeyBind(), this.KE.add("auto-att"))
                .build();
        this.maxAtOnce = this.intBuilder(this.KE.add("max-at-once"))
                .defaultValue(1)
                .validator(Configs.e)
                .build();
        this.weapon = this.builder(this.KF.add("weapon"), Boolean.class)
                .defaultValue(true)
                .build();
        this.hand = this.builder(this.KF.add("hand"), Boolean.class)
                .defaultValue(true)
                .build();
        this.autoAttRate = this.intBuilder(this.KE.add("auto-att-rate"))
                .defaultValue(0)
                .validator(Configs.d)
                .build();
        this.stopAttackWhenEat =
                this.flagBuilder(this.KE.add("stop-attack-when-eat")).build();
        this.stopAttackWhenSpear =
                this.flagBuilder(this.KE.add("stop-attack-when-spear")).build();
        this.airKill = this.flagBuilder(this.KE.add("air-kill")).build();
        this.bindFlag(this.autoAtt);
    }

    public boolean aap() {
        return this.stopAttackWhenSpear.get()
                && mc.player.isUsingItem()
                && VItem.w().b(mc.player.getActiveItem());
    }

    public boolean aao() {
        return this.stopAttackWhenEat.get()
                && mc.player.isUsingItem()
                && VItem.w().h(mc.player.getActiveItem());
    }
}
