package me.matl114.utils.containers;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class KalamaHelperHelperB {
   Map<Object, Map<String, Object>> a = new WeakHashMap<>();

   public <W, T> T c(W val, String key, Supplier<T> supplier) {
      Map<String, Object> var4 = this.a.computeIfAbsent(val, s -> new ConcurrentHashMap<>());
      return (T)var4.computeIfAbsent(key, s -> supplier.get());
   }

   public <W> void a(W val, String key, Object value) {
      this.a.computeIfAbsent(val, s -> new ConcurrentHashMap<>()).compute(key, (k, v) -> value);
   }

   public <W, T> T b(W val, String key) {
      Map var3 = this.a.get(val);
      return (T)(var3 != null ? var3.get(key) : null);
   }
}
