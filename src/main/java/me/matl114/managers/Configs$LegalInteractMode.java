package me.matl114.managers;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.config.ConfigEnum;

public enum Configs$LegalInteractMode implements ConfigEnum {
   NONE,
   MOVEMENT_POST,
   DELAY_MOVEMENT,
   USEITEM_PACKET,
   LEGACY_SLIENT_ROT;

   public static Configs$LegalInteractMode getFromPreset(ModulePreset preset) {
      return switch (preset) {
         case fd, fe -> NONE;
         case fh -> LEGACY_SLIENT_ROT;
         default -> DELAY_MOVEMENT;
      };
   }

   @Override
   public String getConfigEnumType() {
      return "legal_interact_mode";
   }

   public boolean isLegal() {
      return this != NONE;
   }

   public boolean canMultiRotPlace() {
      return this == NONE || this == LEGACY_SLIENT_ROT;
   }
}
