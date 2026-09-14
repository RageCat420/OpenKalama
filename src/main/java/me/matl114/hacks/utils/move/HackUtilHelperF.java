package me.matl114.hacks.utils.move;

import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.ElytraExtra$Al;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class HackUtilHelperF {
    public static boolean b = false;
    public static final MinecraftClient a = MinecraftClient.getInstance();
    public static ElytraExtra$Al c = ElytraExtra$Al.V3;

    public static Vec3d c(Vec3d currentMotion, float pitch, float yaw, double autoRescaleAmount) {
        Vec3d var5 = EntityUtils.pitchYawToRotation(pitch, yaw);
        Vec3d var6 = PlayerStateManager.INSTANCE.js;
        Vec3d var7 = !PlayerStateManager.INSTANCE.jz && !PlayerStateManager.INSTANCE.jy
                ? EntityUtils.calculateGlidingVelocity(a.player, var6, var5, true)
                : EntityUtils.simulateTravelInFluidVelocity(
                        var6, PlayerStateManager.INSTANCE.jz, PlayerStateManager.INSTANCE.jy, true);
        Vec3d var8 = EntityUtils.pitchYawToRotation(PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm);
        double var9 = 0.05;
        Vec3d var11 = var5.normalize();
        Vec3d var12 = var8.normalize();
        double var13 = Math.min(-var9, var11.getX()) + Math.min(-var9, var12.getX());
        double var15 = Math.min(-var9, var11.getY()) + Math.min(-var9, var12.getY());
        double var17 = Math.min(-var9, var11.getZ()) + Math.min(-var9, var12.getZ());
        double var19 = Math.max(var9, var11.getX()) + Math.max(var9, var12.getX());
        double var21 = Math.max(var9, var11.getY()) + Math.max(var9, var12.getY());
        double var23 = Math.max(var9, var11.getZ()) + Math.max(var9, var12.getZ());
        double var25 = Math.min(autoRescaleAmount, currentMotion.length());
        var13 *= var25;
        var19 *= var25;
        var15 *= var25;
        var21 *= var25;
        var17 *= var25;
        var23 *= var25;
        var13 = Math.max(-var25, var13);
        var19 = Math.min(var25, var19);
        var15 = Math.max(-var25, var15);
        var21 = Math.min(var25, var21);
        var17 = Math.max(-var25, var17);
        var23 = Math.min(var25, var23);
        double var29 = Math.min(0.0, var13 - var6.x);
        double var31 = Math.max(0.0, var19 - var6.x);
        double var33 = Math.min(0.0, var15 - var6.y);
        double var35 = Math.max(0.0, var21 - var6.y);
        double var37 = Math.min(0.0, var17 - var6.z);
        double var39 = Math.max(0.0, var23 - var6.z);
        double var41 = 0.0;
        double var43 = var7.x + var29 - var41;
        double var45 = var7.x + var31 + var41;
        double var47 = var7.y + var33;
        double var49 = var7.y + var35;
        double var51 = var7.z + var37 - var41;
        double var53 = var7.z + var39 + var41;
        if (!ViaFabricPlusHooks.isSupportEndTick() && var49 > 1.0E-6) {
            double var55 = var5.length();
            double var57 = var5.horizontalLength();
            if (var57 < 0.04 * var5.y) {
                double var59 = EntityUtils.calculateGlidingVelocity(
                                a.player,
                                currentMotion.multiply(
                                        (var55 + ElytraExtra.INSTANCE.autoRescaleAxisZeroPointThree.get()) / var55),
                                var5,
                                true)
                        .y;
                var49 = Math.max(var49, var59);
            }
        }

        if (ElytraExtra.INSTANCE.speedDropOptimization.get()) {
            Vec3d var61 = ElytraExtra.INSTANCE.agy(currentMotion, var7, var43, var47, var51, var45, var49, var53);
            if (!ElytraExtra.INSTANCE.agz(currentMotion, var7, var61)) {
                b(null);
                return currentMotion;
            } else {
                b(var61);
                return currentMotion;
            }
        } else {
            double var80 = currentMotion.x;
            double var81 = currentMotion.z;
            double var82 = 0.0;
            double var62 = 0.0;
            if (var80 > 0.0) {
                var82 = var80 / var45;
            } else if (var80 < 0.0) {
                var82 = var80 / var43;
            }

            if (var81 > 0.0) {
                var62 = var81 / var53;
            } else if (var81 < 0.0) {
                var62 = var81 / var51;
            }

            double var64 = Math.max(var82, var62);
            if (var64 < 1.0E-6) {
                b(null);
                return currentMotion;
            } else {
                Vec3d var66 = EntityUtils.calculateGlidingVelocity(a.player, currentMotion, var5, true);
                Vec3d var67 = currentMotion.multiply(1.0 / var64);
                if (var67.y > 0.0) {
                    var67 = var67.withAxis(Axis.Y, var49);
                } else if (var67.y < 0.0) {
                    var67 = var67.withAxis(Axis.Y, var47);
                }

                if (var67.lengthSquared() < var66.lengthSquared()) {
                    b(null);
                    return currentMotion;
                } else {
                    b(var67);
                    return currentMotion;
                }
            }
        }
    }

    public static Vec3d d(Vec3d currentMotion, float pitch, float yaw, double autoRescaleAmount) {
        if (currentMotion.lengthSquared() < 1.0E-6) {
            b(null);
            return currentMotion;
        } else if (b) {
            return ElytraExtra.INSTANCE.agC(currentMotion, pitch, yaw);
        } else {
            if (pitch > 0.0F) {
                if (pitch > 60.0F) {
                    return ElytraExtra.INSTANCE.agC(currentMotion, pitch, yaw);
                }

                if (pitch < 6.0F) {
                    return ElytraExtra.INSTANCE.agC(currentMotion, pitch, yaw);
                }
            } else {
                if (pitch < -70.0F) {
                    return ElytraExtra.INSTANCE.agC(currentMotion, pitch, yaw);
                }

                if (pitch > -7.0F) {
                    return ElytraExtra.INSTANCE.agC(currentMotion, pitch, yaw);
                }
            }

            return c(currentMotion, pitch, yaw, autoRescaleAmount);
        }
    }

    public static void a() {
        if (ElytraExtra.INSTANCE.autoRescaleFireworkAl.get().isIn(new ConfigEnum[] {ElytraExtra$Al.V3})) {
            c = ElytraExtra.INSTANCE.autoRescaleFireworkAl.get();
            ElytraExtra.INSTANCE.autoRescaleFireworkAl.set(ElytraExtra$Al.V2);
        } else {
            ElytraExtra.INSTANCE.autoRescaleFireworkAl.set(c);
        }
    }

    public static void b(Vec3d vec3d) {
        ElytraExtra.INSTANCE.agu(vec3d);
    }
}
