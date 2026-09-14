package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.hacks.ExtraTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.MoveTimer;
import me.matl114.hacks.modules.move.Sprint;
import me.matl114.hacks.modules.render.NoRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerMixin extends AbstractClientPlayerEntity implements ClientPlayerAccess {
    @Shadow
    private double field_3926;

    @Shadow
    private double field_3940;

    @Shadow
    private double field_3924;

    @Unique
    private boolean forceNoFall;

    @Shadow
    @Final
    protected MinecraftClient field_3937;

    @Shadow
    private float field_3941;

    @Shadow
    private float field_3925;

    @Shadow
    public Input field_3913;

    @Shadow
    private boolean field_3915;

    @Unique
    public HandledScreen keepedInv = null;

    @Unique
    public ScreenHandler keepedInvHandler = null;

    @Unique
    boolean forceCloseInv = false;

    @Accessor("lastX")
    public abstract double getLastX();

    @Accessor("lastBaseY")
    public abstract double getLastBaseY();

    @Accessor("lastZ")
    public abstract double getLastZ();

    @Accessor("lastOnGround")
    public abstract boolean getLastOnGround();

    @Accessor("lastPitch")
    public abstract float getLastPitch();

    @Accessor("lastYaw")
    public abstract float getLastYaw();

    @Override
    public boolean isForceNoFall() {
        return this.forceNoFall;
    }

    @Override
    public void setForceNoFall(boolean fall) {
        this.forceNoFall = fall;
    }

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public void method_3137() {}

    @Shadow
    public void tick() {}

    @Shadow
    public void move(MovementType movementType, Vec3d movement) {}

    @Shadow
    protected abstract void method_3136();

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public void swingHand(Hand hand) {}

    @Shadow
    public abstract boolean isUsingItem();

    @Unique
    @Override
    public void clearKeepedInventory(boolean closeInv) {
        this.keepedInv = null;
        ScreenHandler handler = this.keepedInvHandler;
        this.keepedInvHandler = null;
        if (closeInv) {
            this.forceCloseInv = true;

            try {
                ((ClientPlayerEntity) (Object) this).closeHandledScreen();
            } catch (Throwable var7) {
                var7.printStackTrace();
            } finally {
                this.forceCloseInv = false;
            }
        }
    }

    @ModifyExpressionValue(
            method = {"tickNausea"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z",
                        ordinal = 0)
            })
    public boolean noNausea(boolean val) {
        return NoRender.INSTANCE.CX() ? false : val;
    }

    @Inject(
            method = {"closeHandledScreen"},
            at = {@At("HEAD")},
            cancellable = true)
    public void closeHandledScreen(CallbackInfo ci) {
        if (!this.forceCloseInv
                && InvExtra.INSTANCE.mP.get()
                && this.field_3937.currentScreen instanceof HandledScreen handled
                && !(handled.getScreenHandler() instanceof PlayerScreenHandler)
                && !(handled.getScreenHandler() instanceof CreativeScreenHandler)) {
            this.keepedInv = handled;
            this.keepedInvHandler = ((ClientPlayerEntity) (Object) this).currentScreenHandler;
            this.method_3137();
            ci.cancel();
        }
    }

    @Unique
    public double getAttributeValue(RegistryEntry<EntityAttribute> attribute) {
        return attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED
                        && MovTasks.at().walkSpeed.get()
                ? MovTasks.at().kS()
                : super.getAttributeValue(attribute);
    }

    @ModifyExpressionValue(
            method = {"tickMovement"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")})
    private boolean noSlowUsingItem(boolean original) {
        return MovTasks.ao().Gx ? false : original;
    }

    @WrapOperation(
            method = {"tickMovement"},
            at = {
                @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canStartSprinting()Z")
            })
    private boolean noSlowUsingItemDoNotBlockSprint1(ClientPlayerEntity instance, Operation<Boolean> original) {
        if (MovTasks.ao().Gx) {
            boolean v = this.field_3915;
            this.field_3915 = false;

            boolean var4;
            try {
                var4 = (Boolean) original.call(new Object[] {instance});
            } finally {
                this.field_3915 = v;
            }

            return var4;
        } else {
            return (Boolean) original.call(new Object[] {instance});
        }
    }

    @ModifyExpressionValue(
            method = {"tickMovement"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldSlowDown()Z")})
    private boolean noSlowSneak(boolean original) {
        return MovTasks.ao().Vb() ? false : original;
    }

    protected float getVelocityMultiplier() {
        return MovTasks.ao().whenWithBlock.get() ? 1.0F : super.getVelocityMultiplier();
    }

    @Inject(
            method = {"getPermissionLevel"},
            at = {@At("HEAD")},
            cancellable = true)
    protected void grantAllClientPermissions(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(4);
    }

    @Unique
    @Override
    public void resyncPos() {
        this.field_3926 = 0.0;
        this.field_3924 = 0.0;
        this.field_3940 = 0.0;
    }

    @Unique
    @Override
    public void resyncRot() {
        this.field_3925 = 0.0F;
        this.field_3941 = 0.0F;
    }

    @WrapOperation(
            method = {"tickMovement"},
            at = {
                @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;jump()V", ordinal = 0)
            })
    public void onCancelJumpAfterToggle(ClientPlayerEntity instance, Operation<Void> original) {}

    public void travel(Vec3d movementInput) {
        super.travel(movementInput);
        MoveTimer timer = MovTasks.av();
        if (timer.isActive()) {
            for (int i = 0; i < timer.multiply.get(); i++) {
                this.method_3136();
                super.travel(movementInput);
            }
        }
    }

    @Unique
    private boolean shouldDirectionalSprint() {
        Sprint sprintModule = MovTasks.au();
        return sprintModule.allDirectionSprint.get() && this.field_3913.movementForward <= -0.8 && sprintModule.Om;
    }

    @ModifyExpressionValue(
            method = {"tickMovement"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z")})
    private boolean allDirectionSprint3(boolean original) {
        return this.shouldDirectionalSprint() ? true : original;
    }

    @Inject(
            method = {"isWalking"},
            at = {@At("HEAD")},
            cancellable = true)
    protected void allDirectionSprint4(CallbackInfoReturnable<Boolean> cir) {
        if (this.shouldDirectionalSprint() && !this.isSubmergedInWater()) {
            cir.setReturnValue(true);
        }
    }

    @ModifyExpressionValue(
            method = {"tickNausea"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;shouldPause()Z")})
    private boolean onPortalGui(boolean original) {
        return ExtraTasks.d().keepGuiOpenOnPortal.get() ? true : original;
    }

    @Unique
    public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        if (!stack.isEmpty()
                && this.getWorld().isClient
                && InvTasks.f.get()
                && !MinecraftClient.getInstance().isOnThread()) {
            this.swingHand(Hand.MAIN_HAND);
            return null;
        } else {
            return super.dropItem(stack, throwRandomly, retainOwnership);
        }
    }

    @Inject(
            method = {"pushOutOfBlocks"},
            at = {@At("HEAD")},
            cancellable = true)
    public void onBlockVelocity(double x, double z, CallbackInfo ci) {
        if (MovTasks.aC().noBlockPush.get()) {
            ci.cancel();
        }
    }

    @Override
    public HandledScreen getKeepedInv() {
        return this.keepedInv;
    }

    @Override
    public ScreenHandler getKeepedInvHandler() {
        return this.keepedInvHandler;
    }
}
