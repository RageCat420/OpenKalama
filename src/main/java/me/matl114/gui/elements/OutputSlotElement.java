package me.matl114.gui.elements;

import me.matl114.versioned.api.VDrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class OutputSlotElement extends SlotElement {
    private static final float l = 0.9375F;
    private static final int m = -4;
    private static final float j = 0.0703125F;
    private static final float i = 0.0F;
    private static final float k = 0.8671875F;
    private static final int n = 22;

    @Override
    protected void renderSlotFrame(VDrawContext context) {
        context.w(SLOT_RESOURCE, -4, 22, -4, 22, 0, 0.0F, 0.0703125F, 0.8671875F, 0.9375F);
    }

    public OutputSlotElement(Inventory inventory, int index) {
        super(inventory, index);
    }

    public OutputSlotElement(ItemStack stack) {
        super(stack);
    }
}
