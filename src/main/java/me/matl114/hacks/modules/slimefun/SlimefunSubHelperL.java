package me.matl114.hacks.modules.slimefun;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import me.matl114.hacks.utils.multiblock.BlockMatcher;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.BlockTags;

class SlimefunSubHelperL implements BlockMatcher {
    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public Set<Block> getPotentials() {
        Set<Block> var1 = ((Named<Block>)
                        Registries.BLOCK.getEntryList(BlockTags.FIRE).orElseThrow())
                .stream().map(RegistryEntry::value).collect(Collectors.toCollection(HashSet::new));
        var1.add(Blocks.AIR);
        return var1;
    }

    @Override
    public boolean match(Block b) {
        return b == Blocks.AIR || b.getRegistryEntry().isIn(BlockTags.FIRE);
    }
}
