package me.matl114.mixins.events;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(
        value = {ItemStack.class},
        priority = 10000)
public class ItemStackEvents {
    @Inject(
            method = {"getTooltip"},
            at = {@At("RETURN")})
    public void onTooltip(
            TooltipContext context,
            @Nullable PlayerEntity player,
            TooltipType type,
            CallbackInfoReturnable<List<Text>> cir) {
        List<Text> tooltip = (List<Text>) cir.getReturnValue();
        Event<List<Text>> event =
                new Event<>(tooltip, false, false, (ItemStack) (Object) this, type.isAdvanced(), type.isCreative());
        RenderListener.x().catchEvent(event);
    }
}
