package me.matl114.utils.collections;

public record FlagEntry<T>(boolean flag, T val) {
   public boolean flag() {
      return this.flag;
   }

   public T val() {
      return this.val;
   }

}
