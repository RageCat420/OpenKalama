package me.matl114.managers.task;

import java.util.LinkedHashMap;
import java.util.Map;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.config.FlagRef;
import org.jetbrains.annotations.NotNull;

public class KalamaHelperHelperJ implements ToggleManager {
   static FlagRef c = new FlagRef(false);
   Map<String, FlagRef> b;

   @Override
   public FlagRef n(String value) {
      return this.b.get(value);
   }

   @Override
   public void register(String value, Runnable task) {
      throw new UnsupportedOperationException();
   }

   public KalamaHelperHelperJ() {
      this.b = new LinkedHashMap<>();
   }

   @NotNull
   @Override
   public Runnable a(String value) {
      return this.k(value);
   }

   @Override
   public Runnable getOrRegister(String value, Runnable task) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void register(String value, FlagRef flagRef) {
      this.b.put(value, flagRef);
   }

   @Override
   public FlagRef getOrRegister(String value, boolean defaultValue) {
      return this.b.computeIfAbsent(value, s -> new FlagRef(defaultValue));
   }

   @Override
   public void l(String value, boolean defaultValue) {
      this.register(value, new FlagRef(defaultValue));
   }

   public KalamaHelperHelperJ(Map<String, FlagRef> flags) {
      this.b = flags;
   }

   @Override
   public Map<String, Runnable> b() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (String var3 : this.b.keySet()) {
         var1.put(var3, this.k(var3));
      }

      return var1;
   }

   @Override
   public Runnable k(String value) {
      FlagRef var2 = this.b.getOrDefault(value, c);
      return var2 == c ? () -> {} : HotKeyUtils.wrapFlagAsToggle(value, var2);
   }

   @Override
   public boolean getState(String value) {
      return this.b.getOrDefault(value, c).get();
   }
}
