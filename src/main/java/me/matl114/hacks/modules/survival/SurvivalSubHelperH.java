package me.matl114.hacks.modules.survival;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Uuids;

public class SurvivalSubHelperH {
    NbtCompound d;
    boolean dirty;
    long lastUpdatedMs;
    public static final Codec<SurvivalSubHelperH> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(SurvivalSubHelperH::e),
                    Uuids.INT_STREAM_CODEC.optionalFieldOf("owner").forGetter(SurvivalSubHelperH::f),
                    Codec.LONG.optionalFieldOf("timestamp", 0L).forGetter(SurvivalSubHelperH::getLastUpdatedMs),
                    NbtCompound.CODEC.optionalFieldOf("data", new NbtCompound()).forGetter(SurvivalSubHelperH::h))
            .apply(instance, SurvivalSubHelperH::new));
    final UUID a;
    public Consumer<LivingEntity> g;
    Optional<UUID> b = Optional.empty();

    public Optional<UUID> f() {
        return this.b;
    }

    public boolean i() {
        return this.dirty;
    }

    public SurvivalSubHelperH(UUID self, Optional<UUID> owner, long lastUpdatedMs, NbtCompound dataContainer) {
        this.d = new NbtCompound();
        this.dirty = false;
        this.a = self;
        this.b = owner;
        this.lastUpdatedMs = lastUpdatedMs;
        this.d = dataContainer.copy();
    }

    public void k(Consumer<LivingEntity> updateCallback) {
        this.g = updateCallback;
    }

    public NbtCompound h() {
        return this.d;
    }

    public Consumer<LivingEntity> j() {
        return this.g;
    }

    public UUID e() {
        return this.a;
    }

    public long getLastUpdatedMs() {
        return this.lastUpdatedMs;
    }

    public void c(LivingEntity entity) {
        this.b();
        if (this.g != null) {
            this.g.accept(entity);
        }
    }

    public SurvivalSubHelperH(UUID uid) {
        this.d = new NbtCompound();
        this.dirty = false;
        this.a = uid;
        this.lastUpdatedMs = System.currentTimeMillis();
    }

    public boolean isEmpty() {
        return this.b.isEmpty() && this.d.isEmpty();
    }

    public void b() {
        this.lastUpdatedMs = System.currentTimeMillis();
    }

    public void a() {
        this.dirty = true;
    }
}
