package me.matl114.hacks.modules.task;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import me.matl114.hacks.utils.world.BlockStorage;
import me.matl114.hacks.utils.world.ChunkStorage;
import me.matl114.hacks.utils.world.EntityStorage;
import me.matl114.hacks.utils.world.IStorage;
import me.matl114.hacks.utils.world.WorldStorage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class TaskSubHelperJ {
   public static final String d = "chunk-storage";
   public final Map<RegistryKey<World>, Map<BlockPos, BlockStorage>> i;
   public static final Codec<TaskSubHelperJ> g = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.fieldOf("server-name").forGetter(TaskSubHelperJ::t), Codec.INT.fieldOf("data-version").forGetter(meta -> meta.version)
         )
         .apply(instance, TaskSubHelperJ::new)
   );
   public final int version;
   public final Map<RegistryKey<World>, WorldStorage> k;
   public static final String c = "block-storage";
   public final Map<RegistryKey<World>, Map<ChunkPos, ChunkStorage>> j;
   public static final int a = 1;
   public static final Codec<TaskSubHelperJ> h = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.fieldOf("server-name").forGetter(TaskSubHelperJ::t),
            Codec.INT.fieldOf("data-version").forGetter(meta -> meta.version),
            Codec.list(BlockStorage.CODEC).optionalFieldOf("block-storage", List.of()).forGetter(TaskSubHelperJ::g),
            Codec.list(ChunkStorage.CODEC).optionalFieldOf("chunk-storage", List.of()).forGetter(TaskSubHelperJ::h),
            Codec.list(WorldStorage.CODEC).optionalFieldOf("world-storage", List.of()).forGetter(TaskSubHelperJ::i)
         )
         .apply(instance, TaskSubHelperJ::new)
   );
   public final Map<UUID, EntityStorage> l;
   public final String n;
   public static final String e = "world-storage";
   public static final String f = "entity-storage";
   public static final int b = 1;

   public boolean isDirty() {
      return Streams.concat(new Stream[]{this.j().stream(), this.k().stream(), this.l().stream(), this.m().stream()}).anyMatch(IStorage::m);
   }

   public Collection<WorldStorage> l() {
      return this.k.values().stream().toList();
   }

   public ChunkStorage o(RegistryKey<World> world, ChunkPos pos, boolean createIfAbsent) {
      if (createIfAbsent) {
         return this.j.computeIfAbsent(world, TaskSubHelperJ::newMap).computeIfAbsent(pos, k -> new ChunkStorage(world, k));
      } else {
         Map var4 = this.j.get(world);
         return var4 != null ? (ChunkStorage)var4.get(pos) : null;
      }
   }

   public Collection<ChunkStorage> k() {
      return this.j.values().stream().flatMap(s -> s.values().stream()).toList();
   }

   public void f(EntityStorage storage) {
      this.l.put(storage.getUuid(), storage);
   }

   public void loadLegacy(List<BlockStorage> blockStorageList, List<ChunkStorage> chunkStorage, List<WorldStorage> worldStorageList) {
      this.i.clear();
      this.j.clear();
      this.k.clear();
      this.l.clear();

      for (BlockStorage var5 : blockStorageList) {
         this.c(var5);
      }

      for (ChunkStorage var8 : chunkStorage) {
         this.d(var8);
      }

      for (WorldStorage var9 : worldStorageList) {
         this.e(var9);
      }
   }

   public TaskSubHelperJ(String serverName, int version) {
      this.n = serverName;
      this.version = version;
      this.i = new ConcurrentHashMap<>();
      this.j = new ConcurrentHashMap<>();
      this.k = new ConcurrentHashMap<>();
      this.l = new ConcurrentHashMap<>();
   }

   public List<WorldStorage> i() {
      return this.l().stream().filter(IStorage::k).toList();
   }

   public List<BlockStorage> g() {
      return this.j().stream().filter(IStorage::k).toList();
   }

   public void markDirty() {
   }

   public void e(WorldStorage storage) {
      this.k.put(storage.getDimension(), storage);
   }

   private static <T, W, R> ConcurrentHashMap<W, R> newMap(T k) {
      return new ConcurrentHashMap<>();
   }

   public void d(ChunkStorage storage) {
      this.j.computeIfAbsent(storage.getDimension(), TaskSubHelperJ::newMap).put(storage.getChunkPos(), storage);
   }

   public EntityStorage q(UUID uuid, boolean createIfAbsent) {
      return createIfAbsent ? this.l.computeIfAbsent(uuid, EntityStorage::new) : this.l.get(uuid);
   }

   public void c(BlockStorage storage) {
      this.i.computeIfAbsent(storage.getDimension(), TaskSubHelperJ::newMap).put(storage.v(), storage);
   }

   public String t() {
      return this.n;
   }

   public TaskSubHelperJ(
      String serverName, int version, List<BlockStorage> blockStorageList, List<ChunkStorage> chunkStorage, List<WorldStorage> worldStorageList
   ) {
      this(serverName, version);
      this.loadLegacy(blockStorageList, chunkStorage, worldStorageList);
   }

   public TaskSubHelperJ(String serverName) {
      this(serverName, 1);
   }

   public WorldStorage p(RegistryKey<World> world, boolean createIfAbsent) {
      return createIfAbsent ? this.k.computeIfAbsent(world, WorldStorage::new) : this.k.get(world);
   }

   public BlockStorage n(RegistryKey<World> world, BlockPos pos, boolean createIfAbsent) {
      if (createIfAbsent) {
         return this.i.computeIfAbsent(world, TaskSubHelperJ::newMap).computeIfAbsent(pos, k -> new BlockStorage(world, k));
      } else {
         Map var4 = this.i.get(world);
         return var4 != null ? (BlockStorage)var4.get(pos) : null;
      }
   }

   public Collection<EntityStorage> m() {
      return this.l.values().stream().toList();
   }

   public List<ChunkStorage> h() {
      return this.k().stream().filter(IStorage::k).toList();
   }

   public Collection<BlockStorage> j() {
      return this.i.values().stream().flatMap(s -> s.values().stream()).toList();
   }
}
