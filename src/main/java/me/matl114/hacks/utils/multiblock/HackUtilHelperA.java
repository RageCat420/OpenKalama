package me.matl114.hacks.utils.multiblock;

import java.util.Set;
import net.minecraft.block.Block;

class HackUtilHelperA implements BlockMatcher {
   @Override
   public boolean match(Block b) {
      return false;
   }

   @Override
   public Set<Block> getPotentials() {
      return Set.of();
   }
}
