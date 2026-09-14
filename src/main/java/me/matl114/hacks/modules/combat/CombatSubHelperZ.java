package me.matl114.hacks.modules.combat;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public record CombatSubHelperZ(BlockPos basePos, BlockHitResult basePlaceResult) implements CombatSubHelperR {
   public BlockHitResult basePlaceResult() {
      return this.basePlaceResult;
   }

   @Override
   public BlockPos basePos() {
      return this.basePos;
   }

   public boolean needBase() {
      return true;
   }




   @Override
   public BlockPos getMetadata() { return null; }


   public boolean isMetaEmpty() { return false; }

}
