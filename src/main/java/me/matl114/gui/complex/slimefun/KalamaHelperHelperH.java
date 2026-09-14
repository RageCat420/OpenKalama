package me.matl114.gui.complex.slimefun;

import java.util.List;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.utils.recipes.IRecipeEntry;

class KalamaHelperHelperH extends SlimefunEntryListScreen<IRecipeEntry> {
   KalamaHelperHelperH(List recipeEntries) {
      super(recipeEntries);
   }

   public DrawableWidget dB(IRecipeEntry entry) {
      return eH(entry);
   }



   @Override
   public DrawableWidget cz(Object arg0) { return null; }

}
