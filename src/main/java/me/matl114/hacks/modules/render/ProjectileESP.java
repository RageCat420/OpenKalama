package me.matl114.hacks.modules.render;

import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.containers.KalamaHelperHelperB;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;

public class ProjectileESP extends BaseModule {
    public final FlagRef renderProjectile;
    public final FlagRef renderFireball;
    public final ModulePath ja;
    public final FlagRef ae;
    public final ModulePath gw = makePath(Configs.i, "detect-entity");
    public final FlagRef calProjectile;
    private static final String flagCalculateProjectile = "kalama:calculate_projectile";
    public final FlagRef calFireball;

    private static void drawArrowTrajectoryWithHitResult(
            MatrixStack stack, List<Vec3d> vec3ds, HitResult result, float tickDelta) {
        if (vec3ds.size() > 3) {
            RenderUtils.j(stack, vec3ds, Color.RED);
            if (!vec3ds.isEmpty()) {
                if (result != null && result.getType() == Type.ENTITY) {
                    Entity var6 = ((EntityHitResult) result).getEntity();
                    Box var5 = RenderUtils.getLerpedBox(var6, tickDelta);
                    RenderUtils.r(stack, var5.getMinPos(), var5.getMaxPos(), ColorUtils.k(Color.GREEN, 0.25F));
                } else {
                    Vec3d var4 = (Vec3d) vec3ds.get(vec3ds.size() - 1);
                    RenderUtils.r(
                            stack, var4.add(RenderTasks.m), var4.add(RenderTasks.o), ColorUtils.k(Color.GREEN, 0.25F));
                }
            }
        }
    }

    public ProjectileESP() {
        super("ProjectileESP");
        this.ja = this.gw.add("calculate-trace");
        this.ae = this.flagBuilder(this.ja).build();
        this.calFireball = this.flagBuilder(this.gw.add("cal-fireball")).build();
        this.calProjectile = this.flagBuilder(this.gw.add("cal-projectile")).build();
        this.renderFireball = this.flagBuilder(this.gw.add("render-fireball")).build();
        this.renderProjectile =
                this.flagBuilder(this.gw.add("render-projectile")).build();
        this.bindFlag(this.ae);
    }

    public static void calArrowTrace(PersistentProjectileEntity arrow) {
        if (mc.player != null) {
            if (arrow.getOwner() == mc.player) {
                return;
            }

            if (mc.player.getPos().squaredDistanceTo(arrow.getPos()) < 0.1) {
                return;
            }

            Vec3d var1 = arrow.getVelocity();
            Vec3d var2 = var1.normalize();
            Vec3d var3 = mc.player.getEyePos();
            Vec3d var4 = var3.subtract(arrow.getPos());
            double var5 = var4.dotProduct(var2);
            if (var5 > 0.0) {
                List<Vec3d> var7 = RenderSubHelperW.of(arrow, 0.0F).predictLine(400);
                Vec3d var8 = null;
                double var9 = 1.44E8;

                for (Vec3d var12 : var7) {
                    double var13 = var12.squaredDistanceTo(var3);
                    if (var13 < var9) {
                        var8 = var12;
                        var9 = var13;
                    }
                }

                if (var8 == null) {
                    return;
                }

                Vector2d var19 = new Vector2d(var8.x, var8.z);
                double var15 = Math.sqrt(var9);
                Vector2d var17 = EntityUtils.getEntityLookXZ(mc.player);
                boolean var18 = var19.dot(var17) > 0.0;
                Debug.chat(
                        "Arrow trace update:",
                        Text.literal("%.2f".formatted(var15)).formatted(Formatting.RED),
                        (var18 ? Text.literal("in front of") : Text.literal("at back of")).formatted(Formatting.GREEN),
                        "you");
            } else {
                Debug.b("Arrow trace update: not towards you");
            }
        }
    }

    private static void drawClassicArrowTrajectory(MatrixStack stack, List<Vec3d> vec3ds, Color clr) {
        if (vec3ds.size() > 3) {
            RenderUtils.j(stack, vec3ds, clr);
            if (!vec3ds.isEmpty()) {
                Vec3d var3 = (Vec3d) vec3ds.get(vec3ds.size() - 1);
                RenderUtils.r(
                        stack, var3.add(RenderTasks.m), var3.add(RenderTasks.o), ColorUtils.k(Color.GREEN, 0.25F));
            }
        }
    }

    public void onRender(Event<MatrixStack> stackE) {
        if (mc.world != null && mc.player != null) {
            if (this.ae.get()) {
                MatrixStack var2 = (MatrixStack) stackE.b;
                boolean var3 = this.renderProjectile.get();
                boolean var4 = this.renderFireball.get();
                float var5 = stackE.<Float>getArgs(0);
                RenderUtils.startDrawVirtual(var2);

                try {
                    for (Entity var7 : mc.world.getEntities()) {
                        if (var7 instanceof ExplosiveProjectileEntity var8 && var4) {
                            RenderUtils.j(var2, predictFireballTrace(var8), Color.RED);
                        }

                        if (var3
                                && var7 instanceof AbstractSkeletonEntity var15
                                && !(var15 instanceof WitherSkeletonEntity)) {
                            renderSkeletonProjectile(var2, var15, var5);
                        } else if (var3 && var7 instanceof PlayerEntity var9) {
                            renderPlayerProjectile(var2, var9, var5);
                        } else if (var3 && var7 instanceof CrossbowUser var10) {
                            renderCrossbowProjectile(var2, var10, var5);
                        } else if (var3 && var7 instanceof PersistentProjectileEntity var11) {
                            renderArrowProjectile(var2, var11, var5);
                        }
                    }
                } finally {
                    RenderUtils.stopDrawVirtual(var2);
                }
            }
        }
    }

    public static void calLineTrace(Vec3d fireballPosition, Vec3d power, EntityType<?> type) {
        if (mc.player != null) {
            power = power.normalize();
            Vec3d var3 = mc.player.getEyePos();
            Vec3d var4 = var3.subtract(fireballPosition);
            double var5 = var4.dotProduct(power);
            if (var5 > 0.0) {
                Vec3d var7 = fireballPosition.add(power.multiply(var5));
                Vec3d var8 = var7.subtract(var3);
                double var9 = var8.length();
                Vector2d var11 = new Vector2d(var8.x, var8.z);
                Vector2d var12 = EntityUtils.getEntityLookXZ(mc.player);
                boolean var13 = var11.dot(var12) > 0.0;
                Debug.chat(
                        type.getName(),
                        "trace:",
                        Text.literal("%.2f".formatted(var9)).formatted(Formatting.RED),
                        (var13 ? Text.literal("in front of") : Text.literal("at back of")).formatted(Formatting.GREEN),
                        "you");
            } else {
                Debug.b("Fireball trace update: not towards you");
            }
        }
    }

    public static ArrayList<Vec3d> predictFireballTrace(ExplosiveProjectileEntity fireball) {
        ArrayList var1 = new ArrayList();
        Vec3d var2 = fireball.getPos();
        float var4 = 0.95F;
        Vec3d var5 = fireball.getVelocity();
        Vec3d var6 = var5.normalize().multiply(fireball.accelerationPower);
        var1.add(var2);

        for (int var7 = 0; var7 < 400; var7++) {
            var2 = var2.add(var5);
            var5 = var5.add(var6).multiply(var4);
            var1.add(var2);
            if (var1.size() > 2) {
                Vec3d var3 = (Vec3d) var1.get(var1.size() - 2);
                if (RaycastUtils.raycastAnySolidBlock(fireball, var3, var2)
                        || RaycastUtils.raycastHitAnyEntityExceptPlayer(fireball, var3, var2)) {
                    break;
                }
            }
        }

        return var1;
    }

    public void onVelocityArrow(Event<Vec3d> arrowEvent) {
        if (this.ae.get() && this.calProjectile.get()) {
            Entity var2 = arrowEvent.getArgs(0);
            if (!(var2 instanceof TridentEntity var3) && var2 instanceof PersistentProjectileEntity var4) {
                Vec3d var5 = (Vec3d) arrowEvent.e();
                if (var5.lengthSquared() > 1.0E-10) {
                    EntityAccess var6 = EntityAccess.of(var4);
                    KalamaHelperHelperB var7 = var6.getMetadata();
                    Integer var8 = var7.b(this, "kalama:calculate_projectile");
                    if (var8 != null) {
                        if (var8 >= 3) {
                            var4.setVelocity(var5);
                            calArrowTrace(var4);
                        }

                        var7.a(this, "kalama:calculate_projectile", var8 + 1);
                    } else {
                        var7.a(this, "kalama:calculate_projectile", 1);
                    }
                }
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aN(), this::mY);
        this.registerListener(Listener.aN(), this::onVelocityArrow);
        this.registerListener(RenderListener.q(), this::onRender);
        this.registerListener(Listener.aT(), this::onEntitySpawn);
    }

    private static void renderPlayerProjectile(MatrixStack stack, PlayerEntity player, float tickDelta) {
        for (Hand var6 : Hand.values()) {
            if (player.getStackInHand(var6).getItem() instanceof RangedWeaponItem var8) {
                Pair var9 = RenderSubHelperW.g(player, var8, var6, tickDelta).predictLineWithHitResult(400);
                drawArrowTrajectoryWithHitResult(
                        stack, (List<Vec3d>) var9.getFirst(), (HitResult) var9.getSecond(), tickDelta);
                return;
            }
        }
    }

    public void onEntitySpawn(Event<Entity> event) {
        if (this.ae.get() && this.calFireball.get() && event.e() instanceof ExplosiveProjectileEntity var3) {
            this.onVelocityFireballCal(var3, var3.getVelocity());
        }
    }

    public void onVelocityFireballCal(ExplosiveProjectileEntity fireball, Vec3d vec) {
        if (vec.lengthSquared() > 1.0E-10) {
            EntityAccess var3 = EntityAccess.of(fireball);
            if (var3.getMetadata().b(this, "kalama:calculate_projectile") == null) {
                var3.getMetadata().a(this, "kalama:calculate_projectile", Boolean.TRUE);
                calLineTrace(fireball.getPos(), vec, fireball.getType());
            }
        }
    }

    private static void renderArrowProjectile(MatrixStack stack, PersistentProjectileEntity arrow, float tickDelta) {
        if (!arrow.isOnGround() && arrow.getVelocity().lengthSquared() > 1.0E-5) {
            drawClassicArrowTrajectory(
                    stack, RenderSubHelperW.of(arrow, tickDelta).predictLine(400), Color.RED);
        }
    }

    private static void renderCrossbowProjectile(MatrixStack stack, CrossbowUser pillagerEntity, float tickDelta) {
        if (pillagerEntity instanceof LivingEntity var3
                && var3.isUsingItem()
                && var3.getActiveItem().getItem() instanceof RangedWeaponItem var5) {
            drawClassicArrowTrajectory(
                    stack, RenderSubHelperW.i(pillagerEntity, tickDelta).predictLine(400), Color.YELLOW);
        }
    }

    public void mY(Event<Vec3d> fireballEvent) {
        if (this.ae.get() && this.calFireball.get()) {
            Entity var2 = fireballEvent.getArgs(0);
            if (var2 instanceof ExplosiveProjectileEntity var3) {
                Vec3d var4 = (Vec3d) fireballEvent.e();
                this.onVelocityFireballCal(var3, var4);
            }
        }
    }

    private static void renderSkeletonProjectile(MatrixStack stack, AbstractSkeletonEntity entity, float tickDelta) {
        if (entity.isUsingItem() && entity.getActiveItem().getItem() instanceof BowItem) {
            drawClassicArrowTrajectory(
                    stack, RenderSubHelperW.d(entity, tickDelta).predictLine(400), Color.YELLOW);
        }
    }
}
