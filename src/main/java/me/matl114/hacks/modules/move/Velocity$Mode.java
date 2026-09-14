package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum Velocity$Mode implements ConfigEnum {
   NONE,
   GRIM_LEGACY_GROUND,
   FREEZE,
   GRIM_NEW_GROUND;

   @Override
   public String getConfigEnumType() {
      return "velocity_bypass_mode";
   }
}
