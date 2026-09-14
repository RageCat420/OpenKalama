package me.matl114.hacks.modules.slimefun;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import lombok.NonNull;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import me.matl114.utils.itemdb.ItemStackDataWithAmount;
import net.minecraft.item.ItemStack;

public class SlimefunSubHelperJ implements IRecipeEntry {
   final ItemStackDataWithAmount JT;
   final ItemStack[] finalizedIngredients;
   final List<ItemStackDataWithAmount> JS;
   final ItemStack JV;
   String JR;
   public static final Codec<SlimefunSubHelperJ> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.optionalFieldOf("rid", "").forGetter(SlimefunSubHelperJ::Co),
            Codec.STRING.optionalFieldOf("id", "").forGetter(SlimefunSubHelperJ::id),
            Codec.list(InvTasks.D).optionalFieldOf("ingredient", List.of()).forGetter(SlimefunSubHelperJ::YZ),
            InvTasks.D.optionalFieldOf("output", ItemStackDataWithAmount.EMPTY).forGetter(SlimefunSubHelperJ::Za)
         )
         .apply(instance, SlimefunSubHelperJ::new)
   );
   String id;

   @Override
   public String id() {
      return this.id;
   }

   @Override
   public String toString() {
      return "SlimefunRecipeEntry[ rid = " + this.JR + " , id = " + this.id + " , ingredient = " + this.JS.toString() + ", output = " + this.JT + " ]";
   }

   public List<ItemStackDataWithAmount> YZ() {
      return this.JS;
   }

   @Override
   public RecipeIngredient[] ingredient() {
      RecipeIngredient[] var1 = new RecipeIngredient[9];

      for (int var2 = 0; var2 < this.finalizedIngredients.length; var2++) {
         var1[var2] = new RecipeIngredient(this.finalizedIngredients[var2]);
      }

      for (int var3 = this.finalizedIngredients.length; var3 < 9; var3++) {
         var1[var3] = RecipeIngredient.EMPTY;
      }

      return var1;
   }

   @Override
   public ItemStack Ct() {
      return this.JV;
   }

   public ItemStack[] Zb() {
      return this.finalizedIngredients;
   }

   public ItemStackDataWithAmount Za() {
      return this.JT;
   }

   @Override
   public String Co() {
      return this.JR;
   }

   public SlimefunSubHelperJ(String rid, String id, List<ItemStackDataWithAmount> ingredientEntry, @NonNull ItemStackDataWithAmount output) {
      if (output == null) {
         throw new NullPointerException("output is marked non-null but is null");
      } else {
         this.JR = rid;
         this.id = id;
         this.JS = List.copyOf(ingredientEntry);
         ItemStack[] var5 = new ItemStack[9];

         for (int var6 = 0; var6 < ingredientEntry.size(); var6++) {
            var5[var6] = ((ItemStackDataWithAmount)ingredientEntry.get(var6)).getAsItemStack();
         }

         for (int var7 = ingredientEntry.size(); var7 < 9; var7++) {
            var5[var7] = ItemStack.EMPTY;
         }

         this.finalizedIngredients = var5;
         this.JT = output;
         this.JV = output.getAsItemStack();
      }
   }
}
