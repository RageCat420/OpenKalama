package me.matl114.events.channels;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class KalamaHelperHelperA<T> implements Comparable<KalamaHelperHelperA<T>>, Predicate<T> {
   T value;
   int priority;

   public int compareTo(@NotNull KalamaHelperHelperA<T> th) {
      return this.priority - th.priority;
   }

   @Override
   public boolean test(T t) {
      if (this.value instanceof Consumer var3) {
         var3.accept(t);
         return true;
      } else {
         return this.value instanceof Predicate var4 ? var4.test(t) : false;
      }
   }

   public KalamaHelperHelperA(int priority, T value) {
      this.priority = priority;
      this.value = (T)value;
   }
}
