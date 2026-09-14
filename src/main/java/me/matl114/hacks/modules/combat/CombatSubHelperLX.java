package me.matl114.hacks.modules.combat;

import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;

public class CombatSubHelperLX {
   Map<PlayerEntity, Double> damageCache;
   int powerLevel;

   public CombatSubHelperLX() {
   }

   public CombatSubHelperLX(int powerLevel, Map<PlayerEntity, Double> damageCache) {
      this.powerLevel = powerLevel;
      this.damageCache = damageCache;
   }
}
