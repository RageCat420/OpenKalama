package me.matl114.hacks.modules.interact;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.KalamaHelperHelperIX;
import me.matl114.hacks.KalamaHelperHelperV;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.combat.CombatExtra;
import me.matl114.hacks.modules.combat.PositionPredict;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntityTypeRegex;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Configs$LegalTargetingMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.FlagEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.Registries;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;

public class Interact extends BaseModule {
    public final FlagRef renderTarget;
    public final FlagRef interactBlockOnlyWhenHandNotBlock;
    public final FlagRef entityPriority;
    public final FlagRef ignoreBlockPlace;
    public final NBTRef<EntrySet<Block>> DH;
    public final Random DO;
    public static Interact INSTANCE;
    public final NBTRef<EntityTypeRegex> entityWhitelist;
    public final EnumRef<Configs$LegalTargetingMode> entityMode;
    public final NBTRef<OptionalPrimitive<Double>> tpInteractRange;
    public final FlagRef ignoreUseItem;
    public final EnumRef<Configs$LegalInteractMode> blockMode;
    public final NBTRef<EntrySet<Item>> DE;
    public final FlagRef swingHand;
    public final FlagRef ae;
    public final FlagRef onlyInteractInteractableEntity;
    public final NBTRef<WrapColor> renderTargetColor;
    public final FlagRef onlyInteractInteractableBlock;
    public final FlagRef enableEntity;
    public final FlagRef enableBlock;
    HitResult DN;
    public final ModulePath aD = makePath(Configs.n, "interact-arua.interact");
    public final KeyBindRef J;

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::onPreTick);
        this.registerListener(RenderListener.q(), this::onRender3D);
        this.registerListener(Listener.bn(), this::onInteract);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
    }

    private boolean processDelayMovementInteract(Entity target) {
        double var2 = CombatExtra.INSTANCE.getAttackAtTargetRange(target);
        boolean var4 =
                RaycastUtils.x(mc.player, PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, target, var2);
        if (var4) {
            InteractionTasks.c(mc.player, target, Hand.MAIN_HAND, this.swingHand.get());
            return true;
        } else {
            ClientPlayerAccess.of(mc.player).getLegalMovementManager().i(new InteractSubHelperLX(this, target, var2));
            return true;
        }
    }

    @Modifiable
    public boolean placeBlock(BlockPos pos) {
        FlagEntry var2 = InteractionTasks.l(
                pos, !this.blockMode.get().isLegal(), !this.blockMode.get().isLegal());
        return InteractUtils.C(mc.player, var2) ? this.interactBlock((BlockHitResult) var2.val()) : false;
    }

    private boolean processExactInteract(
            PlayerEntity player,
            Entity target,
            Deque<MovTasks$MovInfo> movementStack,
            Deque<MovTasks$MovInfo> shouldMoveBackStack,
            boolean vanillaSuccess,
            boolean useTp) {
        PositionPredict var7 = CombatTasks.m();
        if (vanillaSuccess && !var7.considerAntiShield(target)) {
            return true;
        } else if (!useTp) {
            return false;
        } else {
            double var8 = CombatExtra.INSTANCE.getAttackAtTargetRange(player)
                    + this.tpInteractRange.get().getValue();
            Vec3d var10 = player.getPos();
            Vec3d var11 = var7.getExactAttackPosition(target);
            if (var11 != null) {
                List var12 = MovTasks.z(var10, var11, false, 1.5 * var8, true);
                List var13 = MovTasks.z(var11, var10, false, 1.5 * var8, true);
                if ((var12.size() == 2 || var12.size() == 4) && (var13.size() == 2 || var13.size() == 4)) {
                    if (var12.size() == 2) {
                        movementStack.addLast(MovTasks$MovInfo.adA((Vec3d) var12.get(1)));
                    } else {
                        movementStack.addLast(MovTasks$MovInfo.adA((Vec3d) var12.get(1)));
                        movementStack.addLast(MovTasks$MovInfo.adA((Vec3d) var12.get(2)));
                        movementStack.addLast(MovTasks$MovInfo.adA((Vec3d) var12.get(3)));
                    }

                    int var14 = var13.size();

                    for (int var15 = var14 - 2; var15 >= 0; var15--) {
                        shouldMoveBackStack.addFirst(MovTasks$MovInfo.adz((Vec3d) var13.get(var15)));
                    }

                    return true;
                }
            }

            return vanillaSuccess;
        }
    }

    public boolean canInteract(BlockPos bp, double range) {
        BlockState var4 = mc.world.getBlockState(bp);
        if (!var4.isAir() && !var4.isLiquid() && this.DH.get().test(var4.getBlock())) {
            if (!InteractExtra.INSTANCE.fE(mc.player.getPos(), bp, range)) {
                return false;
            } else {
                return this.onlyInteractInteractableBlock.get()
                                && !InteractUtils.t(
                                        mc.world, mc.player, bp, var4, mc.player.getStackInHand(Hand.MAIN_HAND))
                        ? false
                        : !this.interactBlockOnlyWhenHandNotBlock.get()
                                || !(mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BlockItem);
            }
        } else {
            return false;
        }
    }

    @Modifiable
    public boolean interactBlock(BlockHitResult hitResult) {
        double var2 = InteractExtra.INSTANCE.getBlockReachDistance();
        boolean var4 = InteractExtra.INSTANCE.fE(mc.player.getPos(), hitResult.getBlockPos(), var2);
        boolean var5 = RaycastUtils.canRaycastHit(
                mc.player,
                PlayerStateManager.INSTANCE.jl,
                PlayerStateManager.INSTANCE.jm,
                hitResult.getBlockPos(),
                var2);
        if (var5) {
            InteractionTasks.b(Hand.MAIN_HAND, hitResult, this.swingHand.get());
            return true;
        } else {
            switch ((Configs$LegalInteractMode) this.blockMode.get()) {
                case NONE:
                    if (this.canUseTp() && !var4) {
                        return TpInteract.INSTANCE.tpAndInteractBlock(hitResult, Hand.MAIN_HAND, this.swingHand.get());
                    }

                    InteractionTasks.b(Hand.MAIN_HAND, hitResult, this.swingHand.get());
                    return true;
                default:
                    if (var4) {
                        InteractionTasks.g(this.blockMode.get(), hitResult, Hand.MAIN_HAND, this.swingHand.get());
                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    private boolean processLegalInteract(Entity target) {
        ClientPlayerEntity var2 = mc.player;
        if (var2 == null) {
            return false;
        } else {
            return switch ((Configs$LegalTargetingMode) this.entityMode.get()) {
                case DELAY_MOVEMENT -> this.processDelayMovementInteract(target);
                case LEGACY_SLIENT_ROT -> this.processLegacySnapInteract(target);
                case NONE -> {
                    InteractionTasks.c(mc.player, target, Hand.MAIN_HAND, this.swingHand.get());
                    yield true;
                }
            };
        }
    }

    @Modifiable
    public boolean Sr(BlockPos pos) {
        return this.interactBlock(RaycastUtils.g(pos, mc.player.getEyePos()));
    }

    private boolean processLegacySnapInteract(Entity target) {
        double var2 = CombatTasks.j().getAttackAtTargetRange(target);
        boolean var4 =
                RaycastUtils.x(mc.player, PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, target, var2);
        if (var4) {
            InteractionTasks.c(mc.player, target, Hand.MAIN_HAND, this.swingHand.get());
            return true;
        } else {
            Vec3d var5 = mc.player.getPos();
            Vec3d var6 = TargetSelector.INSTANCE.akn(var5, target.getBoundingBox());
            boolean var7 = TargetSelector.INSTANCE.akm(var5, mc.player.getBoundingBox(), var2);
            if (this.tpInteractRange.get().positive() && !var7) {
                Vec3d var8 = MovTasks.tpAttackSearch(var5, target.getBoundingBox(), var2, 9.9, 1).stream()
                        .findFirst()
                        .orElse(null);
                if (var8 != null && var8.squaredDistanceTo(var5) > 1.0E-7) {
                    mc.player.setPosition(var8.add(0.0, 9.0E-8, 0.0));
                    var6 = TargetSelector.INSTANCE.akn(var5, target.getBoundingBox());
                }

                if (!TargetSelector.INSTANCE.akm(var5, mc.player.getBoundingBox(), var2)) {
                    var7 = false;
                    mc.player.setPosition(var5);
                }
            }

            if (var7) {
                if (mc.player.isUsingItem()) {
                    mc.player.stopUsingItem();
                    mc.getNetworkHandler()
                            .sendPacket(new PlayerActionC2SPacket(
                                    Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
                }

                Vec3d var14 = target.getEyePos();
                Vec3d var9 = target.getPos();
                double var10 = this.DO.nextDouble(0.8, 1.0);
                Vec3d var12 = var9.add(var14.subtract(var9).multiply(var10));
                var12.add(
                        this.DO.nextDouble(-0.05, 0.05),
                        this.DO.nextDouble(-0.05, 0.05),
                        this.DO.nextDouble(-0.05, 0.05));
                Vec3d var13 = var12.subtract(var6).normalize();
                LegacySnapRotManager.INSTANCE.ahs(var13, false);
                InteractionTasks.c(mc.player, target, Hand.MAIN_HAND, this.swingHand.get());
            }

            return true;
        }
    }

    public Interact() {
        super("Interact");
        this.ae = this.flagBuilder(this.aD.addEnable()).build();
        this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable())
                .build();
        this.enableEntity = this.builder(this.aD.add("enable-entity"), Boolean.class)
                .defaultValue(true)
                .build();
        this.enableBlock = this.builder(this.aD.add("enable-block"), Boolean.class)
                .defaultValue(false)
                .build();
        this.entityMode = this.builder(this.aD.add("entity-mode"), Configs$LegalTargetingMode.class)
                .defaultValue(Configs$LegalTargetingMode.NONE)
                .build();
        this.blockMode = this.builder(this.aD.add("block-mode"), Configs$LegalInteractMode.class)
                .defaultValue(Configs$LegalInteractMode.NONE)
                .build();
        this.entityWhitelist = this.builder(this.aD.add("entity-whitelist"), EntityTypeRegex.class)
                .defaultValue(new EntityTypeRegex(new Regex("^(villager|chest_minecart)$")))
                .build();
        this.ignoreBlockPlace =
                this.flagBuilder(this.aD.add("ignore-block-place")).build();
        this.ignoreUseItem = this.flagBuilder(this.aD.add("ignore-use-item")).build();
        this.DE = this.builder(this.aD.add("use-item-black-list"), EntrySet.<Item>parameter())
                .defaultValue(new EntrySet<Item>(new Regex("^()$"), Registries.ITEM))
                .build();
        this.entityPriority = this.builder(this.aD.add("entity-priority"), Boolean.class)
                .defaultValue(true)
                .build();
        this.onlyInteractInteractableEntity = this.builder(
                        this.aD.add("only-interact-interactable-entity"), Boolean.class)
                .defaultValue(true)
                .build();
        this.DH = this.builder(this.aD.add("block-whitelist"), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<Block>(new Regex("^(.*chest|shulker.*)$"), Registries.BLOCK))
                .build();
        this.interactBlockOnlyWhenHandNotBlock = this.builder(
                        this.aD.add("interact-block-only-when-hand-not-block"), Boolean.class)
                .defaultValue(true)
                .build();
        this.onlyInteractInteractableBlock = this.builder(
                        this.aD.add("only-interact-interactable-block"), Boolean.class)
                .defaultValue(true)
                .build();
        this.tpInteractRange = this.builder(this.aD.add("tp-interact-range"), OptionalPrimitive.DOUBLE_TYPE)
                .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 10.0))
                .build();
        this.swingHand = this.builder(this.aD.add("swing-hand"), Boolean.class)
                .defaultValue(true)
                .build();
        this.renderTarget = this.flagBuilder(this.aD.add("render-target")).build();
        this.renderTargetColor = this.builder(this.aD.add("render-target-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.RED))
                .build();
        this.DN = null;
        this.DO = new Random();
        this.bindFlag(this.ae);
        INSTANCE = this;
    }

    public void onPreTick(Event<ClientPlayerEntity> event) {
        this.DN = null;
        if (this.ae.get()) {
            HitResult var2 = mc.crosshairTarget;
            if (!this.ignoreUseItem.get()) {
                ItemStack var3 = mc.player.getStackInHand(Hand.MAIN_HAND);
                if (!this.DE.get().test(var3.getItem()) && InteractUtils.z(mc.world, mc.player, var3)) {
                    this.DN = null;
                    return;
                }
            }

            if (var2.getType() == Type.BLOCK) {
                if (!this.ignoreBlockPlace.get()
                        && mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BlockItem var7) {
                    this.DN = var2;
                } else {
                    BlockPos var10 = ((BlockHitResult) var2).getBlockPos();
                    BlockState var5 = mc.world.getBlockState(var10);
                    if (InteractUtils.t(mc.world, mc.player, var10, var5, mc.player.getStackInHand(Hand.MAIN_HAND))) {
                        this.DN = var2;
                    }
                }
            } else if (var2.getType() == Type.ENTITY) {
                Entity var6 = ((EntityHitResult) var2).getEntity();
                if (InteractUtils.isInteractAcceptable(
                        mc.world, mc.player, var6, mc.player.getStackInHand(Hand.MAIN_HAND))) {
                    this.DN = var2;
                }
            }

            if (this.DN != null) {
                return;
            }

            if (this.entityPriority.get()) {
                Entity var8 = this.enableEntity.get() ? this.searchInteractableEntity() : null;
                if (var8 != null) {
                    this.DN = new EntityHitResult(var8);
                } else {
                    BlockPos var11 = this.enableBlock.get() ? this.searchInteractableBlock() : null;
                    if (var11 != null) {
                        this.DN = RaycastUtils.g(var11, mc.player.getEyePos());
                    }
                }
            } else {
                BlockPos var9 = this.enableBlock.get() ? this.searchInteractableBlock() : null;
                if (var9 != null) {
                    this.DN = RaycastUtils.g(var9, mc.player.getEyePos());
                } else {
                    Entity var12 = this.enableEntity.get() ? this.searchInteractableEntity() : null;
                    if (var12 != null) {
                        this.DN = new EntityHitResult(var12);
                    }
                }
            }
        }
    }

    public void onRender3D(Event<MatrixStack> event) {
        if (this.renderTarget.get() && this.DN != null) {
            float var2 = (Float) event.c[0];
            Box var7;
            if (this.DN instanceof BlockHitResult var4 && var4.getType() == Type.BLOCK) {
                BlockPos var13 = var4.getBlockPos();
                BlockState var5 = mc.world.getBlockState(var13);
                VoxelShape var6 = var5.getOutlineShape(mc.world, var13);
                if (var6.isEmpty()) {
                    return;
                }

                var7 = var6.getBoundingBox().offset(var13);
            } else {
                if (!(this.DN instanceof EntityHitResult var8) || var8.getType() != Type.ENTITY) {
                    return;
                }

                var7 = RenderUtils.getLerpedBox(var8.getEntity(), var2);
            }

            RenderUtils.startDrawVirtual((MatrixStack) event.b);

            try {
                float var15 =
                        (float) var7.getCenter().subtract(mc.player.getEyePos()).length();
                float var14 = Math.min(0.6F, 0.2F + var15 * 0.02F);
                RenderUtils.r(
                        (MatrixStack) event.b,
                        var7.getMinPos(),
                        var7.getMaxPos(),
                        ColorUtils.h(this.renderTargetColor.get().color(), var14));
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) event.b);
            }
        }
    }

    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        this.entityMode.set(
                Configs$LegalTargetingMode.getFromPreset((ModulePreset) ((KalamaHelperHelperI) event.b).b()));
        this.blockMode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset) ((KalamaHelperHelperI) event.b).b()));
    }

    public boolean Si(Entity target) {
        return this.entityMode.get().isLegal()
                ? this.processLegalInteract(target)
                : this.processIllegalInteract(target);
    }

    public boolean Se(Entity entity, double range) {
        if (!TargetSelector.INSTANCE.isTargetInRange(entity, range, 0)) {
            return false;
        } else {
            return !entity.isAlive()
                            || entity.isSpectator()
                            || !this.entityWhitelist.get().test(entity.getType())
                    ? false
                    : !this.onlyInteractInteractableEntity.get()
                            || InteractUtils.isInteractAcceptable(
                                    mc.world, mc.player, entity, mc.player.getStackInHand(Hand.MAIN_HAND));
        }
    }

    public BlockPos searchInteractableBlock() {
        double var1 = InteractExtra.INSTANCE.getBlockReachDistance();
        if (this.canUseTp()) {
            var1 += this.tpInteractRange.get().getValue();
        }

        Vec3d var3 = mc.player.getEyePos();
        Vec3d var4 = mc.player.getRotationVector();
        Vec3d var5 = var3.add(var4.normalize().multiply(var1));

        for (BlockPos var7 : RaycastUtils.r(var3, var5)) {
            if (this.canInteract(var7, var1)) {
                return var7;
            }
        }

        BlockPos var10 = mc.player.getSteppingPos().add(0, 1, 0);

        for (Vec3i var8 : InteractExtra.INSTANCE.fw()) {
            BlockPos var9 = var10.add(var8);
            if (this.canInteract(var9, var1)) {
                return var9;
            }
        }

        return null;
    }

    public boolean canUseTp() {
        return this.tpInteractRange.get().test(s -> s > 1.0E-6);
    }

    private boolean processIllegalInteract(Entity target) {
        ClientPlayerEntity var2 = mc.player;
        if (var2 == null) {
            return false;
        } else {
            double var3 = CombatTasks.j().getAttackRange();
            boolean var5 =
                    RaycastUtils.w(mc.player, PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, target);
            ArrayDeque var6 = new ArrayDeque();
            ArrayDeque var7 = new ArrayDeque();
            Vec3d var8 = mc.player.getPos();
            var6.addLast(MovTasks$MovInfo.adB(mc.player.getPos()));
            var7.addFirst(MovTasks$MovInfo.adB(mc.player.getPos()));
            boolean var9 = var5 || TargetSelector.INSTANCE.akm(var2.getPos(), target.getBoundingBox(), var3);
            boolean var10 = this.canUseTp();
            boolean var11 = var10 && !var9;
            boolean var12 = true;
            boolean var13 = false;
            Vec3d var14 = ((MovTasks$MovInfo) var6.peekLast()).vec3d();
            if (var5) {
                var13 = true;
            } else if (target.getBoundingBox().squaredMagnitude(var14.add(0.0, mc.player.getStandingEyeHeight(), 0.0))
                    <= MathUtils.a(CombatTasks.j().getAttackRange())) {
                var13 = true;
            }

            if (var11) {
                if (this.processExactInteract(var2, target, var6, var7, var13, var10)) {
                    var12 = true;
                } else {
                    var12 = false;
                }
            } else {
                var12 = var13;
            }

            if (!var12) {
                return false;
            } else {
                Iterator var15 = var6.iterator();
                Preconditions.checkArgument(var15.hasNext());
                Vec3d var16 = ((MovTasks$MovInfo) var15.next()).vec3d();
                KalamaHelperHelperIX var17 = KalamaHelperHelperIX.create(var16);
                ArrayList var18 = new ArrayList();
                var15.forEachRemaining(var18::add);
                var7.removeFirst();
                int var19 = var18.size();
                var18.addAll(var7);
                List var20 = MovTasks.createMovingPacketsForMovSequence(var17, var18, true, false);

                for (int var21 = 0; var21 < var19; var21++) {
                    ((KalamaHelperHelperV) var20.get(var21)).run();
                }

                InteractionTasks.c(var2, target, Hand.MAIN_HAND, this.swingHand.get());

                for (int var29 = var19; var29 < var20.size(); var29++) {
                    if (!((KalamaHelperHelperV) var20.get(var29)).success) {
                        List var22 = var18.subList(var29, var18.size());
                        Tasks.l(() -> MovTasks.scheduleFarawayMoveInternal(var22, false, var17.mS(), true), 1);
                        break;
                    }

                    ((KalamaHelperHelperV) var20.get(var29)).run();
                }

                if (var10 && (!var7.isEmpty() || !var6.isEmpty())) {
                    mc.player.setPosition(var8);
                    MovTasks.Y();
                }

                List var30 = Streams.concat(new Stream[] {var6.stream(), var7.stream()})
                        .toList();
                int var31 = var30.size();
                if (var31 > 1) {
                    double var23 = -2.1474836E9F;
                    double var25 = 2.147483647E9;

                    for (int var27 = 0; var27 < var31 - 1; var27++) {
                        var23 = Math.max(var23, ((MovTasks$MovInfo) var30.get(var27)).vec3d().y);
                        var25 = Math.min(var25, ((MovTasks$MovInfo) var30.get(var27)).vec3d().y);
                    }

                    if (Math.abs(var23 - var25)
                            > var2.getAttributeValue(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE) - 1.0) {
                        ClientPlayerAccess.of(var2).setForceNoFall(true);
                        var2.setOnGround(false);
                    }
                }

                return true;
            }
        }
    }

    public Entity searchInteractableEntity() {
        double var1 = CombatExtra.INSTANCE.getAttackRange();
        if (this.canUseTp()) {
            var1 += this.tpInteractRange.get().getValue();
        }

        if (mc.crosshairTarget.getType() == Type.ENTITY) {
            Entity var3 = ((EntityHitResult) mc.crosshairTarget).getEntity();
            if (this.Se(var3, var1)) {
                return var3;
            }
        }

        double var4 = var1;
        return TargetSelector.INSTANCE.searchAttack(var1, true, 0, entity -> this.Se(entity, var4));
    }

    public void onInteract(Event<HitResult> hitResult) {
        if (!hitResult.d()) {
            if (this.ae.get()) {
                Hand var2 = hitResult.getArgs(0);
                if (var2 == Hand.MAIN_HAND) {
                    ClientPlayerEntity var3 = mc.player;
                    if (var3 != null && mc.world != null && this.DN != null && this.DN != mc.crosshairTarget) {
                        if (this.DN instanceof EntityHitResult var5
                                && var5.getType() == Type.ENTITY
                                && this.Si(var5.getEntity())) {
                            hitResult.cancel();
                        } else if (this.DN instanceof BlockHitResult var6
                                && var6.getType() == Type.BLOCK
                                && this.interactBlock(var6)) {
                            hitResult.cancel();
                        }
                    }
                }
            }
        }
    }

    public void postInteract(PlayerEntity player, Entity target) {
        InteractionTasks.c(mc.player, target, Hand.MAIN_HAND, this.swingHand.get());
    }

    @Modifiable
    public boolean placeBlockStrict(BlockPos pos, BlockState state) {
        FlagEntry var3 = InteractionTasks.l(
                pos, !this.blockMode.get().isLegal(), !this.blockMode.get().isLegal());
        if (InteractUtils.C(mc.player, var3)) {
            BlockRotate.INSTANCE.OO(pos, state);
            return this.interactBlock((BlockHitResult) var3.val());
        } else {
            return false;
        }
    }
}
