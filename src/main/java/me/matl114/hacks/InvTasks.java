package me.matl114.hacks;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.gui.elements.KalamaHelperHelperL;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.inv.AutoShulker;
import me.matl114.hacks.modules.inv.AutoSteal;
import me.matl114.hacks.modules.inv.AutoStore;
import me.matl114.hacks.modules.inv.ChestHistory;
import me.matl114.hacks.modules.inv.FastCraft;
import me.matl114.hacks.modules.inv.FastInv;
import me.matl114.hacks.modules.inv.GuiMove;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.inv.ItemEditor;
import me.matl114.hacks.modules.inv.KitReplenish;
import me.matl114.hacks.modules.inv.NbtTooltips;
import me.matl114.hacks.modules.inv.NoQDrop;
import me.matl114.hacks.modules.inv.QuickButton;
import me.matl114.hacks.modules.inv.SaveItem;
import me.matl114.hacks.utils.ItemCache;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.utils.itemdb.ItemStackData;
import me.matl114.utils.itemdb.ItemStackDataWithAmount;
import me.matl114.utils.tasks.LimitedSpeedExecutor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class InvTasks {
    private static FastCraft q;
    private static final int[] b = new int[] {
        36, 37, 38, 39, 40, 41, 42, 43, 44, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
        28, 29, 30, 31, 32, 33, 34, 35, 8, 7, 6, 5, 45, 0, 1, 2, 3, 4
    };
    private static AutoSteal t;
    public static final Codec<ItemStackDataWithAmount> D;
    public static int i;
    private static BlockHitResult g;
    private static InvExtra n;
    private static final LimitedSpeedExecutor E;
    public static final ItemStack d;
    private static ChestHistory v;
    private static final int[] c = new int[46];
    public static Deque<ScreenHandler> k;
    private static ItemEditor x;
    private static AutoShulker u;
    private static QuickButton y;

    @Modifiable
    public static final ModuleGroup m;

    private static SaveItem z;
    private static KitReplenish w;
    private static final MinecraftClient a = MinecraftClient.getInstance();
    public static final Codec<ItemStackData> C;
    public static int l;
    private static GuiMove o;
    private static NoQDrop r;
    private static final ItemCache B;
    public static final ThreadLocal<Boolean> f;
    private static final ItemStack e;
    private static NbtTooltips A;
    public static final int j = 8;
    private static FastInv p;
    private static int h;
    private static AutoStore s;

    @Modifiable
    public static ItemStack getHotbarStack(int hotbar) {
        return hotbar == 40
                ? (ItemStack) a.player.getInventory().offHand.get(0)
                : (ItemStack) a.player.getInventory().main.get(hotbar);
    }

    public static void clickSlotAsync(int slotId, int button, SlotActionType actionType) {
        if (a.player != null) {
            ScreenHandler var3 = ClientPlayerAccess.of(a.player).getServerScreenHandler();
            ScreenHandler var4 = a.player.currentScreenHandler;
            int var5 = var3.syncId;
            boolean var6 = var5 != var4.syncId;

            try {
                if (var6) {
                    a.player.currentScreenHandler = var3;
                }

                a.interactionManager.clickSlot(var5, slotId, button, actionType, a.player);
            } finally {
                if (var6) {
                    a.player.currentScreenHandler = var4;
                }
            }
        }
    }

    public static LimitedSpeedExecutor at() {
        return E;
    }

    @Modifiable
    public static void quickMoveSlot(HandledScreen handler, int index) {
        k(handler.getScreenHandler(), index, false);
    }

    public static KalamaHelperHelperL Z() {
        return (item, button) -> {
            if (button == 1) {
                ao().openEditScreen(item, null);
            }

            return true;
        };
    }

    @Modifiable
    public static void takeAllContainerItem() {
        if (getCurrentServerScreen(a.player) instanceof HandledScreen var1) {
            ScreenHandler var2 = var1.getScreenHandler();

            for (int var3 = 0; var3 < var2.slots.size(); var3++) {
                Slot var4 = var2.getSlot(var3);
                if (var4.inventory instanceof PlayerInventory) {
                    int var5 = var3;
                    E.execute(() ->
                            a.interactionManager.clickSlot(var2.syncId, var5, 1, SlotActionType.QUICK_MOVE, a.player));
                }
            }
        }
    }

    public static SaveItem aq() {
        return z;
    }

    public static ChestHistory am() {
        return v;
    }

    @Modifiable
    public static int r(int v) {
        return c[v];
    }

    @Modifiable
    public static void creativeGive(ItemStack itemStack, int count) {
        if (a.player != null
                && a.interactionManager != null
                && a.interactionManager.getCurrentGameMode().isCreative()) {
            PlayerScreenHandler var2 = a.player.playerScreenHandler;
            int var3 = itemStack.getMaxCount();

            for (int var7 : b) {
                Slot var8 = var2.getSlot(var7);
                int var9 = 0;
                ItemStack var10 = null;
                if (var8.getStack().isEmpty()) {
                    var9 = Math.min(var3, count);
                    var10 = itemStack.copyWithCount(var9);
                } else if (var8.getStack().getCount() < var3
                        && ItemStack.areItemsAndComponentsEqual(var8.getStack(), itemStack)) {
                    var9 = Math.min(var3 - var8.getStack().getCount(), count);
                    var10 = itemStack.copyWithCount(var8.getStack().getCount() + var9);
                }

                count -= var9;
                if (var10 != null) {
                    a.interactionManager.clickCreativeStack(var10, var7);
                    var8.setStack(var10);
                }

                if (count <= 0) {
                    return;
                }
            }

            ItemStack var11 = itemStack.copy();

            while (count > 0) {
                int var12 = Math.min(var3, count);
                count -= var12;
                var11.setCount(var12);
                a.interactionManager.dropCreativeStack(var11);
            }
        }
    }

    @Modifiable
    public static boolean dropAllCursorStack() {
        ClientPlayerEntity var0 = a.player;
        if (var0 == null) {
            return false;
        } else {
            ScreenHandler var1 = ClientPlayerAccess.of(var0).getServerScreenHandler();
            if (var1.getCursorStack() != null && !var1.getCursorStack().isEmpty()) {
                ItemStack var2 = ItemStackUtils.I(var1.getCursorStack(), false, false);
                at().execute(() -> MinecraftClient.getInstance()
                        .interactionManager
                        .clickSlot(var1.syncId, -999, 0, SlotActionType.PICKUP, var0));

                for (int var3 = 0; var3 < var1.slots.size(); var3++) {
                    ItemStack var4 = var1.getSlot(var3).getStack();
                    if (ItemStack.areItemsEqual(var4, var2)) {
                        ItemStack var5 = ItemStackUtils.I(var4, false, false);
                        if (ItemStack.areItemsAndComponentsEqual(var5, var2)) {
                            int var6 = var3;
                            at().execute(() -> MinecraftClient.getInstance()
                                    .interactionManager
                                    .clickSlot(var1.syncId, var6, 1, SlotActionType.THROW, var0));
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    static {
        int var0 = 0;

        while (var0 < b.length) {
            c[b[var0]] = var0++;
        }

        d = new ItemStack(Items.BARRIER);
        e = new ItemStack(Items.BEDROCK);
        f = ThreadLocal.withInitial(() -> false);
        g = null;
        h = -1;
        i = 0;
        k = new ArrayDeque<>();
        l = 0;
        m = new ModuleGroup("Inv");
        B = new ItemCache("sfhelper-configs/recipes/item-database.json");
        C = B.createStackDataCodec();
        D = ItemStackDataWithAmount.createCodecOf(C);
        Listener.n(PlayerInteractBlockC2SPacket.class, InvTasks::listenInteractBlockPacket);
        Listener.ap().getChannel(OpenScreenS2CPacket.class).k(InvTasks::onOpenScreen);
        Listener.M().k(InvTasks::W);
        Listener.ah().k(InvTasks::onOpenScreenCreate);
        Listener.ar().getChannel(ScreenHandlerSlotUpdateS2CPacket.class).k(InvTasks::onInventoryOld);
        Listener.ar().getChannel(InventoryS2CPacket.class).k(InvTasks::onInventoryOld2);
        Listener.ap().getChannel(ScreenHandlerSlotUpdateS2CPacket.class).k(InvTasks::fastAsyncUpdateRevision);
        Listener.ap().getChannel(InventoryS2CPacket.class).k(InvTasks::fastAsyncUpdateRevision2);
        m.registerFactories(InvTasks::ac);
        E = new LimitedSpeedExecutor(n.mI);
        Tasks.e(r -> E.reset());
        HackModules.registerModuleGroup(m);
    }

    public static boolean quickDropSlotItem(HandledScreen handled, Slot slot) {
        if (slot != null && slot.getStack() != null && slot.getStack().getItem() != Items.AIR) {
            ItemStack var2 = ItemStackUtils.I(slot.getStack(), false, false);
            ScreenHandler var3 = handled.getScreenHandler();

            for (int var4 = 0; var4 < var3.slots.size(); var4++) {
                Slot var5 = var3.getSlot(var4);
                if (ItemStack.areItemsEqual(var2, var5.getStack())
                        && ItemStack.areItemsAndComponentsEqual(
                                var2, ItemStackUtils.I(var5.getStack(), false, false))) {
                    int var6 = var4;
                    E.execute(
                            () -> a.interactionManager.clickSlot(var3.syncId, var6, 1, SlotActionType.THROW, a.player));
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static ItemStack generateIconForScreen(HandledScreen<?> screen) {
        if (screen instanceof TileInventory var1 && !var1.isVirtual()) {
            Block var2 = var1.getBlockType();
            if (var2 != null) {
                Item var3 = var2.asItem();
                if (var3 != Items.AIR) {
                    return new ItemStack(var3);
                }
            }

            return e;
        } else {
            return d;
        }
    }

    @Modifiable
    public static String createGiveCommand(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder("/minecraft:give @s ");
            var1.append(Registries.ITEM.getId(itemStack.getItem()));
            if (ItemStackUtils.hasInPatch(itemStack)) {
                var1.append('[');
                HashMap var2 = new HashMap(itemStack.components.changedComponents);
                int var3 = 0;

                for (Entry var5 : ((java.util.Set<Entry>) (var2).entrySet())) {
                    if (var3 > 0) {
                        var1.append(',');
                    }

                    Identifier var6 = Registries.DATA_COMPONENT_TYPE.getId((ComponentType) var5.getKey());
                    if (var6 != null) {
                        String var7 = var6.toString();
                        Optional var8 = (Optional) var5.getValue();
                        if (var8.isPresent()) {
                            try {
                                String var9 = ((ComponentType) var5.getKey())
                                        .getCodecOrThrow()
                                        .encodeStart(ItemStackUtils.registry().getOps(NbtOps.INSTANCE), var8.get())
                                        .getOrThrow()
                                        .toString();
                                var1.append(var7).append('=').append(var9);
                                var3++;
                            } catch (Throwable var10) {
                                Debug.b(var10.getMessage());
                            }
                        } else {
                            var1.append('!').append(var7);
                            var3++;
                        }
                    }
                }

                var1.append(']');
            }

            var1.append(" ").append(itemStack.getCount());
            return var1.toString();
        }
    }

    @Modifiable
    public static boolean isScreenHandlerValid(ScreenHandler handler) {
        return a.player != null && a.player.currentScreenHandler == handler;
    }

    @Modifiable
    public static void moveStackToSlotRanged(
            ScreenHandler handledScreen,
            ItemStack itemStack,
            int toSlot,
            int toAmountAdd,
            boolean removeExist,
            int fromRange,
            int toRange) {
        moveStackToSlot(
                handledScreen,
                itemStack,
                toSlot,
                toAmountAdd,
                removeExist,
                IntStream.range(fromRange, toRange).toArray());
    }

    public static ModuleGroup ad() {
        return m;
    }

    @Modifiable
    public static KalamaHelperHelperB getItemStackMatchingSlot(ScreenHandler screen, ItemStack stack, int... list) {
        if (stack.isEmpty()) {
            return getEmptySlots(screen, list);
        } else {
            KalamaHelperHelperB var3 = new KalamaHelperHelperB();
            var3.setItemSample(stack);
            DefaultedList var4 = screen.slots;

            for (int var8 : list) {
                Slot var9 = (Slot) var4.get(var8);
                if (var9 != null
                        && !var9.getStack().isEmpty()
                        && ItemStack.areItemsAndComponentsEqual(var9.getStack(), stack)) {
                    var3.addMatchingSlot(var8, var9);
                }
            }

            return var3;
        }
    }

    @Modifiable
    public static int q(int v) {
        return b[v];
    }

    public static void fastAsyncUpdateRevision(Event<ScreenHandlerSlotUpdateS2CPacket> eventUpdate) {
        if (a.player != null) {
            int var1 = ((ScreenHandlerSlotUpdateS2CPacket) eventUpdate.b).getSyncId();
            if (a.interactionManager.getCurrentGameMode().isSurvivalLike()) {
                if (var1 == 0) {
                    syncPlayerInventoryRevision(((ScreenHandlerSlotUpdateS2CPacket) eventUpdate.b).getRevision());
                } else {
                    ScreenHandler var2 = ClientPlayerAccess.of(a.player).getServerScreenHandler();
                    if (var2.syncId == var1) {
                        var2.revision = ((ScreenHandlerSlotUpdateS2CPacket) eventUpdate.b).getRevision();
                    }
                }
            }
        }
    }

    @Modifiable
    public static void quickMoveSlotOrDrop(ScreenHandler handler, int slot) {
        if (isScreenHandlerValid(handler)) {
            if (!handler.getCursorStack().isEmpty()) {
                a.interactionManager.clickSlot(handler.syncId, -999, 0, SlotActionType.PICKUP, a.player);
            }

            k(handler, slot, true);
            if (!handler.getSlot(slot).getStack().isEmpty()) {
                a.interactionManager.clickSlot(handler.syncId, slot, 1, SlotActionType.THROW, a.player);
            }
        }
    }

    public static QuickButton ap() {
        return y;
    }

    @Modifiable
    public static void moveStackToSlot(
            ScreenHandler handledScreen,
            ItemStack itemStack,
            int toSlot,
            int toAmountAdd,
            boolean removeExist,
            int... trustedSlotIndexList) {
        if (!itemStack.isEmpty()) {
            ScreenHandler var6 = handledScreen;
            if (isScreenHandlerValid(handledScreen)) {
                if (!handledScreen.getCursorStack().isEmpty()) {
                    a.interactionManager.clickSlot(handledScreen.syncId, -999, 0, SlotActionType.PICKUP, a.player);
                }

                ItemStack var7 = handledScreen.getSlot(toSlot).getStack();
                int var8 = toAmountAdd;
                if (!var7.isEmpty()) {
                    if (ItemStack.areItemsAndComponentsEqual(var7, itemStack)) {
                        var8 = toAmountAdd + var7.getCount();
                    } else {
                        if (!removeExist) {
                            return;
                        }

                        a.interactionManager.clickSlot(
                                handledScreen.syncId, toSlot, 1, SlotActionType.QUICK_MOVE, a.player);
                        if (!handledScreen.getSlot(toSlot).getStack().isEmpty()) {
                            a.interactionManager.clickSlot(
                                    handledScreen.syncId, toSlot, 1, SlotActionType.THROW, a.player);
                        }
                    }
                }

                if (var8 >= itemStack.getMaxCount()) {
                    var8 = itemStack.getMaxCount();
                    if (handledScreen.getSlot(toSlot).getStack().getCount() >= var8) {
                        return;
                    }

                    for (int var12 : trustedSlotIndexList) {
                        if (!var6.getSlot(var12).getStack().isEmpty()
                                && ItemStack.areItemsAndComponentsEqual(
                                        var6.getSlot(var12).getStack(), itemStack)) {
                            moveStackFromTo(var6, var12, toSlot);
                            if (var6.getSlot(toSlot).getStack().getCount() >= var8) {
                                break;
                            }
                        }
                    }
                } else {
                    for (int var18 : trustedSlotIndexList) {
                        if (!var6.getSlot(var18).getStack().isEmpty()
                                && ItemStack.areItemsAndComponentsEqual(
                                        var6.getSlot(var18).getStack(), itemStack)) {
                            int var13 = var6.getSlot(toSlot).getStack().getCount();
                            if (var13 + var6.getSlot(var18).getStack().getCount() > var8) {
                                moveStackFromToAmount(var6, var18, toSlot, var8 - var13);
                                break;
                            }

                            moveStackFromTo(var6, var18, toSlot);
                            if (var6.getSlot(toSlot).getStack().getCount() >= var8) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Modifiable
    public static void d() {
        if (getCurrentServerScreen(a.player) instanceof HandledScreen var1) {
            ScreenHandler var2 = var1.getScreenHandler();

            for (int var3 = 0; var3 < var2.slots.size(); var3++) {
                Slot var4 = var2.getSlot(var3);
                if (!(var4.inventory instanceof PlayerInventory)) {
                    int var5 = var3;
                    E.execute(() ->
                            a.interactionManager.clickSlot(var2.syncId, var5, 1, SlotActionType.QUICK_MOVE, a.player));
                }
            }
        }
    }

    public static void syncPlayerInventoryRevision(int revision) {
        if (l < 0) {
            l = revision;
        } else {
            int var1 = Math.abs(l - revision);
            if (var1 > 20) {
                l = revision;
            } else if (l < revision) {
                l = revision;
            }
        }
    }

    @Modifiable
    public static void creativeAddItem(ItemStack itemStack, int count) {
        if (a.player != null
                && a.interactionManager != null
                && a.interactionManager.getCurrentGameMode().isCreative()) {
            int var2 = -1;
            PlayerScreenHandler var3 = a.player.playerScreenHandler;
            int var4 = itemStack.getMaxCount();
            int var5 = count;

            for (int var9 : b) {
                Slot var10 = var3.getSlot(var9);
                if (var10.getStack().isEmpty()) {
                    var2 = var9;
                    break;
                }

                if (var10.getStack().getCount() < var4
                        && ItemStack.areItemsAndComponentsEqual(var10.getStack(), itemStack)) {
                    var2 = var9;
                    var5 = count + var10.getStack().getCount();
                    break;
                }
            }

            ItemStack var11 = itemStack.copyWithCount(Math.min(var4, var5));
            if (var2 != -1) {
                var3.getSlot(var2).setStack(var11);
                a.interactionManager.clickCreativeStack(var11, var2);
            } else {
                a.interactionManager.dropCreativeStack(var11);
            }
        }
    }

    @Modifiable
    public static void creativeDrop(ItemStack itemStack, int count) {
        if (!itemStack.isEmpty()) {
            a.interactionManager.dropCreativeStack(itemStack.copyWithCount(count));
        }
    }

    @Modifiable
    public static KalamaHelperHelperB getPlayerInventorySlots(ScreenHandler handledScreen) {
        KalamaHelperHelperB var1 = new KalamaHelperHelperB();
        DefaultedList var2 = handledScreen.slots;
        int var3 = var2.size();

        for (int var4 = 0; var4 < var3; var4++) {
            Slot var5 = (Slot) var2.get(var4);
            if (var5 != null && var5.inventory instanceof PlayerInventory) {
                var1.c.add(var4);
            }
        }

        return var1;
    }

    public static ItemEditor ao() {
        return x;
    }

    public static void executePredictInventoryAction(Inventory topInventory, Consumer<ScreenHandler> callback) {
        int var2 = i % 100 + 1;
        GenericContainerScreenHandler var3 = new GenericContainerScreenHandler(
                ScreenUtils.getGenericScreenType(topInventory.size()),
                var2,
                a.player.getInventory(),
                topInventory,
                (topInventory.size() - 1) / 9 + 1);
        GenericContainerScreenHandler.createGeneric9x6(var2, a.player.getInventory());
        ScreenHandler var4 = a.player.currentScreenHandler;

        try {
            a.player.currentScreenHandler = var3;
            callback.accept(var3);
        } finally {
            a.player.currentScreenHandler = var4;
        }
    }

    public static void setCreativeInventory(ItemStack itemStack, int slot) {
        if (slot < 36) {
            a.player.getInventory().setStack(slot, itemStack.copy());
            a.interactionManager.clickCreativeStack(itemStack, b[slot]);
        }
    }

    public static void fastAsyncUpdateRevision2(Event<InventoryS2CPacket> eventUpdate) {
        if (a.player != null) {
            int var1 = ((InventoryS2CPacket) eventUpdate.b).getSyncId();
            if (a.interactionManager.getCurrentGameMode().isSurvivalLike()) {
                if (var1 == 0) {
                    syncPlayerInventoryRevision(((InventoryS2CPacket) eventUpdate.b).getRevision());
                } else {
                    ScreenHandler var2 = ClientPlayerAccess.of(a.player).getServerScreenHandler();
                    if (var2.syncId == var1) {
                        var2.revision = ((InventoryS2CPacket) eventUpdate.b).getRevision();
                    }
                }
            }
        }
    }

    public static void onOpenScreenCreate(Event<HandledScreen<?>> eventScreen) {
        if (eventScreen.b != null && ((HandledScreen) eventScreen.b).getScreenHandler() != null) {
            k.addLast(((HandledScreen) eventScreen.b).getScreenHandler());
        }

        while (k.size() > 8) {
            k.removeFirst();
        }
    }

    @Modifiable
    public static boolean quickMoveSlotItem(HandledScreen handler, int index) {
        Slot var2 = handler.getScreenHandler().getSlot(index);
        return var2 != null ? quickDropSlotItem(handler, var2) : false;
    }

    public static FastInv ag() {
        return p;
    }

    @Modifiable
    public static void K() {
        am().openInventoryCacheScreen();
    }

    private static void ac(ModuleManager m) {
        n = new InvExtra().register(m);
        o = new GuiMove().register(m);
        p = new FastInv().register(m);
        q = new FastCraft().register(m);
        r = new NoQDrop().register(m);
        s = new AutoStore().register(m);
        t = new AutoSteal().register(m);
        u = new AutoShulker().register(m);
        v = new ChestHistory().register(m);
        w = new KitReplenish().register(m);
        x = new ItemEditor().register(m);
        y = new QuickButton().register(m);
        z = new SaveItem().register(m);
        A = new NbtTooltips().register(m);
    }

    public static AutoStore aj() {
        return s;
    }

    public static BlockPos predictScreenFrom(Predicate<Block> targetBlock) {
        int var1 = Tasks.b();
        if (var1 < h + 20 && g != null) {
            BlockPos var2 = g.getBlockPos();
            if (var2 != null && targetBlock.test(a.world.getBlockState(var2).getBlock())) {
                return var2;
            }
        }

        return RaycastUtils.rayTraceSpecificBlock(targetBlock).orElse(null);
    }

    @Modifiable
    public static boolean g(HandledScreen screen, int slotIndex) {
        Slot var2 = screen.getScreenHandler().getSlot(slotIndex);
        return var2 != null ? h(screen, var2) : false;
    }

    public static void onOpenScreen(Event<OpenScreenS2CPacket> packet) {
        i = ((OpenScreenS2CPacket) packet.b).getSyncId();
    }

    @Modifiable
    public static void moveStackFromTo(ScreenHandler handler, int fromIndex, int toSlot) {
        a.interactionManager.clickSlot(handler.syncId, fromIndex, 0, SlotActionType.PICKUP, a.player);
        a.interactionManager.clickSlot(handler.syncId, toSlot, 0, SlotActionType.PICKUP, a.player);
        if (!handler.getCursorStack().isEmpty()) {
            a.interactionManager.clickSlot(handler.syncId, fromIndex, 0, SlotActionType.PICKUP, a.player);
        }
    }

    @Modifiable
    public static boolean h(HandledScreen screen, Slot slot) {
        if (slot != null) {
            ScreenHandler var2 = screen.getScreenHandler();
            ItemStack var3 = ItemStackUtils.I(slot.getStack(), false, false);
            boolean var4 = slot.inventory instanceof PlayerInventory;

            for (int var5 = 0; var5 < var2.slots.size(); var5++) {
                Slot var6 = var2.getSlot(var5);
                if (var6.inventory instanceof PlayerInventory == var4
                        && ItemStack.areItemsEqual(var3, var6.getStack())
                        && ItemStack.areItemsAndComponentsEqual(
                                var3, ItemStackUtils.I(var6.getStack(), false, false))) {
                    quickMoveSlot(screen, var5);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static void k(ScreenHandler handler, int index, boolean ignoreConfig) {
        if (!ignoreConfig
                && handler.getSlot(index).getStack().getCount() > 1
                && ag().leftOne.get()) {
            int var3 = handler.syncId;
            if (!handler.getCursorStack().isEmpty()) {
                Debug.b(Text.literal("[left 1] ")
                        .formatted(Formatting.RED)
                        .append(Text.literal("cursor stack needs to be empty to apply left-one quickMove")));
                return;
            }

            Slot var4 = handler.getSlot(index);
            if (var4.getStack().isEmpty()) {
                return;
            }

            boolean var5 = var4.inventory instanceof PlayerInventory;
            boolean var6 = false;

            for (Slot var8 : handler.slots) {
                if (var8.inventory instanceof PlayerInventory != var5
                        && (var8.getStack().isEmpty()
                                || var8.getStack().getCount() < var8.getStack().getMaxCount()
                                        && ItemStack.areItemsAndComponentsEqual(var4.getStack(), var8.getStack()))) {
                    var6 = true;
                    break;
                }
            }

            if (!var6) {
                return;
            }

            a.interactionManager.clickSlot(var3, index, 0, SlotActionType.PICKUP, a.player);
            a.interactionManager.clickSlot(var3, index, 1, SlotActionType.PICKUP, a.player);
            ItemStack var10 = handler.getCursorStack();
            if (var10.isEmpty()) {
                return;
            }

            var10 = var10.copy();

            for (int var12 = 0; var12 < handler.slots.size(); var12++) {
                Slot var9 = (Slot) handler.slots.get(var12);
                if (var9.inventory instanceof PlayerInventory != var5
                        && (var9.getStack().isEmpty()
                                || var9.getStack().getCount() < var9.getStack().getMaxCount()
                                        && ItemStack.areItemsAndComponentsEqual(var4.getStack(), var9.getStack()))) {
                    a.interactionManager.clickSlot(var3, var12, 0, SlotActionType.PICKUP, a.player);
                    if (handler.getCursorStack().isEmpty()) {
                        return;
                    }
                }
            }

            if (!handler.getCursorStack().isEmpty()) {
                a.interactionManager.clickSlot(var3, index, 0, SlotActionType.PICKUP, a.player);
            }
        } else {
            if (handler.getCursorStack().isEmpty()) {
                a.interactionManager.clickSlot(handler.syncId, -999, 0, SlotActionType.PICKUP, a.player);
            }

            a.interactionManager.clickSlot(handler.syncId, index, 0, SlotActionType.QUICK_MOVE, a.player);
        }
    }

    @Modifiable
    public static Screen getCurrentServerScreen(PlayerEntity player) {
        if (player == null) {
            return null;
        } else {
            Object var1 = null;
            if (player instanceof ClientPlayerAccess var2) {
                var1 = var2.getServerOpeningScreen();
            } else {
                var1 = MinecraftClient.getInstance().currentScreen;
            }

            return (Screen) (var1 instanceof CreativeInventoryScreen ? null : var1);
        }
    }

    @Modifiable
    public static void copyGiveCommand(ItemStack itemStack) {
        a.keyboard.setClipboard(createGiveCommand(itemStack.copyWithCount(itemStack.getMaxCount())));
    }

    public static void onInventoryOld2(Event<InventoryS2CPacket> eventInv) {
        int var1 = ((InventoryS2CPacket) eventInv.b).getSyncId();
        if (a.interactionManager.getCurrentGameMode().isSurvivalLike()
                && ClientPlayerAccess.of(a.player).getServerScreenHandler().syncId != var1
                && a.player.currentScreenHandler.syncId != var1
                && var1 != 0) {
            InventoryS2CPacket var2 = (InventoryS2CPacket) eventInv.b;

            for (ScreenHandler var4 : k) {
                if (var4.syncId == var1) {
                    var4.updateSlotStacks(var2.getRevision(), var2.getContents(), var2.getCursorStack());
                    return;
                }
            }
        }
    }

    public static NoQDrop ai() {
        return r;
    }

    @Modifiable
    public static void Y(ItemStack item, Consumer<ItemStack> callback) {
        ao().openEditScreen(item, callback);
    }

    @Modifiable
    public static KalamaHelperHelperB allSlotMatch(HandledScreen handledScreen) {
        KalamaHelperHelperB var1 = new KalamaHelperHelperB();
        int[] var2 = IntStream.range(0, handledScreen.getScreenHandler().slots.size())
                .toArray();
        var1.c = new IntArrayList(var2);
        return var1;
    }

    public static KitReplenish an() {
        return w;
    }

    public static ItemCache as() {
        return B;
    }

    public static AutoSteal ak() {
        return t;
    }

    @Modifiable
    public static void moveStackFromToAmount(ScreenHandler handler, int fromIndex, int toSlot, int amount) {
        Slot var4 = handler.getSlot(fromIndex);
        Slot var5 = handler.getSlot(toSlot);
        int var6 = var4.getStack().getCount();
        if (var6 <= amount) {
            moveStackFromTo(handler, fromIndex, toSlot);
        } else {
            int var7 = var5.getStack().getCount();
            int var8 = var4.getStack().getMaxCount();
            if (var7 + amount >= var8) {
                moveStackFromTo(handler, fromIndex, toSlot);
            } else {
                for (int var9 = 0; var9 < 10; var9++) {
                    if (amount <= 0) {
                        return;
                    }

                    int var10 = (var6 + 1) / 2;
                    int var11 = Math.abs(var10 - amount);
                    int var12 = Math.min(Math.min(amount, var6 - amount), var11);
                    if (var12 == amount) {
                        a.interactionManager.clickSlot(handler.syncId, fromIndex, 0, SlotActionType.PICKUP, a.player);

                        for (int var16 = 0; var16 < amount; var16++) {
                            a.interactionManager.clickSlot(handler.syncId, toSlot, 1, SlotActionType.PICKUP, a.player);
                        }

                        if (!handler.getCursorStack().isEmpty()) {
                            a.interactionManager.clickSlot(
                                    handler.syncId, fromIndex, 0, SlotActionType.PICKUP, a.player);
                        }

                        return;
                    }

                    if (var12 == var6 - amount) {
                        a.interactionManager.clickSlot(handler.syncId, fromIndex, 0, SlotActionType.PICKUP, a.player);

                        for (int var15 = 0; var15 < var12; var15++) {
                            a.interactionManager.clickSlot(
                                    handler.syncId, fromIndex, 1, SlotActionType.PICKUP, a.player);
                        }

                        a.interactionManager.clickSlot(handler.syncId, toSlot, 0, SlotActionType.PICKUP, a.player);
                        return;
                    }

                    if (var10 > amount) {
                        a.interactionManager.clickSlot(handler.syncId, fromIndex, 1, SlotActionType.PICKUP, a.player);
                        int var13 = (var6 + 1) / 2 - amount;

                        for (int var14 = 0; var14 < var13; var14++) {
                            a.interactionManager.clickSlot(
                                    handler.syncId, fromIndex, 1, SlotActionType.PICKUP, a.player);
                        }

                        a.interactionManager.clickSlot(handler.syncId, toSlot, 0, SlotActionType.PICKUP, a.player);
                        return;
                    }

                    a.interactionManager.clickSlot(handler.syncId, fromIndex, 1, SlotActionType.PICKUP, a.player);
                    a.interactionManager.clickSlot(handler.syncId, toSlot, 0, SlotActionType.PICKUP, a.player);
                    amount = amount - var5.getStack().getCount() + var7;
                    var7 = var5.getStack().getCount();
                    var6 = var4.getStack().getCount();
                }

                Debug.b(Text.literal("Error while transfering itemStacks, which takes 10 more loop "));
            }
        }
    }

    public static InvExtra ae() {
        return n;
    }

    @Modifiable
    public static void G(
            ScreenHandler screen, ItemStack[] ingredients, int[] slot, int patternAmount, boolean removeOrigin) {
        int[] var5 = getPlayerInventorySlots(screen).toIntArray();
        moveRecipePatternToContainer(
                screen,
                ingredients,
                slot,
                patternAmount,
                removeOrigin,
                (screen1, itemStack) -> getItemStackMatchingSlot(screen1, itemStack, var5));
    }

    public static AutoShulker al() {
        return u;
    }

    public static void a() {}

    public static void openEditorForPlayer() {
        if (a.player != null) {
            ao().openEditorForPlayer(a.player);
        }
    }

    @Modifiable
    public static KalamaHelperHelperB getContainerSlots(HandledScreen handledScreen) {
        KalamaHelperHelperB var1 = new KalamaHelperHelperB();
        DefaultedList var2 = handledScreen.getScreenHandler().slots;
        int var3 = var2.size();

        for (int var4 = 0; var4 < var3; var4++) {
            Slot var5 = (Slot) var2.get(var4);
            if (var5 != null && !(var5.inventory instanceof PlayerInventory)) {
                var1.c.add(var4);
            }
        }

        return var1;
    }

    @Modifiable
    public static void i(HandledScreen handler, int index, boolean ignoreConfig) {
        k(handler.getScreenHandler(), index, ignoreConfig);
    }

    private static void listenInteractBlockPacket(PlayerInteractBlockC2SPacket packet) {
        if (packet.getBlockHitResult().getType() == Type.BLOCK) {
            g = packet.getBlockHitResult();
            h = Tasks.b();
        }
    }

    public static FastCraft ah() {
        return q;
    }

    @Modifiable
    public static int getTopInventorySize() {
        if (a.currentScreen instanceof HandledScreen var1 && var1.getScreenHandler() != null) {
            int var4 = 0;

            for (Slot var3 : var1.getScreenHandler().slots) {
                if (var3.inventory instanceof PlayerInventory) {
                    return var4;
                }

                var4++;
            }

            return var4;
        } else {
            return 0;
        }
    }

    public static NbtTooltips ar() {
        return A;
    }

    public static int predictOpenVanillaContainerSize(BlockPos blockPos) {
        if (a.world.getBlockEntity(blockPos) instanceof Inventory var2) {
            int var7 = var2.size();
            if (var2 instanceof ChestBlockEntity var3) {
                BlockState var4 = var3.getCachedState();
                if (var4.getBlock() instanceof ChestBlock var6) {
                    if (ChestBlock.isChestBlocked(a.world, blockPos)) {
                        var7 = 0;
                    } else if (ChestBlock.getDoubleBlockType(var4)
                            != net.minecraft.block.DoubleBlockProperties.Type.SINGLE) {
                        var7 = 54;
                    }
                }
            }

            if (var2 instanceof ShulkerBoxBlockEntity var8) {
                BlockState var9 = var8.getCachedState();
                if (var8.getAnimationStage() == AnimationStage.CLOSED
                        && !InteractUtils.canShulkerOpen(a.world, blockPos, var9)) {
                    var7 = 0;
                }
            }

            return var7;
        } else {
            return 0;
        }
    }

    public static void onInventoryOld(Event<ScreenHandlerSlotUpdateS2CPacket> invS2CPacket) {
        int var1 = ((ScreenHandlerSlotUpdateS2CPacket) invS2CPacket.b).getSyncId();
        if (a.interactionManager.getCurrentGameMode().isSurvivalLike()
                && ClientPlayerAccess.of(a.player).getServerScreenHandler().syncId != var1
                && a.player.currentScreenHandler.syncId != var1
                && var1 != 0) {
            ScreenHandlerSlotUpdateS2CPacket var2 = (ScreenHandlerSlotUpdateS2CPacket) invS2CPacket.b;

            for (ScreenHandler var4 : k) {
                if (var4.syncId == var1) {
                    var4.setStackInSlot(var2.getSlot(), var2.getRevision(), var2.getStack());
                    return;
                }
            }
        }
    }

    public static void moveRecipePatternToContainer(
            ScreenHandler screen,
            ItemStack[] ingredients,
            int[] slot,
            int patternAmount,
            boolean removeOrigin,
            BiFunction<ScreenHandler, ItemStack, KalamaHelperHelperB> slotMatchProvider) {
        int var6 = ingredients.length;
        Preconditions.checkArgument(slot.length == var6);
        HashMap<ItemStackSample, IntList> var7 = new HashMap<>();
        IntArrayList var8 = new IntArrayList();

        for (int var9 = 0; var9 < var6; var9++) {
            ItemStack var10 = ingredients[var9];
            if (var10 != null && !var10.isEmpty()) {
                ItemStackSample var11 = new ItemStackSample(var10);
                int var12 = var9;
                var7.compute(var11, (key, list) -> {
                    if (list == null) {
                        list = new IntArrayList();
                    }

                    list.add(var12);
                    return list;
                });
            } else {
                var8.add(var9);
            }
        }

        for (Entry<ItemStackSample, IntList> var23 : var7.entrySet()) {
            ItemStackSample var25 = (ItemStackSample) var23.getKey();
            KalamaHelperHelperB var26 = (KalamaHelperHelperB) slotMatchProvider.apply(screen, var25.fS());
            int var13 = var26.count;
            int[] var14 = var26.toIntArray();
            ItemStack var15 = var26.a;
            if (var15 != null && var13 != 0) {
                int var16 = 0;
                IntListIterator var17 = ((IntList) var23.getValue()).iterator();

                while (var17.hasNext()) {
                    Integer var18 = (Integer) var17.next();
                    var16 += ingredients[var18].getCount();
                }

                int var27 = Math.min(var13 / var16, patternAmount);
                IntListIterator var28 = ((IntList) var23.getValue()).iterator();

                while (var28.hasNext()) {
                    Integer var19 = (Integer) var28.next();
                    int var20 = ingredients[var19].getCount() * var27;
                    moveStackToSlot(screen, var15, slot[var19], var20, removeOrigin, var14);
                }
            }
        }

        IntListIterator var22 = var8.iterator();

        while (var22.hasNext()) {
            Integer var24 = (Integer) var22.next();
            quickMoveSlotOrDrop(screen, var24);
        }
    }

    public static void W(Event<ClientPlayerEntity> gameJoin) {
        i = 0;
        k.clear();
    }

    public static GuiMove af() {
        return o;
    }

    @Modifiable
    public static boolean f(Screen screen) {
        return screen instanceof HandledScreen;
    }

    @Modifiable
    public static KalamaHelperHelperB getEmptySlots(ScreenHandler handledScreen, int... slots) {
        KalamaHelperHelperB var2 = new KalamaHelperHelperB();
        DefaultedList var3 = handledScreen.slots;

        for (int var7 : slots) {
            Slot var8 = (Slot) var3.get(var7);
            if (var8 != null && var8.getStack().isEmpty()) {
                var2.c.add(var7);
            }
        }

        return var2;
    }
}
