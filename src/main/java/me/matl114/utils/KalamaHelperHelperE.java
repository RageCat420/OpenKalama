package me.matl114.utils;

import net.minecraft.util.math.ChunkPos;

public record KalamaHelperHelperE(ChunkPos pos) implements KalamaHelperHelperAd {
    public ChunkPos Im() {
        return this.pos;
    }

    @Override
    public String type() {
        return "Chunk";
    }
}
