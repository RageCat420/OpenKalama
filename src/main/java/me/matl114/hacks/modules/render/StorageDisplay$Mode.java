package me.matl114.hacks.modules.render;

import me.matl114.managers.config.ConfigEnum;

public enum StorageDisplay$Mode implements ConfigEnum {
   MOST,
   ONLY_ONE,
   ALL;

   @Override
   public String getConfigEnumType() {
      return "shulker_storage_display_mode";
   }
}
