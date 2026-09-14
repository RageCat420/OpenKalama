package me.matl114.gui.complex.slimefun;

import java.util.List;
import java.util.function.Function;
import me.matl114.gui.basic.DrawableWidget;

class KalamaHelperHelperE<T> extends SlimefunEntryListScreen<T> {
   @Override
   public DrawableWidget cz(T entry) {
      return (DrawableWidget)(Object)this.gson.apply(entry);
   }

   KalamaHelperHelperE(List var1, Function var2) {
      super(var1);
      this.gson = var2;
   }
}
