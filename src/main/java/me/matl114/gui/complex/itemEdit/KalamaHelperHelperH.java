package me.matl114.gui.complex.itemEdit;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent.Builder;
import net.minecraft.registry.entry.RegistryEntry;

public class KalamaHelperHelperH extends KalamaHelperHelperQ {
    protected boolean showInTooltips;
    protected List<KalamaHelperHelperO> cY;

    @Override
    protected void ah() {}

    protected void saveChanges() {
        Builder var1 = new Builder(ItemEnchantmentsComponent.DEFAULT);

        for (KalamaHelperHelperO var3 : this.cY) {
            Pair var4 = var3.entryValue();
            if (var4.getFirst() != null) {
                var1.add((RegistryEntry) var4.getFirst(), (Integer) var4.getSecond());
            }
        }

        ItemStackUtils.applyItemEnchant(this.M.dO, var1.build());
    }

    protected KalamaHelperHelperH(final KalamaHelperHelperL this$1) {
        super(this$1);
        this.M = this$1;
        this.init();
    }

    protected void init() {
        this.cY = new ArrayList<>();
        ItemEnchantmentsComponent var1 = ItemStackUtils.getItemEnchant(this.M.dO);
        var1.getEnchantmentEntries()
                .forEach(var -> this.cY.add(new KalamaHelperHelperO(
                        ItemStackUtils.solveDynamic((RegistryEntry) var.getKey())
                                .toString(),
                        var.getIntValue())));
        new me.matl114.gui.complex.config.KalamaHelperHelperD(
                        ListEntryWidgetController.mutable(
                                this.cY,
                                () -> new KalamaHelperHelperO("minecraft:", 0),
                                KalamaHelperHelperO::a,
                                20,
                                180),
                        10,
                        0,
                        260,
                        this.M.bw.ci.getTextureHeight() - 10)
                .addToSub(this);
    }

    KalamaHelperHelperL M;

    @Override
    public void ag() {}
}
