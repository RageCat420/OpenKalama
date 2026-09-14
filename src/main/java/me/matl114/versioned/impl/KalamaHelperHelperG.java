package me.matl114.versioned.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
record KalamaHelperHelperG(
        float raiseProgress,
        float raiseProgressStart,
        float raiseProgressMiddle,
        float raiseProgressEnd,
        float swayProgress,
        float lowerProgress,
        float raiseBackProgress,
        float swayIntensity,
        float swayScaleSlow,
        float swayScaleFast) {
    public float swayProgress() {
        return this.swayProgress;
    }

    public float raiseProgressEnd() {
        return this.raiseProgressEnd;
    }

    public float raiseProgressMiddle() {
        return this.raiseProgressMiddle;
    }

    public static KalamaHelperHelperG IE(Item kinetic, float f) {
        KalamaHelperHelperH var2 = LancingUtils.SWORD_KINETIC_MAP.get(kinetic);
        return createContext(var2, f);
    }

    public float swayScaleSlow() {
        return this.swayScaleSlow;
    }

    public float raiseProgress() {
        return this.raiseProgress;
    }

    public static KalamaHelperHelperG createContext(KalamaHelperHelperH kineticWeaponComponent, float f) {
        int var2 = kineticWeaponComponent.delayTicks();
        int var3 = kineticWeaponComponent
                        .knockbackConditions()
                        .map(KalamaHelperHelperF::maxDurationTicks)
                        .orElse(0)
                + var2;
        int var4 = var3 - 20;
        int var5 = kineticWeaponComponent
                        .dismountConditions()
                        .map(KalamaHelperHelperF::maxDurationTicks)
                        .orElse(0)
                + var2;
        int var6 = var5 - 40;
        int var7 = kineticWeaponComponent
                        .dismountConditions()
                        .map(KalamaHelperHelperF::maxDurationTicks)
                        .orElse(0)
                + var2;
        float var8 = LancingUtils.lerpSpear(f, 0.0F, var2);
        float var9 = LancingUtils.lerpSpear(var8, 0.0F, 0.5F);
        float var10 = LancingUtils.lerpSpear(var8, 0.5F, 0.8F);
        float var11 = LancingUtils.lerpSpear(var8, 0.8F, 1.0F);
        float var12 = LancingUtils.lerpSpear(f, var4, var6);
        float var13 = LancingUtils.m(LancingUtils.p(LancingUtils.lerpSpear(f - 20.0F, var6, var5)));
        float var14 = LancingUtils.lerpSpear(f, var7 - 5, var7);
        float var15 = 2.0F * LancingUtils.outBack(var12) - 2.0F * LancingUtils.o(var14);
        float var16 = MathHelper.sin(f * 19.0F * (float) (Math.PI / 180.0)) * var15;
        float var17 = MathHelper.sin(f * 30.0F * (float) (Math.PI / 180.0)) * var15;
        return new KalamaHelperHelperG(var8, var9, var10, var11, var12, var13, var14, var15, var16, var17);
    }

    public float raiseProgressStart() {
        return this.raiseProgressStart;
    }

    public float swayIntensity() {
        return this.swayIntensity;
    }

    public float lowerProgress() {
        return this.lowerProgress;
    }

    public float raiseBackProgress() {
        return this.raiseBackProgress;
    }

    public float swayScaleFast() {
        return this.swayScaleFast;
    }
}
