package me.matl114.hacks.modules.inv;

import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperG;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.other.TradeInformationSubScreen;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.RecipeTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;

public class FastCraft extends BaseModule {
   private RecipeEntry<?> pQ;
   boolean lock;
   public final FlagRef dropCraft;
   KalamaHelperHelperCX pS;
   public final ModulePath pO = makePath(Configs.l, "fast-craft");
   HandledScreen<?> pT;
   public final FlagRef ae = this.flagBuilder(this.pO.add("enable-fastcraft-buttons")).build();

   public void yh(Event<KalamaHelperHelperG> event) {
      if (((KalamaHelperHelperG)event.e()).provider() == this.pT) {
         this.pS.setX(HandledScreenAccess.of(this.pT).getScreenX());
      }
   }

   public void yi(Event<RecipeEntry<?>> event) {
      if (!this.isLock()) {
         this.pQ = (RecipeEntry<?>)event.e();
      }
   }

   public boolean isLock() {
      return this.lock;
   }

   public void craftAtSlotIndex(HandledScreen<?> screen, int maxCraft, int slot) {
      boolean var4 = this.dropCraft.get();
      if (var4) {
         for (int var5 = 0; var5 < maxCraft; var5++) {
            InvTasks.at().execute(() -> mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, slot, 0, SlotActionType.THROW, mc.player));
         }
      } else {
         InvTasks.at().execute(() -> mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, slot, 1, SlotActionType.QUICK_MOVE, mc.player));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ai().c(HandledScreen.class), this::onCraftScreenInitialize);
      this.registerListener(Listener.aj(), this::yh);
      this.registerListener(Listener.am(), this::yi);
      TaskManagers.c().register("button-toggle.drop-craft", this.dropCraft);
   }

   @Unique
   private void addInventoryButton(InventoryScreen screen) {
      HandledScreenAccess var2 = HandledScreenAccess.of(screen);
      KalamaHelperHelperCX var3 = KalamaHelperHelperCX.H(var2.getScreenX(), 0, screen.width, screen.height);
      ExecutableWidget var4 = ExecutableWidget.instance(150, screen.height / 2 - 38, 24, 12)
         .<ExecutableWidget>eV(
            new ButtonElement(
                  TextProvider.c(Text.translatable("widget.fast-craft.craft")),
                  ButtonAction.a(() -> this.placeLastCraftingRecipe(screen, ScreenUtils.hasShiftDown()))
               )
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.fast-craft.craft.tooltips", "")))
         )
         .addToSub(var3);
      ExecutableWidget var5 = ExecutableWidget.instance(150, screen.height / 2 - 25, 24, 12)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.translatable("widget.fast-craft.lock")), ButtonAction.a(this::yj))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.fast-craft.lock.tooltips", "")))
         )
         .addToSub(var3);
      Runnable var6 = HotKeyUtils.wrapFlagAsToggle("fast-craft.drop-craft", this.dropCraft);
      ExecutableWidget var7 = ExecutableWidget.instance(150, screen.height / 2 - 72, 24, 12)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.translatable("widget.fast-craft.toggle-drop")), ButtonAction.a(var6))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.fast-craft.toggle-drop.tooltips", "")))
         )
         .addToSub(var3);
      DrawableWidget var8 = DisplayWidget.instance(132, var2.getScreenY() + 55, 18, 18)
         .<DrawableWidget>setRenderHandler(new SlotElement(this::getDisplayItemStack).aK(false).aL(false))
         .addToSub(var3);
      DrawableWidget var9 = DisplayWidget.instance(142, var2.getScreenY() + 55 + 10, 7, 7)
         .<DrawableWidget>setRenderHandler(new SlotElement(this::getLockItem).aL(false).aK(false).n(v -> this.isLock()))
         .addToSub(var3);
      var2.addDrawableChildTo(var3);
      this.pT = screen;
      this.pS = var3;
   }

   private void yq(MerchantScreen merchantScreen) {
      HandledScreenAccess var2 = HandledScreenAccess.of(merchantScreen);
      var2.addDrawableChildTo(new TradeInformationSubScreen(var2.getScreenX(), var2.getScreenY(), merchantScreen));
   }

   public void yj() {
      this.lock = !this.lock;
      Debug.chat("Toggle RecipeLock", this.lock);
   }

   public void placeLastCraftingRecipe(HandledScreen<? extends AbstractRecipeScreenHandler> craftingScreen, boolean doCraft) {
      RecipeEntry var3 = this.pQ;
      if (var3 != null) {
         mc.interactionManager.clickRecipe(((AbstractRecipeScreenHandler)craftingScreen.getScreenHandler()).syncId, var3, true);
         if (doCraft) {
            int var4 = 64;

            for (Ingredient var6 : RecipeTasks.getIngredients(var3)) {
               for (ItemStack var8 : RecipeTasks.streamIngredientOptions(var6).toList()) {
                  var4 = Math.min(var4, var8.getMaxCount());
               }
            }

            int var9 = ((AbstractRecipeScreenHandler)craftingScreen.getScreenHandler()).getCraftingResultSlotIndex();
            this.craftAtSlotIndex(craftingScreen, var4, var9);
         }
      } else {
         Debug.b("Crafting History Is Empty");
      }
   }

   public void onCraftScreenInitialize(Event<Screen> event) {
      if (this.ae.get()) {
         if (event.b instanceof CraftingScreen var3) {
            this.pT = null;
            this.addCraftingInventoryButton(var3);
         } else if (event.b instanceof InventoryScreen var4) {
            this.pT = null;
            if (!mc.interactionManager.getCurrentGameMode().isCreative()) {
               this.addInventoryButton(var4);
            }
         } else if (event.b instanceof MerchantScreen var5) {
            this.pT = null;
            this.yq(var5);
         }
      }
   }

   private ItemStack getDisplayItemStack() {
      return this.pQ != null ? RecipeTasks.getRecipeResult(this.pQ) : new ItemStack(Items.BARRIER);
   }

   private ItemStack getLockItem() {
      return this.lock ? new ItemStack(Items.BARRIER) : ItemStack.EMPTY;
   }

   public FastCraft() {
      super("FastCraft");
      this.dropCraft = this.flagBuilder(this.pO.add("drop-craft")).build();
      this.pS = null;
      this.pT = null;
      this.bindFlag(this.ae);
   }

   @Unique
   private void addCraftingInventoryButton(CraftingScreen screen) {
      HandledScreenAccess var2 = HandledScreenAccess.of(screen);
      KalamaHelperHelperCX var3 = KalamaHelperHelperCX.H(var2.getScreenX(), 0, screen.width, screen.height);
      ExecutableWidget var4 = ExecutableWidget.instance(120, screen.height / 2 - 25, 24, 12)
         .<ExecutableWidget>eV(
            new ButtonElement(
                  TextProvider.c(Text.translatable("widget.fast-craft.craft")),
                  ButtonAction.a(() -> this.placeLastCraftingRecipe(screen, ScreenUtils.hasShiftDown()))
               )
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.fast-craft.craft.tooltips", "")))
         )
         .addToSub(var3);
      ExecutableWidget var5 = ExecutableWidget.instance(95, screen.height / 2 - 25, 24, 12)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.translatable("widget.fast-craft.lock")), ButtonAction.a(this::yj))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.fast-craft.lock.tooltips", "")))
         )
         .addToSub(var3);
      Runnable var6 = HotKeyUtils.wrapFlagAsToggle("fast-craft.drop-craft", this.dropCraft);
      ExecutableWidget var7 = ExecutableWidget.instance(120, screen.height / 2 - 72, 24, 12)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.translatable("widget.fast-craft.toggle-drop")), ButtonAction.a(var6))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.fast-craft.toggle-drop.tooltips", "")))
         )
         .addToSub(var3);
      DrawableWidget var8 = DisplayWidget.instance(150, var2.getScreenY() + 56, 18, 18)
         .<DrawableWidget>setRenderHandler(new SlotElement(this::getDisplayItemStack).aK(false).aL(false))
         .addToSub(var3);
      DrawableWidget var9 = DisplayWidget.instance(160, var2.getScreenY() + 56 + 10, 7, 7)
         .<DrawableWidget>setRenderHandler(new SlotElement(this::getLockItem).aL(false).aK(false).n(v -> this.isLock()))
         .addToSub(var3);
      var2.addDrawableChildTo(var3);
      this.pT = screen;
      this.pS = var3;
   }

   public RecipeEntry<?> yr() {
      return this.pQ;
   }
}
