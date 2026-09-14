package me.matl114.hacks.modules.survival;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import me.matl114.accessors.access.MerchantScreenAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.interact.Interact;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.WeakEntryPrimitiveMap;
import me.matl114.hacks.utils.config.WeakHolder;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.move.AdjustmentSchedular;
import me.matl114.hacks.utils.move.PathingSchedular;
import me.matl114.hacks.utils.move.goal.GoalBlockPos;
import me.matl114.hacks.utils.move.goal.GoalNearBlockPos;
import me.matl114.hacks.utils.move.goal.IPathGoal;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RegistryUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class AutoLibrarian extends BaseModule {
    public final NBTRef<WeakEntryPrimitiveMap<Enchantment, Integer>> aE;
    private int aR;
    private boolean aT;
    public final FlagRef onlyMaxLeve;
    VillagerEntity aN;
    public final FlagRef autoLockTrade;
    public final FlagRef ae;
    public final NBTRef<WrapColor> renderColor;
    final PathingSchedular aL;
    public final FlagRef log;
    public final NBTRef<EntrySet<Block>> workStationDownBlock;
    public final KeyBindRef J;
    boolean aU;
    private boolean aQ;
    private int aP;
    private boolean aS;
    BlockPos aO;
    public final FlagRef baritoneControl;
    public final FlagRef adjustmentControl;
    public final ModulePath aD = makePath(Configs.o, "survival-mine-utils.auto-librarian");
    public final FlagRef render;
    final AdjustmentSchedular aM;

    public void dd() {
        if (this.aN != null) {
            this.aO = this.dc(this.aN);
        } else {
            List<VillagerEntity> var1 = mc.world.getEntitiesByType(
                    EntityType.VILLAGER, mc.player.getBoundingBox().expand(100.0, 100.0, 100.0), this::db);
            if (var1.isEmpty()) {
                this.cZ();
            } else {
                this.aN = var1.stream()
                        .min(Comparator.comparingDouble(s -> s.getPos().squaredDistanceTo(mc.player.getPos())))
                        .orElseThrow();
                this.aO = this.dc(this.aN);
            }
        }
    }

    public void df(MerchantScreen screen) {
        MerchantScreenHandler var2 = (MerchantScreenHandler) screen.getScreenHandler();
        if (WorldManager.aeZ(var2)) {
            KalamaHelperHelperK var3 = this.dh(var2);
            if (var3 == null) {
                this.aS = false;
                return;
            }

            Integer var4 = this.aE.get().Ax((RegistryEntry<Enchantment>) ((Pair) var3.val()).getSecond());
            if (var4 == null) {
                this.aS = false;
                if (this.log.get()) {
                    this.logI18N("message.module.auto-librarian.enchantment-not-whitelisted", new Object[] {
                        ((Enchantment) ((RegistryEntry) ((Pair) var3.val()).getSecond()).value()).description()
                    });
                }

                return;
            }

            int var5 = this.dg(var2, var3.index());
            if (var5 > var4) {
                this.aS = false;
                if (this.log.get()) {
                    this.logI18N("message.module.auto-librarian.enchantment-price-too-high", new Object[] {
                        ((Enchantment) ((RegistryEntry) ((Pair) var3.val()).getSecond()).value()).description(),
                        var5,
                        var4
                    });
                }

                return;
            }

            int var6 = (Integer) ((Pair) var3.val()).getFirst();
            int var7 = ((Enchantment) ((RegistryEntry) ((Pair) var3.val()).getSecond()).value()).getMaxLevel();
            if (this.onlyMaxLeve.get() && var6 < var7) {
                this.aS = false;
                if (this.log.get()) {
                    this.logI18N("message.module.auto-librarian.enchantment-level-too-low", new Object[] {
                        ((Enchantment) ((RegistryEntry) ((Pair) var3.val()).getSecond()).value()).description(),
                        var6,
                        var7
                    });
                }

                return;
            }

            if (this.log.get()) {
                this.logI18N("message.module.auto-librarian.enchantment-success", new Object[] {
                    ((Enchantment) ((RegistryEntry) ((Pair) var3.val()).getSecond()).value()).description(), var5
                });
            }

            this.aS = true;
            if (this.autoLockTrade.get()) {
                if (InventoryUtils.p(s -> s.isOf(Items.BOOK), true, false) != null) {
                    if (InventoryUtils.C(Items.EMERALD) >= var5) {
                        MerchantScreenAccess var8 = MerchantScreenAccess.of(screen);
                        var8.setSelectedIndex(var8.getSelectedIndex());
                        mc.interactionManager.clickSlot(var2.syncId, 2, 1, SlotActionType.PICKUP, mc.player);
                    } else if (this.log.get()) {
                        this.logI18N(
                                "message.module.auto-librarian.auto-lock.no-item",
                                new Object[] {Items.EMERALD.getName()});
                    }
                } else if (this.log.get()) {
                    this.logI18N(
                            "message.module.auto-librarian.auto-lock.no-item", new Object[] {Items.BOOK.getName()});
                }
            }
        } else {
            WorldManager.INSTANCE.aeX(this.aN, true);
            this.aS = true;
        }
    }

    private boolean da(VillagerEntity villager) {
        VillagerData var2 = villager.getVillagerData();
        if (var2 == null) {
            return true;
        } else {
            VillagerProfession var3 = var2.getProfession();
            if (Objects.equals(var3, VillagerProfession.NONE)) {
                return true;
            } else {
                return !Objects.equals(var3, VillagerProfession.LIBRARIAN)
                        ? false
                        : var2.getLevel() <= 1 && !WorldManager.INSTANCE.aeY(villager);
            }
        }
    }

    public boolean dj() {
        return this.baritoneControl.get() && this.aN != null && this.aO != null;
    }

    public AutoLibrarian() {
        super("AutoLibrarian");
        this.ae = this.flagBuilder(this.aD.addEnable()).build();
        this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable())
                .build();
        this.aE = this.<WeakEntryPrimitiveMap<Enchantment, Integer>>builder(
                        this.aD.add("enchantments"), WeakEntryPrimitiveMap.uA())
                .defaultValue(new WeakEntryPrimitiveMap<>(
                        RegistryKeys.ENCHANTMENT, NBTTypes.c, Map.of(Enchantments.MENDING.getValue(), 1)))
                .build();
        this.onlyMaxLeve = this.builder(this.aD.add("only-max-leve"), Boolean.class)
                .defaultValue(true)
                .build();
        this.log = this.flagBuilder(this.aD.add("log")).build();
        this.workStationDownBlock = this.builder(this.aD.add("work-station-down-block"), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<Block>(Registries.BLOCK, List.of(Blocks.MAGMA_BLOCK, Blocks.OAK_FENCE)))
                .build();
        this.autoLockTrade = this.flagBuilder(this.aD.add("auto-lock-trade")).build();
        this.baritoneControl = this.flagBuilder(this.aD.add("baritone-control")).build();
        this.adjustmentControl =
                this.flagBuilder(this.aD.add("adjustment-control")).build();
        this.render = this.flagBuilder(this.aD.add("render")).build();
        this.renderColor = this.builder(this.aD.add("render-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.GREEN))
                .build();
        this.aL = new PathingSchedular();
        this.aM = new AdjustmentSchedular();
        this.aP = 0;
        this.aQ = false;
        this.aR = -1;
        this.aS = false;
        this.aT = false;
        this.aU = false;
        this.aL.D(this::dj).E(this::di);
        this.aM.k(1.5);
        this.aM.g(this::dk);
        this.bindFlag(this.ae);
    }

    public boolean db(VillagerEntity villagerEntity) {
        return EntityUtils.isEntityValid(villagerEntity)
                && villagerEntity.isOnGround()
                && !villagerEntity.isTouchingWater()
                && this.da(villagerEntity)
                && this.dc(villagerEntity) != null;
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        acceptor.accept(this.createTitleLabel("widget.interact.interact-all.use-argument", 0, dblank, dx, dy));
        if (mc.getNetworkHandler() != null) {
            acceptor.accept(this.createExecuteButton(
                    "widget.auto-librarian.set-min-price", ButtonAction.a(this::cX), 0, dblank, dx, dy));
        }
    }

    public void B(Event<MatrixStack> event) {
        if (this.ae.get() && this.render.get() && this.aN != null && this.aO != null) {
            float var2 = event.<Float>getArgs(0);
            RenderUtils.startDrawVirtual((MatrixStack) event.b);

            try {
                RenderCollector var3 = RenderCollectors.createBoxCollector(true, false, false);
                var3.submit(
                        RenderUtils.getLerpedBox(this.aN, var2),
                        this.renderColor.get().withAlpha(255));
                var3.submit(
                        new Box(this.aO.add(0, 1, 0)), this.renderColor.get().withAlpha(255));
                var3.a((MatrixStack) event.b);
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) event.b);
            }

            if (this.baritoneControl.get()) {
                this.aL.C(event);
            }

            if (this.adjustmentControl.get()) {
                this.aM.d(event);
            }
        }
    }

    private BlockPos dc(VillagerEntity villager) {
        BlockPos var2 = villager.getSteppingPos();

        for (Direction var6 : MathUtils.HORIZONTALS) {
            BlockPos var7 = var2.offset(var6);
            BlockState var8 = mc.world.getBlockState(var7);
            if (this.workStationDownBlock.get().test(var8.getBlock())) {
                return var7;
            }
        }

        return null;
    }

    public void cX() {
        ClientPlayNetworkHandler var1 = mc.getNetworkHandler();
        if (var1 != null) {
            Map var2 = this.aE.get().Au();
            LinkedHashMap var3 = new LinkedHashMap();

            for (Entry var5 : ((java.util.Set<Entry>) (var2).entrySet())) {
                if (Objects.equals(WeakHolder.ru, var5.getKey())) {
                    var3.put((Identifier) var5.getKey(), (Integer) var5.getValue());
                } else {
                    RegistryKey var6 = RegistryKey.of(RegistryKeys.ENCHANTMENT, (Identifier) var5.getKey());
                    RegistryEntry var7 = RegistryUtils.g(var1.getRegistryManager(), var6);
                    if (var7 == null) {
                        var3.put((Identifier) var5.getKey(), (Integer) var5.getValue());
                    } else {
                        Enchantment var8 = (Enchantment) var7.value();
                        int var9 = 2 + 3 * var8.getMaxLevel();
                        if (var7.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                            var9 *= 2;
                        }

                        var3.put((Identifier) var5.getKey(), var9);
                    }
                }
            }

            this.aE.set(new WeakEntryPrimitiveMap<>(RegistryKeys.ENCHANTMENT, NBTTypes.c, var3));
        }
    }

    public IPathGoal di() {
        KalamaHelperHelperK var1 = InventoryUtils.G(mc.player.getInventory(), Items.LECTERN);
        if (var1 == null) {
            this.aU = true;
        }

        if (this.aU) {
            BlockPos var6 = this.aN.getBlockPos();
            Box var7 = new Box(var6).expand(0.0, 1.0, 0.0);
            List<ItemEntity> var8 = mc.world.getEntitiesByType(
                    EntityType.ITEM,
                    mc.player.getBoundingBox().expand(6.0, 2.0, 6.0),
                    item -> !var7.intersects(item.getBoundingBox())
                            && item.getStack().isOf(Items.LECTERN));
            ItemEntity var9 = var8.stream()
                    .min(Comparator.comparingDouble(s -> s.getPos().squaredDistanceTo(mc.player.getPos())))
                    .orElse(null);
            if (var9 == null) {
                this.aU = false;
                return null;
            } else {
                return new GoalNearBlockPos(var9.getBlockPos());
            }
        } else {
            Direction var2 = MathUtils.getHorizontalFacing(this.aO.toCenterPos().subtract(this.aN.getPos()));
            Direction var3 = var2.rotateClockwise(Axis.Y);
            BlockPos var4 = this.aO.add(0, 1, 0).offset(var3);
            BlockState var5 = mc.world.getBlockState(var4);
            if (!var5.getCollisionShape(mc.world, var4, ShapeContext.of(mc.player))
                    .isEmpty()) {
                var4 = this.aO.add(0, 1, 0).offset(var3.getOpposite());
            }

            return new GoalBlockPos(var4);
        }
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.cZ();
    }

    public Vec3d dk() {
        if (this.adjustmentControl.get() && this.aN != null && this.aO != null) {
            BlockPos var1 = this.aO.add(0, 1, 0);
            BlockPos var2 = mc.player.getBlockPos();
            int var3 = MathUtils.getManhattanDistance(var1, var2);
            if (var3 <= 1) {
                Direction var4 =
                        MathUtils.getHorizontalFacing(this.aO.toCenterPos().subtract(this.aN.getPos()));
                Direction var5 = var4.rotateYClockwise();
                Direction var6 = var4.rotateYCounterclockwise();
                BlockPos var7 = var1.offset(var5);
                BlockPos var8 = var1.offset(var6);
                if (Objects.equals(var2, var7)) {
                    Vec3d var10 = var2.toBottomCenterPos()
                            .offset(var4.getOpposite(), 0.2)
                            .offset(var6, 0.15);
                    if (MathUtils.m(var10, mc.player.getPos(), 0.05)) {
                        return null;
                    }

                    return var10.offset(var4.getOpposite(), 0.5);
                }

                if (Objects.equals(var2, var8)) {
                    Vec3d var9 = var2.toBottomCenterPos()
                            .offset(var4.getOpposite(), 0.23)
                            .offset(var5, 0.15);
                    if (MathUtils.m(var9, mc.player.getPos(), 0.05)) {
                        return null;
                    }

                    return var9.offset(var4.getOpposite(), 0.5);
                }

                if (Objects.equals(var2, var1)) {
                    if (MathUtils.m(var7.toCenterPos(), mc.player.getPos(), 1.0)) {
                        return var7.toBottomCenterPos();
                    }

                    return var8.toBottomCenterPos();
                }
            } else if (var3 == 2) {
                return this.aN.getPos();
            }
        }

        return null;
    }

    public KalamaHelperHelperK<Pair<Integer, RegistryEntry<Enchantment>>> dh(MerchantScreenHandler handler) {
        TradeOfferList var2 = handler.getRecipes();
        int var3 = 0;

        for (TradeOffer var5 : var2) {
            ItemStack var6 = var5.getSellItem();
            if (var6.isOf(Items.ENCHANTED_BOOK) && var6.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
                it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var7 =
                        (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry) ((ItemEnchantmentsComponent)
                                        var6.get(DataComponentTypes.STORED_ENCHANTMENTS))
                                .getEnchantmentEntries().stream().findFirst().orElse(null);
                if (var7 != null) {
                    return new KalamaHelperHelperK<>(var3, new Pair(var7.getIntValue(), (RegistryEntry) var7.getKey()));
                }
            }

            var3++;
        }

        return null;
    }

    private int dg(MerchantScreenHandler handler, int idx) {
        TradeOffer var3 = (TradeOffer) handler.getRecipes().get(idx);
        return Math.max(
                var3.getFirstBuyItem().count(),
                var3.getSecondBuyItem().<Integer>map(TradedItem::count).orElse(0));
    }

    private void cZ() {
        this.aN = null;
        this.aO = null;
        this.aT = false;
        this.aR = -1;
        this.aL.A();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bd(), this::bl);
        this.registerListener(RenderListener.q(), this::B);
    }

    public void bl(Event<Void> event) {
        if (!checkNull()) {
            if (this.ae.get()) {
                this.dd();
                if (this.aN != null && this.aO != null) {
                    this.de();
                }

                this.aL.tickPathing(mc.player);
                this.aM.tickAdjustment(mc.player);
            }
        }
    }

    public void de() {
        if (!this.db(this.aN)) {
            this.cZ();
            if (mc.currentScreen instanceof MerchantScreen var10) {
                var10.close();
            }
        } else if (TargetSelector.INSTANCE.isWithinAttackRange(mc.player.getPos(), this.aN)) {
            if (!this.aL.y()) {
                PlayerInteractionAccess var2 = PlayerInteractionAccess.of(mc.interactionManager);
                BlockPos var1 = this.aO.add(0, 1, 0);
                BlockState var3 = mc.world.getBlockState(var1);
                VillagerData var4 = this.aN.getVillagerData();
                VillagerProfession var5 = var4.getProfession();
                if (!var3.isAir() && !var3.isLiquid() && !var3.isReplaceable()) {
                    if (Objects.equals(var5, VillagerProfession.LIBRARIAN)) {
                        if (mc.currentScreen instanceof MerchantScreen var11) {
                            MerchantScreenHandler var13 = (MerchantScreenHandler) var11.getScreenHandler();
                            if (this.aR != var13.syncId) {
                                this.aR = var13.syncId;
                                this.aT = true;
                                this.df(var11);
                            }

                            if (!this.aS) {
                                if (!Objects.equals(var2.getCurrentMiningPos(), var1)) {
                                    var2.sendStartBreakPacket(var1);
                                }

                                if (var2.predictCurrentMiningProgressWithTool(ItemStack.EMPTY) < 0.7) {
                                    return;
                                }

                                mc.player.swingHand(Hand.MAIN_HAND);
                                var2.sendBreakPacket();
                            } else if (!WorldManager.aeZ((MerchantScreenHandler) var11.getScreenHandler())) {
                                WorldManager.INSTANCE.aeX(this.aN, true);
                            }
                        } else if ((mc.currentScreen == null || mc.currentScreen instanceof HandledScreen)
                                && !this.aT
                                && this.aP + 5 < Tasks.b()) {
                            Interact.INSTANCE.Si(this.aN);
                            this.aP = Tasks.b();
                        }
                    }
                } else {
                    this.aT = false;
                    if (Objects.equals(var5, VillagerProfession.NONE)) {
                        KalamaHelperHelperK var6 = InventoryUtils.p(s -> s.isOf(Items.LECTERN), true, false);
                        if (var6 != null) {
                            this.aQ = false;
                            Runnable var7 = InvExtra.INSTANCE.swapInventoryIndexToHand(var6.index());
                            if (var7 != null) {
                                Direction var8 = MathUtils.getHorizontalFacing(
                                        var1.toCenterPos().subtract(this.aN.getPos()));
                                Interact.INSTANCE.placeBlockStrict(var1, (BlockState)
                                        Blocks.LECTERN.getDefaultState().with(HorizontalFacingBlock.FACING, var8));
                                var7.run();
                            }
                        } else {
                            if (!this.aQ && this.log.get()) {
                                this.aQ = true;
                                this.logI18N("message.module.auto-librarian.no-lectern", new Object[0]);
                            }
                        }
                    }
                }
            }
        }
    }
}
