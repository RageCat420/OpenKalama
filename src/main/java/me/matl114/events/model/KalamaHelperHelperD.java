package me.matl114.events.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

public class KalamaHelperHelperD implements GuiModel {
   ItemStack itemStack;

   public KalamaHelperHelperD(ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   public KalamaHelperHelperE a(ItemRenderer itemRenderer) {
      return this.itemStack != null && !this.itemStack.isEmpty()
         ? new KalamaHelperHelperE(
            null, this.itemStack, itemRenderer.getModel(this.itemStack, MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, 0)
         )
         : null;
   }
}
