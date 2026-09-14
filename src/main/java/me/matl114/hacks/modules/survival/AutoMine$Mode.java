package me.matl114.hacks.modules.survival;

import me.matl114.managers.config.ConfigEnum;

public enum AutoMine$Mode implements ConfigEnum {
   BATCH,
   ACCURATE;

   @Override
   public String getConfigEnumType() {
      return "auto_mine_pathing_mode";
   }
}
