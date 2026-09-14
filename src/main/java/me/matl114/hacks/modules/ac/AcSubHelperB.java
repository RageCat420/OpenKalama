package me.matl114.hacks.modules.ac;

import me.matl114.hacks.api.ModulePreset;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

// $VF: synthetic class
class AcSubHelperB {
  static int[] b;
  static int[] a;
   static {
      try {
         b[ModulePreset.fg.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         b[ModulePreset.fh.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         b[ModulePreset.fi.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      a = new int[Action.values().length];

      try {
         a[Action.START_DESTROY_BLOCK.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         a[Action.STOP_DESTROY_BLOCK.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
