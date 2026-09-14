package me.matl114.managers.task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import me.matl114.managers.config.FlagRef;

public interface ToggleManager extends TaskManager {
   Runnable k(String var1);

   void register(String var1, FlagRef var2);

   FlagRef getOrRegister(String var1, boolean var2);

   static ToggleManager of() {
      LinkedHashMap var0 = new LinkedHashMap();
      return new KalamaHelperHelperJ(var0);
   }

   void l(String var1, boolean var2);

   FlagRef n(String var1);

   static ToggleManager i(HashMap<String, FlagRef> map) {
      return new KalamaHelperHelperJ(map);
   }

   boolean getState(String var1);
}
