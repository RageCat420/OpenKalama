package me.matl114.hacks.utils.multiblock;

import java.util.Set;
import net.minecraft.block.Block;

class HackUtilHelperD implements BlockMatcher {
    @Override
    public boolean match(Block b) {
        return true;
    }

    @Override
    public Set<Block> getPotentials() {
        return Set.of();
    }
}
