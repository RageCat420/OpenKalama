package me.matl114.mixins.fix;

import me.matl114.accessors.gui.CustomFocusBehaviourScreenAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({ParentElement.class})
public interface ParentElementButtonFixMixin {
    @Shadow
    void method_25395(@Nullable Element var1);

    @Inject(
            method = {"mouseClicked"},
            at = {@At("RETURN")})
    default void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        boolean returnValue = cir.getReturnValueZ();
        if (!returnValue) {
            Element defaultVal = null;
            ParentElement var9 = (ParentElement) (Object) this;
            if (var9 instanceof CustomFocusBehaviourScreenAccess access) {
                defaultVal = access.getDefaultElement();
            }

            this.method_25395(defaultVal);
        }
    }
}
