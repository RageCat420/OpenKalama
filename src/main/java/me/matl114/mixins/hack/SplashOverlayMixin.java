package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.matl114.hacks.RenderTasks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({SplashOverlay.class})
public class SplashOverlayMixin {
    @Inject(
            method = {"render"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V",
                        ordinal = 1,
                        shift = Shift.AFTER)
            })
    private void onRenderOverlay(
            DrawContext context,
            int mouseX,
            int mouseY,
            float deltaTicks,
            CallbackInfo ci,
            @Local(ordinal = 3) float alpha) {
        if (RenderTasks.F().ae.get()) {
            Identifier identifier = Identifier.tryParse(RenderTasks.F().aW.get());
            int i = context.getScaledWindowWidth();
            int j = context.getScaledWindowHeight();
            int color = RenderTasks.F().customBackgroundColor.get();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.fillGradient(0, 0, i, j, color, color);
            RenderSystem.blendFuncSeparate(1, 771, 1, 771);
            context.drawTexturedQuad(identifier, 0, i, 0, j, 0, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, alpha);
        }
    }

    @ModifyExpressionValue(
            method = {"renderProgressBar"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper$Argb;getArgb(IIII)I")})
    private int onOverrideProgressbar(int original, @Local(ordinal = 5) int j) {
        return RenderTasks.F().customProgressBarColor.get().withAlpha(j);
    }
}
