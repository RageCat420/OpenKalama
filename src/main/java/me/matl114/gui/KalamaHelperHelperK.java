package me.matl114.gui;

import java.util.function.BiPredicate;

public interface KalamaHelperHelperK<T> extends BiPredicate<T, String> {
   boolean isAccepted(T var1, String var2, boolean var3);

   default boolean test(T value, String string) {
      return this.isAccepted((T)value, string, false);
   }
}
