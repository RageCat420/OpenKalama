package me.matl114.hacks.modules.combat;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.KalamaHelperHelperBX;
import me.matl114.hacks.KalamaHelperHelperCX;
import me.matl114.hacks.KalamaHelperHelperDX;
import me.matl114.hacks.KalamaHelperHelperMX;
import me.matl114.hacks.KalamaHelperHelperSX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class SpearAttack extends BaseModule implements HackUtilHelperJ {
    public final DoubleRef spearMaxTp;
    int currentWaitBackTick;
    public final KeyBindRef spearAttackHotkey;
    public final ModulePath dI = makePath(Configs.k, "spear-module");
    public final DoubleRef spearMotionSimulation;
    public final FlagRef renderTarget;
    public final FlagRef ae = this.builder(this.dI.add("spear-attack-enable"), Boolean.class)
            .defaultValue(true)
            .build();
    private static HackUtilHelperD INSTANCE;
    DoubleList fc;
    public final IntRef spearServerTickDelay;

    private void renderPlayerSpearTarget(Event<MatrixStack> event) {
        if (this.ae.get()) {
            MatrixStack var2 = (MatrixStack) event.e();
            float var3 = event.<Float>getArgs(0);
            if (this.renderTarget.get()) {
                RenderUtils.startDrawVirtual(var2);

                try {
                    if (this.iZ()) {
                        Entity var4 =
                                CombatTasks.l().akK(this.spearMotionSimulation.get(), false, SpearAttack::isSpearable);
                        if (var4 != null) {
                            float var5 = var4.distanceTo(mc.player);
                            float var6 = Math.min(0.6F, 0.1F + var5 * 0.02F);
                            Box var7 = RenderUtils.getLerpedBox(var4, var3);
                            RenderUtils.r(var2, var7.getMinPos(), var7.getMaxPos(), ColorUtils.k(Color.GREEN, var6));
                        }
                    }
                } finally {
                    RenderUtils.stopDrawVirtual(var2);
                }
            }
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        return true;
    }

    public boolean iY() {
        if (!this.ae.get() || !this.iZ()) {
            return false;
        } else if (this.currentWaitBackTick > 0) {
            return true;
        } else {
            this.currentWaitBackTick = 1;
            return this.spearAttack();
        }
    }

    public Pair<List<Vec3d>, List<Vec3d>> isValidTpLocation(
            KalamaHelperHelperMX context, Vec3d playerLocation, Vec3d tpLocation, double maxDistance) {
        Vec3d var6 = playerLocation.subtract(tpLocation);
        if (MovTasks.validMoveTo(context, tpLocation, var6)) {
            List var7 = List.of(tpLocation, playerLocation);
            List var8 = context.generateTpSequence(playerLocation, tpLocation, false, 320.0, true);
            return var8.isEmpty() ? null : Pair.of(var8, var7);
        } else {
            return null;
        }
    }

    public SpearAttack() {
        super("SpearAttack");
        this.spearAttackHotkey = this.hotkey(this.dI.add("spear-attack-hotkey"))
                .defaultValue(new MultiKeyBind())
                .registerHotkey(HotKeyUtils.c(this::iY))
                .build();
        this.spearMotionSimulation = this.builder(this.dI.add("spear-motion-simulation"), DoubleRef.TYPE)
                .defaultValue(50.0)
                .build();
        this.spearMaxTp = this.builder(this.dI.add("spear-max-tp"), DoubleRef.TYPE)
                .defaultValue(100.0)
                .build();
        this.renderTarget = this.flagBuilder(this.dI.add("render-target")).build();
        this.spearServerTickDelay = this.intBuilder(this.dI.add("spear-server-tick-delay"))
                .defaultValue(2)
                .validator(Configs.e)
                .build();
        this.currentWaitBackTick = 0;
        this.fc = new DoubleArrayList();
        this.fc.add(0.0);

        for (int var1 = 1; var1 < 10; var1++) {
            this.fc.add(var1);
            this.fc.add(-var1);
        }

        if (INSTANCE == null) {
            INSTANCE = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> INSTANCE);
        }

        INSTANCE.mN(this::cast);
        this.bindFlag(this.ae);
    }

    @Override
    public int priority() {
        return -100000;
    }

    @Override
    public void iB(Event<LegalMovementManager> movementManagerEvent) {
        this.applyBeforeMovementPacketModify(movementManagerEvent);
    }

    public static boolean isSpearable(Entity entity) {
        if (mc.player.getEyePos().subtract(entity.getEyePos()).lengthSquared() <= MathUtils.a(2.0)) {
            return false;
        } else {
            BlockHitResult var1 = mc.world.raycast(new RaycastContext(
                    mc.player.getEyePos(), entity.getEyePos(), ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
            return var1.getType() == Type.MISS;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(RenderListener.q(), this::renderPlayerSpearTarget);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
    }

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.currentWaitBackTick > 0) {
            this.currentWaitBackTick--;
            if (this.currentWaitBackTick > this.spearServerTickDelay.get()) {
                this.currentWaitBackTick = this.spearServerTickDelay.get();
            }

            if (this.currentWaitBackTick > 0) {
                movementManagerEvent.cancel();
                ((LegalMovementManager) movementManagerEvent.b).c.a.setOnGround(false);
            }

            if (this.currentWaitBackTick == 1) {
                if (RenderTasks.i) {
                    Debug.b("Delay finish");
                }

                ClientPlayerAccess.of(mc.player).setForceNoFall(true);
                ClientPlayerAccess.of(mc.player).resyncPos();
                ClientPlayerAccess.of(mc.player).resyncRot();
            }
        }
    }

    // $VF: Unable to simplify switch on enum
    // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a
    // copy of the class file (if you have the rights to distribute it!)
    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        ModulePreset var2 = (ModulePreset) ((KalamaHelperHelperI) event.e()).b();
        switch (var2) {
            case fd:
            case fe:
                if (this.spearMotionSimulation.get() < 0.0) {
                    this.spearMotionSimulation.set(-this.spearMotionSimulation.get());
                }
                break;
            default:
                if (this.spearMotionSimulation.get() > 0.0) {
                    this.spearMotionSimulation.set(-this.spearMotionSimulation.get());
                }
        }
    }

    public boolean iZ() {
        return SpearEnhance.canSpearKineticAttack();
    }

    public Pair<List<Vec3d>, List<Vec3d>> findValidTpPosition(
            Vec3d currentPlayerPos,
            Vec3d tpDirection,
            Vec3d expandDirection,
            double maxDistance,
            double distance,
            double minDistance) {
        KalamaHelperHelperMX var10 = MovTasks.h;
        Pair var11 = null;

        for (double var12 = distance; var12 > minDistance; var12 -= 2.0) {
            DoubleListIterator var14 = this.fc.iterator();

            while (var14.hasNext()) {
                Double var15 = (Double) var14.next();
                Vec3d var16 = currentPlayerPos.add(tpDirection.multiply(var12)).add(expandDirection.multiply(var15));
                if (var16.squaredDistanceTo(currentPlayerPos) > MathUtils.a(maxDistance)) {
                    break;
                }

                if (!var10.checkEnvironmentCollision(mc.player, var16, true)
                        && (var11 = this.isValidTpLocation(var10, currentPlayerPos, var16, maxDistance)) != null) {
                    return var11;
                }
            }
        }

        return null;
    }

    public boolean spearAttack() {
        if (this.spearMotionSimulation.get() < 0.0) {
            this.currentWaitBackTick = 0;
            return false;
        } else {
            Entity var1 = CombatTasks.l().akK(this.spearMotionSimulation.get(), true, SpearAttack::isSpearable);
            if (var1 == null) {
                this.currentWaitBackTick = 0;
                return false;
            } else if (this.spearMaxTp.get() < this.spearMotionSimulation.get()) {
                Debug.b("[Spear] 参数错误, MaxTp不能小于Distance");
                this.currentWaitBackTick = 0;
                return true;
            } else {
                Vec3d var2 = mc.player.getPos();
                Vec2f var3 = new Vec2f(mc.player.getPitch(), mc.player.getYaw());
                Vec3d var4 = CombatTasks.m().spearPredictArgument.get().predict(var1);
                Vec3d var5 =
                        var4.add(0.0, var1.getEyeHeight(var1.getPose()), 0.0).subtract(mc.player.getEyePos());
                if (RenderTasks.i) {
                    RenderTasks.i(new KalamaHelperHelperCX(
                            RenderTasks.DEBUG_TICK,
                            new KalamaHelperHelperBX(mc.player.getEyePos(), var5).g(Color.MAGENTA)));
                }

                double var6 = var5.length();
                var5 = var5.normalize();
                Vec3d var8 = var5.multiply(-1.0);
                Vec3d var9 = MathUtils.getVerticalWithSameXZ(var5);
                Pair var10 = this.findValidTpPosition(
                        var2, var8, var9, this.spearMaxTp.get(), this.spearMotionSimulation.get(), var6);
                if (var10 != null && ((List) var10.getSecond()).size() == 2) {
                    List var11 = (List) var10.getFirst();
                    List var12 = (List) var10.getSecond();
                    ArrayList var13 = new ArrayList();
                    Vec2f var14 = EntityUtils.q(var5);

                    for (int var15 = 0; var15 < var11.size() - 1; var15++) {
                        var13.add(new MovTasks$MovInfo((Vec3d) var11.get(var15), false, false, null));
                    }

                    Vec3d var18 = (Vec3d) var11.get(var11.size() - 1);
                    if (RenderTasks.i) {
                        RenderTasks.i(new KalamaHelperHelperCX(
                                RenderTasks.DEBUG_TICK,
                                new KalamaHelperHelperDX(
                                        var18.add(RenderTasks.l), var18.add(RenderTasks.n), Color.MAGENTA)));
                        RenderTasks.i(new KalamaHelperHelperCX(
                                RenderTasks.DEBUG_TICK, new KalamaHelperHelperSX(var18, Color.MAGENTA)));
                    }

                    var13.add(new MovTasks$MovInfo(var18, false, false, var14));
                    Vec3d var16 = (Vec3d) ((List) var10.getSecond()).get(1);
                    var13.add(new MovTasks$MovInfo(var16, false, false, var14));
                    MovTasks.p(MovTasks.createPlayerMovContext(), var13, false, true);
                    ClientPlayerAccess.of(mc.player).setForceNoFall(false);
                    Debug.b(Text.literal("[Spear] simulate delay %.2f"
                                    .formatted(var2.subtract(var18).dotProduct(var5)))
                            .formatted(Formatting.GREEN));
                    this.currentWaitBackTick = this.spearServerTickDelay.get() + 1;
                    mc.player.setPitch(var3.x);
                    mc.player.setYaw(var3.y);
                    MovTasks.Y();
                    return true;
                } else {
                    Debug.b("[Spear] Too far to reach target");
                    this.currentWaitBackTick = 0;
                    return true;
                }
            }
        }
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {}
}
