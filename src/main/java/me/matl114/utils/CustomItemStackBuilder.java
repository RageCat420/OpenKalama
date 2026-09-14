package me.matl114.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.matl114.bukkit.BukkitItemStackUtils;
import me.matl114.versioned.api.VHideFlag;
import me.matl114.versioned.api.VRecord;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CustomItemStackBuilder {
    List<Text> b;
    ItemStack a = new ItemStack(Items.STONE);

    public CustomItemStackBuilder g() {
        this.b.clear();
        return this;
    }

    public CustomItemStackBuilder skullHash(String owner) {
        ItemStackUtils.setOrRemoveChange(this.a, DataComponentTypes.PROFILE, VRecord.h(owner));
        return this;
    }

    public CustomItemStackBuilder type(Item type) {
        if (type != Items.AIR) {
            int var2 = Math.min(1, this.a.getCount());
            this.a = this.a.copyComponentsToNewStackIgnoreEmpty(type, var2);
        }

        return this;
    }

    public CustomItemStackBuilder name(Text name) {
        ItemStackUtils.setOrRemoveChange(this.a, DataComponentTypes.CUSTOM_NAME, name);
        return this;
    }

    public static CustomItemStackBuilder a() {
        return new CustomItemStackBuilder();
    }

    public CustomItemStackBuilder() {
        this.b = new ArrayList<>();
    }

    public CustomItemStackBuilder l(String hash) {
        ItemStackUtils.setOrRemoveChange(
                this.a,
                DataComponentTypes.PROFILE,
                VRecord.staticProfile(
                        UUID.nameUUIDFromBytes(hash.getBytes(StandardCharsets.UTF_8)),
                        "CS-CoreLib",
                        BukkitItemStackUtils.buildPropertyMap(VRecord.k(), hash)));
        return this;
    }

    public CustomItemStackBuilder h(Text tooltip) {
        this.b.add(tooltip);
        return this;
    }

    public CustomItemStackBuilder b(String type) {
        return this.type((Item) Registries.ITEM.get(Identifier.tryParse(type)));
    }

    public ItemStack build() {
        return this.a.copy();
    }

    public CustomItemStackBuilder k(VHideFlag flag) {
        flag.setHideFlag(this.a, true);
        return this;
    }

    public CustomItemStackBuilder glint() {
        ItemStackUtils.setOrRemoveChange(this.a, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, Boolean.TRUE);
        return this;
    }

    public CustomItemStackBuilder amount(int amount) {
        this.a.setCount(amount);
        return this;
    }

    public CustomItemStackBuilder i(String tooltip) {
        return this.h(ChatUtils.textFromLegacyString(tooltip));
    }

    public CustomItemStackBuilder e(String name) {
        return this.name(ChatUtils.textFromLegacyString(name));
    }

    public CustomItemStackBuilder endLore() {
        ItemStackUtils.setOrRemoveChange(this.a, DataComponentTypes.LORE, new LoreComponent(List.copyOf(this.b)));
        return this;
    }
}
