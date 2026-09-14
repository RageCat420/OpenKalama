package me.matl114.hacks.modules.survival;

import me.matl114.hacks.api.ModulePreset;

import me.matl114.hooks.impl.baritone.BaritoneLanding;

// $VF: synthetic class
class SurvivalSubHelperB {
   private static final int[] $SwitchMap$me$matl114$hacks$api$ModulePreset = new int[BaritoneLanding.values().length];

   static {
      try {
         $SwitchMap$me$matl114$hacks$api$ModulePreset[BaritoneLanding.EMERGENCY.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         $SwitchMap$me$matl114$hacks$api$ModulePreset[BaritoneLanding.PATH_COMPLETE.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
