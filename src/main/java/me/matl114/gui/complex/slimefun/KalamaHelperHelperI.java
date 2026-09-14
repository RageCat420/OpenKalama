package me.matl114.gui.complex.slimefun;

import java.util.function.Predicate;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.commands.params.impl.StringArgumentResult;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

enum KalamaHelperHelperI implements StringArgumentResult {
    BT(
            ItemStackUtils::hasCustomData,
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.has-custom-data",
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.has-custom-data.detail"),
    BQ(
            i -> true,
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.any",
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.any.detail"),
    BU(
            i -> !ItemStackUtils.hasCustomData(i),
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.no-custom-data",
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.no-custom-data.detail"),
    BS(
            i -> !ItemStackUtils.hasInPatch(i),
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.no-nbt",
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.no-nbt.detail"),
    BR(
            ItemStackUtils::hasInPatch,
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.has-nbt",
            "widget.gui.slimefun-choice-screen.nbt-filter.rule.has-nbt.detail");

    final Predicate<ItemStack> BV;
    final Text BY;
    final Text BW;
    final String BX;

    @Override
    public Text resultAsString() {
        return this.BW;
    }

    private KalamaHelperHelperI(Predicate<ItemStack> itemFilter, String displayNameKey, String detailKey) {
        this.BV = itemFilter;
        this.BW = Text.translatable(displayNameKey);
        this.BX = detailKey;
        this.BY = Text.translatable(detailKey);
    }
}
