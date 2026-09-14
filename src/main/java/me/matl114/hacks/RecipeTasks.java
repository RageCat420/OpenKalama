package me.matl114.hacks;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.events.Listener;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class RecipeTasks {
   private static DynamicRegistryManager c;
   public static final Map d = Map.of();
   private static RecipeManager INSTANCE;
   public static final Map<String, RecipeType> e = new LinkedHashMap<>();
   public static MinecraftClient a = MinecraftClient.getInstance();
   private static final Map<String, ItemStack> f = Map.of(
      "minecraft:crafting",
      new ItemStack(Items.CRAFTING_TABLE),
      "minecraft:smelting",
      new ItemStack(Items.FURNACE),
      "minecraft:blasting",
      new ItemStack(Items.BLAST_FURNACE),
      "minecraft:smoking",
      new ItemStack(Items.SMOKER),
      "minecraft:campfire_cooking",
      new ItemStack(Items.CAMPFIRE),
      "minecraft:stonecutting",
      new ItemStack(Items.STONECUTTER),
      "minecraft:smithing",
      new ItemStack(Items.SMITHING_TABLE)
   );
   private static Map<Identifier, KalamaHelperHelperC> b;

   public static Map<Identifier, KalamaHelperHelperC> getAllRecipe() {
      init();
      return b;
   }

   private static void e() {
      INSTANCE = null;
      if (b != null) {
         b.clear();
      }

      b = null;
   }

   public static boolean isVanillaRecipeType(String rid) {
      return Registries.RECIPE_TYPE.getOrEmpty(Identifier.tryParse(rid)).isPresent();
   }

   public static Stream<ItemStack> streamIngredientOptions(Ingredient ingredient) {
      return Arrays.stream(ingredient.getMatchingStacks());
   }

   public static ItemStack getRecipeResult(RecipeEntry<?> recipeEntry) {
      return recipeEntry.value().getResult(MinecraftClient.getInstance().world.getRegistryManager());
   }

   static {
      Listener.O().k(v -> e());
      Listener.n(SynchronizeRecipesS2CPacket.class, p -> e());
   }

   private static void init() {
      if (INSTANCE == null || b == null || b.isEmpty()) {
         e();
         INSTANCE = Objects.requireNonNull(a.getNetworkHandler()).getRecipeManager();
         c = Objects.requireNonNull(a.world).getRegistryManager();
         b = new LinkedHashMap<>();

         for (RecipeType var1 : Registries.RECIPE_TYPE) {
            for (RecipeEntry var4 : INSTANCE.listAllOfType(var1)) {
               b.put(var4.id(), KalamaHelperHelperC.of(var4, var1));
            }
         }
      }
   }

   public static ItemStack b(String rid) {
      return f.getOrDefault(rid, null);
   }

   public static List<Ingredient> getIngredients(RecipeEntry<?> recipeEntry) {
      return recipeEntry.value().getIngredients();
   }

   public static List<Ingredient> h(Recipe<?> recipe) {
      return recipe.getIngredients();
   }

   public static RecipeIngredient[] transfer3x3RecipeDisplay(Recipe<?> instance, RecipeIngredient[] ingred) {
      RecipeIngredient[] var2 = new RecipeIngredient[9];
      if (instance instanceof ShapedRecipe var3) {
         List var4 = h(var3);
         int var5 = var3.getWidth();
         int var6 = var3.getHeight();

         for (int var7 = 0; var7 < 3; var7++) {
            for (int var8 = 0; var8 < 3; var8++) {
               if (var7 < var6 && var8 < var5) {
                  var2[3 * var7 + var8] = new RecipeIngredient(streamIngredientOptions((Ingredient)var4.get(var5 * var7 + var8)).toArray(ItemStack[]::new));
               } else {
                  var2[3 * var7 + var8] = RecipeIngredient.EMPTY;
               }
            }
         }
      } else {
         System.arraycopy(ingred, 0, var2, 0, ingred.length);

         for (int var9 = ingred.length; var9 < 9; var9++) {
            var2[var9] = RecipeIngredient.EMPTY;
         }
      }

      return var2;
   }

   public static RecipeIngredient[] f(KalamaHelperHelperC recipeRecord) {
      return recipeRecord.Cu();
   }
}
