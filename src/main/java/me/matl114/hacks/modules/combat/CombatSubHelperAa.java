package me.matl114.hacks.modules.combat;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.math.BlockPos;

record CombatSubHelperAa(
        Set<BlockPos> mineableBlastResistantPositions,
        Set<BlockPos> unbreakableBlastResistantPositions,
        Set<BlockPos> respawnAnchorPositions,
        Set<BlockPos> holesPositions) {
    public Set<BlockPos> unbreakableBlastResistantPositions() {
        return this.unbreakableBlastResistantPositions;
    }

    public Set<BlockPos> respawnAnchorPositions() {
        return this.respawnAnchorPositions;
    }

    public Set<BlockPos> holesPositions() {
        return this.holesPositions;
    }

    public static CombatSubHelperAa amo() {
        return new CombatSubHelperAa(
                ConcurrentHashMap.newKeySet(),
                ConcurrentHashMap.newKeySet(),
                ConcurrentHashMap.newKeySet(),
                ConcurrentHashMap.newKeySet());
    }

    public Set<BlockPos> mineableBlastResistantPositions() {
        return this.mineableBlastResistantPositions;
    }
}
