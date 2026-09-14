package me.matl114.hacks.modules.combat;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.mine.MineExtra;
import me.matl114.hacks.modules.mine.MineSubHelperD;
import me.matl114.hacks.modules.mine.PacketMine;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AutoCity extends BaseModule {
    public static AutoCity INSTANCE;
    public final FlagRef doubleMineFace;
    public final KeyBindRef hotkey;
    BlockPos ei;
    boolean pendingSwitchPos;
    public final FlagRef enable;
    PlayerEntity eh;
    public final ModulePath ea = makePath(Configs.k, "combat-utils");
    public final FlagRef surround;
    public final FlagRef burrowFirst;
    public final FlagRef headTarget;
    public final FlagRef down;
    public final ModulePath eb = this.ea.add("auto-city");

    private void onMine() {
        Box var1 = this.eh.getBoundingBox();
        LinkedHashSet var2 = new LinkedHashSet();
        LinkedHashSet var3 = new LinkedHashSet();
        Vec3d var4 = mc.player.getEyePos();
        double var5 = InteractExtra.INSTANCE.getBlockReachDistance();
        Predicate<net.minecraft.util.math.BlockPos> var7 =
                np -> MathUtils.r(np).squaredMagnitude(var4) <= MathUtils.a(var5);
        var3.addAll(MathUtils.getOccupiedBlockPositions(var1).stream()
                .sorted(Comparator.comparingInt(Vec3i::getY))
                .toList());
        Comparator<net.minecraft.util.math.BlockPos> var8 =
                Comparator.comparingDouble(v -> MathUtils.r(v).squaredMagnitude(var4));
        if (this.headTarget.get()) {
            Box var9 = var1.stretch(0.0, 0.75, 0.0);
            MathUtils.getOccupiedBlockPositions(var9).stream()
                    .filter(var7)
                    .sorted(var8)
                    .forEach(var2::add);
        }

        if (this.down.get()) {
            Box var24 = var1.stretch(0.0, -0.75, 0.0);
            MathUtils.getOccupiedBlockPositions(var24).stream()
                    .filter(var7)
                    .sorted(var8)
                    .forEach(var2::add);
        }

        if (this.surround.get()) {
            Box var10 = var1.expand(0.99, 0.0, 0.0);
            Box var11 = var1.expand(0.0, 0.0, 0.99);
            BlockPos var12 = this.eh.getBlockPos();
            if (PacketMine.INSTANCE.isMineable(mc.world.getBlockState(var12))) {
                var10 = var10.withMaxY(var1.minY + 0.5);
                var11 = var11.withMaxY(var1.minY + 0.5);
            } else if (var1.maxY > var12.getY() + 1
                    && PacketMine.INSTANCE.isMineable(mc.world.getBlockState(var12.up()))) {
                var10 = var10.withMinY(var1.minY + 1.0);
                var11 = var11.withMinY(var1.minY + 1.0);
            }

            HashSet var13 = new HashSet();
            var13.addAll(MathUtils.getOccupiedBlockPositions(var10));
            var13.addAll(MathUtils.getOccupiedBlockPositions(var11));
            var13.stream().filter(var7).sorted(var8).forEach(var2::add);
        }

        var2.removeAll(var3);
        PlayerInteractionAccess var25 = PlayerInteractionAccess.of(mc.interactionManager);
        BlockPos var26 = var25.getCurrentMiningPos();
        BlockPos var27 = var25.getCurrentFailBreakPos();
        boolean var28 = false;
        boolean var14;
        if (this.burrowFirst.get()) {
            if (MovTasks.u(this.eh)) {
                var14 = !var3.contains(var26) && (var27 == null || !var3.contains(var27)) || !var2.contains(var26);
            } else {
                var14 = !var2.contains(var26);
            }
        } else {
            var14 = !var2.contains(var26);
        }

        if (var14) {
            if (MineExtra.INSTANCE.add(1)) {
                this.pendingSwitchPos = false;
                HashSet var15 = new HashSet();
                if (InteractionTasks.J().ae.get()) {
                    var15.addAll(InteractionTasks.J().getTargetingPos());
                }

                var2.removeAll(var15);
                List var16 = var3.stream().toList();
                List var17 = var2.stream().toList();
                BlockPos var18 = null;
                BlockPos var19 = null;
                boolean var20 = var25.isFailBreakEmpty();
                Iterator var21 = var16.iterator();

                label132:
                while (true) {
                    if (!var21.hasNext()) {
                        var21 = var17.iterator();

                        BlockPos var31;
                        BlockState var32;
                        do {
                            if (!var21.hasNext()) {
                                break label132;
                            }

                            var31 = (BlockPos) var21.next();
                            var32 = mc.world.getBlockState(var31);
                        } while (var32.isAir() || var32.isLiquid() || !PacketMine.INSTANCE.isMineable(var32));

                        var18 = var31;
                        break;
                    }

                    BlockPos var22 = (BlockPos) var21.next();
                    BlockState var23 = mc.world.getBlockState(var22);
                    if (!var23.isAir()
                            && !var23.isLiquid()
                            && PacketMine.INSTANCE.isMineable(var23)
                            && var23.getBlock().getBlastResistance() > 600.0F
                            && !Objects.equals(var22, var27)) {
                        if (var19 != null || !var20 || !this.doubleMineFace.get()) {
                            var18 = var22;
                            break;
                        }

                        var19 = var22;
                    }
                }

                if (var19 != null) {
                    if (var18 != null) {
                        if (!Objects.equals(var26, var19)) {
                            var25.sendStartBreakPacket(var19);
                        }

                        var25.sendFailBreakCurrentPos(null);
                    } else {
                        var18 = var19;
                        var19 = null;
                    }
                }

                if (var18 != null) {
                    var28 = true;
                    if (!Objects.equals(var26, var18)) {
                        var25.sendStartBreakPacket(var18);
                        if (Objects.equals(var25.getCurrentMiningPos(), var18)) {
                            var25.sendAbortBreakPacket();
                        }
                    }
                }
            } else {
                this.pendingSwitchPos = true;
            }
        } else {
            this.pendingSwitchPos = false;
            var28 = true;
        }

        if (var28) {
            PacketMine.INSTANCE.tickMine();
        }
    }

    public void iq(Event<Void> event) {
        if (this.enable.get()) {
            this.pendingSwitchPos = false;
            this.refreshTarget();
            if (this.eh != null) {
                this.onMine();
            }
        }
    }

    public AutoCity() {
        super("AutoCity");
        this.enable = this.flagBuilder(this.eb.add("enable")).build();
        this.hotkey = this.toggleHotkey(this.eb.add("hotkey"), new MultiKeyBind(), this.eb.add("enable"))
                .build();
        this.burrowFirst = this.builder(this.eb.add("burrow-first"), Boolean.class)
                .defaultValue(true)
                .build();
        this.headTarget = this.flagBuilder(this.eb.add("head-target")).build();
        this.down = this.flagBuilder(this.eb.add("down")).build();
        this.surround = this.builder(this.eb.add("surround"), Boolean.class)
                .defaultValue(true)
                .build();
        this.doubleMineFace = this.flagBuilder(this.eb.add("double-mine-face")).build();
        INSTANCE = this;
        this.bindFlag(this.enable);
    }

    private void gu(Event<MineSubHelperD> event) {
        if (this.enable.get() && !event.d() && this.pendingSwitchPos) {
            event.cancel();
        }
    }

    public void refreshTarget() {
        double var1 = InteractExtra.INSTANCE.getBlockReachDistance() + 2.0;
        if (!EntityUtils.isEntityValid(this.eh)
                || this.eh.getBoundingBox().squaredMagnitude(mc.player.getEyePos()) > MathUtils.a(var1)) {
            this.eh = null;
            this.ei = null;
        }

        if (this.eh == null
                && CombatTasks.l().akK(var1, true, pl -> pl instanceof PlayerEntity) instanceof PlayerEntity var4
                && var4 != mc.player) {
            this.eh = var4;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bd(), this::iq);
        this.registerListener(PacketMine.aW(), this::gu);
    }
}
