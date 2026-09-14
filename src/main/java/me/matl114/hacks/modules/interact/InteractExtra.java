package me.matl114.hacks.modules.interact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.utils.MathUtils;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector2i;

public class InteractExtra extends BaseModule {
    public List<Vector2i> bT;
    public final IntRef coolDownRewrite;
    public final DoubleRef reachDistance;
    private final double[] cc;
    public final FlagRef holdUse;
    public List<Vec3i> bS;
    public final FlagRef allowRideInteract;
    public final ModulePath bR = makePath(Configs.n, "interact-fix");
    public final FlagRef noCoolDown;
    public final IntRef holdUseStartTick;
    private final double[] cd;
    public double lastRange;
    public final FlagRef useGrimExpandEyeHeight;
    public static InteractExtra INSTANCE;

    public Stream<Vec3d> fB(Vec3d playerPos) {
        return this.getPotentialEyeHeights().mapToObj(s -> playerPos.add(0.0, s, 0.0));
    }

    public boolean isWithinInteractRange(Vec3d pos, Box box, double range) {
        return box.squaredMagnitude(pos) > MathUtils.a(range + 2.0 + mc.player.dimensions.eyeHeight())
                ? false
                : this.getPotentialEyeHeights()
                        .mapToObj(s -> pos.add(0.0, s, 0.0))
                        .anyMatch(ps -> box.squaredMagnitude(ps) < MathUtils.a(range));
    }

    public Vec3d fG(Vec3d pos, BlockHitResult blockHitResult) {
        BlockPos var3 = blockHitResult.getBlockPos();
        Direction var4 = blockHitResult.getSide();
        Vec3d var5 = var3.toCenterPos().offset(var4, 0.5);
        Vec3d var6 = Vec3d.of(var4.getVector());
        Box var7 = new Box(var3);
        return this.fB(pos)
                .filter(s -> var7.contains(s) ? true : s.subtract(var5).dotProduct(var6) > 0.0)
                .findFirst()
                .orElseGet(() -> pos.add(mc.player.getEyePos().subtract(mc.player.getPos())));
    }

    public void refreshInteractionRange(double val) {
        if (mc.player != null && this.lastRange != val) {
            this.lastRange = val;
            ArrayList var3 = new ArrayList();
            int var4 = (int) this.lastRange;

            for (int var5 = -var4; var5 <= var4; var5++) {
                for (int var6 = -var4; var6 <= var4; var6++) {
                    for (int var7 = -var4; var7 <= var4; var7++) {
                        var3.add(new Vec3i(var5, var6, var7));
                    }
                }
            }

            var3.sort(Comparator.comparingDouble(
                    (Vec3d v) -> v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ()));
            this.bS = var3;
            ArrayList var8 = new ArrayList();

            for (int var9 = -var4; var9 <= var4; var9++) {
                for (int var10 = -var4; var10 <= var4; var10++) {
                    var8.add(new Vector2i(var9, var10));
                }
            }

            var8.sort(Comparator.comparingDouble((org.joml.Vector2i v) -> v.x * v.x + v.y * v.y));
            this.bT = var8;
        }
    }

    public List<Vector2i> getBlocksAround() {
        if (mc.player != null) {
            this.refreshInteractionRange(INSTANCE.getBlockReachDistance());
        }

        return Collections.unmodifiableList(this.bT);
    }

    public boolean fD(Vec3d pos, Box bp) {
        return this.isWithinInteractRange(pos, bp, this.getBlockReachDistance());
    }

    public boolean fE(Vec3d pos, BlockPos bp, double range) {
        return this.isWithinInteractRange(pos, new Box(bp), range);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aB(), this::onCooldown);
    }

    public List<Vec3i> fw() {
        if (mc.player != null) {
            this.refreshInteractionRange(INSTANCE.getBlockReachDistance());
        }

        return Collections.unmodifiableList(this.bS);
    }

    public void onCooldown(Event<Integer> event) {
        if (this.noCoolDown.get() && this.coolDownRewrite.get() >= 0) {
            event.context(this.coolDownRewrite.get());
        }
    }

    public boolean fC(Vec3d pos, BlockPos bp) {
        return this.fE(pos, bp, this.getBlockReachDistance());
    }

    public InteractExtra() {
        super("InteractExtra");
        this.bS = new ArrayList<>();
        this.bT = new ArrayList<>();
        this.useGrimExpandEyeHeight =
                this.flagBuilder(this.bR.add("use-grim-expand-eye-height")).build();
        this.reachDistance = this.builder(this.bR.add("reach-distance"), Double.class)
                .defaultValue(0.0)
                .build();
        this.noCoolDown = this.flagBuilder(this.bR.add("no-cool-down")).build();
        this.coolDownRewrite = this.intBuilder(this.bR.add("cool-down-rewrite"))
                .defaultValue(4)
                .build();
        this.allowRideInteract = this.builder(this.bR.add("allow-ride-interact"), FlagRef.TYPE)
                .defaultValue(true)
                .build();
        this.holdUse = this.builder(this.bR.add("hold-use"), FlagRef.TYPE)
                .defaultValue(false)
                .build();
        this.holdUseStartTick = this.intBuilder(this.bR.add("hold-use-start-tick"))
                .defaultValue(4)
                .build();
        this.cc = new double[] {0.4, 1.62, 1.27};
        this.cd = new double[] {1.62, 1.27, 0.4};
        INSTANCE = this;
    }

    public DoubleStream getPotentialEyeHeights() {
        if (this.useGrimExpandEyeHeight.get()) {
            double var1 = mc.player.getScale();
            return !mc.player.isFallFlying() && !mc.player.isUsingRiptide() && !mc.player.isSwimming()
                    ? DoubleStream.concat(
                            Arrays.stream(this.cd).map(s -> s * var1),
                            DoubleStream.of(mc.player.dimensions.eyeHeight()))
                    : DoubleStream.concat(
                            Arrays.stream(this.cc).map(s -> s * var1),
                            DoubleStream.of(mc.player.dimensions.eyeHeight()));
        } else {
            return DoubleStream.of(mc.player.dimensions.eyeHeight());
        }
    }

    public double getBlockReachDistance() {
        return mc.player.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) + this.reachDistance.get();
    }
}
