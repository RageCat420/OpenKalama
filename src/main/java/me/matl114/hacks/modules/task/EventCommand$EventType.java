package me.matl114.hacks.modules.task;

import me.matl114.managers.config.ConfigEnum;

public enum EventCommand$EventType implements ConfigEnum {
   NONE,
   RESPAWN,
   WORLD_CHANGE,
   JOIN_GAME,
   TRIGGER_TOTEM;

   @Override
   public String getConfigEnumType() {
      return "event_command_event_type";
   }
}
