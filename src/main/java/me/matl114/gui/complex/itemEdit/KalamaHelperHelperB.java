package me.matl114.gui.complex.itemEdit;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;

public class KalamaHelperHelperB extends KalamaHelperHelperQ {
    List<KalamaHelperHelperA> L;

    protected void init() {
        this.L = this.M.dO.components.getChanges().entrySet().stream()
                .map(m -> new KalamaHelperHelperA((ComponentType) m.getKey(), (Optional) m.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
        new me.matl114.gui.complex.config.KalamaHelperHelperD(
                        ListEntryWidgetController.mutable(
                                this.L,
                                () -> new KalamaHelperHelperA("minecraft:"),
                                KalamaHelperHelperA::factory,
                                20,
                                180),
                        10,
                        0,
                        260,
                        this.M.bw.ci.getTextureHeight() - 10)
                .addToSub(this);
    }

    protected void saveChanges() {
        Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap();
        this.L.forEach(i -> i.applyChanges(var1));
        this.M.dO.components.setChanges(new ComponentChanges(var1));
    }

    protected KalamaHelperHelperB(final KalamaHelperHelperL this$1) {
        super(this$1);
        this.M = this$1;
        this.init();
    }

    @Override
    protected void ah() {}

    KalamaHelperHelperL M;

    @Override
    public void ag() {}
}
