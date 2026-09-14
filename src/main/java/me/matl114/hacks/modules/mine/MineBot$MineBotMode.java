package me.matl114.hacks.modules.mine;

import me.matl114.managers.config.ConfigEnum;

public enum MineBot$MineBotMode implements ConfigEnum {
   SPHERICAL,
   LAYERED_UP,
   LAYERED_DOWN,
   SQUARE,
   TUNNEL,
   RANDOM,
   AUTO_TOOL;

   @Override
   public String getConfigEnumType() {
      return "mine_bot_mode";
   }
}
