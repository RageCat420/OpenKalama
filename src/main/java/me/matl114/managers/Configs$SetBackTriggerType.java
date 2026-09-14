package me.matl114.managers;

import me.matl114.managers.config.ConfigEnum;

public enum Configs$SetBackTriggerType implements ConfigEnum {
   SIMULATION,
   CRASH_PACKETS;

   @Override
   public String getConfigEnumType() {
      return "setback_trigger_type";
   }
}
