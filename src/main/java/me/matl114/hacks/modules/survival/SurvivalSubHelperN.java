package me.matl114.hacks.modules.survival;

import me.matl114.hacks.api.ModulePreset;

import me.matl114.managers.Configs$MineTargetingMode;

// $VF: synthetic class
class SurvivalSubHelperN {
   private static final int[] $SwitchMap$me$matl114$hacks$api$ModulePreset = new int[Configs$MineTargetingMode.values().length];

   static {
      try {
         $SwitchMap$me$matl114$hacks$api$ModulePreset[Configs$MineTargetingMode.SWING_HAND_AND_ROT.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         $SwitchMap$me$matl114$hacks$api$ModulePreset[Configs$MineTargetingMode.SWING_HAND_AND_TARGET.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
