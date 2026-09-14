package me.matl114.hacks.modules.survival;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

public class SurvivalSubHelperI {
    boolean dirty;
    BiConsumer<BlockPos, BlockEntity> e;
    final BlockEntityType<?> b;
    long lastUpdatedMs;
    NbtCompound c = new NbtCompound();
    public static final Codec<SurvivalSubHelperI> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Registries.BLOCK_ENTITY_TYPE
                            .getCodec()
                            .fieldOf("block-type")
                            .forGetter(SurvivalSubHelperI::f),
                    Codec.LONG.fieldOf("timestamp").forGetter(SurvivalSubHelperI::getLastUpdatedMs),
                    NbtCompound.CODEC.optionalFieldOf("data", new NbtCompound()).forGetter(SurvivalSubHelperI::g))
            .apply(instance, SurvivalSubHelperI::new));

    public void b() {
        this.lastUpdatedMs = System.currentTimeMillis();
    }

    public NbtCompound g() {
        return this.c;
    }

    public long getLastUpdatedMs() {
        return this.lastUpdatedMs;
    }

    public BiConsumer<BlockPos, BlockEntity> i() {
        return this.e;
    }

    public void a() {
        this.dirty = true;
    }

    public boolean h() {
        return this.dirty;
    }

    public SurvivalSubHelperI(BlockEntityType<?> type, long lastUpdatedMs, NbtCompound dataContainer) {
        this.dirty = false;
        this.b = type;
        this.lastUpdatedMs = lastUpdatedMs;
        this.c = dataContainer.copy();
    }

    public BlockEntityType<?> f() {
        return this.b;
    }

    public void setUpdateCallback(BiConsumer<BlockPos, BlockEntity> updateCallback) {
        this.e = updateCallback;
    }

    public void update(BlockPos pos, BlockEntity blockEntity) {
        this.b();
        if (this.e != null) {
            this.e.accept(pos, blockEntity);
        }
    }

    public boolean isEmpty() {
        return this.c.isEmpty();
    }

    public SurvivalSubHelperI(BlockEntityType<?> type) {
        this.dirty = false;
        this.b = type;
        this.lastUpdatedMs = System.currentTimeMillis();
    }
}
