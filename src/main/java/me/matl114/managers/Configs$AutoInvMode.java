package me.matl114.managers;

import me.matl114.managers.config.ConfigEnum;

public enum Configs$AutoInvMode implements ConfigEnum {
   LAZY,
   TICK;

   @Override
   public String getConfigEnumType() {
      return "auto_inv_mode";
   }
}
