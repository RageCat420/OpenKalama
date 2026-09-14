package me.matl114.hacks.modules.mine;

import com.google.common.util.concurrent.Runnables;
import java.util.Objects;
import javax.annotation.Nonnull;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.channels.EventChannel;
import me.matl114.gui.elements.ResetButtonElement;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class PacketMine extends BaseModule {
    public static final EventChannel<ResetButtonElement> Z = new EventChannel<>();
    public final FlagRef onlyMineOncePerClick;
    public final FlagRef simulateRealBreak;
    public ModulePath H = makePath(Configs.g, "mine-oneblock");
    public final DoubleRef mineThreshold;
    public static PacketMine INSTANCE;
    public final FlagRef autoPickaxe;
    public static final EventChannel<MineSubHelperD> Y = new EventChannel<>();
    public final FlagRef groundOnlyWhenNoControl;
    public final FlagRef enableMineWhiteList;
    public final KeyBindRef J;
    public final FlagRef I = this.flagBuilder(this.H.add("enable")).build();
    public final FlagRef swingHand;
    public final FlagRef considerAirBreak;
    public final IntRef multiplePackets;
    public final FlagRef groundDeceive;
    public final NBTRef<EntrySet<Block>> V;
    public final FlagRef autoPickaxeDoubleBreak;
    BlockPos W;
    public Runnable X;

    public boolean canMine(BlockState state, ItemStack tool) {
        if (this.isMineable(state)) {
            if (this.mineThreshold.get() > 0.0) {
                PlayerInteractionAccess var3 = PlayerInteractionAccess.of(mc.interactionManager);
                float var4 = var3.predictCurrentMiningProgressWithTool(tool);
                if (this.groundDeceive.get() && !mc.player.isOnGround()) {
                    var4 *= 5.0F;
                }

                return var4 > Math.min(0.98, this.mineThreshold.get());
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public BlockPos getCurrentMiningPos() {
        return mc.interactionManager == null
                ? null
                : PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.U(), this::cancelPacketMine);
        this.registerListener(Listener.bl(), this::onMineBlockAction);
    }

    public void tickMine() {
        if (mc.interactionManager != null && mc.player != null) {
            if (this.X != null) {
                this.X.run();
                this.X = null;
            }

            BlockPos var1 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos();
            if (var1 == null) {
                return;
            }

            Runnable var2 = null;
            boolean var3 = false;
            float var4 = 0.0F;
            double var5 = new Box(var1).squaredMagnitude(mc.player.getEyePos());
            if (var5 <= MathUtils.a(mc.player.getBlockInteractionRange() + 1.0)) {
                BlockState var7 = mc.world.getBlockState(var1);
                KalamaHelperHelperK var8 = this.getCurrentUsableTool(var7);
                ItemStack var9 = (ItemStack) var8.val();
                if (this.canMine(var7, var9)) {
                    Event var10 = new Event<>(MineSubHelperD.INSTANCE, true, false, var1);
                    Y.catchEvent(var10);
                    if (!var10.d()) {
                        if (this.groundDeceive.get() && !mc.player.isOnGround()) {
                            boolean var11 = true;
                            if (this.groundOnlyWhenNoControl.get()
                                    && !PlayerInputUtils.of(mc.options).rv()) {
                                var11 = false;
                            }

                            if (var11) {
                                mc.getNetworkHandler()
                                        .sendPacket(LegacySnapRotManager.INSTANCE.ahx(
                                                PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, true));
                                FloatingUtils.INSTANCE.SB(true);
                                FloatingUtils.INSTANCE.SC(true);
                            }
                        }

                        Runnable var14 = InvExtra.INSTANCE.swapInventoryIndexToHand(var8.index());
                        var4 = PlayerInteractionAccess.of(mc.interactionManager)
                                .predictCurrentMiningProgressWithTool(var9);
                        if (this.simulateRealBreak.get() && var4 > 0.98F) {
                            mc.interactionManager.breakBlock(var1);
                        }

                        for (int var12 = 0; var12 < this.multiplePackets.get(); var12++) {
                            if (this.swingHand.get()) {
                                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                            }

                            PlayerInteractionAccess.of(mc.interactionManager).sendBreakPacket(var1);
                        }

                        var2 = var14;
                        var3 = true;
                    }
                }
            }

            if (this.autoPickaxeDoubleBreak.get()
                    && PlayerInteractionAccess.of(mc.interactionManager).getCurrentFailBreakPos() != null) {
                this.tickGhostHandDoubleBreak(var2, this.groundDeceive.get());
            } else if (var2 != null) {
                var2.run();
            }

            if (var3) {
                Z.h(ResetButtonElement.RESET_BUTTON, var1, var4);
            } else if (WorldUtils.o(var1)) {
                BlockState var13 = mc.world.getBlockState(var1);
                if (var13.isAir() || var13.isLiquid()) {
                    this.W = var1;
                }
            }
        }
    }

    public PacketMine() {
        super("PacketMine");
        this.J = this.toggleHotkey(this.H.addHotkey(), new MultiKeyBind(), this.H.addEnable())
                .build();
        this.multiplePackets = this.intBuilder(this.H.add("multiple-packets"))
                .defaultValue(1)
                .validator(Configs.e)
                .build();
        this.simulateRealBreak =
                this.flagBuilder(this.H.add("simulate-real-break")).build();
        this.considerAirBreak =
                this.flagBuilder(this.H.add("consider-air-break")).build();
        this.swingHand = this.flagBuilder(this.H.add("swing-hand")).build();
        this.mineThreshold = this.builder(this.H.add("mine-threshold"), DoubleRef.TYPE)
                .defaultValue(0.7)
                .validator(Configs.doubleRange(-1.0E-4F, 1.0001F))
                .build();
        this.autoPickaxe = this.flagBuilder(this.H.add("auto-pickaxe")).build();
        this.autoPickaxeDoubleBreak =
                this.flagBuilder(this.H.add("auto-pickaxe-double-break")).build();
        this.groundDeceive = this.flagBuilder(this.H.add("ground-deceive")).build();
        this.groundOnlyWhenNoControl =
                this.flagBuilder(this.H.add("ground-only-when-no-control")).build();
        this.onlyMineOncePerClick =
                this.flagBuilder(this.H.add("only-mine-once-per-click")).build();
        this.enableMineWhiteList =
                this.flagBuilder(this.H.add("enable-mine-white-list")).build();
        this.V = this.builder(this.H.add("mine-white-list"), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<Block>(new Regex("^()$"), Registries.BLOCK))
                .build();
        this.X = null;
        this.bindFlag(this.I);
        INSTANCE = this;
    }

    public void tickGhostHandDoubleBreak(Runnable currentTickCallback, boolean groundDeceive) {
        if (this.X == null) {
            BlockPos var3 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentFailBreakPos();
            if (var3 == null) {
                if (currentTickCallback != null) {
                    currentTickCallback.run();
                }
            } else {
                BlockState var4 = mc.world.getBlockState(var3);
                KalamaHelperHelperK var5 = this.getCurrentUsableTool(var4);
                ItemStack var6 = (ItemStack) var5.val();
                if (this.canMineFailBreak(var4, var6, groundDeceive)) {
                    Runnable var7 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
                    Runnable var8 = currentTickCallback == null ? Runnables.doNothing() : currentTickCallback;
                    this.X = () -> {
                        var7.run();
                        var8.run();
                    };
                } else if (currentTickCallback != null) {
                    currentTickCallback.run();
                }
            }
        }
    }

    public boolean isMineable(BlockState state) {
        return state.getBlock().getHardness() >= 0.0F
                && !state.isLiquid()
                && (this.considerAirBreak.get() || !state.isAir())
                && (!this.enableMineWhiteList.get() || !this.V.get().test(state.getBlock()));
    }

    public boolean canMineFailBreak(BlockState state, ItemStack tool, boolean groundDeceive) {
        if (this.isMineable(state)) {
            PlayerInteractionAccess var4 = PlayerInteractionAccess.of(mc.interactionManager);
            float var5 = var4.predictFailMiningProgressWithTool(tool, 0);
            if (groundDeceive && !mc.player.isOnGround()) {
                var5 *= 5.0F;
            }

            return var5 > 0.99;
        } else {
            return false;
        }
    }

    public boolean aK() {
        return this.aJ();
    }

    public boolean aJ() {
        return this.getCurrentMiningPos() != null;
    }

    public void cancelPacketMine(Event<ClientPlayerEntity> tickEvent) {
        if (this.X != null) {
            this.X.run();
            this.X = null;
        }

        if (this.isActive()) {
            if (checkNull()) {
                return;
            }

            BlockPos var2 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos();
            if (this.onlyMineOncePerClick.get() && Objects.equals(var2, this.W)) {
                return;
            }

            this.tickMine();
        }
    }

    public void aN(BlockPos pos) {
        BlockPos var2 = this.getCurrentMiningPos();
        if (Objects.equals(var2, pos)) {
            PlayerInteractionAccess.of(mc.interactionManager).resetCurrentMiningPos();
        }
    }

    public boolean aL(BlockPos pos) {
        return Objects.equals(this.getCurrentMiningPos(), pos);
    }

    @Nonnull
    public KalamaHelperHelperK<ItemStack> getCurrentUsableTool(BlockState currentState) {
        if (this.autoPickaxe.get()) {
            BlockState var2;
            if (!currentState.isAir() && !currentState.isLiquid()) {
                var2 = currentState;
            } else {
                var2 = Blocks.OBSIDIAN.getDefaultState();
            }

            KalamaHelperHelperK var3 = InventoryUtils.v(
                    item -> (double) WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(mc.player, var2, item),
                    true,
                    true);
            if (var3 != null) {
                return var3;
            }
        }

        return InventoryUtils.getSelectedItem();
    }

    public static EventChannel<MineSubHelperD> aW() {
        return Y;
    }

    public void onMineBlockAction(Event<HitResult> event) {
        if (event.b != null && ((HitResult) event.b).getType() == Type.BLOCK) {
            this.W = null;
        }
    }

    public static EventChannel<ResetButtonElement> aX() {
        return Z;
    }
}
