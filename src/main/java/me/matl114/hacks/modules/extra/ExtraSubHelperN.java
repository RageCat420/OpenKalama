package me.matl114.hacks.modules.extra;

import net.minecraft.client.network.ServerInfo.Status;

// $VF: synthetic class
class ExtraSubHelperN {
   private static final int[] $SwitchMap$net$minecraft$client$network$ServerInfo$Status = new int[Status.values().length];

   static {
      try {
         $SwitchMap$net$minecraft$client$network$ServerInfo$Status[Status.INITIAL.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         $SwitchMap$net$minecraft$client$network$ServerInfo$Status[Status.PINGING.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         $SwitchMap$net$minecraft$client$network$ServerInfo$Status[Status.UNREACHABLE.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         $SwitchMap$net$minecraft$client$network$ServerInfo$Status[Status.SUCCESSFUL.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      try {
         $SwitchMap$net$minecraft$client$network$ServerInfo$Status[Status.INCOMPATIBLE.ordinal()] = 5;
      } catch (NoSuchFieldError var1) {
      }
   }
}
