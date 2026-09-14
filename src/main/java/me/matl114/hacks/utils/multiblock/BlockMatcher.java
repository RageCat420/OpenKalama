package me.matl114.hacks.utils.multiblock;

import java.util.Set;
import net.minecraft.block.Block;

public interface BlockMatcher {
    BlockMatcher Wg = new HackUtilHelperA();
    BlockMatcher Wf = new HackUtilHelperD();

    default boolean match(Block b) {
        return this.getPotentials().contains(b);
    }

    Set<Block> getPotentials();
}
