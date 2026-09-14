package me.matl114.gui.complex.itemEdit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.component.type.AttributeModifiersComponent;

public class KalamaHelperHelperC extends KalamaHelperHelperQ {
    boolean showInTooltips;
    protected List<KalamaHelperHelperP> bb;

    @Override
    protected void ah() {}

    @Override
    protected void ag() {
        ItemStackUtils.applyEntityModifier(
                this.M.dO,
                new AttributeModifiersComponent(
                        this.bb.stream()
                                .map(KalamaHelperHelperP::value)
                                .filter(Objects::nonNull)
                                .toList(),
                        this.showInTooltips));
    }

    protected KalamaHelperHelperC(final KalamaHelperHelperL this$1) {
        super(this$1);
        this.M = this$1;
        this.init();
    }

    protected void init() {
        this.bb = new ArrayList<>();
        AttributeModifiersComponent var1 = ItemStackUtils.getEntityModifier(this.M.dO);
        var1.modifiers()
                .forEach(var -> this.bb.add(new KalamaHelperHelperP(
                        this, ItemStackUtils.solveDynamic(var.attribute()).toString(), var.modifier(), var.slot())));
        new me.matl114.gui.complex.config.KalamaHelperHelperD(
                        ListEntryWidgetController.mutable(
                                this.bb, () -> new KalamaHelperHelperP(this), KalamaHelperHelperP::b, 60, 180),
                        10,
                        0,
                        260,
                        this.M.bw.ci.getTextureHeight() - 10)
                .addToSub(this);
    }

    KalamaHelperHelperL M;
}
