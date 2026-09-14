package me.matl114.hacks.modules.inv;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Runnables;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.config.KalamaHelperHelperD;
import me.matl114.gui.complex.invcache.InventoryViewScreen;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.gui.presets.single.ConfirmingWidgetScreen;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.Interact;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.collections.MutableRecord;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.KalamaHelperHelperB;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.kv.EnumAttrKeyValue;
import me.matl114.utils.inventory.ItemStackSample;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.Consumers;
import org.apache.commons.lang3.mutable.MutableObject;

public class KitReplenish extends BaseModule {
    public final EnumRef<KitReplenish$Choice> shulkerMatchChoice;
    public final ModulePath az;
    public InvSubHelperF aB;
    public final FlagRef enableEnderReplenish;
    private boolean av;
    public final FlagRef zeroTickSupply;
    public final ModulePath ai = makePath(Configs.l, "auto-inv.replenish");
    public final KeyBindRef autoPlaceShulker;
    private InvSubHelperB at;
    public final NBTRef<OptionalPrimitive<Integer>> specificSlot;
    public final IntRef searchRange;
    public List<Vec3i> aj = new ArrayList<>();
    private static final List<ItemStack> aC = Collections.nCopies(9, ItemStack.EMPTY);
    private int ay;
    private final TimerExecutor ax;
    public FileStorage aA;
    private InvSubHelperC aw;
    public final FlagRef log;
    public final KeyBindRef executeReplenish;
    public static KitReplenish INSTANCE;
    public final KeyBindRef autoEnderChest;
    private InvSubHelperG au;

    private boolean bA(CommandExecution sender, ArgumentInputStream streamArgs) {
        String var3 = streamArgs.o();
        InvSubHelperB var4 = this.bF(var3);
        if (var4 == null) {
            sender.sm("&c未找到Kit: " + var3);
            return true;
        } else {
            this.at = var4;
            sender.sm("&a已设置本次补给Kit: " + var3);
            return true;
        }
    }

    private void bi(InvSubHelperC request) {
        if (this.aw != null) {
            this.aw.failureCallback().run();
            this.aw = null;
        }

        this.aw = request;
    }

    public void bM(InvSubHelperF list) {
        this.aB = list;
        this.aA.f(InvSubHelperF.CODEC, this.aB);
    }

    public BlockPos bL() {
        BlockPos var1 = mc.player.getBlockPos();
        return this.aj.stream()
                .<BlockPos>map(var1::add)
                .map(s -> {
                    if (!InteractExtra.INSTANCE.fC(mc.player.getPos(), s)) {
                        return null;
                    } else {
                        BlockState var1x = mc.world.getBlockState(s);
                        return (BlockPos)
                                (var1x.getBlock() == Blocks.ENDER_CHEST && InteractUtils.canEnderChestOpen(mc.world, s)
                                        ? s
                                        : null);
                    }
                })
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble(s -> s.getSquaredDistance(var1)))
                .orElse(null);
    }

    private Stream<String> bE() {
        return this.aB.vD().stream().map(InvSubHelperB::name);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.N(), this::bk);
        this.registerListener(Listener.bd(), this::bl);
        this.registerCommandBootstrap(this::bx);
    }

    private int bw(
            Inventory templateInventory,
            PlayerInventory playerInventory,
            ItemStack templateStack,
            int targetIndex,
            int from,
            int to) {
        int var7 = -1;

        for (int var8 = from; var8 < to; var8++) {
            if (var8 != targetIndex) {
                ItemStack var9 = playerInventory.getStack(var8);
                if (ItemStack.areItemsAndComponentsEqual(var9, templateStack)) {
                    if (var7 < 0) {
                        var7 = var8;
                    }

                    ItemStack var10 = templateInventory.getStack(var8);
                    if (!ItemStack.areItemsAndComponentsEqual(var9, var10)) {
                        return var8;
                    }
                }
            }
        }

        return var7;
    }

    private void bj() {
        if (this.au != null && !this.au.isCompleted()) {
            this.au.e(false);
        }

        this.au = null;
    }

    public Stream<Pair<BlockPos, BlockHitResult>> bJ() {
        BlockPos var1 = mc.player.getBlockPos();
        Vec3d var2 = mc.player.getPos();
        Direction var3 = mc.player.getFacing();
        return this.aj.stream()
                .<BlockPos>map(var1::add)
                .map(s -> {
                    BlockState var3x = mc.world.getBlockState(s);
                    if (!var3x.isAir() && !var3x.isLiquid() && !var3x.isReplaceable()) {
                        return null;
                    } else if (!InteractExtra.INSTANCE.fC(mc.player.getPos(), s)) {
                        return null;
                    } else {
                        List<FlagEntry<BlockHitResult>> var4 = InteractionTasks.p(var2, s, var3, false, false);
                        return var4.isEmpty()
                                ? null
                                : var4.stream()
                                        .filter(hitResult -> {
                                            if (InteractUtils.C(mc.player, (FlagEntry<BlockHitResult>) hitResult)
                                                    && InteractExtra.INSTANCE.fC(
                                                            mc.player.getPos(),
                                                            ((BlockHitResult) hitResult.val()).getBlockPos())) {
                                                BlockState var2x = InteractUtils.getBlockPlacement(
                                                        Blocks.SHULKER_BOX, mc.player, mc.world, (BlockHitResult)
                                                                hitResult.val());
                                                return var2x != null
                                                        ? InteractUtils.canShulkerOpen(mc.world, s, var2x)
                                                        : false;
                                            } else {
                                                return false;
                                            }
                                        })
                                        .findFirst()
                                        .map(hit -> Pair.of(s, (BlockHitResult) hit.val()))
                                        .orElse(null);
                    }
                })
                .filter(Objects::nonNull);
    }

    private void bB(CommandExecution sender) {
        this.at = null;
        sender.sm("&a已清除临时补给Kit");
    }

    public void bN() {
        List<MutableRecord> var1 = this.aB.vD().stream()
                .map(s -> MutableRecord.of(InvSubHelperB.ds, s))
                .collect(Collectors.toCollection(ArrayList::new));
        int var2 = this.aB.vC();
        MutableObject<MutableRecord> var3 =
                new MutableObject<>(var2 >= 0 && var2 < var1.size() ? (MutableRecord) var1.get(var2) : null);
        ListEntryWidgetController var4 = ListEntryWidgetController.mutable(
                var1, () -> MutableRecord.of(InvSubHelperB.ds, InvSubHelperB.dr), v -> this.bO(var3, var1, v), 45, 250);
        KalamaHelperHelperD var5 = new KalamaHelperHelperD(var4, 0, 0, 330, 260);
        ConfirmingWidgetScreen var6 = new ConfirmingWidgetScreen(
                Text.translatable("widget.kit-manager.open-kit-list.title"), var5, () -> true, () -> {
                    List<InvSubHelperB> var3x =
                            var1.stream().map(s -> s.j(InvSubHelperB.class)).toList();
                    int var4x = var1.indexOf(var3.getValue());
                    this.bM(new InvSubHelperF(var4x, var3x));
                });
        var6.access().openFromCurrent();
    }

    private void bt(ScreenHandler handler, InvSubHelperG transaction) {
        Inventory var3 = InventoryUtils.getTopInventory(handler);
        InvSubHelperL var4 = transaction.k;
        int var5 = var4.Is();
        int var6 = var4.It();
        PlayerInventory var7 = mc.player.getInventory();
        List<KalamaHelperHelperK<ItemStackSample>> var8 = transaction.r.entrySet().stream()
                .map(s -> new KalamaHelperHelperK<>(
                        s.getValue() * 64 / s.getKey().fS().getMaxCount(), s.getKey()))
                .collect(Collectors.toCollection(ArrayList::new));
        Comparator<KalamaHelperHelperK<ItemStackSample>> var9 =
                Comparator.<KalamaHelperHelperK<ItemStackSample>>comparingInt(KalamaHelperHelperK::index)
                        .thenComparingLong(s -> ((ItemStackSample) s.val()).hashCode())
                        .reversed();
        var8.sort(var9);

        while (!var8.isEmpty()) {
            KalamaHelperHelperK<ItemStackSample> var10 = var8.remove(0);
            int var11 = -1;

            for (int var12 = 0; var12 < var3.size(); var12++) {
                if (ItemStack.areItemsAndComponentsEqual(var3.getStack(var12), ((ItemStackSample) var10.val()).fS())) {
                    var11 = var12;
                    break;
                }
            }

            if (var11 >= 0) {
                boolean var17 = false;

                for (int var13 = var5; var13 < var6; var13++) {
                    ItemStack var14 = var7.getStack(var13);
                    if (var14.isEmpty()) {
                        var17 = true;
                        break;
                    }

                    if (var14.getCount() < var14.getMaxCount()
                            && ItemStack.areItemsAndComponentsEqual(var14, ((ItemStackSample) var10.val()).fS())) {
                        var17 = true;
                        break;
                    }
                }

                if (var17) {
                    int var18 = var3.getStack(var11).getCount();
                    int var19 = ((ItemStackSample) var10.val()).fS().getMaxCount();
                    mc.interactionManager.clickSlot(handler.syncId, var11, 0, SlotActionType.QUICK_MOVE, mc.player);
                    int var15 = var3.getStack(var11).getCount();
                    if (var15 < var18) {
                        int var16 = var10.index() - (var18 - var15) * 64 / var19;
                        if (var16 > 0) {
                            var8.add(new KalamaHelperHelperK<>(var16, (ItemStackSample) var10.val()));
                            var8.sort(var9);
                        }
                    }
                }
            }
        }
    }

    public void bH() {
        if (!checkNull()) {
            if (this.au != null) {
                if (!this.au.isCompleted()) {
                    this.logI18N("message.kit-manager.kit-replenish.request.blocked", new Object[0]);
                    return;
                }

                this.bj();
            }

            this.bj();
            this.aw = null;
            this.av = false;
            this.ay = -1;
            InvSubHelperB var1 = this.at != null ? this.at : this.aB.vB();
            if (var1 != null) {
                this.logI18N("message.kit-manager.kit-replenish.request.success", new Object[] {var1.name()});
                this.au = new InvSubHelperG();
                this.au.setKit(var1);
                this.au.d(this.enableEnderReplenish.get());
            } else {
                this.logI18N("message.kit-manager.kit-replenish.request.failure", new Object[0]);
            }
        }
    }

    private void bx(MainCommand mainCommand) {
        TreeSubCommand var2 = mainCommand.bD().a("replenish").k();
        KalamaHelperHelperB var3 = KalamaHelperHelperA.a().B("name").d(this::bE).v();
        var2.<me.matl114.utils.commands.commandGroup.KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("import")
                .A(KalamaHelperHelperA.a().B("name").v())
                .z(e -> e.executor(KalamaHelperHelperH.n(this::by)))
                .r()
                .<me.matl114.utils.commands.commandGroup.KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("importitem")
                .A(KalamaHelperHelperA.a().B("name").v())
                .z(e -> e.executor(KalamaHelperHelperH.n(this::bz)))
                .r()
                .<me.matl114.utils.commands.commandGroup.KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("request")
                .A(var3)
                .z(e -> e.executor(KalamaHelperHelperH.n(this::bA)))
                .r()
                .<me.matl114.utils.commands.commandGroup.KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("clearRequest")
                .z(e -> e.executor(KalamaHelperHelperH.l(this::bB)))
                .r()
                .<me.matl114.utils.commands.commandGroup.KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("start")
                .z(e -> e.executor(KalamaHelperHelperH.k(this::bC)))
                .r()
                .subBuilder(SubCommand.bo())
                .u("edit")
                .z(e -> e.executor(KalamaHelperHelperH.l(this::bD)))
                .r();
    }

    private boolean by(CommandExecution sender, ArgumentInputStream streamArgs) {
        if (mc.player == null) {
            sender.sm("&c当前不在游戏内");
            return true;
        } else {
            String var3 = streamArgs.o();
            if (this.bF(var3) != null) {
                sender.sm("&cKit已存在: " + var3);
                return true;
            } else {
                InvSubHelperB var4 = this.bR(var3, mc.player.getInventory(), InventoryUtils.F(), InvSubHelperL.wI);
                this.bG(var4);
                sender.sm("&a已导入当前背包为Kit: " + var3);
                return true;
            }
        }
    }

    public boolean bI() {
        if (mc.player != null && this.aw == null) {
            Slot var1 = ScreenUtils.getSelectingOrHandSlot();
            if (var1 != null && this.bn(var1.getStack())) {
                this.bi(new InvSubHelperC(
                        Optional.empty(), Optional.of(var1), Consumers.nop(), Runnables.doNothing(), false));
                return true;
            }
        }

        return false;
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        KalamaHelperHelperCX var5 = new KalamaHelperHelperCX(0, dblank, dx, dy);
        var5.Q(this.createRefKeyLabel(
                () -> Text.translatable("widget.kit-manager.kit-save-map"),
                () -> ChatUtils.parseTranslation("widget.kit-manager.kit-save-map.tooltips", "暂无介绍"),
                140,
                dy));
        var5.Q(this.createExecuteButton(
                "widget.kit-manager.open-kit-list", ButtonAction.a(this::bN), 150, 0, dx - 140 - 10, dy));
        acceptor.accept(var5);
        acceptor.accept(DisplayWidget.instance(0, dblank, dx, dy)
                .setRenderHandler(new ButtonElement(
                        el -> {
                            InvSubHelperB var2 = this.aB.vB();
                            return var2 != null
                                    ? Text.translatable("widget.kit-manager.kit-default.present", new Object[] {
                                        var2.name(), var2.hB().Ir().resultAsString()
                                    })
                                    : Text.translatable("widget.kit-manager.kit-default.absent");
                        },
                        ButtonAction.c())));
        acceptor.accept(this.createTitleLabel("widget.kit-manager.command", 0, dblank, dx, dy));
        acceptor.accept(this.createTitleLabel("widget.interact.interact-all.use-argument", 0, dblank, dx, dy));
    }

    public KitReplenish() {
        super("KitReplenish");
        this.shulkerMatchChoice = this.builder(this.ai.add("shulker-match-choice"), KitReplenish$Choice.class)
                .defaultValue(KitReplenish$Choice.NUM_MATCH)
                .build();
        this.enableEnderReplenish =
                this.flagBuilder(this.ai.add("enable-ender-replenish")).build();
        this.specificSlot = this.builder(this.ai.add("specific-slot"), OptionalPrimitive.INT_TYPE)
                .defaultValue(new OptionalPrimitive<>(false, NBTTypes.c, 8))
                .validator(s -> s.getValue() < 9 && s.getValue() >= 0)
                .build();
        this.searchRange = this.intBuilder(this.ai.add("search-range"))
                .defaultValue(3)
                .validator(Configs.e)
                .updateListener(s -> this.aj = MathUtils.H(s.intValue()))
                .build();
        this.zeroTickSupply = this.flagBuilder(this.ai.add("zero-tick-supply")).build();
        this.log = this.builder(this.ai.add("log"), Boolean.class)
                .defaultValue(true)
                .build();
        this.executeReplenish = this.hotkey(this.ai.add("execute-replenish"), new MultiKeyBind())
                .registerHotkey(HotKeyUtils.b(this::bH))
                .build();
        this.autoEnderChest = this.hotkey(this.ai.add("auto-ender-chest"), new MultiKeyBind())
                .registerHotkey(HotKeyUtils.b(() -> this.av = true))
                .build();
        this.autoPlaceShulker = this.hotkey(this.ai.add("auto-place-shulker"), new MultiKeyBind())
                .registerHotkey(HotKeyUtils.e(this::bI))
                .build();
        this.at = null;
        this.au = null;
        this.av = false;
        this.aw = null;
        this.ax = new TimerExecutor();
        this.ay = -1;
        this.az = makePath(Configs.l, "kit-manager");
        this.aA = FileManager.getInstance().o("kit.nbt");
        this.aB = this.aA.read(InvSubHelperF.CODEC, () -> new InvSubHelperF());
        INSTANCE = this;
    }

    private void bD(CommandExecution sender) {
        Tasks.l(this::bN, 1);
        sender.sm("&a正在打开Kit编辑界面");
    }

    private boolean bn(ItemStack stack) {
        return stack.getItem() instanceof BlockItem var3 && var3.getBlock() instanceof ShulkerBoxBlock;
    }

    private Double bp(ItemStack stack, Inventory view) {
        if (this.bn(stack) && stack.contains(DataComponentTypes.CONTAINER)) {
            ContainerComponent var3 = (ContainerComponent) stack.get(DataComponentTypes.CONTAINER);
            if (var3 != null && !Objects.equals(ContainerComponent.DEFAULT, var3)) {
                Inventory var4 = InventoryUtils.c(var3.stacks);
                LinkedHashMap<Item, Integer> var5 = new LinkedHashMap<>();
                boolean var6 = false;

                for (ItemStack var8 : InventoryUtils.a(view)) {
                    Item var9 = var8.getItem();
                    Integer var10 = var5.get(var9);
                    if (var10 != null) {
                        var5.put(var9, var10 - 1);
                    } else {
                        int var11 = -1;

                        for (ItemStack var13 : InventoryUtils.a(var4)) {
                            if (var13.isOf(var9)) {
                                var6 = true;
                                var11++;
                            }
                        }

                        var5.put(var9, var11);
                    }
                }

                if (!var6) {
                    return null;
                }

                double var14 = 0.0;

                for (Integer var17 : var5.values()) {
                    var14 += -Math.abs(var17.doubleValue());
                }

                return var14;
            }
        }

        return null;
    }

    private Double bo(ItemStack stack, Inventory view) {
        if (this.bn(stack) && stack.contains(DataComponentTypes.CONTAINER)) {
            ContainerComponent var3 = (ContainerComponent) stack.get(DataComponentTypes.CONTAINER);
            if (var3 != null && !Objects.equals(ContainerComponent.DEFAULT, var3)) {
                Inventory var4 = InventoryUtils.c(var3.stacks);
                int var5 = Math.min(view.size(), var4.size());
                double var6 = 0.0;

                for (int var8 = 0; var8 < var5; var8++) {
                    if (ItemStack.areItemsEqual(view.getStack(var8), var4.getStack(var8))) {
                        var6++;
                    }
                }

                return var6 > 0.0 ? var6 * 1000.0 : this.bp(stack, view);
            }
        }

        return null;
    }

    private boolean bC(PlayerEntity ignored, ArgumentInputStream streamArgs) {
        this.bH();
        return true;
    }

    public Stream<Pair<BlockPos, BlockHitResult>> bK(boolean ender) {
        BlockPos var2 = mc.player.getBlockPos();
        Vec3d var3 = mc.player.getPos();
        Direction var4 = mc.player.getFacing();
        return this.aj.stream()
                .<BlockPos>map(var2::add)
                .map(s -> {
                    BlockState var4x = mc.world.getBlockState(s);
                    if (!var4x.isAir() && !var4x.isLiquid() && !var4x.isReplaceable()) {
                        return null;
                    } else if (!InteractExtra.INSTANCE.fC(mc.player.getPos(), s)) {
                        return null;
                    } else {
                        FlagEntry var5 = InteractionTasks.o(var3, s, var4, false, false);
                        if (InteractUtils.C(mc.player, var5)
                                && InteractExtra.INSTANCE.fC(
                                        mc.player.getPos(), ((BlockHitResult) var5.val()).getBlockPos())) {
                            BlockState var6 = InteractUtils.getBlockPlacement(
                                    ender ? Blocks.ENDER_CHEST : Blocks.CHEST, mc.player, mc.world, (BlockHitResult)
                                            var5.val());
                            return var6 == null
                                            || (ender
                                                    ? !InteractUtils.canEnderChestOpen(mc.world, s)
                                                    : !InteractUtils.canChestOpen(mc.world, s, var6))
                                    ? null
                                    : Pair.of(s, (BlockHitResult) var5.val());
                        } else {
                            return null;
                        }
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(s -> ((BlockPos) s.getFirst()).getSquaredDistance(var2)));
    }

    public static Inventory bQ(ContainerComponent component) {
        ArrayList var1 = new ArrayList<>(aC);
        component.stream().forEach(var1::add);
        return InventoryUtils.d(var1);
    }

    public static Inventory bP(InvSubHelperB kit) {
        List<KalamaHelperHelperK<ItemStack>> var1 = kit.hy();
        int var2 = kit.hA();
        ItemStack[] var3 = new ItemStack[var2];
        Arrays.fill(var3, ItemStack.EMPTY);

        for (KalamaHelperHelperK var5 : var1) {
            if (var5.index() >= 0 && var5.index() < var2) {
                var3[var5.index()] = (ItemStack) var5.val();
            }
        }

        return InventoryUtils.f(var3);
    }

    private void bs(ScreenHandler handler, InvSubHelperG transaction) {
        Inventory var3 = InventoryUtils.getTopInventory(handler);
        InvSubHelperL var4 = transaction.k;
        int var5 = var4.Is();
        int var6 = var4.It();
        Inventory var7 = transaction.l;
        PlayerInventory var8 = mc.player.getInventory();

        for (int var9 = var5; var9 < var6; var9++) {
            if (var9 != transaction.q) {
                ItemStack var10 = var7.getStack(var9);
                ItemStack var11 = var8.getStack(var9);
                if (!var10.isEmpty()
                        && !this.bn(var10)
                        && !this.bn(var11)
                        && (var4.dump() || var11.isEmpty() || ItemStack.areItemsAndComponentsEqual(var11, var10))) {
                    int var12 = handler.getSlotIndex(var8, var9).getAsInt();

                    for (int var13 = 0; var13 < var3.size(); var13++) {
                        ItemStack var14 = var3.getStack(var13);
                        if (ItemStack.areItemsAndComponentsEqual(var10, var14)) {
                            InvExtra.INSTANCE.mergeScreenSlotTo(var13, var12);
                            if (var8.getStack(var9).getCount() >= var10.getCount()) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public InvSubHelperB bR(String name, Inventory inventory, int maxSize, InvSubHelperL type) {
        List<KalamaHelperHelperK<ItemStack>> var5 = InventoryUtils.getInventoryEntries(inventory);
        return InvSubHelperB.fromItem(name, var5, maxSize, type);
    }

    public KalamaHelperHelperK<ItemStack> bm(Inventory inventory, InvSubHelperG transaction) {
        Function<ItemStack, Double> var3 =
                switch ((KitReplenish$Choice) this.shulkerMatchChoice.get()) {
                    case SLOT_MATCH -> stack -> this.bo(stack, transaction.m);
                    case ITEM_EXIST -> stack -> this.bp(stack, transaction.m);
                    case NUM_MATCH -> stack -> this.bq(stack, transaction.r);
                };
        return InventoryUtils.J(inventory, var3, false);
    }

    private boolean bz(CommandExecution sender, ArgumentInputStream streamArgs) {
        String var3 = streamArgs.o();
        if (this.bF(var3) != null) {
            sender.sm("&cKit已存在: " + var3);
            return true;
        } else {
            ItemStack var4 = ScreenUtils.getSelectingOrHandItem();
            if (var4 != null && !var4.isEmpty()) {
                ContainerComponent var5 = (ContainerComponent) var4.get(DataComponentTypes.CONTAINER);
                if (var5 == null) {
                    sender.sm("&c当前物品不包含容器内容");
                    return true;
                } else {
                    Inventory var6 = bQ(var5);
                    InvSubHelperB var7 = this.bR(var3, var6, var6.size(), InvSubHelperL.wI);
                    this.bG(var7);
                    sender.sm("&a已导入当前容器物品为Kit: " + var3);
                    return true;
                }
            } else {
                sender.sm("&c当前没有可导入的物品");
                return true;
            }
        }
    }

    private Double bq(ItemStack stack, Map<ItemStackSample, Integer> replenishSupply) {
        if (this.bn(stack) && stack.contains(DataComponentTypes.CONTAINER)) {
            ContainerComponent var3 = (ContainerComponent) stack.get(DataComponentTypes.CONTAINER);
            if (var3 != null && !Objects.equals(ContainerComponent.DEFAULT, var3)) {
                LinkedHashMap<ItemStackSample, Integer> var4 = new LinkedHashMap<>(replenishSupply);

                for (Entry<ItemStackSample, Integer> var6 : var4.entrySet()) {
                    int var7 = ((ItemStackSample) var6.getKey()).fS().getMaxCount();
                    if (var7 < 64) {
                        var6.setValue((Integer) var6.getValue() * 64 / var7);
                    }
                }

                LinkedHashMap<ItemStackSample, Integer> var13 = new LinkedHashMap<>();
                boolean var14 = false;

                for (ItemStack var8 : var3.iterateNonEmpty()) {
                    ItemStackSample var9 = ItemStackSample.of(var8);
                    Integer var10 = var4.get(var9);
                    int var11 = var8.getCount();
                    int var12 = var8.getItem().getMaxCount();
                    if (var12 < 64) {
                        var11 = var11 * 64 / var12;
                    }

                    if (var10 != null) {
                        var14 = true;
                        var10 = var10 - var11;
                        if (var10 > 0) {
                            var4.put(var9, var10);
                        } else {
                            var4.remove(var9);
                            if (var10 < 0) {
                                var13.merge(var9, -var10, Integer::sum);
                            }
                        }
                    } else {
                        var13.merge(var9, var11, Integer::sum);
                    }
                }

                if (!var14) {
                    return null;
                } else {
                    int var16 =
                            var4.values().stream().mapToInt(Integer::intValue).sum();
                    int var17 =
                            var13.values().stream().mapToInt(Integer::intValue).sum();
                    return (double) (-var16 * 1000 - var17);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private InvSubHelperB bF(String name) {
        return this.aB.vD().stream()
                .filter(s -> Objects.equals(s.name(), name))
                .findFirst()
                .orElse(null);
    }

    private void bG(InvSubHelperB kit) {
        ArrayList var2 = new ArrayList<>(this.aB.vD());
        var2.add(kit);
        this.bM(new InvSubHelperF(this.aB.vC(), var2));
    }

    public void bk(Event<World> event) {
        this.bj();
        this.av = false;
        this.aw = null;
    }

    private void br(ScreenHandler handler, InvSubHelperG transaction) {
        MovTasks.aj().Xf();
        switch (transaction.k.Ir()) {
            case Is:
            case Iv:
                this.bs(handler, transaction);
                break;
            case It:
                this.bu(handler, transaction);
                break;
            case Iu:
                this.bt(handler, transaction);
        }
    }

    private void bu(ScreenHandler handler, InvSubHelperG transaction) {
        Inventory var3 = InventoryUtils.getTopInventory(handler);
        InvSubHelperL var4 = transaction.k;
        int var5 = var4.Is();
        int var6 = var4.It();
        PlayerInventory var7 = mc.player.getInventory();
        LinkedHashMap var8 = new LinkedHashMap<>(transaction.r);

        for (int var9 = 0; var9 < var3.size() && !var8.isEmpty(); var9++) {
            ItemStack var10 = var3.getStack(var9);
            if (!var10.isEmpty()) {
                ItemStackSample var11 = ItemStackSample.of(var10);
                Integer var12 = (Integer) var8.get(var11);
                if (var12 != null) {
                    boolean var13 = false;

                    for (int var14 = var5; var14 < var6; var14++) {
                        ItemStack var15 = var7.getStack(var14);
                        if (var15.isEmpty()) {
                            var13 = true;
                            break;
                        }

                        if (var15.getCount() < var15.getMaxCount()
                                && ItemStack.areItemsAndComponentsEqual(var15, var11.fS())) {
                            var13 = true;
                            break;
                        }
                    }

                    if (!var13) {
                        var8.remove(var11);
                    } else {
                        int var17 = var10.getCount();
                        mc.interactionManager.clickSlot(handler.syncId, var9, 0, SlotActionType.QUICK_MOVE, mc.player);
                        int var18 = var3.getStack(var9).getCount();
                        if (var17 <= var18) {
                            var8.remove(var11);
                        } else {
                            int var16 = var12 - (var17 - var18);
                            if (var16 < 0) {
                                var8.remove(var11);
                            } else {
                                var8.put(var11, var16);
                            }
                        }
                    }
                }
            }
        }
    }

    private void bv(Inventory inventory, int from, int to) {
        if (!checkNull()) {
            PlayerInventory var4 = mc.player.getInventory();
            ScreenHandler var5 = mc.player.currentScreenHandler;
            from = Math.clamp((long) from, 0, Math.min(inventory.size(), InventoryUtils.E()));
            to = Math.clamp((long) to, from, Math.min(inventory.size(), InventoryUtils.E()));

            for (int var6 = from; var6 < to; var6++) {
                ItemStack var7 = inventory.getStack(var6);
                if (!var7.isEmpty() && !this.bn(var7)) {
                    ItemStack var8 = var4.getStack(var6);
                    if (!ItemStack.areItemsAndComponentsEqual(var8, var7)) {
                        int var9 = this.bw(inventory, var4, var7, var6, from, to);
                        if (var9 >= 0) {
                            InvExtra.INSTANCE.swapInventoryIndexes(var9, var6);
                        }
                    }
                }
            }

            for (int var15 = from; var15 < to; var15++) {
                ItemStack var16 = inventory.getStack(var15);
                if (!var16.isEmpty() && !this.bn(var16)) {
                    ItemStack var17 = var4.getStack(var15);
                    if (ItemStack.areItemsAndComponentsEqual(var17, var16) && var17.getCount() < var16.getCount()) {
                        int var18 = var5.getSlotIndex(var4, var15).orElse(-1);
                        if (var18 >= 0) {
                            for (int var10 = from; var10 < to; var10++) {
                                if (var10 != var15) {
                                    ItemStack var11 = var4.getStack(var10);
                                    if (ItemStack.areItemsAndComponentsEqual(var11, var16)) {
                                        int var12 =
                                                var5.getSlotIndex(var4, var10).orElse(-1);
                                        if (var12 >= 0) {
                                            InvExtra.INSTANCE.mergeScreenSlotTo(var12, var18);
                                            if (var4.getStack(var15).getCount() >= var16.getCount()) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private KalamaHelperHelperCX bO(
            MutableObject<MutableRecord> indexOf, List<MutableRecord> mutableList, MutableRecord record) {
        KalamaHelperHelperCX var4 = new KalamaHelperHelperCX(0, 0, 250, 45);
        var4.Q(new ExecutableWidget(0, 12, 21, 21)
                .eV(new ButtonElement(TextProvider.c(Text.empty()), ButtonAction.a(() -> {
                            if (indexOf.getValue() != record) {
                                indexOf.setValue(record);
                            } else {
                                indexOf.setValue(null);
                            }
                        }))
                        .cw(el -> indexOf.getValue() == record)));
        String var5 = InvSubHelperB.ds.get(0);
        BaseAttrKeyValue var6 = AttrKeyValue.str("widget.kit-manager.open-kit-list.name", record.d(var5, ""));
        var6.addValidator(s -> {
            for (MutableRecord var5x : mutableList) {
                if (var5x != record && Objects.equals(var5x.b(var5), s)) {
                    return false;
                }
            }

            return true;
        });
        var6.addListener(s -> record.e(var5, s));
        var4.Q(var6.generateKeyValueInput(30, 1, 30, 0, 60, 20));
        String var7 = InvSubHelperB.ds.get(2);
        BaseAttrKeyValue<Integer> var8 =
                AttrKeyValue.integer("widget.kit-manager.open-kit-list.max-size", record.d(var7, 0));
        var8.addValidator(Configs.d);
        var8.addListener(s -> record.e(var7, s));
        var4.Q(var8.generateKeyValueInput(120, 1, 30, 0, 20, 20));
        String var9 = InvSubHelperB.ds.get(3);
        InvSubHelperL var10 = record.d(var9, InvSubHelperL.wI);
        EnumAttrKeyValue<InvSubHelperS> var11 =
                AttrKeyValue.enumMap("widget.kit-manager.open-kit-list.rule.type", var10.Ir(), InvSubHelperS.class);
        var11.addListener(s -> record.e(var9, record.<InvSubHelperL>b(var9).withType(s)));
        BaseAttrKeyValue<Integer> var12 =
                AttrKeyValue.integer("widget.kit-manager.open-kit-list.rule.from", var10.Is());
        var12.addListener(s -> record.e(var9, record.<InvSubHelperL>b(var9).Io(s)));
        var12.addValidator(Configs.intRange(0, InventoryUtils.E()));
        BaseAttrKeyValue<Integer> var13 = AttrKeyValue.integer("widget.kit-manager.open-kit-list.rule.to", var10.It());
        var13.addListener(s -> record.e(var9, record.<InvSubHelperL>b(var9).Ip(s)));
        var13.addValidator(Configs.intRange(0, InventoryUtils.E()));
        Runnable var14 = () -> {
            var6.valueChangeInternal(null, record.d(var5, ""));
            var8.valueChangeInternal(null, record.d(var7, 0));
            InvSubHelperL var9x = record.d(var9, InvSubHelperL.wI);
            var11.valueChangeInternal(null, var9x.Ir());
            var12.valueChangeInternal(null, var9x.Is());
            var13.valueChangeInternal(null, var9x.It());
        };
        var4.Q(var11.generateKeyValueInput(30, 24, 30, 0, 60, 20));
        var4.Q(var12.generateKeyValueInput(120, 24, 30, 0, 20, 20));
        var4.Q(var13.generateKeyValueInput(170, 24, 30, 0, 20, 20));
        var4.Q(new ExecutableWidget(220, 24, 30, 20)
                .eV(new ButtonElement(
                                el -> record.<InvSubHelperL>b(var9).dump()
                                        ? Text.translatable("widget.kit-manager.open-kit-list.rule.dump.true")
                                        : Text.translatable("widget.kit-manager.open-kit-list.rule.dump.false"),
                                ButtonAction.a(() -> record.<InvSubHelperL>f(var9, s -> s.withDump(!s.dump()))))
                        .aO(TooltipHandler.ar(
                                () -> record.<InvSubHelperL>b(var9).dump()
                                        ? ChatUtils.parseTranslation(
                                                "widget.kit-manager.open-kit-list.rule.dump.true.tooltips", "")
                                        : ChatUtils.parseTranslation(
                                                "widget.kit-manager.open-kit-list.rule.dump.false.tooltips", "")))));
        Function<InvSubHelperB, Runnable> var15 = temporaryKit -> () -> {
            Inventory var5x = bP(temporaryKit);
            InventoryViewScreen var6x = new InventoryViewScreen(
                    var5x, Text.literal(temporaryKit.name()), new ItemStack(Items.SHULKER_BOX), true);
            var6x.access().addCloseFuture(() -> {
                InvSubHelperB var6xx =
                        this.bR(temporaryKit.name(), var5x, var5x.size(), record.d(var9, InvSubHelperL.wI));
                MutableRecord var7x = MutableRecord.of(InvSubHelperB.ds, var6xx);
                record.replaceMap(var7x);
                var14.run();
            });
            var6x.access().openFromCurrent();
        };
        if (mc.getNetworkHandler() != null) {
            var4.Q(ExecutableWidget.instance(170, 1, 40, 20)
                    .eV(new ButtonElement(
                            TextProvider.c(Text.translatable("widget.kit-manager.open-kit-list.items")),
                            ButtonAction.a(() -> {
                                InvSubHelperB var2 = record.j(InvSubHelperB.class);
                                ((Runnable) var15.apply(var2)).run();
                            }))));
            var4.Q(ExecutableWidget.instance(210, 1, 40, 20)
                    .eV(new ButtonElement(
                            TextProvider.c(Text.translatable("widget.kit-manager.open-kit-list.items.import")),
                            ButtonAction.a(() -> {
                                if (mc.player != null) {
                                    InvSubHelperB var5x = this.bR(
                                            (String) var6.getOriginValue(),
                                            mc.player.getInventory(),
                                            InventoryUtils.F(),
                                            record.d(var9, record.d(var9, InvSubHelperL.wI)));
                                    ((Runnable) var15.apply(var5x)).run();
                                }
                            }))));
        } else {
            var4.Q(ExecutableWidget.instance(180, 1, 70, 20)
                    .eV(new ButtonElement(
                            TextProvider.c(Text.translatable("widget.kit-manager.open-kit-list.items.error")),
                            ButtonAction.c())));
        }

        return var4;
    }

    public void bl(Event<Void> event) {
        if (!checkNull()) {
            label224:
            if (this.au != null) {
                if (this.au.isCompleted()) {
                    this.bj();
                    return;
                }

                if (this.au.t == InvSubHelperG.a) {
                    if (mc.currentScreen != null) {
                        mc.currentScreen.close();
                    }

                    if (this.au.k.Ir() == InvSubHelperS.Is) {
                        this.bv(this.au.l, this.au.k.Is(), this.au.k.It());
                    }

                    this.au.t = InvSubHelperG.b;
                }

                if (this.au.t == InvSubHelperG.b) {
                    KalamaHelperHelperK var2 = this.bm(mc.player.getInventory(), this.au);
                    this.au.n = var2;
                    if (var2 == null) {
                        if (this.au.o) {
                            this.au.t = InvSubHelperG.c;
                            this.au.p = true;
                            this.av = true;
                        } else {
                            if (this.log.get()) {
                                this.logI18N("message.kit-manager.kit-replenish.failure.no-shulker", new Object[0]);
                            }

                            this.au.e(false);
                        }
                        break label224;
                    }

                    this.au.t = InvSubHelperG.d;
                }

                if (this.au.t == InvSubHelperG.c && !this.av) {
                    if (!(mc.currentScreen instanceof GenericContainerScreen var10)) {
                        this.au.e(false);
                        break label224;
                    }

                    GenericContainerScreenHandler var16 = (GenericContainerScreenHandler) var10.getScreenHandler();
                    Inventory var4 = var16.getInventory();
                    KalamaHelperHelperK var5 = this.bm(var4, this.au);
                    int var6;
                    if (var5 == null
                            || (var6 = var16.getSlotIndex(var4, var5.index()).orElse(-1)) < 0) {
                        if (this.log.get()) {
                            this.logI18N(
                                    "message.kit-manager.kit-replenish.failure.no-shulker-in-ender-chest",
                                    new Object[0]);
                        }

                        this.au.e(false);
                        break label224;
                    }

                    KalamaHelperHelperK var7 = InventoryUtils.findScreenSlot(
                            var16.slots,
                            sl -> {
                                if (!(sl.inventory instanceof PlayerInventory)) {
                                    return null;
                                } else {
                                    ItemStack var1 = sl.getStack();
                                    if (var1.isEmpty()) {
                                        return 1.0E8;
                                    } else {
                                        return !var1.isOf(Items.TOTEM_OF_UNDYING)
                                                        && !var1.isOf(Items.OBSIDIAN)
                                                        && !var1.isOf(Items.SHULKER_BOX)
                                                ? (double) var1.getMaxCount()
                                                : 128.0 - var1.getCount();
                                    }
                                }
                            },
                            true);
                    if (var7 == null) {
                        if (this.log.get()) {
                            this.logI18N(
                                    "message.kit-manager.kit-replenish.failure.no-space-in-inventory", new Object[0]);
                        }

                        this.au.e(false);
                        break label224;
                    }

                    InvExtra.INSTANCE.swapScreenSlots(var6, var7.index());
                    this.au.n =
                            new KalamaHelperHelperK<>(((Slot) var7.val()).getIndex(), ((Slot) var7.val()).getStack());
                    this.au.t = InvSubHelperG.d;
                    var10.close();
                }

                if (this.au.t == InvSubHelperG.d) {
                    if (this.specificSlot.get().isPresent()) {
                        int var11 = Math.clamp(
                                (long) this.specificSlot.get().getValue().intValue(), 0, 8);
                        int var17 = this.au.n.index();
                        InvExtra.INSTANCE.swapInventoryIndexes(var17, var11);
                        this.au.q = var11;
                        this.au.n = new KalamaHelperHelperK<>(var11, this.au.n.val());
                    } else {
                        this.au.q = this.au.n.index();
                    }

                    this.au.t = InvSubHelperG.e;
                }

                if (this.au.t == InvSubHelperG.e) {
                    int var12 = this.au.n.index();
                    ItemStack var18 = mc.player.getInventory().getStack(var12);
                    if (this.bn(var18)) {
                        Pair var21 = this.bJ().findAny().orElse(null);
                        if (var21 != null) {
                            InvSubHelperG var26 = this.au;
                            var26.t = InvSubHelperG.f;
                            this.bi(new InvSubHelperC(
                                    Optional.of(var21),
                                    Optional.of(new Slot(mc.player.getInventory(), var12, 0, 0)),
                                    ch -> {
                                        this.br(ch, var26);
                                        if (this.log.get()) {
                                            this.logI18N(
                                                    "message.kit-manager.kit-replenish.success.replenish-finish",
                                                    new Object[0]);
                                        }

                                        var26.e(true);
                                    },
                                    () -> {
                                        if (this.log.get()) {
                                            this.logI18N(
                                                    "message.kit-manager.kit-replenish.failure.replenish",
                                                    new Object[0]);
                                        }

                                        var26.e(false);
                                    },
                                    this.zeroTickSupply.get()));
                        } else {
                            if (this.log.get()) {
                                this.logI18N(
                                        "message.kit-manager.kit-replenish.failure.no-space-to-place", new Object[0]);
                            }

                            this.au.e(false);
                        }
                    } else {
                        if (this.log.get()) {
                            this.logI18N("message.kit-manager.kit-replenish.failure.shulker-mismatch", new Object[0]);
                        }

                        this.au.e(false);
                    }
                }
            } else {
                this.bj();
            }

            if (this.av) {
                if (this.ay == -1) {
                    this.ay = 20;
                }

                if (this.ay > 0) {
                    this.ay--;
                    if (this.ay == 0) {
                        this.ay = -1;
                        this.av = false;
                    }
                }

                if (ChestHistory.isEnderChest(mc.currentScreen)) {
                    if (this.log.get()) {
                        this.logI18N("message.kit-manager.kit-replenish.success.open-ender-chest", new Object[0]);
                    }

                    this.av = false;
                } else {
                    if (mc.currentScreen != null) {
                        mc.currentScreen.close();
                    }

                    if (this.ax.a(5)) {
                        BlockPos var13 = this.bL();
                        if (var13 != null) {
                            Interact.INSTANCE.interactBlock(RaycastUtils.g(var13, mc.player.getEyePos()));
                            this.ax.f();
                        } else {
                            KalamaHelperHelperK var19 = InventoryUtils.p(s -> s.isOf(Items.ENDER_CHEST), true, false);
                            if (var19 != null) {
                                Pair var22 = this.bK(true).findFirst().orElse(null);
                                if (var22 == null) {
                                    this.av = false;
                                    if (this.log.get()) {
                                        this.logI18N(
                                                "message.kit-manager.kit-replenish.failure.no-space-to-place",
                                                new Object[0]);
                                    }
                                } else {
                                    Runnable var27 = InvExtra.INSTANCE.swapInventoryIndexToHand(var19.index());
                                    if (var27 != null) {
                                        Interact.INSTANCE.interactBlock((BlockHitResult) var22.getSecond());
                                        var27.run();
                                    } else {
                                        this.av = false;
                                    }
                                }
                            } else {
                                this.av = false;
                                if (this.log.get()) {
                                    this.logI18N(
                                            "message.kit-manager.kit-replenish.failure.no-ender-chest", new Object[0]);
                                }
                            }
                        }
                    }
                }
            } else {
                this.ay = -1;
            }

            if (this.aw != null) {
                if (this.aw.placePos().isEmpty()) {
                    Pair var14 = this.bJ().findAny().orElse(null);
                    if (var14 == null) {
                        if (this.log.get()) {
                            this.logI18N("message.kit-manager.kit-replenish.failure.no-space-to-place", new Object[0]);
                        }

                        this.aw.failureCallback().run();
                        this.aw = null;
                        return;
                    }

                    this.aw = this.aw.rS(Optional.of(var14));
                }

                int var15 = -1;
                Optional<Pair<BlockPos, BlockHitResult>> var20 = this.aw.placePos();
                if (var20.isPresent() && this.aw.playerScreenSlot().isPresent()) {
                    Slot var23 = this.aw.playerScreenSlot().get();
                    OptionalInt var28 = mc.player.currentScreenHandler.getSlotIndex(var23.inventory, var23.getIndex());
                    if (var28.isPresent()) {
                        var15 = var28.getAsInt();
                    } else {
                        this.aw = this.aw.rT(Optional.empty());
                        var20 = Optional.empty();
                    }
                }

                if (var20.isEmpty()) {
                    KalamaHelperHelperK var24 =
                            InventoryUtils.x(mc.player.currentScreenHandler.slots, s -> this.bn(s.getStack()), false);
                    if (var24 == null) {
                        if (this.log.get()) {
                            this.logI18N("message.kit-manager.kit-replenish.failure.no-shulker", new Object[0]);
                        }

                        this.aw.failureCallback().run();
                        this.aw = null;
                        return;
                    }

                    var15 = var24.index();
                    this.aw = this.aw.rT(Optional.of((Slot) var24.val()));
                }

                Preconditions.checkArgument(var15 >= 0, "?");
                Pair var25 = this.aw.placePos().get();
                BlockPos var29 = (BlockPos) var25.getFirst();
                if (!(mc.world.getBlockEntity(var29) instanceof ShulkerBoxBlockEntity)) {
                    Runnable var31 = InvExtra.INSTANCE.swapInventorySlotToHand(var15);
                    if (var31 == null) {
                        this.aw.failureCallback().run();
                        this.aw = null;
                        return;
                    }

                    Interact.INSTANCE.interactBlock(
                            (BlockHitResult) (Object) this.aw.placePos().get().getSecond());
                    var31.run();
                    if (!this.aw.useZeroTick()) {
                        return;
                    }
                }

                if (mc.world.getBlockEntity(var29) instanceof ShulkerBoxBlockEntity var30
                        && InteractUtils.canShulkerOpen(mc.world, var29, mc.world.getBlockState(var29))) {
                    if (mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler var33
                            && mc.player.currentScreenHandler instanceof TileInventory var9
                            && Objects.equals(var9.getPos(), var29)) {
                        this.aw.successCallback().accept(mc.player.currentScreenHandler);
                        this.aw = null;
                    } else {
                        Interact.INSTANCE.Sr(var29);
                        if (this.aw.useZeroTick()) {
                            InvTasks.executePredictInventoryAction(var30, handler -> {
                                this.aw.successCallback().accept(handler);
                                this.aw = null;
                            });
                        }
                    }
                } else if (this.aw.useZeroTick()) {
                    this.aw = this.aw.withUseZeroTick(false);
                } else {
                    this.aw.failureCallback().run();
                    this.aw = null;
                }
            }
        }
    }
}
