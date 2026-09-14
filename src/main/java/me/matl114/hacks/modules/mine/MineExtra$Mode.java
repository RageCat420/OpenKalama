package me.matl114.hacks.modules.mine;

import me.matl114.managers.config.ConfigEnum;

public enum MineExtra$Mode implements ConfigEnum {
   NO_BYPASS,
   BYPASS_GRIM_LEGIT,
   BYPASS_GRIM_BAD_PACKETS;

   public boolean hasAc() {
      return this != NO_BYPASS;
   }

   @Override
   public String getConfigEnumType() {
      return "fast_break_bypass_mode";
   }
}
