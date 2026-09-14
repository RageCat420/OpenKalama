package me.matl114.managers;

import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.config.ConfigEnum;

public enum Configs$LegalTargetingMode implements ConfigEnum {
   NONE,
   DELAY_MOVEMENT,
   LEGACY_SLIENT_ROT;

   public boolean isMovement() {
      return this == DELAY_MOVEMENT;
   }

   public boolean isLegal() {
      return this != NONE;
   }

   public boolean isLegacy() {
      return this == LEGACY_SLIENT_ROT;
   }

   public static Configs$LegalTargetingMode getFromPreset(ModulePreset preset) {
      return switch (preset) {
         case fd, fe -> NONE;
         case fh -> LEGACY_SLIENT_ROT;
         default -> DELAY_MOVEMENT;
      };
   }

   @Override
   public String getConfigEnumType() {
      return "legal_targeting_mode";
   }
}
