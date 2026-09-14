package me.matl114.gui.basic;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.text.Text;

public interface TooltipHandler$TooltipProvider {
   static TooltipHandler$TooltipProvider b(List<Text> a) {
      return e -> a;
   }

   List<Text> a(DrawableWidget var1);

   static TooltipHandler$TooltipProvider c(Supplier<List<Text>> t) {
      return e -> (List<Text>)t.get();
   }
}
