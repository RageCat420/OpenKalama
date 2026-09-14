package me.matl114.hacks.modules.interact;

import com.google.common.util.concurrent.Runnables;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.accessors.access.HitResultAccess;
import me.matl114.accessors.access.PlayerInteractBlockC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hooks.LitematicaHooks;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$BypassMode;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.NetworkUtils;
import me.matl114.utils.collections.FlagEntry;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.Orientation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockRotate extends BaseModule {
    public final ModulePath Ax = makePath(Configs.n, "block-rotate");
    public final FlagRef enable3;
    public final FlagRef enable;
    final Map<BlockPos, InteractSubHelperO> AG;
    int timer;
    public static BlockRotate INSTANCE;
    public final FlagRef legalLook;
    public final ModulePath Ay = this.Ax.add("temporary-schematic");
    public final FlagRef clientStateTempFix;
    public final EnumRef<Configs$BypassMode> yawDeceiveBypassMode;
    public final EnumRef<Configs$BypassMode> rotateBypassMode;
    public final FlagRef enable2;
    public final ModulePath Az = this.Ax.add("litematica-shit-fix");

    public void onPreSendInteractBlockRotate(Event<PlayerInteractBlockC2SPacket> e) {
        if (!e.d()) {
            if (this.enable.get()
                    && this.enableBlockRotateModify()
                    && e.b instanceof PlayerInteractBlockC2SPacketAccess var3) {
                Vec2f var10 = new Vec2f(PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm);
                InteractSubHelperY var4 = null;
                Vec3d var5 = null;
                if (var3.hasUseContext()) {
                    if (var3.getUseContext().blockPlace()) {
                        PlayerInteractBlockC2SPacketAccess.UseContext var6 = var3.getUseContext();
                        Item var7 = var6.stack().getItem();
                        Event var8 = new Event<>(new InteractSubHelperY(), false, true);
                        Event var9 = new Event(null, false, true);
                        this.handlePlaceCorrectLitematica(var7, (PlayerInteractBlockC2SPacket) e.b, var6, var8, var9);
                        this.handlePlaceCorrectTemperarySchematic(var7, (PlayerInteractBlockC2SPacket) e.b, var6, var8);
                        if (var8.b != null && ((InteractSubHelperY) var8.b).hasDeceive()) {
                            var4 = (InteractSubHelperY) var8.b;
                        }

                        if (var9.b != null) {
                            var5 = (Vec3d) var9.b;
                        }
                    } else if (var3.getUseContext().isAccepted()) {
                        BlockState var11 = var3.getUseContext().oldState();
                        BlockPos var13 = ((PlayerInteractBlockC2SPacket) e.b)
                                .getBlockHitResult()
                                .getBlockPos();
                        BlockState var14 = mc.world.getBlockState(var13);
                        if (var11 != var14) {
                            Event var15 = new Event<>(new InteractSubHelperY(), false, true);
                            this.handleInteractCorrectLitematica(var13, var14, var15);
                            if (var15.b != null && ((InteractSubHelperY) var15.b).hasDeceive()) {
                                var4 = (InteractSubHelperY) var15.b;
                            }
                        }
                    }
                }

                if (var4 != null
                        && ViaFabricPlusHooks.getInstance().getCurrentVersion().b(21, 0)) {
                    if (this.yawDeceiveBypassMode.get() == Configs$BypassMode.BYPASS_GRIM) {
                        Listener.sendPacketNoEvents(new PlayerInteractBlockC2SPacket(
                                Hand.OFF_HAND,
                                ((PlayerInteractBlockC2SPacket) e.b).getBlockHitResult(),
                                ((PlayerInteractBlockC2SPacket) e.b).getSequence() - 1));
                    }

                    mc.getNetworkHandler()
                            .sendPacket(new PlayerInteractItemC2SPacket(
                                    Hand.MAIN_HAND,
                                    ((PlayerInteractBlockC2SPacket) e.b).getSequence(),
                                    var4.c(var10.y),
                                    var4.b(var10.x)));
                    PlayerInteractBlockC2SPacketAccess.of((PlayerInteractBlockC2SPacket) e.b)
                            .setSequence(NetworkUtils.generateNextSequence());
                }

                if (var4 != null
                        && ViaFabricPlusHooks.getInstance().getCurrentVersion().c(20, 8)) {
                    LegacySnapRotManager.INSTANCE.snapAt(var4.b(var10.x), var4.c(var10.y), true);
                }

                if (var5 != null
                        && (this.rotateBypassMode.get().hasAc()
                                || var4 != null
                                        && ViaFabricPlusHooks.getInstance()
                                                .getCurrentVersion()
                                                .c(20, 8))) {
                    if (ViaFabricPlusHooks.isSupportDupRot()) {
                        PlayerMoveC2SPacket var12 =
                                LegacySnapRotManager.INSTANCE.ahu(var5.subtract(mc.player.getEyePos()));
                        PacketManager.a((Packet<?>) e.b, var12);
                    } else {
                        InteractionTasks.d(var5, mc.player.getEyePos(), Runnables.doNothing());
                    }
                }
            }
        }
    }

    public void le(Event<KalamaHelperHelperI<ModulePreset>> e) {
        switch ((ModulePreset) ((KalamaHelperHelperI) e.b).b()) {
            case fg:
            case fh:
                this.yawDeceiveBypassMode.set(Configs$BypassMode.BYPASS_GRIM);
                break;
            default:
                this.yawDeceiveBypassMode.set(Configs$BypassMode.NO_BYPASS);
        }

        switch ((ModulePreset) ((KalamaHelperHelperI) e.b).b()) {
            case fg:
            case fh:
                this.rotateBypassMode.set(Configs$BypassMode.BYPASS_GRIM);
                break;
            default:
                this.rotateBypassMode.set(Configs$BypassMode.NO_BYPASS);
        }
    }

    public void lh(Event<World> eventWorld) {
        this.AG.clear();
    }

    public void addTempStateSchematic(BlockPos pos, BlockState state, int lastingTicks) {
        if (state != null) {
            this.AG.put(pos, new InteractSubHelperO(pos, lastingTicks, state));
        }
    }

    public BlockRotate() {
        super("BlockRotate");
        this.enable = this.builder(this.Ax.add("enable"), Boolean.class)
                .defaultValue(true)
                .build();
        this.yawDeceiveBypassMode = this.builder(this.Ax.add("yaw-deceive-bypass-mode"), Configs$BypassMode.class)
                .defaultValue(Configs$BypassMode.NO_BYPASS)
                .build();
        this.rotateBypassMode = this.builder(this.Ax.add("rotate-bypass-mode"), Configs$BypassMode.class)
                .defaultValue(Configs$BypassMode.NO_BYPASS)
                .build();
        this.enable2 = this.builder(this.Ay.add("enable"), Boolean.class)
                .defaultValue(true)
                .build();
        this.clientStateTempFix = this.builder(this.Ay.add("client-state-temp-fix"), Boolean.class)
                .defaultValue(true)
                .build();
        this.enable3 = this.flagBuilder(this.Az.add("enable")).build();
        this.legalLook = this.flagBuilder(this.Az.add("legal-look")).build();
        this.AG = new ConcurrentHashMap<>();
        this.timer = 0;
        this.bindFlag(this.enable);
        INSTANCE = this;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(
                Listener.ap().getChannel(PlayerInteractBlockC2SPacket.class), this::onPreSendInteractBlockRotate);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
        this.registerListener(Listener.N(), this::lh);
        this.registerListener(Listener.V(), this::hK);
    }

    public BlockHitResult correctEasyPlaceHitResult(BlockHitResult hitResult, BlockState targetState) {
        BlockPos var3 = InteractUtils.getCurrentPlacePos(mc.player, hitResult);
        FlagEntry var4 = InteractionTasks.r(hitResult.getSide().getOpposite(), var3, targetState, false, false);
        return var4 == null ? hitResult : (BlockHitResult) var4.val();
    }

    public void handlePlaceCorrectTemperarySchematic(
            Item item,
            PlayerInteractBlockC2SPacket packet,
            PlayerInteractBlockC2SPacketAccess.UseContext useContext,
            Event<InteractSubHelperY> yawDeceive) {
        if (this.enable2.get()) {
            BlockHitResult var5 = packet.getBlockHitResult();
            BlockPos var6 = useContext.getPlaceBlockPos(packet.getHand(), var5);
            InteractSubHelperO var7 = this.AG.remove(var6);
            if (var7 == null || var7.expire()) {
                return;
            }

            BlockState var8 = mc.world.getBlockState(var6);
            BlockState var9 = var7.c;
            if (var8 == var9) {
                return;
            }

            BlockHitResult var10 = this.handlePlaceCorrect(item, var6, var9, packet, true);
            if (var10 != null) {
                handleYawDeceive(var9, (InteractSubHelperY) yawDeceive.b);
                if (this.clientStateTempFix.get()) {
                    mc.world.setBlockState(var6, var9, 530);
                }
            }
        }
    }

    public boolean enableBlockRotateModify() {
        return this.enable2.get() || this.enable3.get();
    }

    public static void handleYawInteractDeceive(BlockState targetState, InteractSubHelperY deceive) {
        if (targetState.getBlock() instanceof FenceGateBlock var3) {
            Direction var4 = (Direction) targetState.get(FenceGateBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var4);
        }
    }

    public BlockHitResult handlePlaceCorrect(
            Item item,
            BlockPos modifyingBlockPos,
            BlockState targetState,
            PlayerInteractBlockC2SPacket packet,
            boolean forceCorrect) {
        BlockHitResult var6 = packet.getBlockHitResult();
        BlockState var7 = mc.world.getBlockState(modifyingBlockPos);
        if (!targetState.isAir()
                && targetState.getBlock().asItem() == item
                && targetState.getBlock() == var7.getBlock()) {
            if (forceCorrect) {
                BlockHitResult var8 = this.correctEasyPlaceHitResult(var6, targetState);
                PlayerInteractBlockC2SPacketAccess.of(packet).setBlockHitResult(var8);
                var6 = var8;
            }

            return var6;
        } else {
            return null;
        }
    }

    public static void handleYawDeceive(BlockState targetState, InteractSubHelperY deceive) {
        Block var2 = targetState.getBlock();
        if (var2 instanceof WallMountedBlock var3
                && (targetState.get(WallMountedBlock.FACE) == BlockFace.FLOOR
                        || targetState.get(WallMountedBlock.FACE) == BlockFace.CEILING)) {
            Direction var37 = (Direction) targetState.get(WallMountedBlock.FACING);
            deceive.b = EntityUtils.directionToPitchYaw(var37).y;
        } else if (var2 instanceof ObserverBlock var31) {
            Vec2f var36 = EntityUtils.directionToPitchYaw((Direction) targetState.get(ObserverBlock.FACING));
            deceive.a = var36.x;
            deceive.b = var36.y;
        } else if (var2 instanceof PistonBlock var30) {
            Vec2f var35 =
                    EntityUtils.directionToPitchYaw(((Direction) targetState.get(PistonBlock.FACING)).getOpposite());
            deceive.a = var35.x;
            deceive.b = var35.y;
        } else if (var2 instanceof DispenserBlock var29) {
            Vec2f var34 =
                    EntityUtils.directionToPitchYaw(((Direction) targetState.get(DispenserBlock.FACING)).getOpposite());
            deceive.a = var34.x;
            deceive.b = var34.y;
        } else if (var2 instanceof BarrelBlock var28) {
            Vec2f var33 =
                    EntityUtils.directionToPitchYaw(((Direction) targetState.get(BarrelBlock.FACING)).getOpposite());
            deceive.a = var33.x;
            deceive.b = var33.y;
        } else if (var2 instanceof CrafterBlock var27) {
            Orientation var32 = (Orientation) targetState.get(Properties.ORIENTATION);
            Direction var5 = var32.getFacing();
            Direction var6 = var32.getRotation();

            Vec2f var7 =
                    switch (var5) {
                        case DOWN -> EntityUtils.q(
                                Vec3d.of(var6.getVector()).add(0.0, -4.0, 0.0).normalize());
                        case UP -> EntityUtils.q(Vec3d.of(var6.getOpposite().getVector())
                                .add(0.0, 4.0, 0.0)
                                .normalize());
                        default -> EntityUtils.q(
                                Vec3d.of(var5.getOpposite().getVector()).normalize());
                    };
            deceive.a = var7.x;
            deceive.b = var7.y;
        } else if (var2 instanceof AbstractFurnaceBlock) {
            Direction var26 = (Direction) targetState.get(AbstractFurnaceBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var26.getOpposite());
        } else if (var2 instanceof ChiseledBookshelfBlock) {
            Direction var25 = (Direction) targetState.get(HorizontalFacingBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var25.getOpposite());
        } else if (var2 instanceof VaultBlock) {
            Direction var24 = (Direction) targetState.get(VaultBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var24.getOpposite());
        } else if (var2 instanceof LoomBlock) {
            Direction var23 = (Direction) targetState.get(LoomBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var23.getOpposite());
        } else if (var2 instanceof GlazedTerracottaBlock) {
            Direction var22 = (Direction) targetState.get(GlazedTerracottaBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var22.getOpposite());
        } else if (var2 instanceof BeehiveBlock) {
            Direction var21 = (Direction) targetState.get(BeehiveBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var21.getOpposite());
        } else if (var2 instanceof AbstractRedstoneGateBlock) {
            Direction var20 = (Direction) targetState.get(AbstractRedstoneGateBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var20.getOpposite());
        } else if (var2 instanceof StonecutterBlock) {
            Direction var19 = (Direction) targetState.get(StonecutterBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var19.getOpposite());
        } else if (var2 instanceof FenceGateBlock) {
            Direction var18 = (Direction) targetState.get(FenceGateBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var18);
        } else if (var2 instanceof DoorBlock) {
            Direction var17 = (Direction) targetState.get(DoorBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var17);
        } else if (var2 instanceof CampfireBlock) {
            Direction var16 = (Direction) targetState.get(CampfireBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var16);
        } else if (var2 instanceof DecoratedPotBlock) {
            Direction var15 = (Direction) targetState.get(Properties.HORIZONTAL_FACING);
            deceive.b = EntityUtils.rotationToYaw(var15);
        } else if (var2 instanceof StairsBlock) {
            Direction var14 = (Direction) targetState.get(StairsBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var14);
        } else if (var2 instanceof CalibratedSculkSensorBlock) {
            Direction var13 = (Direction) targetState.get(CalibratedSculkSensorBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var13);
        } else if (var2 instanceof EnderChestBlock) {
            Direction var12 = (Direction) targetState.get(EnderChestBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var12.getOpposite());
        } else if (var2 instanceof LecternBlock) {
            Direction var11 = (Direction) targetState.get(LecternBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var11.getOpposite());
        } else if (var2 instanceof TrapdoorBlock) {
            Direction var10 = (Direction) targetState.get(TrapdoorBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var10.getOpposite());
        } else if (var2 instanceof ChestBlock) {
            Direction var9 = (Direction) targetState.get(ChestBlock.FACING);
            deceive.b = EntityUtils.rotationToYaw(var9.getOpposite());
        } else if (var2 instanceof AnvilBlock) {
            Direction var8 = (Direction) targetState.get(AnvilBlock.FACING);
            Direction var4 = var8.rotateYCounterclockwise();
            deceive.b = EntityUtils.rotationToYaw(var4);
        }
    }

    public void hK(Event<ClientPlayerEntity> eventUpdate) {
        if (this.timer++ > 20) {
            this.timer = 0;
            Iterator var2 = this.AG.entrySet().iterator();

            while (var2.hasNext()) {
                Entry var3 = (Entry) var2.next();
                if (((InteractSubHelperO) var3.getValue()).expire()) {
                    var2.remove();
                }
            }
        }
    }

    public void handlePlaceCorrectLitematica(
            Item item,
            PlayerInteractBlockC2SPacket packet,
            PlayerInteractBlockC2SPacketAccess.UseContext useContext,
            Event<InteractSubHelperY> yawDeceive,
            Event<Vec3d> look) {
        if (this.enable3.get() && LitematicaHooks.getInstance().isEnabled()) {
            BlockHitResult var6 = packet.getBlockHitResult();
            World var7 = LitematicaHooks.getInstance().getSchematicWorld();
            BlockPos var8 = useContext.getPlaceBlockPos(packet.getHand(), var6);
            if (!LitematicaHooks.getInstance().isPositionWithinRange(var8)) {
                return;
            }

            BlockState var9 = var7.getBlockState(var8);
            BlockHitResult var10 = this.handlePlaceCorrect(
                    item, var8, var9, packet, useContext.oldState().isAir());
            if (var10 != null) {
                var6 = var10;
                BlockState var11 = mc.world.getBlockState(var8);
                if (var9 != var11) {
                    BlockHitResult var12 =
                            LitematicaHooks.getInstance().getEasyPlaceClickedPosition(var10, var9, var11);
                    if (var12 != null) {
                        HitResultAccess var13 = HitResultAccess.of(var10);
                        var13.setPos(var12.getPos());
                    }
                }

                if (this.legalLook.get()) {
                    look.context(var10.getBlockPos().toCenterPos());
                }

                handleYawDeceive(var9, (InteractSubHelperY) yawDeceive.b);
            }

            RenderTasks.debugBlockHitResult(var6);
        }
    }

    public void OO(BlockPos pos, BlockState state) {
        this.addTempStateSchematic(pos, state, 1);
    }

    public void handleInteractCorrectLitematica(
            BlockPos pos, BlockState newState, Event<InteractSubHelperY> yawDeceive) {
        if (this.enable3.get() && LitematicaHooks.getInstance().isEnabled()) {
            World var4 = LitematicaHooks.getInstance().getSchematicWorld();
            if (!LitematicaHooks.getInstance().isPositionWithinRange(pos)) {
                return;
            }

            BlockState var5 = var4.getBlockState(pos);
            if (var5.getBlock() == newState.getBlock() && var5 != newState) {
                handleYawInteractDeceive(var5, (InteractSubHelperY) yawDeceive.b);
            }
        }
    }
}
