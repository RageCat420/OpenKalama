package me.matl114.versioned.impl;

import java.util.function.Predicate;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.KalamaHelperHelperAo;
import me.matl114.versioned.api.VHideFlag;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;

public enum TooltipHideFlag_v1_21_1 implements VHideFlag {
    yT(
            "属性",
            ItemStackUtils.componentPredicate(
                    DataComponentTypes.ATTRIBUTE_MODIFIERS, MW(AttributeModifiersComponent::showInTooltip), false),
            KalamaHelperHelperAo.onComponent(
                    DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent::withShowInTooltip)),
    yY(
            "盔甲纹饰",
            ItemStackUtils.componentPredicate(DataComponentTypes.TRIM, MW(armorTrim -> armorTrim.showInTooltip), false),
            KalamaHelperHelperAo.onComponent(DataComponentTypes.TRIM, ArmorTrim::withShowInTooltip)),
    yW(
            "可放置",
            ItemStackUtils.componentPredicate(
                    DataComponentTypes.CAN_PLACE_ON, MW(BlockPredicatesChecker::showInTooltip), false),
            KalamaHelperHelperAo.onComponent(
                    DataComponentTypes.CAN_PLACE_ON, BlockPredicatesChecker::withShowInTooltip)),
    yS(
            "附魔",
            ItemStackUtils.componentPredicate(DataComponentTypes.ENCHANTMENTS, i -> !i.showInTooltip, false),
            KalamaHelperHelperAo.onComponent(
                    DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent::withShowInTooltip)),
    yZ(
            "附魔书",
            ItemStackUtils.componentPredicate(DataComponentTypes.STORED_ENCHANTMENTS, i -> !i.showInTooltip, false),
            KalamaHelperHelperAo.onComponent(
                    DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent::withShowInTooltip)),
    yQ(
            "全部",
            ItemStackUtils.c(DataComponentTypes.HIDE_TOOLTIP),
            KalamaHelperHelperAo.byComponent(DataComponentTypes.HIDE_TOOLTIP)),
    yX(
            "染色",
            ItemStackUtils.componentPredicate(
                    DataComponentTypes.DYED_COLOR, MW(DyedColorComponent::showInTooltip), false),
            KalamaHelperHelperAo.onComponent(DataComponentTypes.DYED_COLOR, DyedColorComponent::withShowInTooltip)),
    yU(
            "无法破坏",
            ItemStackUtils.componentPredicate(
                    DataComponentTypes.UNBREAKABLE, MW(UnbreakableComponent::showInTooltip), false),
            KalamaHelperHelperAo.onComponent(DataComponentTypes.UNBREAKABLE, UnbreakableComponent::withShowInTooltip)),
    yV(
            "可破坏",
            ItemStackUtils.componentPredicate(
                    DataComponentTypes.CAN_BREAK, MW(BlockPredicatesChecker::showInTooltip), false),
            KalamaHelperHelperAo.onComponent(DataComponentTypes.CAN_BREAK, BlockPredicatesChecker::withShowInTooltip)),
    yR(
            "额外",
            ItemStackUtils.c(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP),
            KalamaHelperHelperAo.byComponent(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP));
    public KalamaHelperHelperAo zc;
    public String za;
    public Predicate<ItemStack> zb;
    // $VF: synthetic field

    @Override
    public boolean isHide(ItemStack stack) {
        return this.zb.test(stack);
    }

    private static <T> Predicate<T> MW(Predicate<T> tt) {
        return val -> !tt.test(val);
    }

    @Override
    public void setHideFlag(ItemStack stack, boolean hide) {
        this.zc.apply(stack, !hide);
    }

    @Override
    public String displayName() {
        return this.za;
    }

    private TooltipHideFlag_v1_21_1(String display, Predicate<ItemStack> stack, KalamaHelperHelperAo toggle) {
        this.zb = stack;
        this.zc = toggle;
        this.za = display;
    }
}
