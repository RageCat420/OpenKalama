package me.matl114.hacks;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.commands.MainCommand;
import me.matl114.events.annotations.Modifiable;
import me.matl114.gui.complex.slimefun.SlimefunChoiceScreen;
import me.matl114.gui.complex.slimefun.SlimefunEntryListScreen;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.models.ModelExtra2;
import me.matl114.hacks.modules.slimefun.CopyId;
import me.matl114.hacks.modules.slimefun.MultiBlockHelper;
import me.matl114.hacks.modules.slimefun.RecipeDatabase;
import me.matl114.hacks.modules.slimefun.ShowIdTooltips;
import me.matl114.hacks.modules.slimefun.SlimefunGuide;
import me.matl114.hacks.modules.slimefun.SlimefunSubHelperF;
import me.matl114.hacks.modules.slimefun.SlimefunSubHelperJ;
import me.matl114.hacks.modules.slimefun.SlimefunSubHelperO;
import me.matl114.hacks.modules.slimefun.SlimefunSubHelperQ;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimefunTasks {
   private static SlimefunGuide i;
   private static RecipeDatabase g;
   private static ShowIdTooltips k;
   private static CopyId j;
   private static final MinecraftClient a = MinecraftClient.getInstance();
   public static ItemStack e;
   public static final ModuleGroup f = new ModuleGroup("Slimefun");
   public static ItemStack d;
   public static ItemStack b;
   private static MultiBlockHelper h;
   public static ItemStack c;

   public static void a() {
   }

   public static Collection<SlimefunSubHelperO> k(World world, BlockPos dispensor) {
      HashSet var2 = new HashSet();

      for (SlimefunSubHelperO var4 : u().WL().values()) {
         Collection var5 = var4.lookup().lookup(world, dispensor);
         if (var5 != null && !var5.isEmpty()) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public static List<IRecipeEntry> getInventoryRelativeRecipes(Screen inventory, boolean hard) {
      if (!(inventory instanceof HandledScreen var2)) {
         return List.of();
      } else {
         ScreenHandler var3 = var2.getScreenHandler();
         DefaultedList var4 = var3.slots;
         HashSet var5 = new HashSet();
         int var6 = var4.size();

         for (int var7 = 0; var7 < var6; var7++) {
            ItemStack var8 = ((Slot)var4.get(var7)).getStack();
            if (var8 != null && !var8.isEmpty()) {
               String var9 = d(var8);
               if (var9 != null) {
                  var5.add(var9);
               }
            }
         }

         ArrayList var16 = new ArrayList();

         label55:
         for (SlimefunSubHelperJ var18 : u().WK().values()) {
            ItemStack[] var10 = var18.Zb();

            for (ItemStack var14 : var10) {
               if (!var14.isEmpty()) {
                  String var15 = d(var14);
                  if (var15 != null && var5.contains(var15)) {
                     if (!hard) {
                        var16.add(var18);
                        break;
                     }
                  } else if (hard) {
                     continue label55;
                  }
               }
            }

            if (hard) {
               var16.add(var18);
            }
         }

         return var16;
      }
   }

   public static Stream<IRecipeEntry> o() {
      return u().WK().values().stream().map(IRecipeEntry.class::cast);
   }

   public static SlimefunGuide w() {
      return i;
   }

   public static ItemStack e(String id) {
      return InvTasks.as().getFromCodecId(id);
   }

   private static void s(ModuleManager m) {
      g = new RecipeDatabase().register(m);
      h = new MultiBlockHelper().register(m);
      i = new SlimefunGuide().register(m);
      j = new CopyId().register(m);
      k = new ShowIdTooltips().register(m);
   }

   public static Collection<ModelExtra2> j(World world, BlockPos dispensor) {
      HashSet var2 = new HashSet();

      for (SlimefunSubHelperO var4 : u().WL().values()) {
         Collection<SlimefunSubHelperF> var5 = var4.lookup().lookup(world, dispensor);
         if (var5 != null && !var5.isEmpty()) {
            for (SlimefunSubHelperF var7 : var5) {
               var2.add(new ModelExtra2(var4, var7));
            }
         }
      }

      return var2;
   }

   static {
      Tasks.l(() -> {
         Debug.a("Running Slimefun Post Setup Tasks");
         initIcon();
      }, 1);
      MainCommand.aH("sf", KalamaHelperHelperY::new);
      f.registerFactories(SlimefunTasks::s);
      HackModules.registerModuleGroup(f);
   }

   @Modifiable
   public static void moveSlimefunRecipePatternToContainer(IRecipeEntry entry, ScreenHandler screen, int amount, boolean removeOrigin, int... acceptSlots) {
      Preconditions.checkArgument(acceptSlots.length == 9);
      ItemStack[] var5 = new ItemStack[9];
      RecipeIngredient[] var6 = entry.ingredient();
      Preconditions.checkArgument(var6.length <= 9);

      for (int var7 = 0; var7 < var6.length; var7++) {
         RecipeIngredient var8 = var6[var7];
         if (var8.isEmpty()) {
            var5[var7] = ItemStack.EMPTY;
         } else {
            var5[var7] = var8.matchingStack()[0];
         }
      }

      for (int var9 = var6.length; var9 < 9; var9++) {
         var5[var9] = ItemStack.EMPTY;
      }

      int[] var10 = InvTasks.getPlayerInventorySlots(screen).toIntArray();
      InvTasks.moveRecipePatternToContainer(
         screen, var5, acceptSlots, amount, removeOrigin, (screen1, itemStack) -> getItemStackMatchingSlot(screen1, itemStack, true, var10)
      );
   }

   public static void openOrSwitch(Screen sf) {
      ScreenAccess var1 = ScreenAccess.of(sf);
      if (a.currentScreen instanceof SlimefunEntryListScreen var3) {
         if (sf instanceof SlimefunEntryListScreen) {
            var1.switchFromCurrent();
         } else if (sf instanceof SlimefunChoiceScreen var5) {
            var3.close();
            openOrSwitch(sf);
         } else {
            var1.openFromCurrent();
         }
      } else if (a.currentScreen instanceof SlimefunChoiceScreen var4) {
         if (sf instanceof SlimefunChoiceScreen) {
            var1.switchFromCurrent();
         } else {
            var1.openFromCurrent();
         }
      } else {
         var1.openFromCurrent();
      }
   }

   public static ItemStack g(String rid) {
      ItemStack var1;
      if ((var1 = f(rid)) == null) {
         var1 = h(rid);
      }

      return var1;
   }

   public static ModuleGroup t() {
      return f;
   }

   public static RecipeDatabase u() {
      return g;
   }

   public static Optional<SlimefunSubHelperQ> i(SlimefunSubHelperO entry) {
      return entry.id() == null ? Optional.empty() : u().WJ().values().stream().filter(ct -> entry.id().equals(ItemStackUtils.aa(ct.icon().yN()))).findFirst();
   }

   public static String c(ItemStack item) {
      return InvTasks.as().getItemIdOrNull(item);
   }

   public static CopyId x() {
      return j;
   }

   public static ShowIdTooltips y() {
      return k;
   }

   private static ItemStack f(String rid) {
      return RecipeTasks.isVanillaRecipeType(rid) ? RecipeTasks.b(rid) : null;
   }

   public static String generateId(ItemStack item) {
      if (item != null && !item.isEmpty()) {
         String var1 = ItemStackUtils.aa(item);
         return var1 != null ? var1 : Registries.ITEM.getId(item.getItem()).toString();
      } else {
         return "minecraft:air";
      }
   }

   public static Map<String, IRecipeEntry> n() {
      return u().WK();
   }

   public static String d(ItemStack item) {
      String var1 = ItemStackUtils.aa(item);
      return var1 == null ? c(item) : var1;
   }

   public static MultiBlockHelper v() {
      return h;
   }

   public static ItemStack h(String rid) {
      return u().WJ().getOrDefault(rid, SlimefunSubHelperQ.Wl).icon().getAsItemStack();
   }

   private static void initIcon() {
      ItemStack var0 = new ItemStack(Items.ENCHANTED_BOOK);
      ItemStackUtils.setCustomModelData(var0, 2200001);
      b = var0;
      c = new ItemStack(Items.KNOWLEDGE_BOOK);
      d = new ItemStack(Items.CRAFTING_TABLE);
      e = new ItemStack(Items.CHAIN_COMMAND_BLOCK);
   }

   @Modifiable
   public static KalamaHelperHelperB getItemStackMatchingSlot(ScreenHandler screen, ItemStack stack, boolean weakMatch, int... slots) {
      if (stack.isEmpty()) {
         return InvTasks.getEmptySlots(screen, slots);
      } else if (!weakMatch) {
         return InvTasks.getItemStackMatchingSlot(screen, stack, slots);
      } else {
         KalamaHelperHelperB var4 = new KalamaHelperHelperB();
         ItemStack var5 = null;
         String var6 = d(stack);
         DefaultedList var7 = screen.slots;

         for (int var11 : slots) {
            Slot var12 = (Slot)var7.get(var11);
            if (var12 != null && var12.inventory instanceof PlayerInventory && !var12.getStack().isEmpty()) {
               if (var5 != null) {
                  if (ItemStack.areItemsAndComponentsEqual(var12.getStack(), var5)) {
                     var4.addMatchingSlot(var11, var12);
                  }
               } else if (Objects.equals(var6, d(var12.getStack()))) {
                  var5 = var12.getStack();
                  var4.setItemSample(var5);
                  var4.addMatchingSlot(var11, var12);
               }
            }
         }

         return var4;
      }
   }
}
