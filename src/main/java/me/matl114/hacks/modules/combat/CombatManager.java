package me.matl114.hacks.modules.combat;

import com.google.common.base.Predicates;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.channels.EventChannel;
import me.matl114.events.impl.KalamaHelperHelperD;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.utils.algorithms.SerialExecutor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;

public class CombatManager extends BaseModule {
   private final Executor Kp;
   private Set<ChunkSectionPos> Kv;
   private static final float BLAST_RESISTANCE_THRESHOLD = 600.0F;
   private static final EventChannel<CombatSubHelperMX> Ko;
   public final ModulePath sf = makePath(Configs.k, "attack");
   public static CombatManager INSTANCE;
   public volatile Set<BlockPos> Ku;
   private Map<ChunkSectionPos, CombatSubHelperAa> Kq;
   public static final int SECTION_RADIUS = 1;
   public final Set<EndCrystalEntity> Kx;
   public volatile Map<BlockPos, BlockState> Kt;
   private ChunkSectionPos Kw;
   private static final Set<Block> Km;
   public volatile Map<BlockPos, BlockState> Kr;
   private volatile CombatSubHelperMX Ky;
   private static final Set<Block> Kn;
   public volatile Map<BlockPos, BlockState> Ks;

   public void onUpdatePlayerPosition() {
      ChunkSectionPos var1 = ChunkSectionPos.from(mc.player);
      this.Kq
         .entrySet()
         .removeIf(
            re -> Math.abs(re.getKey().getX() - var1.getX()) > 1
               || Math.abs(re.getKey().getZ() - var1.getZ()) > 1
               || Math.abs(re.getKey().getY() - var1.getY()) > 1
         );
      this.Kv.removeIf(re -> Math.abs(re.getX() - var1.getX()) > 1 || Math.abs(re.getZ() - var1.getZ()) > 1 || Math.abs(re.getY() - var1.getY()) > 1);

      for (int var2 = -1; var2 <= 1; var2++) {
         for (int var3 = -1; var3 <= 1; var3++) {
            for (int var4 = -1; var4 <= 1; var4++) {
               ChunkSectionPos var5 = ChunkSectionPos.from(var1.getX() + var2, var1.getY() + var3, var1.getZ() + var4);
               if (!this.Kq.containsKey(var5)) {
                  this.Kv.add(var5);
               }
            }
         }
      }

      this.Kw = var1;
   }

   public void onPosUpdate(BlockPos pos, BlockState state) {
      ChunkSectionPos var3 = ChunkSectionPos.from(pos);
      if (this.isTrackedSection(var3)) {
         CombatSubHelperAa var4 = this.Kq.get(var3);
         if (var4 != null) {
            Block var5 = state.getBlock();
            if (this.Ky.c() && Km.contains(var5)) {
               var4.unbreakableBlastResistantPositions().add(pos);
            } else {
               var4.unbreakableBlastResistantPositions().remove(pos);
            }

            if (this.Ky.c() && Kn.contains(var5)) {
               var4.holesPositions().add(pos);
            } else {
               var4.holesPositions().remove(pos);
            }

            if (this.Ky.d() && var5 instanceof RespawnAnchorBlock) {
               var4.mineableBlastResistantPositions().add(pos);
            } else {
               var4.mineableBlastResistantPositions().remove(pos);
            }
         } else {
            this.Kv.add(var3);
         }
      }

      if (this.Ky.e()) {
         this.updateHoleCandidates(pos);
      }
   }

   public void y(Event<World> event) {
      this.Zs();
   }

   public static EventChannel<CombatSubHelperMX> getRequestEnableEvent() {
      return Ko;
   }

   private void updateHoleCandidates(BlockPos pos) {
      this.updateHoleState(pos);
      this.updateHoleState(pos.north());
      this.updateHoleState(pos.south());
      this.updateHoleState(pos.west());
      this.updateHoleState(pos.east());
   }

   public CombatManager() {
      super("CombatManager");
      this.Kp = new SerialExecutor(CompletableFuture::runAsync);
      this.Kq = new ConcurrentHashMap<>();
      this.Kr = new ConcurrentHashMap<>();
      this.Ks = new ConcurrentHashMap<>();
      this.Kt = new ConcurrentHashMap<>();
      this.Ku = ConcurrentHashMap.newKeySet();
      this.Kv = new HashSet<>();
      this.Kw = ChunkSectionPos.from(0, 0, 0);
      this.Kx = new HashSet<>();
      this.Ky = new CombatSubHelperMX();
      INSTANCE = this;
   }

   public void tI(Event<Void> event) {
      this.Zs();
   }

   private void Zr() {
      this.Kr.clear();
      this.Kt.clear();
      this.Kx.clear();
      this.Ks.clear();
      this.Ku.clear();
   }

   static {
      LinkedHashSet var0 = new LinkedHashSet();

      for (Block var2 : Registries.BLOCK) {
         if (var2.getBlastResistance() >= 600.0F && var2.getHardness() >= 0.0F) {
            var0.add(var2);
         }
      }

      Km = Set.copyOf(var0);
      var0 = new LinkedHashSet();

      for (Block var12 : Registries.BLOCK) {
         if (var12.getBlastResistance() >= 600.0F && var12.getHardness() < 0.0F) {
            var0.add(var12);
         }
      }

      Kn = Set.copyOf(var0);
      Ko = new EventChannel<>();
   }

   public void onChunkData(Event<ChunkPos> event) {
      if (!checkNull()) {
         ChunkPos var2 = (ChunkPos)event.e();
         this.scheduleDirtyChunks(var2.x, var2.z);
      }
   }

   public void onPreTick(Event<ClientPlayerEntity> event) {
      if (!checkNull()) {
         CombatSubHelperMX var2 = this.Ky;
         this.Ky = new CombatSubHelperMX();
         Ko.broadcast(this.Ky);
         if (!Objects.equals(this.Ky, var2)) {
            this.Kv.addAll(this.Kq.keySet());
         }

         if (this.Ky.a()) {
            this.Zs();
         } else {
            this.onUpdatePlayerPosition();
            CombatSubHelperMX var3 = this.Ky;
            this.Zt(var3);
            this.updateTrackedMaps(this.Kq, false, var3);
            Set<ChunkSectionPos> var4 = this.Kv;
            this.Kv = new HashSet<>();
            ClientWorld var5 = mc.world;
            ConcurrentHashMap var6 = new ConcurrentHashMap();
            this.Kp.execute(() -> {
               for (ChunkSectionPos var6x : var4) {
                  var6.put(var6x, this.scanSection(var5, var6x, var3));
               }

               this.Kq.putAll(var6);
               if (Objects.equals(this.Ky, var3)) {
                  this.updateTrackedMaps(this.Kq, true, var3);
               }
            });
            if (var3.d()) {
               this.updateTrackedEntities();
            } else {
               this.Kx.clear();
            }
         }
      }
   }

   private void Zs() {
      this.Zr();
      this.Kq = new ConcurrentHashMap<>();
   }

   private boolean isTrackedSection(ChunkSectionPos sectionPos) {
      return Math.abs(sectionPos.getX() - this.Kw.getX()) <= 1
         && Math.abs(sectionPos.getZ() - this.Kw.getZ()) <= 1
         && Math.abs(sectionPos.getY() - this.Kw.getY()) <= 1;
   }

   private void Zt(CombatSubHelperMX service) {
      if (!service.c()) {
         this.Kr.clear();
         this.Ks.clear();
      }

      if (!service.d()) {
         this.Kt.clear();
         this.Kx.clear();
      }

      if (!service.e()) {
         this.Ku.clear();
      }
   }

   private void updateHoleState(BlockPos pos) {
      ChunkSectionPos var2 = ChunkSectionPos.from(pos);
      if (this.isTrackedSection(var2)) {
         CombatSubHelperAa var3 = this.Kq.get(var2);
         if (var3 == null) {
            this.Kv.add(var2);
         } else {
            if (this.isHole(mc.world, pos)) {
               var3.respawnAnchorPositions().add(pos);
            } else {
               var3.respawnAnchorPositions().remove(pos);
            }
         }
      }
   }

   private CombatSubHelperAa scanSection(ClientWorld world, ChunkSectionPos key, CombatSubHelperMX service) {
      WorldChunk var4 = world.getChunkManager().getWorldChunk(key.getX(), key.getZ());
      if (var4 == null) {
         return CombatSubHelperAa.amo();
      } else {
         int var5 = key.getY() - world.getBottomSectionCoord();
         ChunkSection[] var6 = var4.getSectionArray();
         if (var5 >= 0 && var5 < var6.length) {
            ChunkSection var7 = var6[var5];
            if (var7 != null && !var7.isEmpty()) {
               PalettedContainer var8 = var7.getBlockStateContainer();
               KeySetView var9 = ConcurrentHashMap.newKeySet();
               KeySetView var10 = ConcurrentHashMap.newKeySet();
               KeySetView var11 = ConcurrentHashMap.newKeySet();
               KeySetView var12 = ConcurrentHashMap.newKeySet();
               int var13 = key.getX() << 4;
               int var14 = key.getY() << 4;
               int var15 = key.getZ() << 4;

               for (int var16 = 0; var16 < 16; var16++) {
                  for (int var17 = 0; var17 < 16; var17++) {
                     for (int var18 = 0; var18 < 16; var18++) {
                        int var19 = var18 | var17 << 4 | var16 << 8;
                        BlockState var20 = (BlockState)var8.get(var19);
                        BlockPos var21 = new BlockPos(var13 + var18, var14 + var16, var15 + var17);
                        Block var22 = var20.getBlock();
                        if (service.c() && Km.contains(var22)) {
                           var9.add(var21);
                        }

                        if (service.c() && Kn.contains(var22)) {
                           var10.add(var21);
                        }

                        if (service.d() && var22 instanceof RespawnAnchorBlock) {
                           var11.add(var21);
                        }

                        if (service.e() && this.isHole(world, var21)) {
                           var12.add(var21);
                        }
                     }
                  }
               }

               return new CombatSubHelperAa(var9, var10, var11, var12);
            } else {
               return CombatSubHelperAa.amo();
            }
         } else {
            return CombatSubHelperAa.amo();
         }
      }
   }

   private void scheduleDirtyChunks(int chunkX, int chunkZ) {
      if (Math.abs(chunkX - this.Kw.getX()) <= 1 && Math.abs(chunkZ - this.Kw.getZ()) <= 1) {
         for (int var3 = -1; var3 <= 1; var3++) {
            this.Kv.add(ChunkSectionPos.from(chunkX, this.Kw.getY() + var3, chunkZ));
         }
      }
   }

   private synchronized void updateTrackedMaps(Map<ChunkSectionPos, CombatSubHelperAa> updateMap, boolean trust, CombatSubHelperMX service) {
      ConcurrentHashMap var4 = new ConcurrentHashMap();
      ConcurrentHashMap var5 = new ConcurrentHashMap();
      ConcurrentHashMap var6 = new ConcurrentHashMap();
      KeySetView var7 = ConcurrentHashMap.newKeySet();

      for (CombatSubHelperAa var9 : updateMap.values()) {
         if (service.c()) {
            for (BlockPos var11 : var9.unbreakableBlastResistantPositions()) {
               BlockState var12 = mc.world.getBlockState(var11);
               if (trust || Km.contains(var12.getBlock())) {
                  var4.put(var11, var12);
               }
            }

            for (BlockPos var16 : var9.holesPositions()) {
               BlockState var19 = mc.world.getBlockState(var16);
               if (trust || Kn.contains(var19.getBlock())) {
                  var6.put(var16, var19);
               }
            }
         }

         if (service.d()) {
            for (BlockPos var17 : var9.mineableBlastResistantPositions()) {
               BlockState var20 = mc.world.getBlockState(var17);
               if (trust || var20.getBlock() instanceof RespawnAnchorBlock) {
                  var5.put(var17, var20);
               }
            }
         }

         if (service.e()) {
            for (BlockPos var18 : var9.respawnAnchorPositions()) {
               if (trust || this.isHole(mc.world, var18)) {
                  var7.add(var18);
               }
            }
         }
      }

      this.Kr = var4;
      this.Ks = var6;
      this.Kt = var5;
      this.Ku = var7;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.U(), this::onPreTick);
      this.registerListener(Listener.N(), this::y);
      this.registerListener(Listener.O(), this::tI);
      this.registerListener(Listener.aW(), this::onBlockUpdate);
      this.registerListener(Listener.aX(), this::onChunkData);
   }

   public void updateTrackedEntities() {
      BlockPos var1 = this.Kw.getMinPos();
      Box var2 = new Box(var1.getX() - 16, var1.getY() - 16, var1.getZ() - 16, var1.getX() + 32, var1.getY() + 32, var1.getZ() + 32);
      this.Kx.clear();
      this.Kx.addAll(mc.world.getEntitiesByType(EntityType.END_CRYSTAL, var2, Predicates.alwaysTrue()));
   }

   private boolean isHole(ClientWorld world, BlockPos pos) {
      return !world.getBlockState(pos).isAir()
         ? false
         : !world.getBlockState(pos.north()).isAir()
            && !world.getBlockState(pos.south()).isAir()
            && !world.getBlockState(pos.west()).isAir()
            && !world.getBlockState(pos.east()).isAir();
   }

   public void onBlockUpdate(Event<KalamaHelperHelperD> event) {
      if (mc.world != null && mc.player != null) {
         this.onPosUpdate(((KalamaHelperHelperD)event.b).pos(), ((KalamaHelperHelperD)event.b).oldState());
      }
   }
}
