package me.matl114.events.model;

import java.util.function.UnaryOperator;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public record KalamaHelperHelperE(UnaryOperator<MatrixStack> stackTransformer, ItemStack stack, BakedModel state) {
    public BakedModel state() {
        return this.state;
    }

    public KalamaHelperHelperE(UnaryOperator<MatrixStack> stackTransformer, ItemStack stack, BakedModel state) {
        this.stackTransformer = stackTransformer;
        this.stack = stack;
        this.state = state;
    }

    public KalamaHelperHelperE UB(BakedModel state) {
        return this.state == state ? this : new KalamaHelperHelperE(this.stackTransformer, this.stack, state);
    }

    public KalamaHelperHelperE Uz(UnaryOperator<MatrixStack> stackTransformer) {
        return this.stackTransformer == stackTransformer
                ? this
                : new KalamaHelperHelperE(stackTransformer, this.stack, this.state);
    }

    public UnaryOperator<MatrixStack> stackTransformer() {
        return this.stackTransformer;
    }

    public KalamaHelperHelperE UA(ItemStack stack) {
        return this.stack == stack ? this : new KalamaHelperHelperE(this.stackTransformer, stack, this.state);
    }
}
