package me.matl114.utils.world;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager.ClientChunkMap;
import net.minecraft.world.chunk.Chunk;

public class ChunkIterator implements Iterator<Chunk> {
    private Chunk H;
    private final int J;
    private int L;
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final int I;
    private final int K;
    private final ClientChunkMap F = mc.world.getChunkManager().chunks;
    private final boolean onlyWithLoadedNeighbours;
    private int M;

    @Override
    public boolean hasNext() {
        return this.H != null;
    }

    public Chunk e() {
        if (this.H == null) {
            throw new NoSuchElementException();
        } else {
            return this.getNext();
        }
    }

    private boolean isInRadius(Chunk chunk) {
        int var2 = chunk.getPos().x;
        int var3 = chunk.getPos().z;
        return mc.world.getChunkManager().isChunkLoaded(var2 + 1, var3)
                && mc.world.getChunkManager().isChunkLoaded(var2 - 1, var3)
                && mc.world.getChunkManager().isChunkLoaded(var2, var3 + 1)
                && mc.world.getChunkManager().isChunkLoaded(var2, var3 - 1);
    }

    public ChunkIterator(boolean onlyWithLoadedNeighbours) {
        this.onlyWithLoadedNeighbours = onlyWithLoadedNeighbours;
        int var2 = Math.min(this.F.radius, Math.max(2, mc.options.getClampedViewDistance()) + 3);
        int var3 = this.F.centerChunkX;
        int var4 = this.F.centerChunkZ;
        this.I = var3 - var2;
        this.J = var3 + var2;
        int var5 = var4 - var2;
        this.K = var4 + var2;
        this.L = this.I;
        this.M = var5;
        this.getNext();
    }

    private Chunk getNext() {
        Chunk var1 = this.H;

        for (this.H = null; this.M <= this.K; this.L = this.I) {
            while (this.L <= this.J) {
                int var2 = this.F.getIndex(this.L++, this.M);
                this.H = (Chunk) (Object) this.F.chunks.get(var2);
                if (this.H != null && (!this.onlyWithLoadedNeighbours || this.isInRadius(this.H))) {
                    return var1;
                }
            }

            this.M++;
        }

        return var1;
    }

    @Override
    public Chunk next() {
        return this.e();
    }
}
