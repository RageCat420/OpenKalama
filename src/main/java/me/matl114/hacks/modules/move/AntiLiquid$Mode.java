package me.matl114.hacks.modules.move;

import me.matl114.managers.config.ConfigEnum;

public enum AntiLiquid$Mode implements ConfigEnum {
   NONE(false, false),
   ANTI_WATER(true, false),
   ANTI_LAVA(false, true),
   ALL(true, true);

   final boolean antiWater;
   final boolean antiLava;

   @Override
   public String getConfigEnumType() {
      return "anti_liquid_anti_liquid_mode";
   }

   public boolean isAntiWater() {
      return this.antiWater;
   }

   public boolean isAntiLava() {
      return this.antiLava;
   }

   private AntiLiquid$Mode(final boolean antiWater, final boolean antiLava) {
      this.antiWater = antiWater;
      this.antiLava = antiLava;
   }
}
