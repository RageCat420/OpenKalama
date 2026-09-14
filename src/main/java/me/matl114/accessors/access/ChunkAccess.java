package me.matl114.accessors.access;

import java.util.Map.Entry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public interface ChunkAccess {
   Iterable<Entry<BlockPos, BlockEntity>> blockEntityEntries();

   static ChunkAccess of(Chunk chunk) {
      return (ChunkAccess)chunk;
   }
}
