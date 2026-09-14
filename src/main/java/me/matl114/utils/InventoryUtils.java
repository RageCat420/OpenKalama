package me.matl114.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.InvTasks;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.inventory.ImmutableListInventory;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.utils.inventory.KalamaHelperHelperC;
import me.matl114.utils.inventory.MutableArrayInventory;
import me.matl114.utils.inventory.MutableInventory;
import me.matl114.utils.inventory.SlotInventory;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

@Modifiable
public class InventoryUtils {
    public static final Codec<KalamaHelperHelperK<NbtCompound>> b = NbtCompound.CODEC.comapFlatMap(
            s -> {
                if (!s.contains("id")) {
                    return DataResult.error(() -> "Can not find field \"id\"");
                } else if (s.get("Slot") instanceof AbstractNbtNumber var2) {
                    NbtCompound var3 = new NbtCompound(new HashMap(s.entries));
                    var3.remove("Slot");
                    return DataResult.success(new KalamaHelperHelperK<>(var2.intValue(), var3));
                } else {
                    return DataResult.error(() -> "Can not find field \"Slot\"");
                }
            },
            s -> {
                NbtCompound var1 = new NbtCompound(new HashMap(((NbtCompound) s.val()).entries));
                var1.putByte("Slot", (byte) s.index());
                return var1;
            });
    public static final Codec<KalamaHelperHelperK<ItemStack>> a = RecordCodecBuilder.create(instance -> instance.group(
                    Codecs.UNSIGNED_BYTE.fieldOf("Slot").orElse(0).forGetter(KalamaHelperHelperK::index),
                    VItem.d.forGetter(KalamaHelperHelperK::val))
            .apply(instance, KalamaHelperHelperK::new));
    private static final MinecraftClient c = MinecraftClient.getInstance();

    public static int F() {
        return 41;
    }

    public static KalamaHelperHelperK<ItemStack> findItem(
            Inventory inventory, Predicate<ItemStack> predicate, boolean acceptEmpty) {
        for (int var3 = 0; var3 < inventory.size(); var3++) {
            if ((acceptEmpty || !inventory.getStack(var3).isEmpty()) && predicate.test(inventory.getStack(var3))) {
                return new KalamaHelperHelperK<>(var3, inventory.getStack(var3));
            }
        }

        return null;
    }

    public static KalamaHelperHelperK<ItemStack> findPlayerHotBarItem(
            Predicate<ItemStack> predicate, boolean acceptEmpty, boolean acceptOffhand) {
        PlayerInventory var3 = c.player.getInventory();
        ItemStack var4 = c.player.getStackInHand(Hand.MAIN_HAND);
        int var5 = var3.selectedSlot;
        if ((acceptEmpty || !var4.isEmpty()) && predicate.test(var4)) {
            return new KalamaHelperHelperK<>(var5, var4);
        } else {
            if (acceptOffhand) {
                var4 = c.player.getStackInHand(Hand.OFF_HAND);
                if ((acceptEmpty || !var4.isEmpty()) && predicate.test(var4)) {
                    return new KalamaHelperHelperK<>(40, var4);
                }
            }

            for (int var6 = 0; var6 < 9; var6++) {
                ItemStack var7 = var3.getStack(var6);
                if (var6 != var5 && (acceptEmpty || !var7.isEmpty()) && predicate.test(var7)) {
                    return new KalamaHelperHelperK<>(var6, var7);
                }
            }

            return null;
        }
    }

    public static KalamaHelperHelperK<Slot> findScreenSlot(
            List<Slot> slots, Function<Slot, Double> maxFunction, boolean acceptEmpty) {
        KalamaHelperHelperK var3 = null;
        Double var4 = null;

        for (int var5 = 0; var5 < slots.size(); var5++) {
            Slot var6 = (Slot) slots.get(var5);
            ItemStack var7 = var6.getStack();
            if (acceptEmpty || !var7.isEmpty()) {
                Double var8 = (Double) maxFunction.apply(var6);
                if (var8 != null && (var4 == null || var4 < var8)) {
                    var4 = var8;
                    var3 = new KalamaHelperHelperK<>(var5, var6);
                }
            }
        }

        return var3;
    }

    public static List<ItemStack> getContainerFromItem(ItemStack itemStack) {
        if (ItemStackUtils.g(itemStack, DataComponentTypes.CONTAINER)) {
            ContainerComponent var1 = ItemStackUtils.getInPatch(itemStack, DataComponentTypes.CONTAINER);
            if (var1 != null) {
                return getContainerInventory(var1);
            }
        }

        return null;
    }

    public static KalamaHelperHelperK<Slot> x(List<Slot> slots, Predicate<Slot> predicate, boolean acceptEmpty) {
        for (int var3 = 0; var3 < slots.size(); var3++) {
            Slot var4 = (Slot) slots.get(var3);
            ItemStack var5 = var4.getStack();
            if ((acceptEmpty || !var5.isEmpty()) && predicate.test(var4)) {
                return new KalamaHelperHelperK<>(var3, var4);
            }
        }

        return null;
    }

    public static Inventory createSubInventoryView(Inventory view, int from, int to) {
        return new KalamaHelperHelperO(view, to, from);
    }

    public static int C(Item maxFunction) {
        return (int) computePlayerInventory(stack -> stack.isOf(maxFunction) ? (double) stack.getCount() : null, false);
    }

    public static Inventory getBottomInventory(ScreenHandler handler) {
        DefaultedList var1 = handler.slots;
        int var2 = 0;

        for (int var3 = 0; var3 < var1.size(); var3++) {
            if (((Slot) var1.get(var3)).inventory instanceof PlayerInventory var5) {
                var2 = var3;
                break;
            }
        }

        return new SlotInventory(var1.subList(var2, var1.size()));
    }

    public static KalamaHelperHelperK<Slot> findPlayerBackpackSlot(
            Predicate<Slot> predicate, boolean acceptEmpty, boolean includeCraft) {
        PlayerScreenHandler var3 = c.player.playerScreenHandler;
        ScreenHandler var4 = ClientPlayerAccess.of(c.player).getServerScreenHandler();
        if (var4.syncId != var3.syncId) {
            return null;
        } else {
            if (includeCraft) {
                for (int var5 = 1; var5 < 5; var5++) {
                    Slot var6 = (Slot) var3.slots.get(var5);
                    if ((acceptEmpty || !var6.getStack().isEmpty()) && predicate.test(var6)) {
                        return new KalamaHelperHelperK<>(var5, var6);
                    }
                }
            }

            PlayerInventory var9 = c.player.getInventory();

            for (int var10 = 0; var10 < F(); var10++) {
                int var7 = InvTasks.q(var10);
                Slot var8 = (Slot) var3.slots.get(var7);
                if ((acceptEmpty || !var8.getStack().isEmpty()) && predicate.test(var8)) {
                    return new KalamaHelperHelperK<>(var7, var8);
                }
            }

            return null;
        }
    }

    public static KalamaHelperHelperK<ItemStack> J(
            Inventory inventory, Function<ItemStack, Double> predicate, boolean acceptEmpty) {
        return K(inventory, v -> (Double) predicate.apply(v.val()), acceptEmpty);
    }

    public static Inventory getTopInventory(ScreenHandler screen) {
        if (screen instanceof GenericContainerScreenHandler var1) {
            return var1.getInventory();
        } else {
            DefaultedList var2 = screen.slots;
            int var3 = 0;

            for (int var4 = 0; var4 < var2.size(); var4++) {
                if (((Slot) var2.get(var4)).inventory instanceof PlayerInventory var6) {
                    var3 = var4;
                    break;
                }
            }

            return new SlotInventory(var2.subList(0, var3));
        }
    }

    public static Inventory createInventory(int size, List<ItemStack> itemStackSupplier) {
        return new MutableInventory(size, itemStackSupplier);
    }

    public static Iterable<ItemStack> a(Inventory inventory) {
        return () -> new KalamaHelperHelperTX(inventory);
    }

    public static KalamaHelperHelperK<Slot> B(List<Slot> slots, Predicate<ItemStack> predicate, boolean acceptEmpty) {
        for (int var3 = 0; var3 < slots.size(); var3++) {
            Slot var4 = (Slot) slots.get(var3);
            ItemStack var5 = var4.getStack();
            if ((acceptEmpty || !var5.isEmpty()) && predicate.test(var5)) {
                return new KalamaHelperHelperK<>(var3, var4);
            }
        }

        return null;
    }

    public static KalamaHelperHelperK<ItemStack> r(
            Predicate<ItemStack> predicate,
            boolean doNotFSearchWhenOpenOtherScreen,
            boolean acceptEmpty,
            boolean handPriority,
            boolean offHandPriority) {
        return findPlayerInventory(
                val -> predicate.test(val.val()),
                doNotFSearchWhenOpenOtherScreen,
                acceptEmpty,
                handPriority,
                offHandPriority);
    }

    public static KalamaHelperHelperK<ItemStack> findPlayerInventory(
            Predicate<KalamaHelperHelperK<ItemStack>> predicate,
            boolean doNotFSearchWhenOpenOtherScreen,
            boolean acceptEmpty,
            boolean handPriority,
            boolean offHandPriority) {
        PlayerInventory var5 = c.player.getInventory();
        ItemStack var6 = c.player.getStackInHand(Hand.MAIN_HAND);
        int var7 = var5.selectedSlot;
        KalamaHelperHelperK var8 = null;
        KalamaHelperHelperK var9;
        if ((acceptEmpty || !var6.isEmpty()) && predicate.test(var9 = new KalamaHelperHelperK<>(var7, var6))) {
            var8 = var9;
        }

        if (handPriority && var8 != null) {
            return var8;
        } else {
            if (var8 == null && offHandPriority) {
                var6 = c.player.getStackInHand(Hand.OFF_HAND);
                if ((acceptEmpty || !var6.isEmpty()) && predicate.test(var9 = new KalamaHelperHelperK<>(40, var6))) {
                    var8 = var9;
                }

                if (var8 != null) {
                    return var8;
                }
            }

            if (doNotFSearchWhenOpenOtherScreen
                    && ClientPlayerAccess.of(c.player).getServerScreenHandler().syncId
                            != c.player.playerScreenHandler.syncId) {
                return var8;
            } else {
                for (int var10 = 0; var10 < F(); var10++) {
                    ItemStack var11 = var5.getStack(var10);
                    var9 = new KalamaHelperHelperK<>(var10, var11);
                    if ((acceptEmpty || !var11.isEmpty()) && predicate.test(var9)) {
                        return new KalamaHelperHelperK<>(var10, var11);
                    }
                }

                return null;
            }
        }
    }

    public static Inventory f(ItemStack[] array) {
        return new MutableArrayInventory(array);
    }

    public static KalamaHelperHelperK<ItemStack> findInventory(
            Inventory inventory, Predicate<KalamaHelperHelperK<ItemStack>> predicate, boolean acceptEmpty) {
        for (int var3 = 0; var3 < inventory.size(); var3++) {
            KalamaHelperHelperK var4;
            if ((acceptEmpty || !inventory.getStack(var3).isEmpty())
                    && predicate.test(var4 = new KalamaHelperHelperK<>(var3, inventory.getStack(var3)))) {
                return var4;
            }
        }

        return null;
    }

    public static KalamaHelperHelperK<ItemStack> K(
            Inventory inventory, Function<KalamaHelperHelperK<ItemStack>, Double> predicate, boolean acceptEmpty) {
        KalamaHelperHelperK var3 = null;
        Double var4 = null;

        for (int var5 = 0; var5 < inventory.size(); var5++) {
            if (acceptEmpty || !inventory.getStack(var5).isEmpty()) {
                KalamaHelperHelperK var6 = new KalamaHelperHelperK<>(var5, inventory.getStack(var5));
                Double var7 = (Double) predicate.apply(var6);
                if (var7 != null && (var4 == null || var4 < var7)) {
                    var4 = var7;
                    var3 = var6;
                }
            }
        }

        return var3;
    }

    public static Map<ItemStackSample, IntList> collectItemIndexes(Iterable<ItemStack> stacks) {
        Map<ItemStackSample, IntList> var1 = new LinkedHashMap<>();
        int var2 = 0;

        for (ItemStack var4 : stacks) {
            var1.computeIfAbsent(ItemStackSample.of(var4), i -> new IntArrayList())
                    .add(var2);
            var2++;
        }

        return var1;
    }

    public static Inventory i(HandledScreen<?> screen) {
        if (screen instanceof GenericContainerScreen var1) {
            return ((GenericContainerScreenHandler) var1.getScreenHandler()).getInventory();
        } else {
            DefaultedList var2 = screen.getScreenHandler().slots;
            int var3 = 0;

            for (int var4 = 0; var4 < var2.size(); var4++) {
                if (((Slot) var2.get(var4)).inventory instanceof PlayerInventory var6) {
                    var3 = var4;
                    break;
                }
            }

            return new SlotInventory(var2.subList(0, var3));
        }
    }

    public static int getSelectedSlot() {
        return c.player.getInventory().selectedSlot;
    }

    public static KalamaHelperHelperK<ItemStack> s(
            Predicate<KalamaHelperHelperK<ItemStack>> predicate,
            boolean doNotFSearchWhenOpenOtherScreen,
            boolean acceptEmpty) {
        return findPlayerInventory(predicate, doNotFSearchWhenOpenOtherScreen, acceptEmpty, true, false);
    }

    public static double computePlayerInventory(Function<ItemStack, Double> maxFunction, boolean acceptEmpty) {
        double var2 = 0.0;
        PlayerInventory var4 = c.player.getInventory();

        for (int var5 = 0; var5 < F(); var5++) {
            ItemStack var6 = var4.getStack(var5);
            Double var7;
            if ((acceptEmpty || !var6.isEmpty()) && (var7 = (Double) maxFunction.apply(var6)) != null) {
                var2 += var7;
            }
        }

        return var2;
    }

    public static KalamaHelperHelperK<ItemStack> findPlayerItem(
            Predicate<ItemStack> predicate,
            boolean doNotFSearchWhenOpenOtherScreen,
            boolean acceptEmpty,
            boolean handPriority) {
        return r(predicate, doNotFSearchWhenOpenOtherScreen, acceptEmpty, handPriority, false);
    }

    public static Inventory d(List<ItemStack> itemStackSupplier) {
        return createInventory(itemStackSupplier.size(), itemStackSupplier);
    }

    public static Inventory c(List<ItemStack> itemStackSupplier) {
        return new KalamaHelperHelperC(itemStackSupplier);
    }

    public static int E() {
        return 36;
    }

    public static KalamaHelperHelperK<ItemStack> v(
            Function<ItemStack, Double> maxFunction, boolean doNotFSearchWhenOpenOtherScreen, boolean acceptEmpty) {
        return findBestPlayerInventory(
                s -> (Double) maxFunction.apply(s.val()), doNotFSearchWhenOpenOtherScreen, acceptEmpty);
    }

    public static Stream<ItemStack> streamInventory(Inventory inv) {
        return IntStream.range(0, inv instanceof PlayerInventory var1 ? F() : inv.size())
                .mapToObj(inv::getStack);
    }

    public static Inventory k(HandledScreen<?> screen) {
        DefaultedList var1 = screen.getScreenHandler().slots;
        int var2 = 0;

        for (int var3 = 0; var3 < var1.size(); var3++) {
            if (((Slot) var1.get(var3)).inventory instanceof PlayerInventory var5) {
                var2 = var3;
                break;
            }
        }

        return new SlotInventory(var1.subList(var2, var1.size()));
    }

    @Nonnull
    public static KalamaHelperHelperK<ItemStack> getSelectedItem() {
        int var0 = getSelectedSlot();
        return new KalamaHelperHelperK<>(
                var0, (ItemStack) c.player.getInventory().main.get(var0));
    }

    public static KalamaHelperHelperK<ItemStack> p(
            Predicate<ItemStack> predicate, boolean doNotFSearchWhenOpenOtherScreen, boolean acceptEmpty) {
        return findPlayerItem(predicate, doNotFSearchWhenOpenOtherScreen, acceptEmpty, true);
    }

    public static KalamaHelperHelperK<ItemStack> G(Inventory inventory, Item predicate) {
        return findItem(inventory, v -> v.isOf(predicate), predicate == Items.AIR);
    }

    public static List<KalamaHelperHelperK<ItemStack>> getInventoryEntries(Inventory inv) {
        ArrayList var1 = new ArrayList();

        for (int var2 = 0; var2 < inv.size(); var2++) {
            ItemStack var3 = inv.getStack(var2);
            if (!var3.isEmpty()) {
                var1.add(new KalamaHelperHelperK<>(var2, var3));
            }
        }

        return var1;
    }

    public static List<ItemStack> getContainerInventory(ContainerComponent container) {
        return container.stream().toList();
    }

    public static KalamaHelperHelperK<ItemStack> findBestPlayerInventory(
            Function<KalamaHelperHelperK<ItemStack>, Double> maxFunction,
            boolean doNotFSearchWhenOpenOtherScreen,
            boolean acceptEmpty) {
        PlayerInventory var3 = c.player.getInventory();
        ItemStack var4 = c.player.getStackInHand(Hand.MAIN_HAND);
        int var5 = var3.selectedSlot;
        Double var6 = null;
        KalamaHelperHelperK<ItemStack> var7 = null;
        KalamaHelperHelperK<ItemStack> var8 = null;
        if (acceptEmpty || !var4.isEmpty()) {
            var8 = new KalamaHelperHelperK<>(var5, var4);
            var6 = maxFunction.apply(var8);
            if (var6 != null) {
                var7 = var8;
            }
        }

        if (doNotFSearchWhenOpenOtherScreen
                && ClientPlayerAccess.of(c.player).getServerScreenHandler().syncId
                        != c.player.playerScreenHandler.syncId) {
            return var7;
        } else {
            for (int var9 = 0; var9 < F(); var9++) {
                ItemStack var10 = var3.getStack(var9);
                var8 = new KalamaHelperHelperK<>(var9, var10);
                Double var11;
                if ((acceptEmpty || !var10.isEmpty())
                        && (var11 = maxFunction.apply(var8)) != null
                        && (var6 == null || var11 > var6)) {
                    var6 = var11;
                    var7 = var8;
                }
            }

            return var7;
        }
    }

    public static KalamaHelperHelperK<Slot> y(
            Predicate<ItemStack> predicate, boolean acceptEmpty, boolean includeCraft) {
        return findPlayerBackpackSlot(slot -> predicate.test(slot.getStack()), acceptEmpty, includeCraft);
    }

    public static Inventory b(Supplier<ItemStack> itemStackSupplier) {
        return new ImmutableListInventory(itemStackSupplier);
    }
}
