package me.matl114.hacks.modules.combat;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs$SetBackTriggerType;

// $VF: synthetic class
class CombatSubHelperJ {
  static int[] b;
  static int[] a;
   static {
      try {
         b[ModulePreset.fh.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         b[ModulePreset.fg.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         b[ModulePreset.fd.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }

      try {
         b[ModulePreset.fe.ordinal()] = 4;
      } catch (NoSuchFieldError var3) {
      }

      a = new int[Configs$SetBackTriggerType.values().length];

      try {
         a[Configs$SetBackTriggerType.CRASH_PACKETS.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         a[Configs$SetBackTriggerType.SIMULATION.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
