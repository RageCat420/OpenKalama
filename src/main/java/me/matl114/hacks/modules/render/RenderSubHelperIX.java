package me.matl114.hacks.modules.render;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class RenderSubHelperIX {
    ItemStack[] equipments;
    Text e;
    float c;
    float f;
    PlayerEntity a;
    Map<RegistryEntry<StatusEffect>, Text> g;
    Text b;

    public RenderSubHelperIX(
            PlayerEntity player,
            Text display,
            ItemStack[] equipments,
            Text otherInfo,
            Map<RegistryEntry<StatusEffect>, Text> effects) {
        this.a = player;
        this.b = display;
        this.c = display == null
                ? 0.0F
                : MinecraftClient.getInstance().textRenderer.getTextHandler().getWidth(display);
        if (this.c <= 0.0F) {
            this.b = null;
        }

        this.equipments = equipments;
        this.e = otherInfo;
        this.f = otherInfo == null
                ? 0.0F
                : MinecraftClient.getInstance().textRenderer.getTextHandler().getWidth(otherInfo);
        if (this.f <= 0.0F) {
            this.e = null;
        }

        this.g = effects;
    }
}
