package me.matl114.utils.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArgsMap {
   public final Map<String, Object> args;

   public <T> T get(String key) {
      return (T)(Object)this.args.get(key);
   }

   public <T> T c(String key, Supplier<T> defaultValue) {
      Object var3 = this.get(key);
      return (T)(var3 == null ? defaultValue.get() : var3);
   }

   public ArgsMap() {
      this.args = new HashMap<>();
   }

   public ArgsMap(Map<String, Object> args) {
      this.args = new HashMap<>(args);
   }

   public <T> ArgsMap a(String key, T value) {
      this.args.put(key, value);
      return this;
   }
}
