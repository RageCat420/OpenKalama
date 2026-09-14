package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.matl114.accessors.access.ClientAccess;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.modules.combat.CombatExtra;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.render.RenderExtra;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({MinecraftClient.class})
public abstract class ClientMixin implements Cloneable, ClientAccess {
    @Shadow
    @Nullable
    public ClientPlayerEntity field_1724;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager field_1761;

    @Shadow
    @Nullable
    public HitResult field_1765;

    @Shadow
    private int field_1752;

    @Shadow
    static MinecraftClient field_1700;

    @Final
    @Shadow
    public GameOptions field_1690;

    boolean lastUse = false;

    @Shadow
    @Nullable
    public Screen field_1755;

    @Shadow
    public int field_1771;

    @Unique
    @Override
    public void setItemUseCooldown(int cooldown) {
        this.field_1752 = cooldown;
    }

    @Unique
    @Override
    public int getItemUseCooldown() {
        return this.field_1752;
    }

    @ModifyArg(
            method = {"handleInputEvents"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
                            ordinal = 1))
    public Screen onRedirectInventoryKeyPress(Screen screen) {
        if (InvExtra.INSTANCE.mP.get()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null
                    && ClientPlayerAccess.of(player).getKeepedInvHandler() != null
                    && ClientPlayerAccess.of(player).getKeepedInv() != null) {
                HandledScreen screen1 = ClientPlayerAccess.of(player).getKeepedInv();
                player.currentScreenHandler = ClientPlayerAccess.of(player).getKeepedInvHandler();
                ClientPlayerAccess.of(player).clearKeepedInventory(false);
                return screen1;
            }
        }

        return screen;
    }

    @ModifyExpressionValue(
            method = {"doAttack"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")})
    public boolean onEnableRidingAttack(boolean original) {
        return CombatExtra.INSTANCE.ridingAttack.get() ? false : original;
    }

    @WrapOperation(
            method = {"handleInputEvents"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 2)})
    public boolean onHoldUse(KeyBinding instance, Operation<Boolean> original) {
        boolean pressed = (Boolean) original.call(new Object[] {instance});
        if (InteractionTasks.C().holdUse.get()) {
            boolean lastUseFlag = this.lastUse;
            this.lastUse = pressed;
            if (this.field_1724.isUsingItem()) {
                if (lastUseFlag == pressed) {
                    return true;
                }

                if (this.field_1724.getItemUseTime()
                        < InteractionTasks.C().holdUseStartTick.get()) {
                    return pressed;
                }

                return lastUseFlag;
            }
        }

        return pressed;
    }

    @WrapOperation(
            method = {"handleInputEvents"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
                        ordinal = 0)
            })
    public boolean onAllowingPlayerAttackWhenUseItem(ClientPlayerEntity instance, Operation<Boolean> original) {
        boolean flag = (Boolean) original.call(new Object[] {instance});
        if (flag && CombatExtra.INSTANCE.shieldingAttack.get()) {
            boolean bl3 = false;

            while (this.field_1690.attackKey.wasPressed()) {
                bl3 |= this.method_1536();
            }

            while (this.field_1690.pickItemKey.wasPressed()) {
                this.method_1511();
            }
        }

        return flag;
    }

    @WrapOperation(
            method = {"handleBlockBreaking"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
                        ordinal = 0)
            })
    public boolean onAllowingPlayerBreakingWhenUseItem(ClientPlayerEntity instance, Operation<Boolean> original) {
        return CombatExtra.INSTANCE.shieldingAttack.get() ? false : (Boolean) original.call(new Object[] {instance});
    }

    @WrapOperation(
            method = {"doItemUse"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")})
    public boolean onAllowRidingUse(ClientPlayerEntity instance, Operation<Boolean> original) {
        return InteractExtra.INSTANCE.allowRideInteract.get()
                ? false
                : (Boolean) original.call(new Object[] {instance});
    }

    @Shadow
    protected abstract void method_1590(boolean var1);

    @Shadow
    protected abstract void method_1511();

    @Shadow
    protected abstract boolean method_1536();

    @Unique
    @Override
    public void setAttackCooldown(int cooldown) {
        this.field_1771 = cooldown;
    }

    @Unique
    @Override
    public int getAttackCooldown() {
        return this.field_1771;
    }

    @Shadow
    public abstract Window method_22683();

    @Shadow
    protected abstract void method_1583();

    @Override
    public ClientAccess clone() {
        try {
            ClientAccess clone = (ClientMixin) super.clone();
            return clone;
        } catch (CloneNotSupportedException var2) {
            throw new AssertionError();
        }
    }

    @Inject(
            method = {"hasReducedDebugInfo"},
            at = {@At("HEAD")},
            cancellable = true)
    private void onEnhanceDebug(CallbackInfoReturnable<Boolean> cir) {
        if (RenderExtra.INSTANCE.enhancedDebugHud.get()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    @Override
    public void simulateRightClick() {
        this.method_1583();
    }

    @Unique
    @Override
    public void simulateLeftClick() {
        this.method_1536();
    }

    @Unique
    @Override
    public ActionResult simulateUseItem(Hand hand) {
        ItemStack itemStack = this.field_1724.getStackInHand(hand);
        if (!itemStack.isEmpty()) {
            ActionResult actionResult3 = this.field_1761.interactItem(this.field_1724, hand);
            if (actionResult3.isAccepted() && actionResult3.shouldSwingHand()) {
                this.field_1724.swingHand(hand);
            }

            return actionResult3;
        } else {
            return ActionResult.FAIL;
        }
    }
}
