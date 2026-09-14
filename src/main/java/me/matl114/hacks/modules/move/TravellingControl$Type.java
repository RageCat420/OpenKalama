package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum TravellingControl$Type implements ConfigEnum {
   ELYTRASKY,
   ELYTRA_PITCH40,
   ELYTRA_GRIM_FLY40,
   MOV_VOID,
   MOV_VOID_2,
   PEARL,
   TEST;

   @Override
   public String getConfigEnumType() {
      return "travel_control_type";
   }
}
