package me.matl114.gui.complex.itemEdit;

import java.util.ArrayList;
import java.util.List;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class KalamaHelperHelperM extends KalamaHelperHelperQ {
    List<AttrKeyValue<String>> dX;
    AttrKeyValue<String> dW;

    @Override
    protected void ah() {}

    @Override
    protected void ag() {
        String var1 = this.dW.getOriginValue();
        MutableText var2 = var1 != null && !var1.isEmpty() ? ChatUtils.textFromLegacyString(var1) : null;
        ItemStackUtils.setCustomName(this.M.dO, var2);
        ArrayList var3 = new ArrayList();

        for (AttrKeyValue var5 : this.dX) {
            String var6 = (String) var5.getOriginValue();
            MutableText var7 = var6 != null && !var6.isEmpty() ? ChatUtils.textFromLegacyString(var6) : null;
            var7 = var7 == null ? Text.empty() : var7;
            var3.add(var7);
        }

        ItemStackUtils.setLore(this.M.dO, var3);
    }

    protected KalamaHelperHelperM(final KalamaHelperHelperL this$1) {
        super(this$1);
        this.M = this$1;
        this.af();
    }

    protected void af() {
        Text var1 = ItemStackUtils.getCustomName(this.M.dO);
        String var2 = var1 == null ? "" : ChatUtils.q(var1);
        this.dW = AttrKeyValue.str("widget.gui.item-edit-screen.nbt-editor.display.custom-name", var2);
        this.dX = new ArrayList<>();

        for (Text var5 : ItemStackUtils.getLore(this.M.dO)) {
            this.dX.add(AttrKeyValue.str(
                    "widget.gui.item-edit-screen.nbt-editor.display.lore", var5 == null ? "" : ChatUtils.q(var5)));
        }

        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(10, 0, 260, 20, 50, this.dW).addToSub(this);
        new me.matl114.gui.complex.config.KalamaHelperHelperD(
                        ListEntryWidgetController.mutable(
                                this.dX,
                                () -> AttrKeyValue.str("widget.gui.item-edit-screen.nbt-editor.display.lore", ""),
                                str -> new me.matl114.gui.complex.config.KalamaHelperHelperE<>(0, 0, 180, 20, 30, str),
                                20,
                                180),
                        10,
                        40,
                        260,
                        this.M.bw.ci.getTextureHeight() - 50)
                .addToSub(this);
    }

    KalamaHelperHelperL M;
}
