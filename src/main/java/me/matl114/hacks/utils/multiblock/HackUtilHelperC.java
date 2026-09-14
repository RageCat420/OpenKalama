package me.matl114.hacks.utils.multiblock;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.TagKey;

public record HackUtilHelperC(TagKey<Block> blockTagKey) implements BlockMatcher {
   public TagKey<Block> Lf() {
      return this.blockTagKey;
   }

   @Override
   public Set<Block> getPotentials() {
      return ((Named)Registries.BLOCK.getEntryList(this.blockTagKey).orElseThrow())
         .stream()
         .<Block>map(RegistryEntry::value)
         .collect(Collectors.toUnmodifiableSet());
   }

   @Override
   public boolean match(Block b) {
      return b.getRegistryEntry().isIn(this.blockTagKey);
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof HackUtilHelperC && ((HackUtilHelperC)o).blockTagKey == this.blockTagKey;
   }
}
