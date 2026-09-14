package me.matl114.hacks;

import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public record KalamaHelperHelperC(Identifier identifier, Recipe<?> instance, RecipeType<?> type, ItemStack output, RecipeIngredient[] ingredients)
   implements IRecipeEntry {
   @Override
   public String id() {
      return this.identifier.toString();
   }

   public static KalamaHelperHelperC of(RecipeEntry<?> instance, RecipeType type) {
      return new KalamaHelperHelperC(
         instance.id(),
         instance.value(),
         type,
         instance.value().getResult(RecipeTasks.a.world.getRegistryManager()),
         RecipeTasks.transfer3x3RecipeDisplay(
            instance.value(), instance.value().getIngredients().stream().map(v -> new RecipeIngredient(v.getMatchingStacks())).toArray(RecipeIngredient[]::new)
         )
      );
   }

   public Recipe<?> Cr() {
      return this.instance;
   }

   @Override
   public ItemStack output() {
      return this.output;
   }

   public RecipeIngredient[] ingredients() {
      return this.ingredients;
   }

   public KalamaHelperHelperC(Identifier identifier, Recipe<?> instance, RecipeType<?> type, ItemStack output, RecipeIngredient[] ingredients) {
      this.identifier = identifier;
      this.instance = instance;
      this.type = type;
      this.output = output;
      this.ingredients = ingredients;
   }


   public String rid() {
      return Registries.RECIPE_TYPE.getId(this.type).toString();
   }

   public RecipeType<?> Cs() {
      return this.type;
   }

   public Identifier identifier() {
      return this.identifier;
   }



   @Override
   public RecipeIngredient[] ingredient() { return null; }


   public ItemStack Ct() { return null; }


   public String Co() { return null; }

}
