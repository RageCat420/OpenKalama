package me.matl114.events.model;

import java.util.List;
import javax.annotation.Nonnull;
import me.matl114.events.RenderListener;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public interface GuiModel {
    GuiModel EMPTY = new KalamaHelperHelperC();

    default void render(
            ItemRenderer itemRenderer,
            ItemDisplayContext renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay) {
        KalamaHelperHelperE var8 = this.a(itemRenderer);
        if (var8 != null) {
            if (var8.stackTransformer() != null) {
                matrices.push();
                var8.stackTransformer().apply(matrices);
                itemRenderer.renderItem(
                        var8.stack(), renderMode, leftHanded, matrices, vertexConsumers, light, overlay, var8.state());
                matrices.pop();
            } else {
                itemRenderer.renderItem(
                        var8.stack(), renderMode, leftHanded, matrices, vertexConsumers, light, overlay, var8.state());
            }
        }
    }

    KalamaHelperHelperE a(ItemRenderer var1);

    static GuiModel of(Identifier id) {
        if (id == null) {
            return EMPTY;
        } else {
            ItemStack var1 = new ItemStack(Items.BARRIER);
            BakedModel var2 = RenderListener.getCustomModelOf(id);
            return new KalamaHelperHelperA(var1, var2);
        }
    }

    @Nonnull
    static GuiModel b(List<GuiModel> guiModelList) {
        if (guiModelList != null && !guiModelList.isEmpty()) {
            guiModelList = guiModelList.stream()
                    .filter(s -> !(s instanceof KalamaHelperHelperC))
                    .toList();
            if (guiModelList.isEmpty()) {
                return EMPTY;
            } else {
                return (GuiModel)
                        (guiModelList.size() == 1
                                ? (GuiModel) guiModelList.get(0)
                                : new KalamaHelperHelperF(guiModelList));
            }
        } else {
            return EMPTY;
        }
    }

    static GuiModel c(ItemStack stack) {
        return (GuiModel) (stack != null && !stack.isEmpty() ? new KalamaHelperHelperD(stack) : EMPTY);
    }
}
