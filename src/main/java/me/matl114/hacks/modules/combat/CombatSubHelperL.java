package me.matl114.hacks.modules.combat;

import net.minecraft.util.math.BlockPos;

public record CombatSubHelperL(BlockPos basePos) implements CombatSubHelperR {
   @Override
   public BlockPos getMetadata() {
      return this.basePos;
   }

   public boolean needBase() {
      return false;
   }



   @Override
   public boolean isMetaEmpty() { return false; }

}
