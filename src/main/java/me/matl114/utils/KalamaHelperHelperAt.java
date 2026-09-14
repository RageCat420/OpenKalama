package me.matl114.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class KalamaHelperHelperAt {
   public static <T> Predicate<T> a(Consumer<T> consumer) {
      return v -> {
         try {
            consumer.accept(v);
            return true;
         } catch (Throwable var3) {
            return false;
         }
      };
   }

   public static <T> Predicate<T> b(Runnable runnable) {
      return v -> {
         try {
            runnable.run();
            return true;
         } catch (Throwable var3) {
            return false;
         }
      };
   }
}
