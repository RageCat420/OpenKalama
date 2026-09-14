package me.matl114.utils;

import me.matl114.gui.basic.ColorProvider;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;

public interface KalamaHelperHelperAo {
   static KalamaHelperHelperAo byComponent(ComponentType<Unit> type) {
      return (stack, showInTooltip) -> {
         if (showInTooltip) {
            stack.remove(type);
         } else {
            stack.set(type, Unit.INSTANCE);
         }
      };
   }

   void apply(ItemStack var1, boolean var2);

   static <T> KalamaHelperHelperAo onComponent(ComponentType<T> type, ColorProvider<T> toggle) {
      return (stack, showInTooltips) -> {
         T var4 = stack.get(type);
         if (var4 != null) {
            stack.set(type, toggle.provideTextColor(var4, showInTooltips));
         }
      };
   }
}
