package me.matl114.utils.itemdb;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.item.ItemStack;

public record KalamaHelperHelperA(String customId) implements ItemStackData {
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj instanceof KalamaHelperHelperA var2) {
         return var2.customId.equals(this.customId);
      } else {
         return obj instanceof ItemStackData var3 ? false : false;
      }
   }

   public String gg() {
      return this.customId;
   }

   @Override
   public ItemStack ge() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ItemStack gf() {
      return Iy;
   }

   @Override
   public boolean isValid() {
      return false;
   }

   public JsonElement getAsJson() {
      return JsonNull.INSTANCE;
   }

   @Override
   public void resolveItemStack() {
   }



   @Override
   public JsonElement gb() { return null; }

}
