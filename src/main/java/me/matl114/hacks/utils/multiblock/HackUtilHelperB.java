package me.matl114.hacks.utils.multiblock;

import java.util.Set;
import net.minecraft.block.Block;

public record HackUtilHelperB(Block block) implements BlockMatcher {
   public Block GS() {
      return this.block;
   }

   @Override
   public Set<Block> getPotentials() {
      return Set.of(this.block);
   }

   @Override
   public boolean match(Block b) {
      return b == this.block;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof HackUtilHelperB && this.block == ((HackUtilHelperB)o).block;
   }
}
