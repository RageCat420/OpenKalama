package me.matl114.hacks.modules.inv;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.config.FlagRef;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class AutoStore extends BaseModule {
    public final ModulePath wQ;
    public final ModulePath wP = makePath(Configs.l, "auto-inv");
    public final FlagRef enable;

    @Override
    public void registerAll() {
        super.registerAll();
        TaskManagers.c().register("button-toggle.auto-store", this.enable);
        this.registerListener(Listener.U(), this::onTick);
    }

    public void onTick(Event<ClientPlayerEntity> event) {
        if (this.enable.get()) {
            ClientPlayerEntity var2 = (ClientPlayerEntity) event.e();
            if (InvTasks.getCurrentServerScreen(var2) instanceof HandledScreen var4) {
                ScreenHandler var5 = var4.getScreenHandler();
                if (var4 instanceof CreativeInventoryScreen || var4 instanceof InventoryScreen) {
                    return;
                }

                IntArrayList var6 = new IntArrayList();
                IntArrayList var7 = new IntArrayList();

                for (int var8 = 0; var8 < var5.slots.size(); var8++) {
                    Slot var9 = (Slot) var5.slots.get(var8);
                    if (var9.inventory instanceof PlayerInventory) {
                        var6.add(var8);
                    } else {
                        var7.add(var8);
                    }
                }

                IntListIterator var13 = var6.iterator();

                while (var13.hasNext()) {
                    int var14 = (Integer) var13.next();
                    ItemStack var10 = ((Slot) var5.slots.get(var14)).getStack();
                    if (var10 != null && !var10.isEmpty() && var10.getCount() >= 4) {
                        if (!var5.getCursorStack().isEmpty()) {
                            ItemStack var15 = var5.getCursorStack();
                            int var16 = anyMatch(var5.slots, var15, var15.getCount(), var7.toIntArray());
                            if (var16 >= 0) {
                                InvTasks.at()
                                        .execute(() -> mc.interactionManager.clickSlot(
                                                var4.getScreenHandler().syncId, var16, 0, SlotActionType.PICKUP, var2));
                            } else {
                                InvTasks.at()
                                        .execute(() -> mc.interactionManager.clickSlot(
                                                var4.getScreenHandler().syncId, var16, 0, SlotActionType.THROW, var2));
                            }

                            return;
                        }

                        int var11 = (var10.getCount() + 1) / 2;
                        int var12 = anyMatch(var5.slots, var10, var11, var7.toIntArray());
                        if (var12 >= 0) {
                            InvTasks.at().execute(() -> {
                                mc.interactionManager.clickSlot(
                                        var4.getScreenHandler().syncId, var14, 1, SlotActionType.PICKUP, var2);
                                mc.interactionManager.clickSlot(
                                        var4.getScreenHandler().syncId, var12, 0, SlotActionType.PICKUP, var2);
                            });
                            return;
                        }
                    }
                }
            }
        }
    }

    public AutoStore() {
        super("AutoStore");
        this.wQ = this.wP.add("auto-store");
        this.enable = this.flagBuilder(this.wQ.add("enable")).build();
        this.bindFlag(this.enable);
    }

    private static int anyMatch(DefaultedList<Slot> slots, ItemStack stack, int amount, int... index) {
        for (int var7 : index) {
            ItemStack var8 = ((Slot) slots.get(var7)).getStack();
            if (var8 != null
                    && (var8.isEmpty()
                            || var8.getCount() + amount <= var8.getMaxCount()
                                    && ItemStack.areItemsAndComponentsEqual(stack, var8))) {
                return var7;
            }
        }

        return -1;
    }
}
