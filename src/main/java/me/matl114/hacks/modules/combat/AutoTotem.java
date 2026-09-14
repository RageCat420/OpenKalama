package me.matl114.hacks.modules.combat;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Random;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.tasks.StateExecutor;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class AutoTotem extends BaseModule {
    private final Random Dd = new Random();
    StateExecutor Dh;
    public final FlagRef autoTotemLog;
    int swapCntCounter;
    TimerExecutor Dk;
    public final KeyBindRef autoTotemHotkey;
    TimerExecutor Di;
    public final ModulePath hg = makePath(Configs.k, "totem");
    public final FlagRef antiMiss;
    public final IntRef totemSwapCooldown;
    public final FlagRef smartAutoTotem;
    public final NBTRef<EntrySet<Item>> Df;
    public final FlagRef ae = this.flagBuilder(this.hg.add("auto-totem")).build();

    public void aO(Event<ClientPlayerEntity> ev) {
        if (this.ae.get()) {
            this.onTotemLazy();
        }
    }

    public void onTotem(Event<EntityStatusS2CPacket> eventTotem) {
        if (!checkNull()) {
            if (this.ae.get()
                    && this.antiMiss.get()
                    && ((EntityStatusS2CPacket) eventTotem.b).getStatus() == 35
                    && ((EntityStatusS2CPacket) eventTotem.b).getEntity(mc.world) == mc.player) {
                ItemStack var2 = mc.player.getMainHandStack();
                int var3 = var2.getItem() == Items.TOTEM_OF_UNDYING ? InventoryUtils.getSelectedSlot() : 40;
                mc.player.getInventory().setStack(var3, ItemStack.EMPTY);
                ScreenHandler var4 = ClientPlayerAccess.of(mc.player).getServerScreenHandler();
                DefaultedList var5 = var4.slots;

                for (int var6 = var5.size() - 1; var6 >= 0; var6--) {
                    if (var6 != var3
                            && (((Slot) var5.get(var6)).inventory instanceof PlayerInventory
                                    || var4 == mc.player.playerScreenHandler)
                            && ((Slot) var5.get(var6)).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
                        MovTasks.aj().Xf();
                        InvTasks.clickSlotAsync(var6, var3, SlotActionType.SWAP);
                        this.Rw();
                        Debug.h("handle antimiss success");
                        this.Dk.mark(1);
                        return;
                    }
                }

                this.Rv();
            }
        }
    }

    public void onTotemLazy() {
        if (this.Dk.a(this.totemSwapCooldown.get())) {
            if (!this.canBeAccepted(mc.player.getOffHandStack())) {
                if (this.smartAutoTotem.get() && mc.player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
                    return;
                }

                int var1 = this.smartAutoTotem.get()
                                && mc.player.getMainHandStack().isEmpty()
                                && !mc.player.getOffHandStack().isEmpty()
                        ? InventoryUtils.getSelectedSlot()
                        : 40;
                ScreenHandler var2 = ClientPlayerAccess.of(mc.player).getServerScreenHandler();
                DefaultedList var3 = var2.slots;

                for (int var4 = 0; var4 < var3.size(); var4++) {
                    if ((((Slot) var3.get(var4)).inventory instanceof PlayerInventory
                                    || var2 == mc.player.playerScreenHandler)
                            && ((Slot) var3.get(var4)).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
                        MovTasks.aj().Xf();
                        InvTasks.clickSlotAsync(var4, var1, SlotActionType.SWAP);
                        Debug.h("handle swap success");
                        this.Rw();
                        return;
                    }
                }

                this.Rv();
            }
        }
    }

    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {}

    private boolean canBeAccepted(ItemStack ex) {
        return ex.getItem() == Items.TOTEM_OF_UNDYING || this.Df.get().test(ex.getItem());
    }

    public void RA(Event<ClientPlayerEntity> eventPlayer) {
        this.Dh.state(false);
    }

    public AutoTotem() {
        super("AutoTotem");
        this.autoTotemHotkey = this.toggleHotkey(
                        this.hg.add("auto-totem-hotkey"), new MultiKeyBind(), this.hg.add("auto-totem"))
                .build();
        this.autoTotemLog = this.builder(this.hg.add("auto-totem-log"), Boolean.class)
                .defaultValue(true)
                .build();
        this.totemSwapCooldown = this.builder(this.hg.add("totem-swap-cooldown"), IntRef.TYPE)
                .defaultValue(1)
                .build();
        this.smartAutoTotem = this.flagBuilder(this.hg.add("smart-auto-totem")).build();
        this.Df = this.builder(this.hg.add("enable-hand-items"), EntrySet.<Item>parameter())
                .defaultValue(new EntrySet<Item>(new Regex("^()$"), Registries.ITEM))
                .build();
        this.antiMiss = this.flagBuilder(this.hg.add("anti-miss")).build();
        this.Dh = new StateExecutor();
        this.Di = new TimerExecutor();
        this.swapCntCounter = 0;
        this.Dk = new TimerExecutor();
        this.bindFlag(this.ae);
    }

    public void onTotemTick() {
        if (!this.canBeAccepted(mc.player.getOffHandStack())) {
            this.onTotemLazy();
        } else {
            IntArrayList var1 = new IntArrayList();
            ScreenHandler var2 = ClientPlayerAccess.of(mc.player).getServerScreenHandler();
            DefaultedList var3 = var2.slots;

            for (int var4 = 0; var4 < var3.size(); var4++) {
                if ((((Slot) var3.get(var4)).inventory instanceof PlayerInventory
                                || var2 == mc.player.playerScreenHandler)
                        && ((Slot) var3.get(var4)).getStack().getItem() == Items.TOTEM_OF_UNDYING
                        && var4 != 40) {
                    var1.add(var4);
                }
            }

            if (!var1.isEmpty()) {
                int var5 = var1.getInt(this.Dd.nextInt(var1.size()));
                MovTasks.aj().Xf();
                InvTasks.clickSlotAsync(var5, 40, SlotActionType.SWAP);
                this.Rw();
            } else {
                this.Rv();
            }
        }
    }

    private void Rv() {
        this.Dh.a(true, () -> {
            if (this.autoTotemLog.get()) {
                this.logI18N("message.module.auto-totem.not-found", new Object[0]);
            }
        });
    }

    private void Rw() {
        this.Dh.state(false);
        this.Dk.f();
        this.Di.b(20, () -> this.swapCntCounter = 0);
        if (++this.swapCntCounter > 5) {
            this.swapCntCounter = 0;
            if (this.autoTotemLog.get()) {
                this.logI18N("message.module.auto-totem.swap-too-frequent", new Object[0]);
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::aO);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
        this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::onTotem);
        this.registerListener(Listener.aE(), this::RA);
    }
}
