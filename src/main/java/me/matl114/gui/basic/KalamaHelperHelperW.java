package me.matl114.gui.basic;

import java.util.function.Supplier;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.item.ItemStack;

class KalamaHelperHelperW implements RenderHandler {
    @Override
    public void renderAtCentered(
            DrawableWidget element,
            VDrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            float alpha,
            boolean shouldHighlight) {
        RenderHandler.drawSingleItem(
                context, (ItemStack) (Object) this.val$item.get(), this.aj, this.ak, this.val$inSlot);
    }

    KalamaHelperHelperW(Supplier var1, int var2, int var3, boolean var4) {
        this.val$item = var1;
        this.aj = var2;
        this.ak = var3;
        this.val$inSlot = var4;
    }

    int aj;
    int ak;
    final Supplier val$item;
    final boolean val$inSlot;
}
