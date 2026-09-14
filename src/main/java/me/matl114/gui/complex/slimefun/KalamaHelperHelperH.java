package me.matl114.gui.complex.slimefun;

import java.util.List;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.utils.recipes.IRecipeEntry;

class KalamaHelperHelperH extends SlimefunEntryListScreen<IRecipeEntry> {
    KalamaHelperHelperH(List<IRecipeEntry> recipeEntries) {
        super(recipeEntries);
    }

    @Override
    public DrawableWidget cz(IRecipeEntry entry) {
        return eH(entry);
    }
}
