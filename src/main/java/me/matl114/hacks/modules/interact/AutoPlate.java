package me.matl114.hacks.modules.interact;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;

public class AutoPlate extends BaseModule {
    public final NBTRef<WrapColor> renderColor;
    final List<BlockPos> Mp;
    public final DoubleRef interactRange;
    public final FlagRef render;
    int timer;
    public final EnumRef<Configs$LegalInteractMode> mode;
    final RenderCollector<Box> Du;
    public final FlagRef ae;
    public final ModulePath Mj = makePath(Configs.n, "place-utils.auto-plate");
    public final FlagRef useBlockRotate;
    public final KeyBindRef J;
    public final IntRef fillDepth;
    public final FlagRef swingHand;
    public final FlagRef copyState;
    Optional<BlockState> Mq;
    public List<Vec3i> Dp;
    public final IntRef delay;
    public final FlagRef ghostHandSwapBack;
    public final FlagRef airPlace;
    public final IntRef multiply;
    public final DoubleRef expandRange;

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bd(), this::gt);
        this.registerListener(RenderListener.q(), this::Gn);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::IU);
    }

    public void gt(Event<Void> event) {
        if (!checkNull()) {
            if (this.ae.get() && ++this.timer > this.delay.get()) {
                this.timer = 0;
                this.refreshState();
                if (!this.Mp.isEmpty()) {
                    this.tickPlace();
                }
            }
        }
    }

    public AutoPlate() {
        super("AutoPlate");
        this.ae = this.flagBuilder(this.Mj.addEnable()).build();
        this.J = this.toggleHotkey(this.Mj.addHotkey(), new MultiKeyBind(), this.Mj.addEnable())
                .build();
        this.Dp = new ArrayList<>();
        this.mode = this.builder(this.Mj.add("mode"), Configs$LegalInteractMode.class)
                .defaultValue(Configs$LegalInteractMode.NONE)
                .build();
        this.airPlace = this.flagBuilder(this.Mj.add("air-place")).build();
        this.delay = this.intBuilder(this.Mj.add("delay")).defaultValue(5).build();
        this.multiply = this.intBuilder(this.Mj.add("multiply")).defaultValue(1).build();
        this.expandRange = this.doubleBuilder(this.Mj.add("expand-range"))
                .defaultValue(2.0)
                .validator(Configs.doubleRange(0.0, 100.0))
                .build();
        this.interactRange = this.doubleBuilder(this.Mj.add("interact-range"))
                .defaultValue(5.0)
                .validator(Configs.doubleRange(0.0, 100.0))
                .updateListener(s -> this.Dp = MathUtils.create2DPointListInRange(s, 0))
                .build();
        this.fillDepth = this.intBuilder(this.Mj.add("fill-depth"))
                .defaultValue(0)
                .validator(Configs.d)
                .build();
        this.copyState = this.builder(this.Mj.add("copy-state"), Boolean.class)
                .defaultValue(true)
                .build();
        this.useBlockRotate = this.flagBuilder(this.Mj.add("use-block-rotate")).build();
        this.ghostHandSwapBack =
                this.flagBuilder(this.Mj.add("ghost-hand-swap-back")).build();
        this.swingHand = this.builder(this.Mj.add("swing-hand"), Boolean.class)
                .defaultValue(true)
                .build();
        this.render = this.flagBuilder(this.Mj.add("render")).build();
        this.renderColor = this.builder(this.Mj.add("render-color"), WrapColor.class)
                .defaultValue(new WrapColor(Color.GREEN))
                .build();
        this.Mp = new ArrayList<>();
        this.Mq = Optional.empty();
        this.Du = RenderCollectors.createBoxCollector(true, false, false);
        this.bindFlag(this.ae);
    }

    public void refreshState() {
        this.Du.clear();
        this.Mp.clear();
        this.Mq = Optional.empty();
        Box var1 = mc.player.getBoundingBox().expand(this.expandRange.get(), 0.0, this.expandRange.get());
        Box var2 = new Box(var1.minX, var1.minY - this.expandRange.get(), var1.minZ, var1.maxX, var1.maxY, var1.maxZ);
        List<BlockPos> var3 = CollisionUtil.getIntersectingBlockPositions(mc.world, var2, false);
        if (!var3.isEmpty()) {
            int var4 = var3.stream().mapToInt(Vec3i::getY).max().getAsInt();
            this.Mp.addAll(this.Dp.stream()
                    .flatMap(s -> IntStream.range(0, this.fillDepth.get() + 1)
                            .mapToObj(j -> new BlockPos(
                                    s.getX() + mc.player.getBlockX(), var4 - j, s.getZ() + mc.player.getBlockZ())))
                    .toList());
            this.Mp.forEach(
                    s -> this.Du.submit(new Box(s), this.renderColor.get().withAlpha(255)));
            if (this.copyState.get()) {
                List<BlockPos> var5 =
                        var3.stream().filter(s -> s.getY() == var4).toList();
                this.Mq = var5.stream()
                        .filter(s -> {
                            BlockState var1x = mc.world.getBlockState(s);
                            return !var1x.isAir() && !var1x.isLiquid();
                        })
                        .min(Comparator.comparingDouble(s -> s.getSquaredDistance(mc.player.getPos())))
                        .map(mc.world::getBlockState);
            }
        }
    }

    public void tickPlace() {
        int var1 = 0;
        int var2 =
                Disabler.INSTANCE.isMultiRotPlaceCheckDisabled(this.mode.get().canMultiRotPlace())
                        ? this.multiply.get()
                        : 1;
        ArrayList var3 = new ArrayList(var2);

        for (BlockPos var5 : this.Mp) {
            BlockState var6 = mc.world.getBlockState(var5);
            if (var6.isAir() || var6.isLiquid() || var6.isReplaceable()) {
                int var7;
                BlockState var8;
                if (this.Mq.isPresent()) {
                    var7 = this.supplyBlocks(this.Mq.get().getBlock());
                    if (var7 == -1) {
                        break;
                    }

                    var8 = this.Mq.get();
                } else {
                    KalamaHelperHelperK var9 = this.supplyAnyBlocks();
                    if (var9 == null || !(((ItemStack) var9.val()).getItem() instanceof BlockItem var11)) {
                        break;
                    }

                    var7 = var9.index();
                    var8 = var11.getBlock().getDefaultState();
                }

                if (var7 == -1) {
                    break;
                }

                FlagEntry var14 = InteractionTasks.q(
                        var5, var8, this.airPlace.get(), !this.mode.get().isLegal());
                if (InteractUtils.C(mc.player, var14)
                        && InteractExtra.INSTANCE.fE(
                                mc.player.getPos(),
                                ((BlockHitResult) var14.val()).getBlockPos(),
                                this.interactRange.get())
                        && InteractUtils.getBlockPlacement(
                                        var8.getBlock(), mc.player, mc.world, (BlockHitResult) var14.val())
                                != null) {
                    Runnable var15 = InvExtra.INSTANCE.swapInventoryIndexToHand(var7);
                    if (var15 == null) {
                        break;
                    }

                    var3.add(var15);
                    if (this.useBlockRotate.get()) {
                        BlockRotate.INSTANCE.OO(var5, var8);
                    }

                    InteractionTasks.g(
                            this.mode.get(), (BlockHitResult) var14.val(), Hand.MAIN_HAND, this.swingHand.get());
                    mc.world.setBlockState(var5, var8, 530);
                    if (++var1 >= var2) {
                        break;
                    }
                }
            }
        }

        if (this.ghostHandSwapBack.get()) {
            int var12 = var3.size();

            for (int var13 = var12 - 1; var13 >= 0; var13--) {
                ((Runnable) var3.get(var13)).run();
            }
        }
    }

    public void IU(Event<KalamaHelperHelperI<ModulePreset>> event) {
        this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset) ((KalamaHelperHelperI) event.b).b()));
        this.airPlace.set(!((ModulePreset) ((KalamaHelperHelperI) event.b).b()).hasAC());
    }

    public int supplyBlocks(Block needBlock) {
        Item var2 = needBlock.asItem();
        if (var2 == Items.AIR) {
            return -1;
        } else {
            KalamaHelperHelperK var3 = InventoryUtils.p(item -> item.getItem() == var2, true, false);
            return var3 == null ? -1 : var3.index();
        }
    }

    public KalamaHelperHelperK<ItemStack> supplyAnyBlocks() {
        return InventoryUtils.p(item -> item.getItem() instanceof BlockItem, true, false);
    }

    public void Gn(Event<MatrixStack> eventVDraw) {
        if (this.ae.get() && this.render.get()) {
            RenderUtils.startDrawVirtual((MatrixStack) eventVDraw.b);

            try {
                this.Du.a((MatrixStack) eventVDraw.b);
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) eventVDraw.b);
            }
        }
    }
}
