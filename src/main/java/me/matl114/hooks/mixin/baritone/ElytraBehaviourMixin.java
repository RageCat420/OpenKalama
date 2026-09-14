package me.matl114.hooks.mixin.baritone;

import baritone.api.utils.IPlayerController;
import baritone.behavior.InventoryBehavior;
import baritone.process.elytra.ElytraBehavior;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.hacks.modules.survival.BaritoneFix;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin({ElytraBehavior.class})
public class ElytraBehaviourMixin {
    @WrapOperation(
            method = {
                "a(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZ)V",
                "tickUseFireworks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZ)V"
            },
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lbaritone/api/utils/IPlayerController;processRightClick(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")
            },
            require = 0)
    public ActionResult onUseFireworks(
            IPlayerController instance,
            ClientPlayerEntity player,
            World world,
            Hand hand,
            Operation<ActionResult> original) {
        if (BaritoneFix.INSTANCE.enableFireworkSwap.get()) {
            ElytraExtra.INSTANCE.afI();
            return ActionResult.SUCCESS;
        } else {
            return (ActionResult) original.call(new Object[] {instance, player, world, hand});
        }
    }

    @WrapOperation(
            method = {"a(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZ)V"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/behavior/InventoryBehavior;a(ZLjava/util/function/Predicate;)Z")
            },
            require = 0,
            remap = false)
    private boolean onCancelInventorySwap(
            InventoryBehavior instance,
            boolean b,
            Predicate<? super ItemStack> predicate,
            Operation<Boolean> original) {
        return BaritoneFix.INSTANCE.enableFireworkSwap.get()
                ? true
                : (Boolean) original.call(new Object[] {instance, b, predicate});
    }

    @WrapOperation(
            method = {"tickUseFireworks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZ)V"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/behavior/InventoryBehavior;throwaway(ZLjava/util/function/Predicate;)Z")
            },
            require = 0,
            remap = false)
    private boolean onCancelInventorySwap2(
            InventoryBehavior instance,
            boolean b,
            Predicate<? super ItemStack> predicate,
            Operation<Boolean> original) {
        return BaritoneFix.INSTANCE.enableFireworkSwap.get()
                ? true
                : (Boolean) original.call(new Object[] {instance, b, predicate});
    }

    @WrapOperation(
            method = {
                "a(Lbaritone/process/elytra/ElytraBehavior$SolverContext;Lnet/minecraft/util/math/Vec3d;ILit/unimi/dsi/fastutil/floats/FloatArrayList;III)Lbaritone/process/elytra/ElytraBehavior$PitchResult;",
                "Lbaritone/process/elytra/ElytraBehavior;simulate(Lbaritone/process/elytra/ElytraBehavior$SolverContext;Lnet/minecraft/world/phys/Vec3;FIII)Ljava/util/List;"
            },
            at = {
                @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;")
            },
            require = 0)
    private Box fixHitBoxCalculation(Box instance, double x, double y, double z, Operation<Box> original) {
        return BaritoneFix.INSTANCE.fixBaritoneSimulateError.get()
                ? instance.offset(x, y, z)
                : (Box) original.call(new Object[] {instance, x, y, z});
    }

    @Inject(
            method = {"Lbaritone/process/elytra/ElytraBehavior;tick()V"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/elytra/ElytraBehavior;logVerbose(Ljava/lang/String;)V",
                        ordinal = 2)
            },
            require = 0,
            remap = false)
    private void onNoSolution3(CallbackInfo ci) {
        if (BaritoneFix.INSTANCE.fixWhenFailCalculate.get()) {
            BaritoneFix.INSTANCE.logI18N("message.module.baritone-fix.freeze-all", new Object[0]);
            FloatingUtils.INSTANCE.SB(true);
        }
    }

    @Inject(
            method = {"Lbaritone/process/elytra/ElytraBehavior;tick()V"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/elytra/ElytraBehavior;logVerbose(Ljava/lang/String;)V",
                        ordinal = 3)
            },
            require = 0,
            remap = false)
    private void onNoSolution4(CallbackInfo ci) {
        if (BaritoneFix.INSTANCE.fixWhenFailCalculate.get()) {
            BaritoneFix.INSTANCE.logI18N("message.module.baritone-fix.freeze-pitch", new Object[0]);
            FloatingUtils.INSTANCE.SB(true);
        }
    }

    @ModifyExpressionValue(
            method = {"a()V", "Lbaritone/process/elytra/ElytraBehavior;pathTo()V"},
            at = {@At(value = "FIELD", target = "Lbaritone/api/Settings$Setting;value:Ljava/lang/Object;")},
            require = 0,
            remap = false)
    private Object onAutoJumpFix(Object original) {
        return original instanceof Boolean bl && BaritoneFix.INSTANCE.autoJumpFix.get() ? false : original;
    }
}
