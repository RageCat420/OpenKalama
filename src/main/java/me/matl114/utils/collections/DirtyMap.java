package me.matl114.utils.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DirtyMap<K, V> implements Map<K, V> {
   volatile boolean dirty = false;
   private final Map<K, V> delegate;

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   @Override
   public int hashCode() {
      return this.delegate.hashCode();
   }

   public Map<K, V> MV() {
      return this.delegate;
   }

   public boolean MU() {
      return this.dirty;
   }

   @Override
   public Set<K> keySet() {
      return new KalamaHelperHelperD<>(this, this.delegate.keySet());
   }

   @Override
   public V remove(Object key) {
      Object var2 = this.delegate.remove(key);
      if (var2 != null) {
         this.MS();
      }

      return (V)var2;
   }

   public DirtyMap(int size) {
      this(new HashMap<>(size));
   }

   @Override
   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   @Override
   public V put(K key, V value) {
      Object var3 = this.delegate.put((K)key, (V)value);
      if (var3 != value) {
         this.MS();
      }

      return (V)var3;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      this.delegate.putAll(m);
      this.MS();
   }

   public void MS() {
      this.setDirty(true);
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Map var2 && var2.equals(this.delegate);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new KalamaHelperHelperD<>(this, this.delegate.entrySet());
   }

   @Override
   public String toString() {
      return "DirtyMap:[handle = " + this.delegate.toString() + ", dirty = ]" + this.dirty;
   }

   @Override
   public Collection<V> values() {
      return new KalamaHelperHelperD<>(this, this.delegate.values());
   }

   @Override
   public void clear() {
      if (!this.delegate.isEmpty()) {
         this.MS();
         this.delegate.clear();
      }
   }

   @Override
   public int size() {
      return this.delegate.size();
   }

   public DirtyMap(Map<K, V> map) {
      this.delegate = map;
   }

   @Override
   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @Override
   public V get(Object key) {
      return this.delegate.get(key);
   }
}
