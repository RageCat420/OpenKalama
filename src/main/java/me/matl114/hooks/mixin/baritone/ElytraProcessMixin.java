package me.matl114.hooks.mixin.baritone;

import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.process.ElytraProcess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.matl114.events.Event;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.hacks.modules.survival.BaritoneFix;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.hooks.impl.baritone.BaritoneFuture;
import me.matl114.hooks.impl.baritone.BaritoneLanding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin({ElytraProcess.class})
public class ElytraProcessMixin {
    @Unique
    private BaritoneFuture landingFuture;

    @Inject(
            method = {"a()Z", "shouldLandForSafety()Z"},
            at = {@At("HEAD")},
            require = 0,
            cancellable = true,
            remap = false)
    private void hookShouldLandForSafety(CallbackInfoReturnable<Boolean> ci) {
        if (BaritoneFix.INSTANCE.disableInventoryCheck.get()) {
            ci.setReturnValue(!BaritoneFix.INSTANCE.xd());
        }
    }

    @WrapWithCondition(
            method = {"onTick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/ElytraProcess;logDirect(Ljava/lang/String;)V",
                        ordinal = 4)
            },
            require = 0,
            remap = false)
    private boolean hookLogDirect(ElytraProcess instance, String string) {
        return !BaritoneFix.INSTANCE.emergencyLandingFix.get();
    }

    @ModifyExpressionValue(
            method = {"a(Lnet/minecraft/util/math/BlockPos;Z)V", "pathTo0(Lnet/minecraft/util/math/BlockPos;Z)V"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/registry/RegistryKey;")
            },
            require = 0)
    private RegistryKey<World> hookGetRegistryKey(RegistryKey<World> original) {
        return BaritoneFix.INSTANCE.dimensionFix.get() && original != World.NETHER ? World.NETHER : original;
    }

    @Inject(
            method = {"onTick"},
            at = {
                @At(
                        value = "FIELD",
                        target = "Lbaritone/api/Settings;elytraAllowEmergencyLand:Lbaritone/api/Settings$Setting;",
                        shift = Shift.BEFORE)
            },
            cancellable = true,
            require = 0,
            remap = false)
    private void hookAllowEmergencyLand(boolean par1, boolean par2, CallbackInfoReturnable<PathingCommand> cir) {
        BaritoneFuture future = new BaritoneFuture();
        Event<BaritoneFuture> event = new Event<>(future, true, false, BaritoneLanding.EMERGENCY);
        BaritoneHooks.getLandingEvent().catchEvent(event);
        if (event.d()) {
            future.onCancel();
            cir.setReturnValue(new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL));
        } else {
            this.landingFuture = future;
        }
    }

    @Inject(
            method = {"onTick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/ElytraProcess;logDirect(Ljava/lang/String;)V",
                        ordinal = 5)
            },
            cancellable = true,
            require = 0,
            remap = false)
    private void hookLogDirect(boolean par1, boolean par2, CallbackInfoReturnable<PathingCommand> cir) {
        BaritoneFuture future = new BaritoneFuture();
        Event<BaritoneFuture> event = new Event<>(future, true, false, BaritoneLanding.PATH_COMPLETE);
        BaritoneHooks.getLandingEvent().catchEvent(event);
        if (event.d()) {
            future.onCancel();
            cir.setReturnValue(new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL));
        } else {
            this.landingFuture = future;
        }
    }

    @WrapWithCondition(
            method = {"onTick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/ElytraProcess;logDirect(Ljava/lang/String;)V",
                        ordinal = 9)
            },
            require = 0,
            remap = false)
    private boolean hookLanding(ElytraProcess instance, String s) {
        if (this.landingFuture != null) {
            this.landingFuture.onComplete();
            this.landingFuture = null;
        }

        return true;
    }

    @Inject(
            method = {"onTick"},
            at = {@At("HEAD")},
            cancellable = true,
            require = 0,
            remap = false)
    private void hookPauseElytraProcess(boolean par1, boolean par2, CallbackInfoReturnable<PathingCommand> cir) {
        if (BaritoneFix.INSTANCE.xj()) {
            cir.setReturnValue(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
        }
    }

    @Inject(
            method = {"onTick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/elytra/ElytraBehavior;a(Ljava/lang/String;)V",
                        ordinal = 2)
            },
            require = 0,
            remap = false)
    private void onNoSolution1(boolean par1, boolean par2, CallbackInfoReturnable<PathingCommand> cir) {
        if (BaritoneFix.INSTANCE.fixWhenFailCalculate.get()) {
            BaritoneFix.INSTANCE.logI18N("message.module.baritone-fix.freeze-all", new Object[0]);
            FloatingUtils.INSTANCE.SB(true);
        }
    }

    @Inject(
            method = {"onTick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lbaritone/process/elytra/ElytraBehavior;a(Ljava/lang/String;)V",
                        ordinal = 3)
            },
            require = 0,
            remap = false)
    private void onNoSolution2(boolean par1, boolean par2, CallbackInfoReturnable<PathingCommand> cir) {
        if (BaritoneFix.INSTANCE.fixWhenFailCalculate.get()) {
            BaritoneFix.INSTANCE.logI18N("message.module.baritone-fix.freeze-pitch", new Object[0]);
            FloatingUtils.INSTANCE.SB(true);
        }
    }

    @WrapOperation(
            method = {"onTick"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z")},
            require = 0)
    private boolean onAutoJumpRewrite(ClientPlayerEntity instance, Operation<Boolean> original) {
        return BaritoneFix.INSTANCE.xi() ? false : (Boolean) original.call(new Object[] {instance});
    }

    @Inject(
            method = {"pathTo(Lnet/minecraft/util/math/BlockPos;)V"},
            at = {@At("RETURN")},
            require = 0)
    private void pathTo(BlockPos par1, CallbackInfo ci) {
        BaritoneHooks.getElytraPathingEvent().broadcast(par1);
    }
}
