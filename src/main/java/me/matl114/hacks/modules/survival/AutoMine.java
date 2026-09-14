package me.matl114.hacks.modules.survival;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.MineTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.mine.MineExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.move.PathingSchedular;
import me.matl114.hacks.utils.move.goal.GoalNear;
import me.matl114.hacks.utils.move.goal.IPathGoal;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$MineTargetingMode;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.AttributeUtils;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RegistryUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.versioned.api.VPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2i;

public class AutoMine extends BaseModule {
    private static final int EB = 2;
    public final FlagRef considerCooldown;
    public final IntRef requiredEmptySlots;
    public final FlagRef useDoubleBreak;
    public final IntRef heightSearchLimit;
    private static final int EA = 30;
    private final PathingSchedular aL = new PathingSchedular();
    public final FlagRef autoSwap;
    public final EnumRef<AutoMine$Mode> mode;
    public final IntRef mineHeight;
    public final FlagRef enableBaritone;
    private static final int ED = 9;
    private SurvivalSubHelperM EE = SurvivalSubHelperM.xZ;
    public final DoubleRef collectEnterDistance;
    public final EnumRef<Configs$MineTargetingMode> legalMode;
    public final KeyBindRef J;
    public final FlagRef ae;
    public final FlagRef durabilityProtect;
    private BlockPos EF;
    private final Set<UUID> EJ;
    private int EI;
    public final DoubleRef collectSearchRadius;
    private int EH;
    public final IntRef routeSpacing;
    public final NBTRef<EntrySet<Block>> EL;
    private List<BlockPos> EG = Collections.emptyList();
    public final FlagRef refreshCollectDrops;
    public final IntRef startDownOffset;
    public final ModulePath aD;
    public final FlagRef mineDuringCollect;
    public final IntRef horizontalRange;
    private BlockPos W;
    private static final int EC = 4;

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bd(), this::bl);
        this.registerListener(RenderListener.q(), this::B);
    }

    private boolean Un() {
        return this.ae.get()
                && this.mode.get() == AutoMine$Mode.BATCH
                && this.enableBaritone.get()
                && this.EE != SurvivalSubHelperM.yc;
    }

    private void ensureBatchPlan() {
        if (this.EF == null) {
            BlockPos var1 = mc.player.getSteppingPos().add(0, 1, 0);
            this.EF = new BlockPos(var1.getX(), this.resolveTargetStandY(var1), var1.getZ());
            this.EG = this.buildBatchRoute(this.EF);
            this.EH = 0;
            this.EI = 0;
            this.EE = this.EG.isEmpty() ? SurvivalSubHelperM.yc : SurvivalSubHelperM.xZ;
        }
    }

    private boolean isInsidePlan(BlockPos pos) {
        return this.EF == null
                ? false
                : Math.abs(pos.getX() - this.EF.getX()) <= this.horizontalRange.get()
                        && Math.abs(pos.getZ() - this.EF.getZ()) <= this.horizontalRange.get()
                        && pos.getY() >= this.EF.getY()
                        && pos.getY() < this.EF.getY() + this.mineHeight.get();
    }

    public void B(Event<MatrixStack> event) {
        if (this.ae.get() && this.mode.get() == AutoMine$Mode.BATCH) {
            this.aL.C(event);
        }
    }

    private boolean TV() {
        if (this.mode.get() == AutoMine$Mode.BATCH && this.EF != null && !this.TY()) {
            return switch (this.EE) {
                case yc -> true;
                case ya -> this.mineDuringCollect.get();
                default -> false;
            };
        } else {
            return false;
        }
    }

    private BlockPos findMinePosAround(BlockPos standPos, boolean requireReachable) {
        for (int var3 = this.mineHeight.get() - 1; var3 >= 0; var3--) {
            for (Vector2i var5 : InteractExtra.INSTANCE.getBlocksAround()) {
                BlockPos var6 = standPos.add(var5.x, var3, var5.y);
                if (this.isInsidePlan(var6)
                        && (requireReachable
                                ? this.checkDistanceAndCondition(var6)
                                : this.isMineable(mc.world.getBlockState(var6)))) {
                    return var6;
                }
            }
        }

        return null;
    }

    private int resolveTargetStandY(BlockPos currentStandPos) {
        int var2 = Math.max(mc.world.getBottomY() + 1, currentStandPos.getY() - this.startDownOffset.get());
        int var3 = Math.max(mc.world.getBottomY(), var2 - this.heightSearchLimit.get());

        for (int var4 = var2 - 1; var4 >= var3; var4--) {
            BlockPos var5 = new BlockPos(currentStandPos.getX(), var4, currentStandPos.getZ());
            if (!this.isMineable(mc.world.getBlockState(var5))) {
                return var4 + 1;
            }
        }

        return var2;
    }

    private boolean checkDistanceAndCondition(BlockPos newPos) {
        if (newPos == null) {
            return false;
        } else {
            PlayerInteractionAccess var2 = PlayerInteractionAccess.of(mc.interactionManager);
            if (Objects.equals(var2.getCurrentFailBreakPos(), newPos)) {
                return false;
            } else if (!this.isInsidePlan(newPos)) {
                return false;
            } else {
                return !this.isMineable(mc.world.getBlockState(newPos))
                        ? false
                        : !MineTasks.distanceOutOfReach(newPos, mc.player.getEyePos());
            }
        }
    }

    private IPathGoal processCollectGoal() {
        ItemEntity var1 = this.findCollectTarget(this.refreshCollectDrops.get());
        if (var1 != null) {
            return new GoalNear(var1.getPos(), 1.25);
        } else {
            while (this.EI >= 0) {
                BlockPos var2 = this.EG.get(this.EI);
                if (!this.isNear(var2, 1.5)) {
                    return PathingSchedular.pathToOrNearStopGoal(var2, 1.25);
                }

                this.EI--;
            }

            return null;
        }
    }

    private ItemEntity findCollectTarget(boolean dynamic) {
        Box var2 =
                mc.player.getBoundingBox().expand(this.collectSearchRadius.get(), 2.0, this.collectSearchRadius.get());
        return mc
                .world
                .getEntitiesByType(
                        EntityType.ITEM,
                        var2,
                        item -> !this.isCollectibleDrop(item) ? false : dynamic || this.EJ.contains(item.getUuid()))
                .stream()
                .min(Comparator.comparingDouble(item -> item.getPos().squaredDistanceTo(mc.player.getPos())))
                .orElse(null);
    }

    private boolean TY() {
        return this.countEmptyInventorySlots() < this.requiredEmptySlots.get();
    }

    private void TS() {
        while (this.EH < this.EG.size()) {
            if (this.Ue(this.EG.get(this.EH))) {
                return;
            }

            this.EH++;
        }
    }

    private boolean shouldDischargeStack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BlockItem var3
                ? this.EL.get().test(var3.getBlock())
                : false;
    }

    private boolean isMineable(BlockState state) {
        if (state != null && !state.isAir() && !state.isLiquid()) {
            Block var2 = state.getBlock();
            return var2.getHardness() >= 0.0F && this.EL.get().test(var2);
        } else {
            return false;
        }
    }

    private BlockPos Uf() {
        return this.findMinePosAround(this.getMiningBasePos(), true);
    }

    private boolean shouldEnterCollectMode() {
        if (!this.hasCollectTarget(true)) {
            return false;
        } else if (this.Uf() != null) {
            return false;
        } else {
            return this.EH >= this.EG.size()
                    ? true
                    : mc.player.getPos().squaredDistanceTo(this.EG.get(this.EH).toCenterPos())
                            > MathUtils.a(this.collectEnterDistance.get());
        }
    }

    private void tickBatch() {
        this.ensureBatchPlan();
        this.TR();
        this.aL.tickPathing(mc.player);
        if (this.TV()) {
            this.onMineCommon(this::Uf);
            this.TR();
        }
    }

    private boolean Ue(BlockPos standPos) {
        return this.findMinePosAround(standPos, false) != null;
    }

    private int onMineCommon(Supplier<BlockPos> posFinder) {
        int var2 = 0;
        Vec2f var3 = new Vec2f(mc.player.getPitch(), mc.player.getYaw());

        do {
            if (!this.checkDistanceAndCondition(this.W)) {
                this.W = (BlockPos) posFinder.get();
            }

            if (this.W == null || this.considerCooldown.get() && MineExtra.INSTANCE.ade() > 0) {
                break;
            }

            PlayerInteractionAccess.of(mc.interactionManager).setMiningCooldown(0);
            BlockState var4 = mc.world.getBlockState(this.W);
            KalamaHelperHelperK var5 = this.autoSwap.get()
                    ? InventoryUtils.v(
                            stack -> this.isDurabilityOk(stack)
                                    ? (double) WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(
                                            mc.player, var4, stack)
                                    : null,
                            true,
                            true)
                    : InventoryUtils.getSelectedItem();
            if (var5 == null || !this.isDurabilityOk((ItemStack) var5.val())) {
                if (this.durabilityProtect.get()) {
                    this.ae.set(false);
                    break;
                }

                var5 = InventoryUtils.getSelectedItem();
            }

            InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
            AttributeUtils.updateAttribute(mc.player);
            float var6 = MineExtra.INSTANCE.adq(this.W);
            var2++;
            Vec3d var7 = this.W.toCenterPos().subtract(mc.player.getEyePos());
            Direction var8 = Direction.getFacing(var7).getOpposite();
            switch ((Configs$MineTargetingMode) this.legalMode.get()) {
                case SWING_HAND_AND_ROT:
                    Vec3d var11 = mc.player.getRotationVector();
                    Vec3d var13 = new Vec3d(var11.x, 0.0, var11.z);
                    if (var13.dotProduct(var7) < 0.0) {
                        PlayerStateManager.nT(mc.player, mc.player.getYaw() + 180.0F);
                        mc.getNetworkHandler()
                                .sendPacket(VPacket.h(
                                        mc.player.getYaw(),
                                        mc.player.getPitch(),
                                        mc.player.isOnGround(),
                                        mc.player.horizontalCollision));
                    }
                    break;
                case SWING_HAND_AND_TARGET:
                    Vec3d var9 = var7.normalize();
                    Vec2f var10 = EntityUtils.q(var9);
                    if (Math.abs(EntityUtils.j(mc.player.getYaw(), var10.y)) > 30.0F) {
                        mc.player.setPitch(var10.x);
                        mc.player.setYaw(var10.y);
                        mc.getNetworkHandler()
                                .sendPacket(VPacket.h(
                                        mc.player.getYaw(),
                                        mc.player.getPitch(),
                                        mc.player.isOnGround(),
                                        mc.player.horizontalCollision));
                    }
            }

            mc.interactionManager.updateBlockBreakingProgress(this.W, var8);
            if (this.legalMode.get().hasSwing()) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }

            if (!MineExtra.INSTANCE.adl(var6)) {
                PlayerInteractionAccess var12 = PlayerInteractionAccess.of(mc.interactionManager);
                if (!this.useDoubleBreak.get()
                        || !Objects.equals(var12.getCurrentMiningPos(), this.W)
                        || !var12.isFailBreakEmpty()) {
                    break;
                }

                var12.sendFailBreakCurrentPos(null);
            }
        } while (!mc.interactionManager.isBreakingBlock() && var2 < 30);

        if (mc.player.getPitch() != var3.x || mc.player.getYaw() != var3.y) {
            mc.player.setPitch(var3.x);
            mc.player.setYaw(var3.y);
            ClientPlayerAccess.of(mc.player).resyncRot();
        }

        return var2;
    }

    private boolean hasCollectTarget(boolean dynamic) {
        return this.findCollectTarget(dynamic) != null;
    }

    public AutoMine() {
        super("AutoMine");
        this.EJ = new HashSet<>();
        this.aD = makePath(Configs.o, "survival-mine-utils.auto-mine");
        this.ae = this.flagBuilder(this.aD.addEnable()).build();
        this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable())
                .build();
        this.mode = this.builder(this.aD.add("mode"), AutoMine$Mode.class)
                .defaultValue(AutoMine$Mode.BATCH)
                .build();
        this.enableBaritone = this.flagBuilder(this.aD.add("enable-baritone"))
                .defaultValue(true)
                .build();
        this.EL = this.builder(this.aD.add("whitelist"), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<Block>(
                        new Regex("^(sand|red_sand|gravel|clay|dirt|grass_block)$"), Registries.BLOCK))
                .build();
        this.startDownOffset = this.intBuilder(this.aD.add("start-down-offset"))
                .defaultValue(3)
                .validator(Configs.d)
                .build();
        this.heightSearchLimit = this.intBuilder(this.aD.add("height-search-limit"))
                .defaultValue(24)
                .validator(Configs.e)
                .build();
        this.horizontalRange = this.intBuilder(this.aD.add("horizontal-range"))
                .defaultValue(8)
                .validator(Configs.d)
                .build();
        this.mineHeight = this.intBuilder(this.aD.add("mine-height"))
                .defaultValue(6)
                .validator(Configs.e)
                .build();
        this.routeSpacing = this.intBuilder(this.aD.add("route-spacing"))
                .defaultValue(3)
                .validator(Configs.e)
                .build();
        this.collectEnterDistance = this.doubleBuilder(this.aD.add("collect-enter-distance"))
                .defaultValue(8.0)
                .build();
        this.collectSearchRadius = this.doubleBuilder(this.aD.add("collect-search-radius"))
                .defaultValue(8.0)
                .build();
        this.mineDuringCollect = this.flagBuilder(this.aD.add("mine-during-collect"))
                .defaultValue(true)
                .build();
        this.refreshCollectDrops = this.flagBuilder(this.aD.add("refresh-collect-drops"))
                .defaultValue(true)
                .build();
        this.requiredEmptySlots = this.intBuilder(this.aD.add("required-empty-slots"))
                .defaultValue(1)
                .validator(Configs.d)
                .build();
        this.legalMode = this.builder(this.aD.add("legal-mode"), Configs$MineTargetingMode.class)
                .defaultValue(Configs$MineTargetingMode.NO_BYPASS)
                .build();
        this.considerCooldown = this.builder(this.aD.add("consider-cooldown"), Boolean.class)
                .defaultValue(true)
                .build();
        this.autoSwap =
                this.flagBuilder(this.aD.add("auto-swap")).defaultValue(true).build();
        this.durabilityProtect = this.flagBuilder(this.aD.add("durability-protect"))
                .defaultValue(true)
                .build();
        this.useDoubleBreak = this.flagBuilder(this.aD.add("use-double-break")).build();
        this.aL.d(true).D(this::Un).E(this::di).G(this::TY).I(this::shouldDischargeStack);
        this.bindFlag(this.ae);
    }

    private IPathGoal di() {
        if (this.mode.get() == AutoMine$Mode.BATCH && this.EF != null) {
            return switch (this.EE) {
                case xZ -> PathingSchedular.pathToOrNearStopGoal(this.EF, 1.25);
                case yc -> this.TW();
                case ya -> this.processCollectGoal();
                case yb -> null;
            };
        } else {
            return null;
        }
    }

    private List<BlockPos> buildBatchRoute(BlockPos center) {
        int var2 = this.horizontalRange.get();
        int var3 = Math.max(1, this.routeSpacing.get());
        it.unimi.dsi.fastutil.ints.IntArrayList var4 = new it.unimi.dsi.fastutil.ints.IntArrayList();
        it.unimi.dsi.fastutil.ints.IntArrayList var5 = new it.unimi.dsi.fastutil.ints.IntArrayList();

        for (int var6 = center.getX() - var2; var6 <= center.getX() + var2; var6 += var3) {
            var4.add(var6);
        }

        if (var4.isEmpty() || (Integer) var4.get(var4.size() - 1) != center.getX() + var2) {
            var4.add(center.getX() + var2);
        }

        for (int var13 = center.getZ() - var2; var13 <= center.getZ() + var2; var13 += var3) {
            var5.add(var13);
        }

        if (var5.isEmpty() || (Integer) var5.get(var5.size() - 1) != center.getZ() + var2) {
            var5.add(center.getZ() + var2);
        }

        ArrayList var14 = new ArrayList();
        boolean var7 = false;

        for (int var9 : var5) {
            if (!var7) {
                for (int var16 : var4) {
                    BlockPos var12 = new BlockPos(var16, center.getY(), var9);
                    if (this.Ue(var12)) {
                        var14.add(var12);
                    }
                }
            } else {
                for (int var10 = var4.size() - 1; var10 >= 0; var10--) {
                    BlockPos var11 = new BlockPos((Integer) var4.get(var10), center.getY(), var9);
                    if (this.Ue(var11)) {
                        var14.add(var11);
                    }
                }
            }

            var7 = !var7;
        }

        if (var14.isEmpty()) {
            var14.add(center);
        }

        return var14;
    }

    private void TM() {
        this.EE = SurvivalSubHelperM.xZ;
        this.EF = null;
        this.EG = Collections.emptyList();
        this.EH = 0;
        this.EI = 0;
        this.W = null;
        this.EJ.clear();
        this.aL.A();
    }

    public void bl(Event<Void> event) {
        if (!checkNull() && this.ae.get()) {
            switch ((AutoMine$Mode) this.mode.get()) {
                case BATCH:
                    this.tickBatch();
                    break;
                case ACCURATE:
                    this.TM();
            }
        }
    }

    private IPathGoal TW() {
        this.TS();
        return this.EH >= this.EG.size() ? null : PathingSchedular.pathToOrNearStopGoal(this.EG.get(this.EH), 1.25);
    }

    private void TR() {
        if (this.EF != null && !this.TY()) {
            switch (this.EE) {
                case xZ:
                    if (this.isNear(this.EF, 1.75)) {
                        this.EE = SurvivalSubHelperM.ya;
                    }
                    break;
                case yc:
                    this.TS();
                    if (this.EH >= this.EG.size()) {
                        if (this.hasCollectTarget(true)) {
                            this.enterCollectMode();
                        } else {
                            this.EE = SurvivalSubHelperM.yc;
                        }

                        return;
                    }

                    if (this.shouldEnterCollectMode()) {
                        this.enterCollectMode();
                    }
                    break;
                case ya:
                    if (this.findCollectTarget(this.refreshCollectDrops.get()) != null) {
                        return;
                    }

                    if (this.EI >= 0) {
                        return;
                    }

                    this.TS();
                    if (this.EH < this.EG.size()) {
                        this.EE = SurvivalSubHelperM.ya;
                    } else if (!this.hasCollectTarget(true)) {
                        this.EE = SurvivalSubHelperM.yc;
                    }
                    break;
                case yb:
                    if (this.hasCollectTarget(true)) {
                        this.enterCollectMode();
                    }
            }
        }
    }

    private BlockPos getMiningBasePos() {
        return new BlockPos(mc.player.getBlockX(), this.EF.getY(), mc.player.getBlockZ());
    }

    private boolean isNear(BlockPos pos, double distance) {
        return pos != null && mc.player.getPos().squaredDistanceTo(pos.toCenterPos()) <= MathUtils.a(distance);
    }

    private boolean isDurabilityOk(ItemStack item) {
        if (item.isEmpty()) {
            return true;
        } else {
            int var2;
            if (item.get(DataComponentTypes.UNBREAKABLE) != null) {
                var2 = 0;
            } else {
                if (item.get(DataComponentTypes.MAX_DAMAGE) == null) {
                    return true;
                }

                RegistryEntry var3 = RegistryUtils.g(ItemStackUtils.registry(), Enchantments.UNBREAKING);
                int var4 = 1;
                if (var3 != null) {
                    var4 = EnchantmentHelper.getLevel(var3, item) + 1;
                }

                var2 = 8 / var4;
            }

            int var5 = Math.max(9, var2);
            return item.getDamage() <= item.getMaxDamage() - var5;
        }
    }

    private int countEmptyInventorySlots() {
        int var1 = 0;

        for (int var2 = 0; var2 < 36; var2++) {
            if (mc.player.getInventory().getStack(var2).isEmpty()) {
                var1++;
            }
        }

        return var1;
    }

    private void enterCollectMode() {
        this.EE = SurvivalSubHelperM.yb;
        this.EI = Math.min(this.EG.size() - 1, Math.max(this.EH - 1, 0));
        this.EJ.clear();
        if (!this.refreshCollectDrops.get()) {
            Box var1 = mc.player
                    .getBoundingBox()
                    .expand(this.collectSearchRadius.get(), 2.0, this.collectSearchRadius.get());

            for (ItemEntity var3 : mc.world.getEntitiesByType(EntityType.ITEM, var1, this::isCollectibleDrop)) {
                this.EJ.add(var3.getUuid());
            }
        }
    }

    private boolean isCollectibleDrop(ItemEntity itemEntity) {
        return itemEntity != null
                && !itemEntity.isRemoved()
                && !itemEntity.getStack().isEmpty();
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.TM();
    }
}
