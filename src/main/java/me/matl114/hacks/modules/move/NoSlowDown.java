package me.matl114.hacks.modules.move;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.OptionalInt;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.PlayerInteractEntityC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$BypassMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractAtHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class NoSlowDown extends BaseModule implements HackUtilHelperJ {
    public final ModulePath Gb;
    public final KeyBindRef fakeSneakHotkey;
    BlockPos Gz;
    public final FlagRef blockInKeepY;
    public final EnumRef<Configs$BypassMode> blockInBypass;
    boolean Gu;
    public final FlagRef whenUseItem;
    public final KeyBindRef Gq;
    public final FlagRef fakeSneak;
    boolean GA;
    public boolean Gx;
    public final EnumRef<Configs$BypassMode> fakeSneakMode;
    public final IntRef useItemSwapItemDelay;
    public final ModulePath hm = makePath(Configs.m, "move-speed");
    public final FlagRef whenOnBlock;
    public final EnumRef<NoSlowDown$PacketSneakMode> fakeSneakStatusMode;
    boolean Gs;
    public final FlagRef whenSpecialBlock;
    public static HackUtilHelperD instance;
    int lastNoSlowUseTick;
    boolean Gw;
    boolean Gy;
    public final FlagRef whenWithBlock;
    public final ModulePath Gc;
    public final FlagRef useItemSwapNoSprint;
    public final FlagRef whenSneak;
    Runnable Gt;
    public final FlagRef whenInBlock;
    public final EnumRef<NoSlowDown$UseBypassMode> useItemBypass;
    public final FlagRef blockInMineWhenJump;

    private HackUtilHelperD newMovementInstance() {
        this.UZ();
        return instance;
    }

    public void postSwap(boolean v3) {
        if (this.Gt != null) {
            if (v3) {
                PlayerStateManager.INSTANCE.sendSprintStatus(true);
                ClientPlayerAccess.of(mc.player).setLastSprintFlag(true);
            }

            this.Gt.run();
            this.Gt = null;
        }
    }

    public void onServerSyncSneak(Event<SerializedEntry<?>> event) {
        if (!event.d()) {
            if (this.Gs && event.getArgs(0) instanceof ClientPlayerEntity var3 && var3 == mc.player) {
                SerializedEntry var6 = (SerializedEntry) event.e();
                if (var6.id() == 0) {
                    byte var4 = (Byte) var6.value();
                    boolean var5 = (var4 & 2) != 0;
                    if (!var5) {
                        this.Gs = false;
                        Debug.b("[NoSlow] 伪造的潜行状态被重置了");
                    }
                }
            }
        }
    }

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        if (this.shouldFakeSneakStatus()) {
            if (!this.GA && var2.isSneaking()) {
                PlayerInputUtils.a(var2).rC(false).applyInput(var2);
            }

            if (this.GA) {
                ClientPlayerAccess.of(mc.player).resyncSneak();
            }
        }

        this.Vo(null);
        this.GA = false;
    }

    public boolean Vb() {
        return this.whenSneak.get() && (!this.GA || !this.fakeSneakMode.get().hasAc());
    }

    // $VF: Unable to simplify switch on enum
    // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a
    // copy of the class file (if you have the rights to distribute it!)
    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        ModulePreset var2 = (ModulePreset) ((KalamaHelperHelperI) event.e()).b();
        switch (var2) {
            case fd:
                this.whenSneak.set(true);
                this.whenWithBlock.set(true);
                this.whenOnBlock.set(true);
                this.whenSpecialBlock.set(true);
                break;
            default:
                this.whenSneak.set(false);
                this.whenWithBlock.set(false);
                this.whenOnBlock.set(false);
                this.whenInBlock.set(false);
                this.whenSpecialBlock.set(false);
        }

        switch (var2) {
            case fd:
                this.whenUseItem.set(true);
                this.useItemBypass.set(NoSlowDown$UseBypassMode.NO_BYPASS);
                break;
            case fg:
            case fh:
                this.whenUseItem.set(true);
                this.useItemBypass.set(NoSlowDown$UseBypassMode.BYPASS_GRIM_LAZY_V3);
                break;
            default:
                this.whenUseItem.set(false);
        }

        switch (var2) {
            case fd:
            case fe:
                this.whenInBlock.set(true);
                this.blockInBypass.set(Configs$BypassMode.NO_BYPASS);
                break;
            case fg:
            case fh:
            case fj:
            case fi:
            case ff:
                this.whenInBlock.set(true);
                this.blockInBypass.set(Configs$BypassMode.BYPASS_GRIM);
                break;
            default:
                this.whenInBlock.set(false);
        }
    }

    public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        if (this.shouldFakeSneakStatus() && var2.isSneaking()) {
            if (this.Gz != null) {
                BlockPos var3 = this.Gz;
                Vec3d var4 = var2.getVelocity();
                Vec3d var5 = var2.getPos();
                Vec3d var6 = var5.subtract(0.0, 0.500001F, 0.0);
                BlockPos var7 = BlockPos.ofFloored(var6);
                BlockState var8 = mc.world.getBlockState(var7);
                if (var8.isAir() || !var8.isFullCube(mc.world, var7)) {
                    double var9 = 0.1F;
                    double var11 = var3.getX() - var9;
                    double var13 = var3.getZ() - var9;
                    double var15 = var3.getX() + 1 + var9;
                    double var17 = var3.getZ() + 1 + var9;
                    boolean var19 = var5.x > var11 && var5.x < var15;
                    boolean var20 = var5.z > var13 && var5.z < var17;
                    if (!var19 || !var20) {
                        Vec3d var21 = var3.toCenterPos();
                        boolean var22 = var5.x < var21.x;
                        boolean var23 = var5.z < var21.z;
                        if (!var19 && var22 == var4.x < 0.0 || !var20 && var23 == var4.z < 0.0 || !var19 && !var20) {
                            this.GA = true;
                        }
                    }
                }
            }

            this.Gz = var2.getVelocityAffectingPos();
        }

        if (this.whenUseItem.get()
                && this.useItemBypass.get().isIn(new ConfigEnum[] {NoSlowDown$UseBypassMode.BYPASS_GRIM_LAZY_V3})
                && this.useItemSwapNoSprint.get()
                && this.Gw
                && !mc.player.hasVehicle()
                && PlayerInputUtils.a(var2).ru()
                && this.getActiveItemSpeedMultiplier() < 0.99F) {
            PlayerInputUtils.a(var2).rD(false).applyInput(var2);
        }
    }

    public void UZ() {
        this.Gs = false;
    }

    public void Vl(Event<SerializedEntry<?>> eventEntityDataUpdate) {
        if (this.whenUseItem.get()
                && eventEntityDataUpdate.getArgs(0) == mc.player
                && ((SerializedEntry) eventEntityDataUpdate.b).id() == 8
                && ((SerializedEntry) eventEntityDataUpdate.b).value() instanceof Number var3) {
            byte var5 = var3.byteValue();
            boolean var4 = (var5 & 1) > 0;
            if (var4) {
                Tasks.r(
                        () -> {
                            this.Gw = true;
                            return false;
                        },
                        1,
                        1,
                        2);
            }

            if (!var4) {
                this.lastNoSlowUseTick = 0;
            }
        }
    }

    public void Vp(Event<Packet<?>> event) {
        this.postSwap(false);
    }

    public void onConsume(Event<EntityStatusS2CPacket> eventStatus) {
        if (!checkNull()) {
            if (this.whenUseItem.get()
                    && ((EntityStatusS2CPacket) eventStatus.b).getStatus() == 9
                    && ((EntityStatusS2CPacket) eventStatus.b).getEntity(mc.world) == mc.player) {
                this.Gw = false;
                this.lastNoSlowUseTick = 0;
            }
        }
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        if (this.Vb()) {
            PlayerInputUtils.a(var2).rC(mc.options.sneakKey.isPressed()).applyInput(var2);
        }

        this.Gx = false;
        if (this.whenUseItem.get() && mc.player.isUsingItem()) {
            if (this.useItemBypass.get() == NoSlowDown$UseBypassMode.BYPASS_GRIM_50) {
                PlayerInputUtils$Input var3 = PlayerInputUtils.a(var2);
                if (var3.ru()) {
                    if (this.Gy) {
                        this.Gy = false;
                        this.Gx = false;
                    } else {
                        this.Gy = true;
                        this.Gx = true;
                    }
                } else if (this.Gy) {
                    ClientPlayerAccess.of(mc.player).resyncPos();
                    this.Gy = false;
                }
            } else {
                this.Gx = true;
            }
        } else {
            this.Gy = false;
        }
    }

    public void Vj() {
        this.Gu = true;
    }

    public void preSwap(boolean v3) {
        KalamaHelperHelperK var2 = InventoryUtils.findPlayerHotBarItem(ItemStack::isEmpty, true, true);
        int var3;
        if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
            var3 = InventoryUtils.getSelectedSlot();
        } else {
            var3 = 40;
        }

        int var4;
        if (var2 != null) {
            var4 = var2.index();
        } else if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
            var4 = 40;
        } else {
            var4 = InventoryUtils.getSelectedSlot();
        }

        ItemStack var5 = mc.player.getInventory().getStack(var4);
        ScreenHandler var6 = ClientPlayerAccess.of(mc.player).getServerScreenHandler();
        if (v3) {
            PlayerStateManager.INSTANCE.sendSprintStatus(true);
        } else {
            PlayerStateManager.INSTANCE.sendSprintStatus(mc.player.isSprinting());
        }

        this.Gt = null;
        if (!var5.isEmpty()) {
            int var7 = var3;
            ItemStack var8 = mc.player.getInventory().getStack(var3);
            KalamaHelperHelperK var9 = InventoryUtils.findScreenSlot(
                    var6.slots,
                    sl -> {
                        if (sl.getStack().isEmpty() && sl.canInsert(var8)) {
                            return sl.inventory instanceof PlayerInventory ? 1.0 : null;
                        } else {
                            return null;
                        }
                    },
                    true);
            if (var9 != null) {
                mc.interactionManager.clickSlot(var6.syncId, var9.index(), var3, SlotActionType.SWAP, mc.player);
                int var10 = var9.index();
                this.Gt =
                        () -> mc.interactionManager.clickSlot(var6.syncId, var10, var7, SlotActionType.SWAP, mc.player);
            } else if (mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                int var15 = var3 == 8 ? 7 : 8;
                OptionalInt var11 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), var15);
                if (var11.isPresent()) {
                    mc.interactionManager.clickSlot(var6.syncId, var11.getAsInt(), 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(
                            var6.syncId, var11.getAsInt(), var3, SlotActionType.SWAP, mc.player);
                    this.Gt = () -> {
                        mc.interactionManager.clickSlot(
                                var6.syncId, var11.getAsInt(), var3, SlotActionType.SWAP, mc.player);
                        mc.interactionManager.clickSlot(
                                var6.syncId, var11.getAsInt(), 0, SlotActionType.PICKUP, mc.player);
                    };
                }
            } else {
                int var16 = var3 == 8 ? 7 : 8;
                KalamaHelperHelperK var17 = InventoryUtils.x(
                        var6.slots,
                        sl -> sl.getStack().isEmpty()
                                && sl.canInsert(var8)
                                && !(sl.inventory instanceof PlayerInventory),
                        true);
                OptionalInt var12 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), var16);
                if (var17 != null && var12.isPresent()) {
                    mc.interactionManager.clickSlot(var6.syncId, var17.index(), var16, SlotActionType.SWAP, mc.player);
                    mc.interactionManager.clickSlot(
                            var6.syncId, var12.getAsInt(), var3, SlotActionType.SWAP, mc.player);
                    this.Gt = () -> {
                        mc.interactionManager.clickSlot(
                                var6.syncId, var12.getAsInt(), var3, SlotActionType.SWAP, mc.player);
                        mc.interactionManager.clickSlot(
                                var6.syncId, var17.index(), var16, SlotActionType.SWAP, mc.player);
                    };
                }
            }
        } else {
            int var13 = var4;
            OptionalInt var14 = var6.getSlotIndex(mc.player.getInventory(), var3);
            if (var14.isPresent()) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId, var14.getAsInt(), var13, SlotActionType.SWAP, mc.player);
                ClientPlayerAccess.of(mc.player).resyncPos();
                this.Gt = () -> mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId, var14.getAsInt(), var13, SlotActionType.SWAP, mc.player);
            }
        }

        if (v3) {
            PlayerStateManager.INSTANCE.sendSprintStatus(mc.player.isSprinting());
        }
    }

    public void onSendStartUse(Event<PlayerInteractItemC2SPacket> eventPost) {
        if (this.whenUseItem.get() && mc.player.isUsingItem()) {
            this.Gw = true;
            this.lastNoSlowUseTick = 0;
        }
    }

    public void onSneakStatus() {
        if (mc.player != null) {
            if (this.Gs) {
                this.Gs = false;
                ClientPlayerAccess.of(mc.player).resyncSneak();
                PlayerInputUtils$Input var8 = PlayerInputUtils.a(mc.player);
                PlayerInputUtils$Input var11 = var8.rw();
                var11.rC(true).sendPlayerSneakUpdatePacket();
                var11.rC(false).sendPlayerSneakUpdatePacket();
                var11.applyInput(mc.player);
                Debug.b("[NoSlow] 取消当前伪造潜行状态");
            } else {
                NoSlowDown$PacketSneakMode var1 = this.fakeSneakStatusMode.get();
                if (mc.player.isSneaking()) {
                    PlayerInputUtils$Input var2 = PlayerInputUtils.a(mc.player).rC(false);
                    var2.sendPlayerSneakUpdatePacket();
                    var2.applyInput(mc.player);
                }

                mc.options.sneakKey.setPressed(false);
                switch (var1) {
                    case BAD_PACKET:
                    case INTERACT:
                        boolean var5;
                        Entity var10;
                        if (mc.crosshairTarget instanceof EntityHitResult var4) {
                            var10 = var4.getEntity();
                            var5 = true;
                        } else {
                            ArrayList<Entity> var12 = new ArrayList<>();

                            for (Entity var7 : mc.world.getEntities()) {
                                if (var7 != mc.player) {
                                    var12.add(var7);
                                }
                            }

                            var12.sort(Comparator.comparingDouble(s -> s.squaredDistanceTo(mc.player)));
                            if (!var12.isEmpty()) {
                                var10 = (Entity) var12.get(0);
                                var5 = false;
                            } else {
                                var10 = null;
                                var5 = false;
                            }
                        }

                        if (!var5 && var1 != NoSlowDown$PacketSneakMode.BAD_PACKET) {
                            if (var10 == null
                                    || var10.getBoundingBox().squaredMagnitude(mc.player.getEyePos())
                                            > MathUtils.a(mc.player.getEntityInteractionRange() + 0.5)) {
                                Debug.b("[NoSlow] 当前模式下需要一个实体以交互");
                                return;
                            }

                            Entity var15 = Objects.requireNonNull(var10);
                            ClientPlayerAccess.of(mc.player)
                                    .getLegalMovementManager()
                                    .i(new MoveSubHelperCX(this, var15));
                        } else {
                            int var14 = var10 == null ? mc.player.getId() - 1 : var10.getId();
                            PlayerInputUtils$Input var13 = PlayerInputUtils.a(mc.player);
                            var13.rC(true).sendPlayerSneakUpdatePacket();
                            var13.rC(false).sendPlayerSneakUpdatePacket();
                            mc.interactionManager.sendSequencedPacket(
                                    mc.world,
                                    seq -> new PlayerInteractEntityC2SPacket(
                                            var14, true, new InteractAtHandler(Hand.MAIN_HAND, mc.player.getPos())));
                            this.Gs = true;
                            Debug.b("[NoSlow] 成功伪造状态");
                        }
                        break;
                    case GRIM_FALLFLYING:
                        PlayerInputUtils$Input var9 = PlayerInputUtils.a(mc.player);
                        var9.rC(true).sendPlayerSneakUpdatePacket();
                        var9.rC(false).sendPlayerSneakUpdatePacket();
                        if (!mc.player.isOnGround() && ViaFabricPlusHooks.isSupportEndTick()) {
                            var9.rB(true).rl();
                            var9.applyInput(mc.player);
                        }

                        mc.getNetworkHandler()
                                .sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                        this.Gs = true;
                        Debug.b("[NoSlow] 成功伪造状态");
                }
            }
        }
    }

    public NoSlowDown() {
        super("NoSlowDown");
        this.Gb = this.hm.add("no-slowdown");
        this.Gc = this.hm.add("fake-sneak-status");
        this.whenSneak = this.flagBuilder(this.Gb.add("when-sneak")).build();
        this.whenUseItem = this.flagBuilder(this.Gb.add("when-use-item")).build();
        this.whenWithBlock = this.flagBuilder(this.Gb.add("when-with-block")).build();
        this.whenOnBlock = this.flagBuilder(this.Gb.add("when-on-block")).build();
        this.whenInBlock = this.flagBuilder(this.Gb.add("when-in-block")).build();
        this.whenSpecialBlock =
                this.flagBuilder(this.Gb.add("when-special-block")).build();
        this.fakeSneak = this.flagBuilder(this.Gb.add("fake-sneak")).build();
        this.fakeSneakHotkey = this.toggleHotkey(
                        this.Gb.add("fake-sneak-hotkey"), new MultiKeyBind(), this.Gb.add("fake-sneak"))
                .build();
        this.useItemBypass = this.builder(this.Gb.add("use-item-bypass"), NoSlowDown$UseBypassMode.class)
                .defaultValue(NoSlowDown$UseBypassMode.NO_BYPASS)
                .build();
        this.useItemSwapItemDelay = this.builder(this.Gb.add("use-item-swap-item-delay"), Integer.class)
                .show(() -> this.useItemBypass.get().isIn(new ConfigEnum[] {
                    NoSlowDown$UseBypassMode.BYPASS_GRIM_LAZY, NoSlowDown$UseBypassMode.BYPASS_GRIM_LAZY_V3
                }))
                .defaultValue(1)
                .build();
        this.useItemSwapNoSprint = this.flagBuilder(this.Gb.add("use-item-swap-no-sprint"))
                .show(() ->
                        this.useItemBypass.get().isIn(new ConfigEnum[] {NoSlowDown$UseBypassMode.BYPASS_GRIM_LAZY_V3}))
                .build();
        this.blockInBypass = this.builder(this.Gb.add("block-in-bypass"), Configs$BypassMode.class)
                .defaultValue(Configs$BypassMode.NO_BYPASS)
                .build();
        this.blockInKeepY = this.flagBuilder(this.Gb.add("block-in-keep-y"))
                .show(() -> this.blockInBypass.get().isIn(new ConfigEnum[] {Configs$BypassMode.BYPASS_GRIM}))
                .build();
        this.blockInMineWhenJump = this.flagBuilder(this.Gb.add("block-in-mine-when-jump"))
                .show(() -> this.blockInBypass.get().isIn(new ConfigEnum[] {Configs$BypassMode.BYPASS_GRIM}))
                .build();
        this.fakeSneakMode = this.builder(this.Gb.add("fake-sneak-mode"), Configs$BypassMode.class)
                .defaultValue(Configs$BypassMode.NO_BYPASS)
                .build();
        this.Gq = this.hotkey(this.Gc)
                .defaultValue(new MultiKeyBind())
                .registerHotkey(HotKeyUtils.b(this::onSneakStatus))
                .build();
        this.fakeSneakStatusMode = this.builder(this.Gb.add("fake-sneak-status-mode"), NoSlowDown$PacketSneakMode.class)
                .defaultValue(NoSlowDown$PacketSneakMode.BAD_PACKET)
                .build();
        this.Gs = false;
        this.Gt = null;
        this.lastNoSlowUseTick = 0;
        this.Gw = false;
        this.Gy = false;
        this.GA = false;
        if (instance == null) {
            instance = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(this::newMovementInstance);
        }

        instance.mN(this::cast);
    }

    // $VF: Unable to simplify switch on enum
    // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a
    // copy of the class file (if you have the rights to distribute it!)
    public void UY(Event<Vec3d> slowMovement) {
        if (this.whenInBlock.get()) {
            BlockPos var2 = slowMovement.getArgs(0);
            switch ((Configs$BypassMode) this.blockInBypass.get()) {
                case BYPASS_GRIM:
                    if (this.blockInKeepY.get()) {
                        slowMovement.context(((Vec3d) slowMovement.e()).withAxis(Axis.Y, 1.0));
                    }

                    PlayerInputUtils$Input var3 = PlayerInputUtils.a(mc.player);
                    if (this.blockInMineWhenJump.get()
                            && !mc.player.isFallFlying()
                            && (mc.player.getVelocity().y >= 0.0 || mc.player.isOnGround())
                            && var3.rI()) {
                        mc.interactionManager.sendSequencedPacket(
                                mc.world,
                                seq -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var2, Direction.UP, seq));
                        mc.interactionManager.sendSequencedPacket(
                                mc.world,
                                seq -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var2, Direction.UP, seq));
                        slowMovement.cancel();
                        return;
                    }

                    if (mc.player.isFallFlying()) {
                        return;
                    }

                    if (var3.rt()) {
                        mc.player.setVelocity(EntityUtils.N(mc.player.getVelocity(), 0.64));
                    }

                    return;
                case NO_BYPASS:
                    slowMovement.cancel();
                    return;
            }
        }
    }

    public void onInteractSend(Event<PlayerInteractEntityC2SPacket> interactPacket) {
        if (this.Gs) {
            PlayerInteractEntityC2SPacket var2 = (PlayerInteractEntityC2SPacket) interactPacket.e();
            if (!var2.isPlayerSneaking()) {
                PlayerInteractEntityC2SPacketAccess.of(var2).setPlayerSneaking(true);
            }
        }
    }

    private boolean checkSneakSpeed() {
        return mc.player.getAttributeValue(EntityAttributes.PLAYER_SNEAKING_SPEED) < 0.9F;
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        this.Vp(null);
        this.Gu = false;
        return true;
    }

    public void Vo(Event<Packet<?>> event) {
        if (this.noSlowUseItemGrim()) {
            this.preSwap(false);
        }
    }

    public boolean shouldFakeSneakStatus() {
        return (this.Gs || this.fakeSneak.get() && this.checkSneakSpeed()) && mc.player.isOnGround();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
        this.registerListener(Listener.au().c(EntityType.PLAYER), this::onServerSyncSneak);
        this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::onInteractSend);
        this.registerListener(Listener.ay(), this::UY);
        this.registerListener(Listener.au().c(EntityType.PLAYER), this::Vl);
        this.registerListener(Listener.ar().getChannel(EntityStatusS2CPacket.class), this::onConsume);
        this.registerListener(Listener.ap().getChannel(PlayerInteractItemC2SPacket.class), this::onSendStartUse);
    }

    private float getActiveItemSpeedMultiplier() {
        return 0.2F;
    }

    public boolean noSlowUseItemGrim() {
        if (mc.player.isUsingItem() && this.whenUseItem.get()) {
            return switch ((NoSlowDown$UseBypassMode) this.useItemBypass.get()) {
                case NO_BYPASS, BYPASS_GRIM_50 -> false;
                case BYPASS_GRIM_LAZY -> {
                    if (this.Gu) {
                        yield true;
                    } else {
                        boolean var1;
                        if (mc.player.isFallFlying()) {
                            if (mc.player.isTouchingWater()) {
                                var1 = true;
                            } else {
                                var1 = false;
                            }
                        } else {
                            var1 = true;
                        }

                        if (var1
                                && !mc.player.hasVehicle()
                                && PlayerInputUtils.a(mc.player).ru()
                                && this.getActiveItemSpeedMultiplier() < 0.99F) {
                            if (this.lastNoSlowUseTick >= Tasks.b() - this.useItemSwapItemDelay.get()) {
                                yield false;
                            } else {
                                this.lastNoSlowUseTick = Tasks.b();
                                yield true;
                            }
                        } else {
                            yield false;
                        }
                    }
                }
                case BYPASS_GRIM_LAZY_V3 -> {
                    if (mc.player.hasVehicle()
                            || !PlayerInputUtils.a(mc.player).ru()
                            || !(this.getActiveItemSpeedMultiplier() < 0.99F)) {
                        yield false;
                    } else if (this.Gw) {
                        this.Gw = false;
                        yield true;
                    } else {
                        yield false;
                    }
                }
            };
        } else {
            this.lastNoSlowUseTick = 0;
            return false;
        }
    }
}
