package me.matl114.hacks.modules.ac;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

// $VF: synthetic class
class AcSubHelperD {
  static int[] b;
  static int[] a;
   static {
      try {
         b[Mode.START_SPRINTING.ordinal()] = 1;
      } catch (NoSuchFieldError var10) {
      }

      try {
         b[Mode.STOP_SPRINTING.ordinal()] = 2;
      } catch (NoSuchFieldError var9) {
      }

      try {
         b[Mode.START_FALL_FLYING.ordinal()] = 3;
      } catch (NoSuchFieldError var8) {
      }

      a = new int[Action.values().length];

      try {
         a[Action.SWAP_ITEM_WITH_OFFHAND.ordinal()] = 1;
      } catch (NoSuchFieldError var7) {
      }

      try {
         a[Action.DROP_ITEM.ordinal()] = 2;
      } catch (NoSuchFieldError var6) {
      }

      try {
         a[Action.DROP_ALL_ITEMS.ordinal()] = 3;
      } catch (NoSuchFieldError var5) {
      }

      try {
         a[Action.RELEASE_USE_ITEM.ordinal()] = 4;
      } catch (NoSuchFieldError var4) {
      }

      try {
         a[Action.STOP_DESTROY_BLOCK.ordinal()] = 5;
      } catch (NoSuchFieldError var3) {
      }

      try {
         a[Action.ABORT_DESTROY_BLOCK.ordinal()] = 6;
      } catch (NoSuchFieldError var2) {
      }

      try {
         a[Action.START_DESTROY_BLOCK.ordinal()] = 7;
      } catch (NoSuchFieldError var1) {
      }
   }
}
