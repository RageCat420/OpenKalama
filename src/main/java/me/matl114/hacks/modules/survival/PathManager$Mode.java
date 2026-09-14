package me.matl114.hacks.modules.survival;

import me.matl114.managers.config.ConfigEnum;

public enum PathManager$Mode implements ConfigEnum {
   BARITONE,
   BARITONE_GOAL,
   ELYTRA_FLIGHT;

   @Override
   public String getConfigEnumType() {
      return "path_manager_flight_mode";
   }
}
