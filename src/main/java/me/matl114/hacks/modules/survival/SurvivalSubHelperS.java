package me.matl114.hacks.modules.survival;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.versioned.api.VItem;
import net.minecraft.item.ItemStack;

public record SurvivalSubHelperS(ItemStack result, ItemStack buy1, ItemStack buy2, int buyLimit) {
   public static final Codec<SurvivalSubHelperS> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            VItem.e.fieldOf("result").forGetter(SurvivalSubHelperS::result),
             VItem.e.optionalFieldOf("buy1", ItemStack.EMPTY).forGetter(SurvivalSubHelperS::buy1),
            VItem.e.optionalFieldOf("buy2", ItemStack.EMPTY).forGetter(SurvivalSubHelperS::buy2),
            Codec.INT.fieldOf("buy-limit").forGetter(SurvivalSubHelperS::buyLimit)
         )
         .apply(instance, SurvivalSubHelperS::new)
   );
   public ItemStack buy1() {
      return this.buy1;
   }

   public SurvivalSubHelperS(ItemStack result, ItemStack buy1, ItemStack buy2, int buyLimit) {
      this.result = result;
      this.buy1 = buy1;
      this.buy2 = buy2;
      this.buyLimit = buyLimit;
   }

   public ItemStack result() {
      return this.result;
   }

   public ItemStack buy2() {
      return this.buy2;
   }

   public int buyLimit() {
      return this.buyLimit;
   }
}
