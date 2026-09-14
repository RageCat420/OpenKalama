package me.matl114.hacks.modules.inv;

import com.google.common.util.concurrent.Runnables;
import java.util.Locale;
import java.util.OptionalInt;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.SlotClickAction;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.AttributeUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.collections.Point;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class InvExtra extends BaseModule {
    public final ModulePath mO;
    public final FlagRef ghostHandAttributeSync;
    public static final String mQ = "clear-keep";
    public final FlagRef mP;
    public static InvExtra INSTANCE;
    public final FlagRef sprintClickGrimFix;
    public final FlagRef expandBackpackInventory;
    public final FlagRef moveClickGrimFix;
    public final ModulePath mH = makePath(Configs.l, "inventory");
    public final KeyBindRef pickItem;
    public final IntRef mI = this.intBuilder(this.mH.add("packet-limit"))
            .defaultValue(40)
            .validator(Configs.e)
            .build();

    public Runnable swapInventorySlotToHand(int slot) {
        int var2 = InventoryUtils.getSelectedSlot();
        OptionalInt var3 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), var2);
        return var3.isPresent() ? this.swapScreenSlots(slot, var3.getAsInt()) : null;
    }

    public void mergeScreenSlotTo(int from, int to) {
        if (from != to) {
            ScreenHandler var3 = ClientPlayerAccess.of(mc.player).getServerScreenHandler();
            DefaultedList var4 = var3.slots;
            if (var4.size() > from && var4.size() > to) {
                MovTasks.aj().Xf();
                Slot var5 = (Slot) var3.slots.get(from);
                Slot var6 = (Slot) var3.slots.get(to);
                ItemStack var7 = var5.getStack();
                if (!var7.isEmpty()) {
                    if (var6.canInsert(var7)) {
                        if (!var6.getStack().isEmpty() && ItemStack.areItemsAndComponentsEqual(var7, var6.getStack())) {
                            ItemStack var8 = var6.getStack();
                            int var9 = var8.getMaxCount();
                            boolean var10 = var7.getCount() + var8.getCount() > var9;
                            mc.interactionManager.clickSlot(var3.syncId, from, 0, SlotActionType.PICKUP, mc.player);
                            mc.interactionManager.clickSlot(var3.syncId, to, 0, SlotActionType.PICKUP, mc.player);
                            if (var10) {
                                mc.interactionManager.clickSlot(var3.syncId, from, 0, SlotActionType.PICKUP, mc.player);
                            }
                        } else {
                            this.swapScreenSlots(from, to);
                        }
                    }
                }
            }
        }
    }

    public Runnable switchOrSwapInventoryIndexToHand(int hand) {
        int var2 = InventoryUtils.getSelectedSlot();
        if (var2 != hand) {
            if (hand < 9) {
                PlayerInteractionAccess.of(mc.interactionManager).syncSelectedHotbar(hand);
                this.syncAttr();
                return () -> {
                    PlayerInteractionAccess.of(mc.interactionManager).syncSelectedHotbar(var2);
                    this.syncAttr();
                };
            } else {
                OptionalInt var3 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), hand);
                if (var3.isPresent()) {
                    int var4 = var3.getAsInt();
                    if (var4 >= 0) {
                        MovTasks.aj().Xf();
                        mc.interactionManager.clickSlot(
                                mc.player.currentScreenHandler.syncId, var4, var2, SlotActionType.SWAP, mc.player);
                        this.syncAttr();
                        return () -> {
                            MovTasks.aj().Xf();
                            mc.interactionManager.clickSlot(
                                    mc.player.currentScreenHandler.syncId, var4, var2, SlotActionType.SWAP, mc.player);
                            this.syncAttr();
                        };
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return Runnables.doNothing();
        }
    }

    public InvExtra() {
        super("InvExtra");
        this.moveClickGrimFix =
                this.flagBuilder(this.mH.add("move-click-grim-fix")).build();
        this.sprintClickGrimFix =
                this.flagBuilder(this.mH.add("sprint-click-grim-fix")).build();
        this.expandBackpackInventory =
                this.flagBuilder(this.mH.add("expand-backpack-inventory")).build();
        this.ghostHandAttributeSync =
                this.flagBuilder(this.mH.add("ghost-hand-attribute-sync")).build();
        this.pickItem = this.hotkey(this.mH.add("pick-item"))
                .defaultValue(new MultiKeyBind(341, -98))
                .registerHotkey(HotKeyUtils.e(this::onPickItem))
                .build();
        this.mO = this.mH.add("keep-inv");
        this.mP = this.flagBuilder(this.mO).build();
        INSTANCE = this;
    }

    public Runnable swapInventoryIndexToHand(int hand) {
        int var2 = InventoryUtils.getSelectedSlot();
        if (var2 != hand) {
            OptionalInt var3 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), hand);
            if (var3.isPresent()) {
                int var4 = var3.getAsInt();
                if (var4 >= 0) {
                    MovTasks.aj().Xf();
                    mc.interactionManager.clickSlot(
                            mc.player.currentScreenHandler.syncId, var4, var2, SlotActionType.SWAP, mc.player);
                    this.syncAttr();
                    return () -> {
                        MovTasks.aj().Xf();
                        mc.interactionManager.clickSlot(
                                mc.player.currentScreenHandler.syncId, var4, var2, SlotActionType.SWAP, mc.player);
                        this.syncAttr();
                    };
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return Runnables.doNothing();
        }
    }

    public void nI(Event<SlotClickAction> event) {
        this.onInvClick(((SlotClickAction) event.b).syncId());
    }

    public Runnable swapInventoryIndexes(int slot1, int slot2) {
        OptionalInt var3 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), slot1);
        OptionalInt var4 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), slot2);
        return var3.isPresent() && var4.isPresent() ? this.swapScreenSlots(var3.getAsInt(), var4.getAsInt()) : null;
    }

    public Runnable uh(int hand) {
        if (hand == 40) {
            return Runnables.doNothing();
        } else {
            OptionalInt var2 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), hand);
            if (var2.isPresent()) {
                int var3 = var2.getAsInt();
                if (var3 >= 0) {
                    MovTasks.aj().Xf();
                    mc.interactionManager.clickSlot(
                            mc.player.currentScreenHandler.syncId, var3, 40, SlotActionType.SWAP, mc.player);
                    this.syncAttr();
                    return () -> {
                        MovTasks.aj().Xf();
                        mc.interactionManager.clickSlot(
                                mc.player.currentScreenHandler.syncId, var3, 40, SlotActionType.SWAP, mc.player);
                        this.syncAttr();
                    };
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public void onInvClick(int syncId) {
        if (mc.currentScreen instanceof HandledScreen var3
                && var3.getScreenHandler().syncId == syncId
                && this.sprintClickGrimFix.get()) {
            MovTasks.aj().sendSprintPacketsForInventoryAction();
        }

        if (this.moveClickGrimFix.get()) {
            MovTasks.aj().sendPacketsForInventoryAction();
        }
    }

    public void clearKeep() {
        ClientPlayerEntity var1 = MinecraftClient.getInstance().player;
        if (var1 != null) {
            ClientPlayerAccess var2 = ClientPlayerAccess.of(var1);
            var2.clearKeepedInventory(true);
            Debug.b(Text.literal("已清除界面历史记录"));
        }
    }

    public void onCloseScreen(Event<CloseHandledScreenC2SPacket> closeS2C) {
        if (this.expandBackpackInventory.get()
                && ((CloseHandledScreenC2SPacket) closeS2C.b).getSyncId() == mc.player.playerScreenHandler.syncId) {
            closeS2C.cancel();
        }
    }

    public boolean onPickItem() {
        ClientPlayerEntity var1 = mc.player;
        if (var1 == null) {
            return false;
        } else {
            if (!var1.isCreative() && InvTasks.getCurrentServerScreen(var1) instanceof HandledScreen var3) {
                Point var4 = ScreenUtils.getMouseCoord(mc);
                Slot var5 = HandledScreenAccess.of(var3).reallyGetSlotAt(var4.a, var4.b);
                if (var5 != null) {
                    if (var5.inventory instanceof PlayerInventory) {
                        if (var5.getIndex() >= 36) {
                            Debug.chat("Invalid slot for player Inventory", var5.getIndex());
                        } else if (ViaFabricPlusHooks.getInstance().isViaEnabled()
                                && ViaFabricPlusHooks.getInstance()
                                        .getCurrentVersion()
                                        .c(21, 3)) {
                            ViaFabricPlusHooks.ViaPacketWrapper var6 =
                                    ViaFabricPlusHooks.getInstance().createViaPacket();
                            var6.writePacketType("v1_21_2to1_21_4", "pick_item".toUpperCase(Locale.ROOT));
                            var6.write("VAR_INT", var5.getIndex());
                            var6.scheduleSendToServer("v1_21_2to1_21_4", true);
                            Debug.b("run pickup");
                        } else {
                            Debug.b("No Longer support this feat in version "
                                    + ViaFabricPlusHooks.getInstance().getCurrentVersion());
                        }

                        return true;
                    }

                    Debug.b("Invalid slot outside player Inventory");
                }
            }

            return false;
        }
    }

    private void swapTwoIdiotSlot(ScreenHandler handler, int targetSlot, int armorSlot) {
        int var4 = InventoryUtils.getSelectedSlot() == 8 ? 7 : 8;
        mc.interactionManager.clickSlot(handler.syncId, targetSlot, var4, SlotActionType.SWAP, mc.player);
        mc.interactionManager.clickSlot(handler.syncId, armorSlot, var4, SlotActionType.SWAP, mc.player);
        mc.interactionManager.clickSlot(handler.syncId, targetSlot, var4, SlotActionType.SWAP, mc.player);
    }

    public void syncAttr() {
        if (this.ghostHandAttributeSync.get()) {
            AttributeUtils.updateAttribute(mc.player);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ak(), this::nI);
        this.registerListener(Listener.ap().getChannel(CloseHandledScreenC2SPacket.class), this::onCloseScreen);
        TaskManagers.c().register("button-toggle.keep-inv", this.mP);
        TaskManagers.b().register("button-task.clear-keep", this::clearKeep);
    }

    public Runnable uj(int slot) {
        byte var2 = 40;
        OptionalInt var3 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), var2);
        return var3.isPresent() ? this.swapScreenSlots(slot, var3.getAsInt()) : null;
    }

    public Runnable swapScreenSlots(int armorSlot, int targetSlot) {
        if (armorSlot == targetSlot) {
            return Runnables.doNothing();
        } else {
            ScreenHandler var3 = ClientPlayerAccess.of(mc.player).getServerScreenHandler();
            DefaultedList var4 = var3.slots;
            if (var4.size() > armorSlot && var4.size() > targetSlot) {
                MovTasks.aj().Xf();
                Slot var5 = (Slot) var3.slots.get(targetSlot);
                if (!(var5.inventory instanceof PlayerInventory) || var5.getIndex() >= 9 && var5.getIndex() != 40) {
                    Slot var8 = (Slot) var3.slots.get(armorSlot);
                    if (!(var8.inventory instanceof PlayerInventory) || var8.getIndex() >= 9 && var8.getIndex() != 40) {
                        this.swapTwoIdiotSlot(var3, targetSlot, armorSlot);
                        this.syncAttr();
                        return () -> {
                            MovTasks.aj().Xf();
                            this.swapTwoIdiotSlot(var3, targetSlot, armorSlot);
                            this.syncAttr();
                        };
                    } else {
                        int var7 = var8.getIndex();
                        mc.interactionManager.clickSlot(var3.syncId, targetSlot, var7, SlotActionType.SWAP, mc.player);
                        this.syncAttr();
                        return () -> {
                            MovTasks.aj().Xf();
                            mc.interactionManager.clickSlot(
                                    var3.syncId, targetSlot, var7, SlotActionType.SWAP, mc.player);
                            this.syncAttr();
                        };
                    }
                } else {
                    int var6 = var5.getIndex();
                    mc.interactionManager.clickSlot(var3.syncId, armorSlot, var6, SlotActionType.SWAP, mc.player);
                    this.syncAttr();
                    return () -> {
                        MovTasks.aj().Xf();
                        mc.interactionManager.clickSlot(var3.syncId, armorSlot, var6, SlotActionType.SWAP, mc.player);
                        this.syncAttr();
                    };
                }
            } else {
                return null;
            }
        }
    }
}
