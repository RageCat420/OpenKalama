package me.matl114.utils.collections;

public class MutableEntry<K, V> {
   public V b;
   public K a;

   public void c(K key) {
      this.a = (K)key;
   }

   public void d(V value) {
      this.b = (V)value;
   }

   public K a() {
      return this.a;
   }

   public MutableEntry(K key, V value) {
      this.a = (K)key;
      this.b = (V)value;
   }

   public V b() {
      return this.b;
   }
}
