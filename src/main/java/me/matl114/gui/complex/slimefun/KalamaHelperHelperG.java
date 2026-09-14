package me.matl114.gui.complex.slimefun;

import java.util.LinkedHashSet;
import java.util.Set;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.presets.choices.RegistryChooseScreen2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

class KalamaHelperHelperG {
    Set<Item> items;
    boolean blacklist = true;

    public boolean acceptable(ItemStack stack) {
        return this.blacklist != this.items.contains(stack.getItem());
    }

    public void openModifyItemScreen(Runnable callback) {
        ScreenAccess.of(new RegistryChooseScreen2<>(Registries.ITEM, this.items, i -> {
                    this.items = i;
                    callback.run();
                }))
                .openFromCurrent();
    }

    public KalamaHelperHelperG() {
        this.items = new LinkedHashSet<>();
    }

    public void b(Runnable callback) {
        this.items.clear();
        this.blacklist = true;
        callback.run();
    }
}
