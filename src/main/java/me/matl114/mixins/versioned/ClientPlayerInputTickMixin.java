package me.matl114.mixins.versioned;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.matl114.versioned.accessors.PlayerInputAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerEntity.class})
public class ClientPlayerInputTickMixin {
    @Shadow
    public Input field_3913;

    @ModifyExpressionValue(
            method = {"tickMovement"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z")})
    public boolean rewriteSprintPressWithInputFlag(boolean original) {
        return PlayerInputAccess.of(this.field_3913).isPressingSprint();
    }
}
