package me.matl114.hacks.modules.mine;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MineTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$MineTargetingMode;
import me.matl114.managers.config.ConfigEnum;
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
import me.matl114.utils.RegistryUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.versioned.api.VPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector2i;

public class MineBot extends BaseModule {
    private static final int NS = 400;
    public final FlagRef autoSwap;
    public final EnumRef<MineBot$MineBotMode> mineMode;
    public final ModulePath NI;
    public final IntRef maxDy;
    private final Random eU = new Random();
    public final IntRef maxWidth;
    private final TimerExecutor NR;
    public final FlagRef considerCooldown;
    public final FlagRef durabilityProtect;
    public final KeyBindRef el;
    public final FlagRef ae;
    public final IntRef minDy;
    public final EnumRef<Configs$MineTargetingMode> legalMode;
    public final NBTRef<EntrySet<Block>> NJ;
    public final FlagRef useDoubleBreak;
    public BlockPos W;
    public final IntRef maxInstantMine;
    public static final int NP = 4;
    public static final int NQ = 9;

    private boolean isMineable(BlockState state) {
        if (state != null && !state.isAir() && !state.isLiquid()) {
            Block var2 = state.getBlock();
            if (var2.getHardness() >= 0.0F && this.NJ.get().test(var2)) {
                return true;
            }
        }

        return false;
    }

    public int aeB() {
        return this.onMineCommon(this::aeH);
    }

    private BlockPos findNextMinePosLayer(boolean up) {
        BlockPos var2 = mc.player.getSteppingPos();
        BlockPos var3 = var2.add(0, 1, 0);
        int var4 = this.minDy.get();
        int var5 = this.maxDy.get();
        IntArrayList var6 = IntArrayList.toList(IntStream.range(var4, var5));
        if (!up) {
            Collections.reverse(var6);
        }

        IntListIterator var7 = var6.iterator();

        while (var7.hasNext()) {
            int var8 = (Integer) var7.next();

            for (Vector2i var10 : InteractExtra.INSTANCE.getBlocksAround()) {
                int var11 = var10.x;
                int var12 = var10.y;
                BlockPos var13 = var3.add(var11, var8, var12);
                if (this.checkDistanceAndCondition(var13)) {
                    return var13;
                }
            }
        }

        return null;
    }

    private BlockPos findNextMinePosSpherical() {
        BlockPos var1 = mc.player.getSteppingPos();
        BlockPos var2 = var1.add(0, 1, 0);
        int var3 = this.minDy.get();
        int var4 = this.maxDy.get();

        for (Vec3i var6 : InteractExtra.INSTANCE.fw()) {
            int var7 = var6.getX();
            int var8 = var6.getY();
            int var9 = var6.getZ();
            if (var8 >= var3 && var8 <= var4) {
                BlockPos var10 = var2.add(var7, var8, var9);
                if (this.checkDistanceAndCondition(var10)) {
                    return var10;
                }
            }
        }

        return null;
    }

    public boolean isDurabilityOk(ItemStack item) {
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

    private BlockPos aeH() {
        BlockPos var1 = mc.player.getSteppingPos();
        BlockPos var2 = var1.add(0, 1, 0);
        int var3 = this.minDy.get();
        int var4 = this.maxDy.get();
        ArrayList var5 = new ArrayList();

        for (Vec3i var7 : InteractExtra.INSTANCE.fw()) {
            int var8 = var7.getX();
            int var9 = var7.getY();
            int var10 = var7.getZ();
            if (var9 >= var3 && var9 <= var4) {
                BlockPos var11 = var2.add(var8, var9, var10);
                if (this.checkDistanceAndCondition(var11)) {
                    var5.add(var11);
                }
            }
        }

        return var5.isEmpty() ? null : (BlockPos) var5.get(this.eU.nextInt(0, var5.size()));
    }

    public int aeA() {
        return this.onMineCommon(this::findNextMinePosTunnel);
    }

    private BlockPos findNextMinePosSquare() {
        BlockPos var1 = mc.player.getSteppingPos();
        BlockPos var2 = var1.add(0, 1, 0);
        int var3 = this.minDy.get();
        int var4 = this.maxDy.get();
        int var5 = this.maxWidth.get();

        for (int var6 = var3; var6 < var4; var6++) {
            for (int var7 = -var5; var7 <= var5; var7++) {
                for (int var8 = -var5; var8 <= var5; var8++) {
                    BlockPos var9 = var2.add(var7, var6, var8);
                    if (this.checkDistanceAndCondition(var9)) {
                        return var9;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::onTick);
    }

    public MineBot() {
        super("MineBot");
        this.NI = makePath(Configs.g, "mine-bot");
        this.mineMode = this.builder(this.NI.add("mine-mode"), MineBot$MineBotMode.class)
                .defaultValue(MineBot$MineBotMode.SPHERICAL)
                .build();
        this.ae = this.flagBuilder(this.NI.addEnable()).build();
        this.el = this.moduleEntry(
                        this.NI.addHotkey(), new MultiKeyBind(), this.NI.addEnable(), moduleMeta(() -> this.mineMode))
                .build();
        this.NJ = this.builder(this.NI.add("whitelist"), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<Block>(new Regex("^(cobblestone|stone|.*ore)$"), Registries.BLOCK))
                .build();
        this.legalMode = this.builder(this.NI.add("legal-mode"), Configs$MineTargetingMode.class)
                .defaultValue(Configs$MineTargetingMode.NO_BYPASS)
                .show(() -> this.mineMode.get().isNotIn(new ConfigEnum[] {MineBot$MineBotMode.AUTO_TOOL}))
                .build();
        this.minDy = this.intBuilder(this.NI.add("min-dy"))
                .defaultValue(0)
                .show(() -> this.mineMode.get().isNotIn(new ConfigEnum[] {MineBot$MineBotMode.AUTO_TOOL}))
                .build();
        this.maxDy = this.intBuilder(this.NI.add("max-dy"))
                .defaultValue(6)
                .show(() -> this.mineMode.get() != MineBot$MineBotMode.AUTO_TOOL)
                .build();
        this.maxWidth = this.intBuilder(this.NI.add("max-width"))
                .defaultValue(1)
                .show(() -> this.mineMode
                        .get()
                        .isIn(new ConfigEnum[] {MineBot$MineBotMode.SQUARE, MineBot$MineBotMode.TUNNEL}))
                .validator(Configs.d)
                .build();
        this.maxInstantMine = this.intBuilder(this.NI.add("max-instant-mine"))
                .defaultValue(30)
                .build();
        this.useDoubleBreak = this.flagBuilder(this.NI.add("use-double-break")).build();
        this.considerCooldown = this.builder(this.NI.add("consider-cooldown"), Boolean.class)
                .defaultValue(true)
                .build();
        this.autoSwap =
                this.flagBuilder(this.NI.add("auto-swap")).defaultValue(true).build();
        this.durabilityProtect = this.builder(this.NI.add("durability-protect"), Boolean.class)
                .defaultValue(true)
                .build();
        this.W = null;
        this.NR = new TimerExecutor();
        this.bindFlag(this.ae);
    }

    public int aex() {
        return this.onMineCommon(this::findNextMinePosSpherical);
    }

    public int aez() {
        return this.onMineCommon(this::findNextMinePosSquare);
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.W = null;
    }

    public int onMineCommon(Supplier<BlockPos> posFinder) {
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
                            s -> this.isDurabilityOk(s)
                                    ? (double) WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(
                                            mc.player, var4, s)
                                    : null,
                            true,
                            true)
                    : InventoryUtils.getSelectedItem();
            if (var5 == null || !this.isDurabilityOk((ItemStack) var5.val())) {
                if (this.durabilityProtect.get()) {
                    this.logI18N("message.module.mine-bot.tool-broken-stop", new Object[0]);
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
        } while (!mc.interactionManager.isBreakingBlock() && var2 < this.maxInstantMine.get());

        if (mc.player.getPitch() != var3.x || mc.player.getYaw() != var3.y) {
            mc.player.setPitch(var3.x);
            mc.player.setYaw(var3.y);
            ClientPlayerAccess.of(mc.player).resyncRot();
        }

        if (var2 == 0) {
            this.NR.b(400, () -> this.logI18N("message.module.mine-bot.no-blocks", new Object[0]));
        } else {
            this.NR.f();
        }

        return var2;
    }

    public void onTick(Event<ClientPlayerEntity> player) {
        if (this.isActive()) {
            this.onMineBotTick();
        }
    }

    private boolean checkDistanceAndCondition(BlockPos newPos) {
        PlayerInteractionAccess var2 = PlayerInteractionAccess.of(mc.interactionManager);
        if (Objects.equals(var2.getCurrentFailBreakPos(), newPos)) {
            return false;
        } else {
            return MineTasks.distanceOutOfReach(newPos, mc.player.getEyePos())
                    ? false
                    : this.isMineable(mc.world.getBlockState(newPos));
        }
    }

    public void onMineBotTick() {
        if (mc.player != null && mc.world != null && mc.interactionManager != null) {
            switch ((MineBot$MineBotMode) this.mineMode.get()) {
                case SPHERICAL:
                    this.aex();
                    break;
                case LAYERED_UP:
                    this.onMineLayered(true);
                    break;
                case LAYERED_DOWN:
                    this.onMineLayered(false);
                    break;
                case SQUARE:
                    this.aez();
                    break;
                case TUNNEL:
                    this.aeA();
                    break;
                case RANDOM:
                    this.aeB();
                    break;
                case AUTO_TOOL:
                    this.onMineCustomTool();
            }
        }
    }

    public int onMineLayered(boolean up) {
        return this.onMineCommon(() -> this.findNextMinePosLayer(up));
    }

    public int onMineCustomTool() {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.BLOCK) {
            BlockHitResult var1 = (BlockHitResult) mc.crosshairTarget;
            BlockPos var2 = var1.getBlockPos();
            if (this.isMineable(mc.world.getBlockState(var2))) {
                mc.interactionManager.sendSequencedPacket(
                        mc.world,
                        sequence -> new PlayerInteractItemC2SPacket(
                                Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch()));
            }
        }

        return 0;
    }

    private BlockPos findNextMinePosTunnel() {
        BlockPos var1 = mc.player.getSteppingPos();
        BlockPos var2 = var1.add(0, 1, 0);
        int var3 = this.minDy.get();
        int var4 = this.maxDy.get();
        Direction var5 = mc.player.getHorizontalFacing();
        Direction var6 = var5.rotateYClockwise();
        IntArrayList var7 = new IntArrayList();
        var7.add(0);

        for (int var8 = 1; var8 <= this.maxWidth.get(); var8++) {
            var7.add(var8);
            var7.add(-var8);
        }

        double var9 = InteractExtra.INSTANCE.getBlockReachDistance();

        for (int var11 = 0; var11 <= var9; var11++) {
            BlockPos var12 = var2.offset(var5, var11);

            for (int var13 = var3; var13 < var4; var13++) {
                BlockPos var14 = var12.add(0, var13, 0);
                IntListIterator var15 = var7.iterator();

                while (var15.hasNext()) {
                    Integer var16 = (Integer) var15.next();
                    BlockPos var17 = var14.offset(var6, var16);
                    if (this.checkDistanceAndCondition(var17)) {
                        return var17;
                    }
                }
            }
        }

        return null;
    }
}
