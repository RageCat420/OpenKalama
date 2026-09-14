package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum NoSlowDown$UseBypassMode implements ConfigEnum {
   NO_BYPASS,
   BYPASS_GRIM_LAZY,
   BYPASS_GRIM_LAZY_V3,
   BYPASS_GRIM_50;

   @Override
   public String getConfigEnumType() {
      return "use_item_noslow_bypass";
   }
}
