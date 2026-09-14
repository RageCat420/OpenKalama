package me.matl114.gui.complex.slimefun;

import java.util.List;
import java.util.function.Function;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.hacks.modules.slimefun.SlimefunGuide;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import net.minecraft.text.Text;

public abstract class SlimefunEntryListScreen<T> extends SlimefunPageScreen {
   private static final int dn = 64;
   private static final int ENTRY_WIDTH = 144;
   List<T> recipeEntries;
   protected int entryPerPage;
   private ContentDelegateWidget[] pageContent;
   private static final int dp = 4;

   public static SlimefunRecipeWidget eH(IRecipeEntry entry) {
      return new SlimefunRecipeWidget(0, 0, entry, SlimefunTasks.w()::onClickItemStack, SlimefunTasks.w()::aed);
   }

   public static SlimefunEntryListScreen<IRecipeEntry> eF(List<IRecipeEntry> list) {
      return new KalamaHelperHelperH(list);
   }

   public static SlimefunRecipeWidget generateRecipeEntryContent(IRecipeEntry entry, int x, int y) {
      return new SlimefunRecipeWidget(x, y, entry, SlimefunTasks.w()::onClickItemStack, SlimefunTasks.w()::aed);
   }

   public SlimefunEntryListScreen(List<T> recipeEntries) {
      super(Text.translatable("widget.gui.slimefun-entry-list-screen.title"));
      this.recipeEntries = recipeEntries;
   }

   @Override
   protected void init() {
      super.init();
      int var1 = this.backgroundHeight - 32 - 4;
      int var2 = Math.max(1, var1 / 68);
      this.entryPerPage = var2;
      this.aN.cp(Math.max(1, 1 + (this.recipeEntries.size() - 1) / var2));
      this.bE();
      this.pageContent = new ContentDelegateWidget[this.entryPerPage];
      int var3 = (this.backgroundWidth - 144) / 2;

      for (int var4 = 0; var4 < this.entryPerPage; var4++) {
         this.pageContent[var4] = new ContentDelegateWidget(this.x + var3, this.y + 32 + 4 + 68 * var4, 64, 64).addTo(this);
      }

      this.bC();
   }

   @Override
   protected int getPageContentHeight() {
      return this.backgroundHeight - 32 - 4;
   }

   @Override
   protected List<Text> provideTitleTooltips(DrawableWidget widget) {
      return SlimefunGuide.NC;
   }

   @Override
   protected void bC() {
      this.aN.co(Math.max(1, 1 + (this.recipeEntries.size() - 1) / this.entryPerPage));
      int var1 = this.aN.cq();
      int var2 = this.recipeEntries.size();
      int var3 = (var1 - 1) * this.entryPerPage;

      for (int var4 = 0; var4 < this.entryPerPage; var4++) {
         if (this.pageContent[var4] == null) {
            int var5 = (this.backgroundWidth - 144) / 2;
            this.pageContent[var4] = new ContentDelegateWidget(this.x + var5, this.y + 32 + 4 + 68 * var4, 64, 64).addTo(this);
         }

         int var6 = var3 + var4;
         if (var6 >= var2) {
            this.pageContent[var4].setContentDelegate(null);
         } else {
            this.pageContent[var4].setContentDelegate(this.cz(this.recipeEntries.get(var6)));
         }
      }
   }

   public static <T> SlimefunEntryListScreen<T> mapToWidget(List<T> list, Function<T, DrawableWidget> factory) {
      return new KalamaHelperHelperE(list, factory);
   }

   public abstract DrawableWidget cz(T var1);
}
