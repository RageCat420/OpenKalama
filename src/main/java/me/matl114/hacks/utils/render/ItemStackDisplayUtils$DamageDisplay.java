package me.matl114.hacks.utils.render;

import me.matl114.managers.config.ConfigEnum;

public enum ItemStackDisplayUtils$DamageDisplay implements ConfigEnum {
   NONE,
   DAMAGE_LEFT,
   DAMAGE,
   PERCENTAGE;

   @Override
   public String getConfigEnumType() {
      return "equipment_hud_damage_display_type";
   }
}
