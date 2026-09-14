package me.matl114.mixins.hack;

import me.matl114.hacks.modules.render.NoRender;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public class CameraMixin {
    @Inject(
            method = {"getSubmersionType"},
            at = {@At("HEAD")},
            cancellable = true)
    private void onGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
        if (NoRender.INSTANCE.CI()) {
            cir.setReturnValue(CameraSubmersionType.NONE);
        }
    }
}
