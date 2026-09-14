package me.matl114.hacks.modules.combat;

import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public record CombatSubHelperJX(BlockState state, Map<PlayerEntity, Double> damageCache) {
    public BlockState state() {
        return this.state;
    }

    public Map<PlayerEntity, Double> damageCache() {
        return this.damageCache;
    }
}
