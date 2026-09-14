package me.matl114.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.utils.InventoryUtils;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.util.Identifier;

public class SlotElement extends AbstractElement {
   private boolean U = true;
   protected static final int S = 18;
   private BooleanSupplier W;
   private boolean V = true;
   final Inventory M;
   protected static final int Q = 0;
   protected static final int R = 222;
   protected static final Identifier SLOT_RESOURCE = new Identifier("kalama", "textures/custom/recipecontainer.png");
   final int index;
   protected static final int T = 18;
   final KalamaHelperHelperL O;

   public SlotElement(ItemStack itemStack, KalamaHelperHelperL callback) {
      this(new SimpleInventory(new ItemStack[]{itemStack}), 0, callback);
   }

   public SlotElement(Supplier<ItemStack> sup) {
      this(InventoryUtils.b(sup));
   }

   @Override
   public AbstractElement aO(TooltipHandler handler) {
      if (handler == null) {
         return this;
      } else {
         this.W = () -> false;
         return super.aO(handler);
      }
   }

   public SlotElement(Inventory inventory, int index, KalamaHelperHelperL callback) {
      this.W = () -> true;
      this.M = inventory;
      this.index = index;
      this.O = callback;
   }

   public static SlotElement aI(ItemStack itemStack) {
      return new SlotElement(itemStack);
   }

   public SlotElement aL(boolean slot) {
      this.V = slot;
      return this;
   }

   public SlotElement aM(boolean tooltips) {
      return this.aN(() -> tooltips);
   }

   public SlotElement(Inventory inventory) {
      this(inventory, 0, KalamaHelperHelperL.a);
   }

   public SlotElement(Inventory inventory, int index) {
      this(inventory, index, KalamaHelperHelperL.a);
   }

   public static SlotElement instance(ItemStack itemStack, KalamaHelperHelperL handle) {
      return new SlotElement(itemStack, handle);
   }

   @Override
   public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return this.O.a(this.M.getStack(this.index), button);
   }

   @Override
   public void renderExtra0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      super.renderExtra0(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      if (shouldHighlight && this.W != null && this.W.getAsBoolean()) {
         ItemStack var8 = this.M.getStack(this.index);
         if (!var8.isEmpty()) {
            context.drawTooltip(
               mc.textRenderer, var8.getTooltip(TooltipContext.create(mc.world), mc.player, TooltipType.ADVANCED), var8.getTooltipData(), mouseX, mouseY
            );
         }
      }
   }

   protected void renderSlotFrame(VDrawContext context) {
      context.Q(SLOT_RESOURCE, 0, 0, 0, 222, 18, 18);
   }

   @Override
   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      context.setShaderAlpha(alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      float var8 = element.getTextureWidth() / 18.0F;
      float var9 = element.getTextureHeight() / 18.0F;
      boolean var10 = var8 != 1.0F || var9 != 1.0F;
      if (var10) {
         context.f().pushMatrix();
         context.f().scale(var8, var9);
      }

      if (this.U) {
         this.renderSlotFrame(context);
      }

      context.setShaderAlpha(1.0F);
      ItemStack var11 = this.M.getStack(this.index);
      RenderHandler.drawSingleItem(context, var11, 1, 1, this.V);
      if (shouldHighlight) {
         context.E(1, 1, 17, 17, -2130706433, -2130706433, 0);
      }

      if (var10) {
         context.f().popMatrix();
      }
   }

   public SlotElement(ItemStack itemStack) {
      this(new SimpleInventory(new ItemStack[]{itemStack}), 0);
   }

   public SlotElement aK(boolean slot) {
      this.U = slot;
      return this;
   }

   public SlotElement aN(BooleanSupplier tooltips) {
      this.W = tooltips;
      return this;
   }
}
