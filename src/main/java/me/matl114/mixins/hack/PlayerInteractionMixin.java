package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.mine.MineExtra;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.AttributeUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerInteractionManager.class})
public abstract class PlayerInteractionMixin implements PlayerInteractionAccess {
    @Shadow
    private float field_3715;

    @Shadow
    private boolean field_3717;

    @Shadow
    private ItemStack field_3718;

    @Shadow
    private int field_3716;

    @Shadow
    private float field_3713;

    @Shadow
    private BlockPos field_3714;

    @Nullable
    @Unique
    private BlockPos currentFailBreakPos = null;

    @Unique
    private int failBreakStartTick;

    @Shadow
    private GameMode field_3719;

    @Shadow
    @Final
    private MinecraftClient field_3712;

    @Shadow
    @Final
    private ClientPlayNetworkHandler field_3720;

    @Unique
    private int lastBreakCooldown = 0;

    @Shadow
    protected abstract void method_41931(ClientWorld var1, SequencedPacketCreator var2);

    @Shadow
    public abstract boolean method_2899(BlockPos var1);

    @Override
    public BlockPos getCurrentMiningPos() {
        return this.field_3714;
    }

    @Override
    public void resetCurrentMiningPos() {
        this.field_3714 = new BlockPos(-1, -1, -1);
        this.resetLocalMiningProgress();
    }

    @Nullable
    @Override
    public BlockPos getCurrentFailBreakPos() {
        return this.currentFailBreakPos;
    }

    @Unique
    @Override
    public boolean isFailBreakEmpty() {
        return this.currentFailBreakPos == null;
    }

    @Unique
    @Override
    public int getCurrentMiningTicks() {
        return Tasks.b() - MineExtra.INSTANCE.Nc;
    }

    @Unique
    @Override
    public int getFailBreakMiningTicks() {
        return Tasks.b() - this.failBreakStartTick;
    }

    @Unique
    @Override
    public int getMiningCooldown() {
        return this.field_3716;
    }

    @Unique
    @Override
    public void setMiningCooldown(int val) {
        this.field_3716 = val;
    }

    @Override
    public float getCurrentMiningProgress(@Nullable ItemStack tool) {
        BlockState block = MinecraftClient.getInstance().world.getBlockState(this.field_3714);
        if (block.isAir()) {
            return -1.0F;
        } else if (!this.field_3717 && !MineExtra.INSTANCE.sameBlockOptimize.get()) {
            return -1.0F;
        } else if (tool == null && this.field_3717 && this.method_2922(this.field_3714)) {
            return this.field_3715 == 0.0F ? -1.0F : this.field_3715;
        } else {
            ItemStack usedTool = tool == null ? this.field_3712.player.getMainHandStack() : tool;
            return this.predictCurrentMiningProgressWithTool(usedTool);
        }
    }

    @Unique
    public boolean beginFailBreak(BlockPos pos) {
        if (this.currentFailBreakPos == null) {
            this.currentFailBreakPos = pos;
            this.field_3714 = pos;
            this.failBreakStartTick = MineExtra.INSTANCE.Nc;
            MineExtra.INSTANCE.Ng = Tasks.b();
            return true;
        } else {
            return false;
        }
    }

    @Unique
    public boolean moveCurrentMiningToFailBreak() {
        return this.beginFailBreak(this.field_3714);
    }

    @Unique
    public void clearFailBreak() {
        this.currentFailBreakPos = null;
        this.failBreakStartTick = 0;
    }

    @Unique
    private void resetLocalMiningProgress() {
        this.field_3715 = 0.0F;
    }

    @Unique
    private void clearBreakingState() {
        this.field_3717 = false;
    }

    @Unique
    private void applyPostStopState(boolean resetProgress) {
        if (resetProgress) {
            this.resetLocalMiningProgress();
        }

        this.field_3713 = 0.0F;
        this.field_3716 = MineExtra.INSTANCE.ade();
    }

    @Unique
    @Override
    public void sendBreakPacket(BlockPos pos, Direction direction) {
        this.method_41931(
                MinecraftClient.getInstance().world,
                sequence -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos, direction, sequence));
    }

    @Unique
    private void continueSameBlockMining(BlockPos pos, Direction direction) {
        this.field_3714 = pos;
        this.field_3715 = this.getCurrentMiningProgress(null);
        this.field_3716 = 0;
        this.field_3717 = true;
        this.field_3718 = this.field_3712.player.getMainHandStack();
        this.field_3712.world.setBlockBreakingInfo(
                this.field_3712.player.getId(), this.field_3714, this.method_51888());
        this.method_2902(pos, direction);
    }

    @Unique
    private boolean tryAbortCurrentMiningIntoFailBreak() {
        if (MineExtra.INSTANCE.doubleBreak.get() && this.isFailBreakEmpty()) {
            ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
            if (!playerEntity.canInteractWithBlockAt(this.field_3714, 1.0)) {
                return false;
            } else {
                BlockState state = MinecraftClient.getInstance().world.getBlockState(this.field_3714);
                if (!state.isAir() && !state.isLiquid()) {
                    float speed = state.calcBlockBreakingDelta(
                            MinecraftClient.getInstance().player,
                            MinecraftClient.getInstance().player.getEntityWorld(),
                            this.field_3714);
                    if (speed <= 0.0F) {
                        return false;
                    } else {
                        this.moveCurrentMiningToFailBreak();
                        MineExtra.INSTANCE.adi(this.field_3714, speed, this.field_3715);
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Unique
    private boolean shouldClearFailBreakBecauseInvalidState() {
        if (MinecraftClient.getInstance().world == null) {
            return false;
        } else {
            BlockState state = MinecraftClient.getInstance().world.getBlockState(this.currentFailBreakPos);
            if (this.field_3712.player == null || this.field_3719 != GameMode.SURVIVAL) {
                return true;
            } else if (state != null && !state.isAir() && !state.isLiquid()) {
                float speed = state.calcBlockBreakingDelta(
                        MinecraftClient.getInstance().player,
                        MinecraftClient.getInstance().world,
                        this.currentFailBreakPos);
                return speed > 0.0F && (Tasks.b() - this.failBreakStartTick - 1) * speed > 1.0F
                        ? true
                        : this.field_3712.player != null
                                && this.field_3712.player.getPos().squaredDistanceTo(this.field_3714.toCenterPos())
                                        > 225.0;
            } else {
                return true;
            }
        }
    }

    @Unique
    public boolean calculateInstantBlockBreakingDeltaWithGhostHand(BlockState instance, BlockPos pos) {
        if (MineExtra.INSTANCE.ghostHandMine.get()) {
            KalamaHelperHelperK<ItemStack> bestTool = MineExtra.INSTANCE.acU(instance);
            if (MineExtra.INSTANCE.ghostHandSwapWhenStart.get()
                    || WorldUtils.calcBlockBreakingDelta(
                                    instance,
                                    this.field_3712.world,
                                    pos,
                                    WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(
                                            this.field_3712.player, instance, bestTool.val()))
                            > 1.01) {
                MineExtra.INSTANCE.MV = Pair.of(InvExtra.INSTANCE.swapInventoryIndexToHand(bestTool.index()), pos);
                return true;
            }
        }

        return false;
    }

    @Unique
    @Override
    public void startMiningBlock(BlockPos pos, Direction direction) {
        this.method_41931(MinecraftClient.getInstance().world, sequence -> {
            BlockState state = this.field_3712.world.getBlockState(pos);
            Disabler.INSTANCE.ajC();
            if (!this.field_3712.player.getAbilities().creativeMode
                    && (state.isAir()
                            || !this.calculateInstantBlockBreakingDeltaWithGhostHand(state, pos)
                                    && !(state.calcBlockBreakingDelta(
                                                    this.field_3712.player, this.field_3712.world, pos)
                                            > 1.0))) {
                this.resetLocalMiningProgress();
                this.field_3714 = pos;
            } else {
                this.method_2899(pos);
            }

            return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, direction, sequence);
        });
    }

    @Override
    public void abortBreak(Direction direction) {
        this.field_3720.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.field_3714, direction));
        this.field_3717 = false;
    }

    @Unique
    @Override
    public void syncSelectedHotbar(int x) {
        this.field_3712.player.getInventory().selectedSlot = x;
        this.method_2911();
    }

    @Unique
    @Override
    public boolean breakIfComplete() {
        BlockState state = this.field_3712.world.getBlockState(this.field_3714);
        if (!state.isAir() && !state.isLiquid()) {
            Vec3d shouldFacing = this.field_3714
                    .toCenterPos()
                    .subtract(MinecraftClient.getInstance().player.getEyePos());
            Direction direction = Direction.getFacing(shouldFacing).getOpposite();
            return this.breakIfComplete(this.field_3714, state, direction);
        } else {
            return true;
        }
    }

    @Unique
    public boolean breakIfComplete(BlockPos pos, BlockState blockState, Direction direction) {
        MineExtra mineExtra = MineExtra.INSTANCE;
        KalamaHelperHelperK<ItemStack> tool = MineExtra.INSTANCE.acU(blockState);
        float progress = this.getCurrentMiningProgress(tool.val());
        if (mineExtra.adk(progress)) {
            this.field_3715 = progress;
            Disabler.INSTANCE.ajC();
            this.clearBreakingState();
            Runnable fastBreakGhostHand = InvExtra.INSTANCE.swapInventoryIndexToHand(tool.index());
            AttributeUtils.updateAttribute(this.field_3712.player);
            float speed =
                    blockState.calcBlockBreakingDelta(MinecraftClient.getInstance().player, this.field_3712.world, pos);
            this.method_41931(MinecraftClient.getInstance().world, sequence -> {
                this.method_2899(pos);
                return new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos, direction, sequence);
            });
            if (fastBreakGhostHand != null) {
                fastBreakGhostHand.run();
            }

            mineExtra.adi(pos, speed, this.field_3715);
            this.applyPostStopState(!mineExtra.sameBlockOptimize.get());
            return true;
        } else {
            return false;
        }
    }

    @Inject(
            method = {"updateBlockBreakingProgress"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/tutorial/TutorialManager;onBlockBreaking(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V",
                        ordinal = 1,
                        shift = Shift.AFTER)
            },
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void fastbreak(
            BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        if (this.breakIfComplete(pos, blockState, direction)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = {"attackBlock"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                        ordinal = 1,
                        shift = Shift.BEFORE)
            },
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    public void samePositionOptimize(
            BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        MineExtra mineExtra = MineExtra.INSTANCE;
        if (mineExtra.sameBlockOptimize.get()) {
            if (Objects.equals(pos, this.field_3714)) {
                if (!mineExtra.adj()) {
                    return;
                }

                this.continueSameBlockMining(pos, direction);
                cir.setReturnValue(true);
            } else {
                float predictedProgress = this.getCurrentMiningProgress(null);
                if (mineExtra.adn(predictedProgress)) {
                    this.sendFailBreakCurrentPos(direction);
                }
            }
        }
    }

    @WrapOperation(
            method = {"cancelBlockBreaking"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")
            })
    private void onDoubleBreak(ClientPlayNetworkHandler instance, Packet packet, Operation<Void> original) {
        if (MineExtra.INSTANCE.sameBlockOptimize.get() || !this.sendFailBreakCurrentPos(null)) {
            original.call(new Object[] {instance, packet});
        }
    }

    @Unique
    @Override
    public boolean sendFailBreakCurrentPos(@Nullable Direction direction) {
        if (this.tryAbortCurrentMiningIntoFailBreak()) {
            if (direction == null) {
                Vec3d shouldFacing = this.field_3714
                        .toCenterPos()
                        .subtract(MinecraftClient.getInstance().player.getEyePos());
                direction = Direction.getFacing(shouldFacing).getOpposite();
            }

            this.sendBreakPacket(this.field_3714, direction);
            return true;
        } else {
            return false;
        }
    }

    @WrapOperation(
            method = {"attackBlock"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")
            })
    private void onDoubleBreak2(
            ClientPlayNetworkHandler instance,
            Packet packet,
            Operation<Void> original,
            @Local(argsOnly = true) Direction direction) {
        if (MineExtra.INSTANCE.sameBlockOptimize.get() || !this.sendFailBreakCurrentPos(direction)) {
            original.call(new Object[] {instance, packet});
        }
    }

    @Shadow
    protected abstract int method_51888();

    @Shadow
    public abstract boolean method_2902(BlockPos var1, Direction var2);

    @Shadow
    protected abstract boolean method_2922(BlockPos var1);

    @Shadow
    protected abstract void method_2911();

    @Shadow
    protected abstract ActionResult method_41934(ClientPlayerEntity var1, Hand var2, BlockHitResult var3);

    @Inject(
            method = {"attackBlock"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                        ordinal = 0,
                        shift = Shift.AFTER)
            },
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void instaBreakPacket(
            BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        MineExtra.INSTANCE.adg(pos, Float.MAX_VALUE, true);
    }

    @Inject(
            method = {"attackBlock"},
            at = {
                @At(
                        value = "FIELD",
                        target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I",
                        shift = Shift.BEFORE)
            },
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    public void fastBreakCreative(
            BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        this.applyPostStopState(false);
        if (MineExtra.INSTANCE.MA.get()) {
            cir.setReturnValue(true);
        }
    }

    @WrapOperation(
            method = {"method_41930"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")
            })
    public float fastBreakGhostHand(
            BlockState instance,
            PlayerEntity player,
            BlockView blockView,
            BlockPos blockPos,
            Operation<Float> original) {
        if (this.calculateInstantBlockBreakingDeltaWithGhostHand(instance, blockPos)) {
            AttributeUtils.updateAttribute(player);
        }

        return (Float) original.call(new Object[] {instance, player, blockView, blockPos});
    }

    @Inject(
            method = {"attackBlock"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                        ordinal = 1,
                        shift = Shift.AFTER)
            },
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void earlyBreakPacket(
            BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        KalamaHelperHelperK<ItemStack> usingTool = MineExtra.INSTANCE.acU(blockState);
        float playerSpeed = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(
                this.field_3712.player, blockState, usingTool.val());
        float speed = WorldUtils.calcBlockBreakingDelta(blockState, this.field_3712.world, pos, playerSpeed);
        MineExtra mineExtra = MineExtra.INSTANCE;
        mineExtra.adg(pos, speed, false);
        if (mineExtra.ado() && !blockState.isAir()) {
            if (mineExtra.adm(speed)) {
                Disabler.INSTANCE.ajC();
                Runnable fastbreakCallback = InvExtra.INSTANCE.swapInventoryIndexToHand(usingTool.index());
                AttributeUtils.updateAttribute(this.field_3712.player);
                this.clearBreakingState();
                this.method_41931(MinecraftClient.getInstance().world, sequence -> {
                    this.method_2899(pos);
                    return new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos, direction, sequence);
                });
                if (fastbreakCallback != null) {
                    fastbreakCallback.run();
                }

                mineExtra.adi(pos, speed, this.field_3715);
                this.applyPostStopState(!mineExtra.sameBlockOptimize.get());
            }
        }
    }

    @Inject(
            method = {"updateBlockBreakingProgress"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                        ordinal = 0,
                        shift = Shift.AFTER)
            },
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void instaBreakPacketWhenUpdate(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        MineExtra.INSTANCE.adg(pos, Float.MAX_VALUE, true);
        this.applyPostStopState(false);
    }

    @Inject(
            method = {"updateBlockBreakingProgress"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                        ordinal = 1,
                        shift = Shift.AFTER)
            })
    private void onCommonBlockBreak(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        MineExtra.INSTANCE.adh(pos);
    }

    @Inject(
            method = {"hasLimitedAttackSpeed"},
            at = {@At("HEAD")},
            cancellable = true)
    public void cancelAttackSpeedLimit(CallbackInfoReturnable<Boolean> cir) {
        if (CombatTasks.j().cancelInterval.get()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = {"tick"},
            at = {@At("RETURN")})
    public void onTick(CallbackInfo ci) {
        if (MineExtra.INSTANCE.vanillaBreak.get()) {
            if (this.lastBreakCooldown != this.field_3716) {
                this.lastBreakCooldown = this.field_3716;
            } else if (this.field_3716 > 0) {
                this.field_3716--;
                this.lastBreakCooldown = this.field_3716;
            }
        }

        if (!this.isFailBreakEmpty() && this.shouldClearFailBreakBecauseInvalidState()) {
            this.clearFailBreak();
        }
    }

    @ModifyExpressionValue(
            method = {"clickSlot"},
            at = {
                @At(
                        value = "FIELD",
                        target =
                                "Lnet/minecraft/entity/player/PlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;")
            })
    public ScreenHandler onClickSlot(ScreenHandler original, @Local(argsOnly = true) PlayerEntity player) {
        return player instanceof ClientPlayerAccess clientPlayer ? clientPlayer.getServerScreenHandler() : original;
    }

    @Inject(
            method = {"isCurrentlyBreaking"},
            at = {@At("HEAD")},
            cancellable = true)
    public void onCurrentlyBreaking(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Objects.equals(pos, this.field_3714)
                && ItemStackUtils.matchItemMiningAbility(this.field_3712.player.getMainHandStack(), this.field_3718));
    }

    @Inject(
            method = {"interactItem"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V",
                        shift = Shift.AFTER)
            },
            order = -114514)
    private void onInteractPreSend(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LegacySnapRotManager.INSTANCE.betweenViaPacket = true;
    }

    @Inject(
            method = {"interactItem"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lorg/apache/commons/lang3/mutable/MutableObject;<init>()V",
                        remap = false)
            },
            order = 114514)
    private void onInteractPostSend(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LegacySnapRotManager.INSTANCE.betweenViaPacket = false;
    }

    @Unique
    @Override
    public ActionResult simulateInteractBlock(Hand hand, BlockHitResult hitResult) {
        return this.method_41934(this.field_3712.player, hand, hitResult);
    }

    @Unique
    @Override
    public ActionResult simulateInteractItem(Hand hand) {
        ClientPlayerEntity player = this.field_3712.player;
        ItemStack itemStack = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            return ActionResult.PASS;
        } else {
            TypedActionResult<ItemStack> actionResult = itemStack.use(this.field_3712.world, player, hand);
            player.setStackInHand(hand, itemStack);
            return actionResult.getResult();
        }
    }
}
