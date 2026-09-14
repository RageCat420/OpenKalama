package me.matl114.hacks.utils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.versioned.api.VNbt;
import me.matl114.versioned.impl.VCodec;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ChunkStorage extends IStorage {
    public final ChunkPos chunkPos;
    public static final Codec<ChunkStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    World.CODEC.fieldOf("dim").forGetter(IStorage::getDimension),
                    VCodec.CHUNK_POS_CODEC.fieldOf("chunk-pos").forGetter(ChunkStorage::getChunkPos),
                    Codec.unboundedMap(Codec.STRING, VNbt.a).fieldOf("storage").forGetter(v -> v.b))
            .apply(instance, ChunkStorage::new));

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public ChunkStorage(RegistryKey<World> dimension, ChunkPos chunkPos, Map<String, NbtElement> storage) {
        super(dimension, storage);
        this.chunkPos = chunkPos;
    }

    public ChunkStorage(ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    public ChunkStorage(RegistryKey<World> dimension, ChunkPos chunkPos) {
        this(dimension, chunkPos, new ConcurrentHashMap<>());
    }
}
