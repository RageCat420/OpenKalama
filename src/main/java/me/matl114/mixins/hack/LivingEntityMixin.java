package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.matl114.accessors.access.LivingEntityAccess;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.utils.EntityUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {
    @Shadow
    protected int field_6239;

    @Accessor("jumpingCooldown")
    @Override
    public abstract void setJumpingCooldown(int var1);

    @Shadow
    public abstract float getYaw(float tickDelta);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract float method_56994(float var1);

    @Shadow
    public abstract boolean method_6128();

    @Shadow
    public void remove(RemovalReason reason) {}

    @Shadow
    public void method_5673(EquipmentSlot var1, ItemStack var2) {}

    @Shadow
    public void method_29242(boolean var1) {}

    @Unique
    @Override
    public float getJumpUpwardSpeed(float strength) {
        return this.method_56994(1.0F);
    }

    @Inject(
            method = {"jump"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/entity/LivingEntity;addVelocityInternal(Lnet/minecraft/util/math/Vec3d;)V",
                        shift = Shift.BEFORE)
            },
            cancellable = true)
    private void fixJumpingWhileSprintingBackward(CallbackInfo ci, @Local Vec3d vec3d) {
        if (MovTasks.au().allDirectionSprint.get()) {
            Vec3d rot = EntityUtils.pitchYawToRotation(0.0F, this.getYaw());
            if (rot.x * vec3d.x + rot.z * vec3d.z < 0.0) {
                this.addVelocityInternal(rot.normalize().multiply(-0.2));
                this.velocityDirty = true;
                ci.cancel();
            }
        }
    }

    @ModifyExpressionValue(
            method = {"travel"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSlipperiness()F")})
    private float onIgnoreSlipperiness(float original) {
        return MovTasks.ao().whenOnBlock.get() ? 0.6F : original;
    }

    @Inject(
            method = {"travel"},
            at = {@At("HEAD")},
            cancellable = true)
    private void onWaterGlide(Vec3d movementInput, CallbackInfo ci) {
        if (this.checkClientPlayer()) {
            Vec3d overriding = ElytraExtra.INSTANCE.agv();
            if (this.method_6128()
                    && (ElytraExtra.INSTANCE.fireworkLiquidFly.get() && ElytraExtra.INSTANCE.agj()
                            || overriding != null)) {
                ci.cancel();
                Vec3d vec3d = this.getVelocity();
                if (overriding != null) {
                    this.setVelocity(overriding);
                } else {
                    this.setVelocity(EntityUtils.calculateGlidingVelocity(
                            (ClientPlayerEntity) (Object) this, vec3d, this.getRotationVector(), !this.hasNoGravity()));
                }

                this.move(MovementType.SELF, this.getVelocity());
                this.method_29242(this instanceof Flutterer);
            }
        }
    }

    @WrapOperation(
            method = {"travel"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                        ordinal = 6)
            })
    private void travelGliding(LivingEntity instance, Vec3d oldVelocity, Operation<Vec3d> original) {
        if (this.checkClientPlayer()) {
            Vec3d overriding = ElytraExtra.INSTANCE.agv();
            if (overriding != null) {
                original.call(new Object[] {instance, overriding});
                return;
            }
        }

        original.call(new Object[] {instance, oldVelocity});
    }
}
