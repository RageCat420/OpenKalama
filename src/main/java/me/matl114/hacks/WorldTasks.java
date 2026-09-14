package me.matl114.hacks;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.catchers.PacketCatcher;
import me.matl114.events.impl.KalamaHelperHelperD;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

public class WorldTasks {
   static int tickCounter = 0;
   public static Map<ChunkPos, CompletableFuture<Void>> a = new ConcurrentHashMap<>();
   private static final Executor d = Executors.newSingleThreadExecutor();
   private static final MinecraftClient b = MinecraftClient.getInstance();
   private static final ExecutorService c = new ThreadPoolExecutor(
      Runtime.getRuntime().availableProcessors() / 2,
      Runtime.getRuntime().availableProcessors() / 2,
      60L,
      TimeUnit.SECONDS,
      new LinkedBlockingQueue<>(2500),
      new CallerRunsPolicy()
   );

   private static void onBlockStateUpdate(Event<KalamaHelperHelperD> event) {
      if (shouldExecuteWorldScan()) {
         BlockPos var1 = ((KalamaHelperHelperD)event.b).pos().toImmutable();
         ChunkPos var2 = new ChunkPos(var1);
         scheduleChunkTask(var2, () -> onSingleBlockValueChange(var1), true);
      }
   }

   static {
      registerListener(Listener.aW(), WorldTasks::onBlockStateUpdate);
      registerListener(Listener.aX(), WorldTasks::onChunkUpdate);
      registerListener(Listener.N(), WorldTasks::e);
      registerListener(Listener.O(), WorldTasks::f);
      registerListener(Listener.V(), WorldTasks::onTick);
   }

   private static <W> void registerListener(PacketCatcher<W> listener, Consumer<W> handler) {
      listener.k(handler);
   }

   public static void cancelPendingChunkTask(ChunkPos chunkPos) {
      b.execute(() -> a.remove(chunkPos));
   }

   public static void onChunkUpdate(Event<ChunkPos> chunkDataS2CPacketEvent) {
      if (b.world != null && b.player != null) {
         if (shouldExecuteWorldScan()) {
            ChunkPos var1 = (ChunkPos)chunkDataS2CPacketEvent.b;
            Chunk var2 = b.world.getChunk(var1.x, var1.z, ChunkStatus.FULL, false);
            if (var2 != null) {
               cancelPendingChunkTask(var1);
               scheduleChunkTask(var1, () -> onChunkReScann(var1), true);
            }
         }
      }
   }

   public static void cancelAllPendingChunkTasks() {
      b.execute(() -> a.clear());
   }

   public static void scheduleChunkTask(ChunkPos pos, Runnable runnable, boolean async) {
      if (async) {
         a.compute(pos, (v, t) -> t == null ? CompletableFuture.runAsync(runnable, c) : t.thenRunAsync(runnable, c));
      } else {
         a.compute(pos, (v, t) -> t == null ? CompletableFuture.runAsync(runnable, b) : t.thenRunAsync(runnable, b));
      }
   }

   public static void restartWorldScanner() {
      Listener.aZ().broadcast(null);
      cancelAllPendingChunkTasks();
      if (b.player != null && b.world != null) {
         refreshAllChunks();
      }
   }

   private static void onSingleBlockValueChange(BlockPos pos) {
      ChunkPos var1 = CommonUtils.toChunk(pos);
      if (b.world.getChunkManager().isChunkLoaded(var1.x, var1.z)) {
         BlockState var2 = b.world.getBlockState(pos);
         c.execute(() -> {
            Event var3 = new Event<>(var2, false, false, pos, var1);
            Listener.bb().b(var3);
         });
      }
   }

   public static void f(Event<Void> event) {
      cancelAllPendingChunkTasks();
   }

   private static void onChunkReScann(ChunkPos chunkPos) {
      if (b.player != null && b.world != null) {
         if (b.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) {
            WorldChunk var1 = b.world.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z);
            if (var1 != null) {
               ArrayList<BiPredicate<BlockPos, BlockState>> var2 = new ArrayList<>();
               Listener.ba().broadcast(var2);
               if (var2.isEmpty()) {
                  return;
               }

               BiPredicate<BlockPos, BlockState> var3 = (b, s) -> var2.stream().anyMatch(s1 -> s1.test(b, s));
               Map var4 = WorldUtils.scannChunk(var1, var3);
               d.execute(() -> {
                  Event var2x = new Event<>(var4, false, false, chunkPos);
                  Listener.bc().catchEvent(var2x);
               });
            }
         }
      }
   }

   public static boolean shouldExecuteWorldScan() {
      if (!Listener.aY().d()) {
         Event var0 = new Event<>(false, false, true);
         Listener.aY().catchEvent(var0);
         return var0.b == Boolean.TRUE;
      } else {
         return false;
      }
   }

   public static void refreshAllChunks() {
      if (shouldExecuteWorldScan()) {
         for (Chunk var1 : CommonUtils.chunks(false)) {
            ChunkPos var2 = var1.getPos();
            scheduleChunkTask(var2, () -> onChunkReScann(var2), true);
         }
      }
   }

   public static void e(Event<World> event) {
      cancelAllPendingChunkTasks();
   }

   public static void onTick(Event<ClientPlayerEntity> eventUpdate) {
      if (++tickCounter > 5) {
         tickCounter = 0;
         Set<ChunkPos> var1 = a.keySet();
         ArrayList<ChunkPos> var2 = new ArrayList(32);

         for (ChunkPos var4 : var1) {
            if (!b.world.getChunkManager().isChunkLoaded(var4.x, var4.z)) {
               var2.add(var4);
            }
         }

         for (ChunkPos var6 : var2) {
            cancelPendingChunkTask(var6);
         }
      }
   }

   public static void a() {
   }
}
