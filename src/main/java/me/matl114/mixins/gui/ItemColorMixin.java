package me.matl114.mixins.gui;

import me.matl114.hacks.ModelTasks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin({ItemColors.class})
public class ItemColorMixin {
    @ModifyArgs(
            method = {"create"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/color/item/ItemColors;register(Lnet/minecraft/client/color/item/ItemColorProvider;[Lnet/minecraft/item/ItemConvertible;)V",
                            ordinal = -1))
    private static void injectColorProvider2(Args args) {
        ItemColorProvider provider = (ItemColorProvider) args.get(0);
        args.set(0, (ItemColorProvider) (stack, tintIndex) -> {
            int injectResult = ModelTasks.e().shouldEnableNewStyle(stack) ? -1 : -999;
            return injectResult != -999 ? injectResult : provider.getColor(stack, tintIndex);
        });
    }
}
