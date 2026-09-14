package me.matl114.events.impl;

public class KalamaHelperHelperI<T> {
   public T b;
   public Class<T> a;

   public T b() {
      return this.b;
   }

   public KalamaHelperHelperI(Class<T> type, T value) {
      this.a = type;
      this.b = (T)value;
   }

   public Class<T> a() {
      return this.a;
   }
}
