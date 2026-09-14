package me.matl114.versioned.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class LancingUtils {
    public static final Map<Item, KalamaHelperHelperH> SWORD_KINETIC_MAP = new HashMap<>();

    public static float outBack(float t) {
        return (float) Math.sqrt(1.0F - MathHelper.square(t - 1.0F));
    }

    public static float g(float t) {
        if (t < 0.5F) {
            return t == 0.0F ? 0.0F : (float) (Math.pow(2.0, 20.0 * t - 10.0) / 2.0);
        } else {
            return t == 1.0F ? 1.0F : (float) ((2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0);
        }
    }

    public static float m(float t) {
        return 1.0F - l(1.0F - t);
    }

    public static float h(float t) {
        float var1 = 1.70158F;
        float var2 = 2.5949094F;
        if (t < 0.5F) {
            return 4.0F * t * t * (7.189819F * t - 2.5949094F) / 2.0F;
        } else {
            float var3 = 2.0F * t - 2.0F;
            return (var3 * var3 * (3.5949094F * var3 + 2.5949094F) + 2.0F) / 2.0F;
        }
    }

    public static float f(float t) {
        return t * t;
    }

    public static float o(float f) {
        return (float) (-Math.sqrt(1.0F - f * f)) + 1.0F;
    }

    public static float e(float t) {
        float var1 = 1.70158F;
        float var2 = 2.70158F;
        return 1.0F + 2.70158F * l(t - 1.0F) + 1.70158F * MathHelper.square(t - 1.0F);
    }

    private static float a(float f) {
        return 0.4F * (outQuart(lerpSpear(f, 1.0F, 3.0F)) - inOutSine(lerpSpear(f, 3.0F, 10.0F)));
    }

    static float lerpSpear(float f, float g, float h) {
        return MathHelper.clamp(MathHelper.getLerpProgress(f, g, h), 0.0F, 1.0F);
    }

    public static float p(float t) {
        float var1 = (float) Math.PI * 4.0F / 9.0F;
        if (t == 0.0F) {
            return 0.0F;
        } else if (t == 1.0F) {
            return 1.0F;
        } else {
            double var2 = Math.sin((20.0 * t - 11.125) * (float) Math.PI * 4.0F / 9.0F);
            return t < 0.5F
                    ? (float) (-(Math.pow(2.0, 20.0 * t - 10.0) * var2) / 2.0)
                    : (float) (Math.pow(2.0, -20.0 * t + 10.0) * var2 / 2.0 + 1.0);
        }
    }

    public static void positionArmForSpear(
            ModelPart arm, ModelPart head, boolean right, ItemStack itemStack, LivingEntity state) {
        int var5 = right ? 1 : -1;
        arm.yaw = -0.1F * var5 + head.yaw;
        arm.pitch = (float) (-Math.PI / 2) + head.pitch + 0.8F;
        if (state.isFallFlying()) {
            arm.pitch -= 0.9599311F;
        }

        arm.yaw = (float) (Math.PI / 180.0) * Math.clamp((180.0F / (float) Math.PI) * arm.yaw, -60.0F, 60.0F);
        arm.pitch = (float) (Math.PI / 180.0) * Math.clamp((180.0F / (float) Math.PI) * arm.pitch, -120.0F, 30.0F);
        if (!(state.getItemUseTime() <= 0.0F)
                && (!state.isUsingItem() || state.getActiveHand() == (right ? Hand.MAIN_HAND : Hand.OFF_HAND))) {
            KalamaHelperHelperH var6 = SWORD_KINETIC_MAP.get(itemStack.getItem());
            if (var6 != null) {
                KalamaHelperHelperG var7 = KalamaHelperHelperG.createContext(var6, state.getItemUseTime());
                arm.yaw = arm.yaw
                        + -var5 * var7.raiseProgressStart() * (float) (Math.PI / 180.0) * var7.swayProgress() * 1.0F;
                arm.roll = arm.roll
                        + -var5 * var7.raiseProgressMiddle() * (float) (Math.PI / 180.0) * var7.swayProgress() * 0.5F;
                arm.pitch = arm.pitch
                        + (float) (Math.PI / 180.0)
                                * (-40.0F * var7.raiseProgressEnd()
                                        + 30.0F * var7.swayProgress()
                                        + -20.0F * var7.raiseProgressStart()
                                        + 20.0F * var7.raiseProgressMiddle()
                                        + 10.0F * var7.raiseProgressEnd()
                                        + 0.6F * var7.raiseProgressMiddle() * var7.swayProgress());
            }
        }
    }

    public static void applyHeldItemFeatureArm(MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
        KalamaHelperHelperH var4 = SWORD_KINETIC_MAP.get(itemStack.getItem());
        if (var4 != null && f != 0.0F) {
            float var5 = f(lerpSpear(0.0F, 0.05F, 0.2F));
            float var6 = g(lerpSpear(0.0F, 0.4F, 1.0F));
            KalamaHelperHelperG var7 = KalamaHelperHelperG.createContext(var4, f);
            int var8 = arm == Arm.RIGHT ? 1 : -1;
            float var9 = 1.0F - e(1.0F - var7.raiseProgress());
            float var10 = 0.125F;
            float var11 = a(0.0F);
            matrixStack.translate(
                    0.0, -var11 * 0.4, -var4.forwardMovement() * (var9 - var7.raiseProgressEnd()) + var11);
            matrixStack.multiply(
                    RotationAxis.NEGATIVE_X.rotationDegrees(
                            70.0F * (var7.raiseProgress() - var7.raiseProgressEnd()) - 40.0F * (var5 - var6)),
                    0.0F,
                    -0.03125F,
                    0.125F);
            matrixStack.multiply(
                    RotationAxis.POSITIVE_Y.rotationDegrees(
                            var8 * 90 * (var7.raiseProgress() - var7.raiseProgress() + 3.0F * var6 + var5)),
                    0.0F,
                    0.0F,
                    0.125F);
        }
    }

    public static float l(float n) {
        return n * n * n;
    }

    public static float outQuart(float t) {
        return 1.0F - MathHelper.square(MathHelper.square(1.0F - t));
    }

    public static void applySpearKineticTransform(
            Item item, float f, MatrixStack matrixStack, float g, Arm arm, ItemStack itemStack) {
        if (SWORD_KINETIC_MAP.containsKey(item)) {
            KalamaHelperHelperG var6 = KalamaHelperHelperG.IE(item, g);
            int var7 = arm == Arm.RIGHT ? 1 : -1;
            matrixStack.translate(
                    var7
                            * (var6.raiseProgress() * 0.15F
                                    + var6.raiseProgressStart() * -0.05F
                                    + var6.raiseProgress() * -0.1F
                                    + var6.raiseProgressMiddle() * 0.005F),
                    var6.raiseProgress() * -0.075F + var6.swayProgress() * 0.075F + var6.raiseProgressStart() * 0.01F,
                    var6.raiseProgressEnd() * 0.05
                            + var6.raiseProgressStart() * -0.05
                            + var6.raiseProgressMiddle() * 0.005F);
            matrixStack.multiply(
                    RotationAxis.POSITIVE_X.rotationDegrees(-65.0F * h(var6.raiseProgress())
                            - 35.0F * var6.raiseProgressMiddle()
                            + 100.0F * var6.raiseProgressEnd()
                            + -0.5F * var6.raiseProgressStart()),
                    0.0F,
                    0.1F,
                    0.0F);
            matrixStack.multiply(
                    RotationAxis.NEGATIVE_Y.rotationDegrees(var7
                            * (-90.0F * lerpSpear(var6.raiseProgress(), 0.5F, 0.55F)
                                    + 90.0F * var6.raiseProgress()
                                    + 2.0F * var6.raiseProgressMiddle())),
                    var7 * 0.15F,
                    0.0F,
                    0.0F);
            matrixStack.translate(0.0F, -a(f), 0.0F);
        }
    }

    public static float inOutSine(float t) {
        return -(MathHelper.cos((float) Math.PI * t) - 1.0F) / 2.0F;
    }

    static {
        SWORD_KINETIC_MAP.put(
                Items.WOODEN_SWORD,
                new KalamaHelperHelperH(
                        10,
                        15,
                        0.38F,
                        0.7F,
                        Optional.of(KalamaHelperHelperF.Cx(100, 14.0F)),
                        Optional.of(KalamaHelperHelperF.Cx(200, 5.1F)),
                        Optional.of(KalamaHelperHelperF.Cy(300, 4.6F))));
        SWORD_KINETIC_MAP.put(
                Items.STONE_SWORD,
                new KalamaHelperHelperH(
                        10,
                        14,
                        0.38F,
                        0.82F,
                        Optional.of(KalamaHelperHelperF.Cx(90, 10.0F)),
                        Optional.of(KalamaHelperHelperF.Cx(180, 5.1F)),
                        Optional.of(KalamaHelperHelperF.Cy(275, 4.6F))));
        SWORD_KINETIC_MAP.put(
                Items.IRON_SWORD,
                new KalamaHelperHelperH(
                        10,
                        12,
                        0.38F,
                        0.95F,
                        Optional.of(KalamaHelperHelperF.Cx(50, 8.0F)),
                        Optional.of(KalamaHelperHelperF.Cx(135, 5.1F)),
                        Optional.of(KalamaHelperHelperF.Cy(225, 4.6F))));
        SWORD_KINETIC_MAP.put(
                Items.GOLDEN_SWORD,
                new KalamaHelperHelperH(
                        10,
                        14,
                        0.38F,
                        0.7F,
                        Optional.of(KalamaHelperHelperF.Cx(70, 10.0F)),
                        Optional.of(KalamaHelperHelperF.Cx(170, 5.1F)),
                        Optional.of(KalamaHelperHelperF.Cy(275, 4.6F))));
        SWORD_KINETIC_MAP.put(
                Items.DIAMOND_SWORD,
                new KalamaHelperHelperH(
                        10,
                        10,
                        0.38F,
                        1.075F,
                        Optional.of(KalamaHelperHelperF.Cx(60, 7.5F)),
                        Optional.of(KalamaHelperHelperF.Cx(130, 5.1F)),
                        Optional.of(KalamaHelperHelperF.Cy(200, 4.6F))));
        SWORD_KINETIC_MAP.put(
                Items.NETHERITE_SWORD,
                new KalamaHelperHelperH(
                        10,
                        8,
                        0.38F,
                        1.2F,
                        Optional.of(KalamaHelperHelperF.Cx(50, 7.0F)),
                        Optional.of(KalamaHelperHelperF.Cx(110, 5.1F)),
                        Optional.of(KalamaHelperHelperF.Cy(175, 4.6F))));
    }
}
