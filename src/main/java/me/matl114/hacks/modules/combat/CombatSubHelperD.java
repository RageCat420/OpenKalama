package me.matl114.hacks.modules.combat;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs$LegalInteractMode;

// $VF: synthetic class
class CombatSubHelperD {
  static int[] b;
  static int[] a;
   static {
      try {
         b[ModulePreset.fd.ordinal()] = 1;
      } catch (NoSuchFieldError var7) {
      }

      try {
         b[ModulePreset.fe.ordinal()] = 2;
      } catch (NoSuchFieldError var6) {
      }

      try {
         b[ModulePreset.fh.ordinal()] = 3;
      } catch (NoSuchFieldError var5) {
      }

      a = new int[Configs$LegalInteractMode.values().length];

      try {
         a[Configs$LegalInteractMode.LEGACY_SLIENT_ROT.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         a[Configs$LegalInteractMode.DELAY_MOVEMENT.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         a[Configs$LegalInteractMode.MOVEMENT_POST.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      try {
         a[Configs$LegalInteractMode.USEITEM_PACKET.ordinal()] = 4;
      } catch (NoSuchFieldError var1) {
      }
   }
}
