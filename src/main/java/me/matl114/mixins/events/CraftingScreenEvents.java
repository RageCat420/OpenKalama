package me.matl114.mixins.events;

import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperG;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin({CraftingScreen.class})
public abstract class CraftingScreenEvents implements RecipeBookProvider {
    @Shadow
    @Final
    private RecipeBookWidget field_2880;

    @ModifyArg(
            method = {"init"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/gui/widget/TexturedButtonWidget;<init>(IIIILnet/minecraft/client/gui/screen/ButtonTextures;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V"),
            index = 5)
    public PressAction modifyPressAction(PressAction pressAction) {
        return button -> {
            pressAction.onPress(button);
            if (!Listener.aj().d()) {
                Listener.aj().broadcast(new KalamaHelperHelperG(this, this.field_2880, button));
            }
        };
    }
}
