package me.matl114.hacks.utils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.versioned.api.VNbt;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStorage extends IStorage {
    public Block h;
    public static final Codec<BlockStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC
                            .xmap(Registries.BLOCK::get, Registries.BLOCK::getId)
                            .fieldOf("type")
                            .forGetter(BlockStorage::u),
                    World.CODEC.fieldOf("dim").forGetter(IStorage::getDimension),
                    BlockPos.CODEC.fieldOf("pos").forGetter(BlockStorage::v),
                    Codec.unboundedMap(Codec.STRING, VNbt.a).fieldOf("storage").forGetter(v -> v.b))
            .apply(instance, BlockStorage::new));
    public final BlockPos i;

    public Block u() {
        return this.h;
    }

    public BlockStorage(Block type, RegistryKey<World> dimension, BlockPos pos) {
        this(type, dimension, pos, new ConcurrentHashMap<>());
    }

    public void setType(Block type) {
        if (this.h != type) {
            this.h = type;
            this.dirty = true;
        }
    }

    public BlockStorage(Block type, RegistryKey<World> dimension, BlockPos pos, Map<String, NbtElement> storage) {
        super(dimension, storage);
        this.h = type;
        this.i = pos;
    }

    public BlockPos v() {
        return this.i;
    }

    public BlockStorage(RegistryKey<World> dimension, BlockPos pos) {
        this(Blocks.AIR, dimension, pos);
    }

    public BlockStorage(BlockPos pos) {
        this.i = pos;
        this.h = mc.world.getBlockState(pos).getBlock();
    }
}
