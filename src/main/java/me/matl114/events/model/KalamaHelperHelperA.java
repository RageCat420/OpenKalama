package me.matl114.events.model;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;

public class KalamaHelperHelperA implements GuiModel {
   BakedModel b;
   ItemStack a;

   public KalamaHelperHelperA(ItemStack itemStack, BakedModel itemModel) {
      this.a = itemStack;
      this.b = itemModel;
   }

   public KalamaHelperHelperE a(ItemRenderer itemRenderer) {
      return new KalamaHelperHelperE(null, this.a, this.b);
   }
}
