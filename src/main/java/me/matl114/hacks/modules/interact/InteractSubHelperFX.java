package me.matl114.hacks.modules.interact;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.stream.Streams;

public record InteractSubHelperFX(Item item, RegistryEntry<Potion> potionType) {
    public RegistryEntry<Potion> potionType() {
        return this.potionType;
    }

    public Item item() {
        return this.item;
    }

    public Double matches(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && this.item != null) {
            if (!this.item.equals(stack.getItem())) {
                return null;
            } else if (this.potionType == null) {
                return 10.0;
            } else {
                PotionContentsComponent var2 = (PotionContentsComponent) stack.get(DataComponentTypes.POTION_CONTENTS);
                if (!InteractManager.INSTANCE.ignorePotionLevel.get()) {
                    return var2 != null && var2.matches(this.potionType) ? 10.0 : null;
                } else if (var2 == null) {
                    return null;
                } else if (var2.matches(this.potionType)) {
                    return 10.0;
                } else {
                    Set var3 = Streams.of(var2.getEffects())
                            .map(StatusEffectInstance::getEffectType)
                            .collect(Collectors.toSet());
                    Set var4 = Streams.of(((Potion) (Object) this.potionType.value()).getEffects())
                            .map(StatusEffectInstance::getEffectType)
                            .collect(Collectors.toSet());
                    return var3.containsAll(var4) ? 5.0 : null;
                }
            }
        } else {
            return null;
        }
    }

    public String asString() {
        Identifier var1 = this.item == null ? null : Registries.ITEM.getId(this.item);
        return (var1 == null ? "air" : var1.toString()) + (this.potionType == null ? "" : "[" + this.potionType + "]");
    }
}
