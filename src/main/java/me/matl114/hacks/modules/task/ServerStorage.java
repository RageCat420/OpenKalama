package me.matl114.hacks.modules.task;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.channels.EventChannel;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.PrimitivePairList;
import me.matl114.hacks.utils.world.BlockStorage;
import me.matl114.hacks.utils.world.ChunkStorage;
import me.matl114.hacks.utils.world.EntityStorage;
import me.matl114.hacks.utils.world.IStorage;
import me.matl114.hacks.utils.world.WorldStorage;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.KalamaHelperHelperG;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.CollectionUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ServerStorage extends BaseModule {
   private static final EventChannel<TaskSubHelperJ> xr = new EventChannel<>();
   public static ServerStorage INSTANCE;
   static final String xl = "ip-proxies";
   static Map<ChunkPos, ChunkStorage> xq;
   TaskSubHelperL xm;
   static final String xk = "persistent-storage-name-mapper";
   private static final EventChannel<TaskSubHelperJ> xs = new EventChannel<>();
   FileStorage aA;
   public ModulePath iE = makePath(Configs.r, "world-storage");
   static TaskSubHelperJ xo;
   public static String xt = "ws_";
   public final FlagRef ae = this.builder(this.iE.add("enable-persistent-storage"), Boolean.class).defaultValue(true).build();
   public final FlagRef enableProxyBaritoneStorage;
   static Map<BlockPos, BlockStorage> xp;
   static String xn;
   public final FlagRef xi = this.flagBuilder(this.iE.add("enable-proxy-xaeromap-storage")).build();
   public static final File C = FileManager.getInstance().c("server_storage");
   String xu;

   public static void Jg(ChunkPos pos, ChunkStorage blockStorage) {
      if (xo != null) {
         Map var2 = xq;
         if (var2 == null && mc.world != null) {
            Jq(mc.world.getRegistryKey());
         }

         if (var2 != null) {
            Jj(pos, blockStorage, var2);
         } else {
            Map var3 = xo.j.computeIfAbsent(mc.world.getRegistryKey(), k -> new ConcurrentHashMap<>());
            Jj(pos, blockStorage, var3);
         }
      }
   }

   private static NbtCompound JG(IStorage storage) {
      NbtCompound var1 = new NbtCompound();

      for (Entry var3 : storage.b.entrySet()) {
         var1.put((String)var3.getKey(), ((NbtElement)var3.getValue()).copy());
      }

      return var1;
   }

   private static File JE(File folder, String fileName) {
      return new File(folder, fileName + ".nbt");
   }

   private void JK(String serverName, TaskSubHelperJ currentSaveStorage) {
      File var3 = this.Jw(serverName);
      FileManager.getInstance().b(var3);

      try (FileStorage var4 = FileManager.getInstance().n(this.Jx(serverName)).t()) {
         var4.f(TaskSubHelperJ.g, currentSaveStorage);
      }

      this.JJ(this.Jy(serverName, "block-storage"), currentSaveStorage.i.entrySet().iterator(), ServerStorage::JA, BlockStorage.CODEC);
      this.JJ(this.Jy(serverName, "chunk-storage"), currentSaveStorage.j.entrySet().iterator(), ServerStorage::JB, ChunkStorage.CODEC);
      this.JI(this.Jy(serverName, "world-storage"), currentSaveStorage.k.entrySet().iterator(), ServerStorage::JC, WorldStorage.CODEC);
      this.JI(this.Jy(serverName, "entity-storage"), currentSaveStorage.l.entrySet().iterator(), ServerStorage::JD, EntityStorage.CODEC);
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      ModulePath var5 = this.iE.add("persistent-storage-name-mapper");
      NBTRef<PrimitivePairList<String, String>> var6 = new NBTRef<>(
         new PrimitivePairList<>("widget.server-storage.ip", "widget.server-storage.name", NBTTypes.g, NBTTypes.g, CollectionUtils.j(this.xm.ipToFolder()))
      );
      var6.addUpdateListener(s -> this.IX(this.xm.VE(CollectionUtils.pairListToMap(s.list()))));
      acceptor.accept(this.createRefEditor(var5.asString(), var6, 0, dblank, dx, dy));
      ModulePath var7 = this.iE.add("ip-proxies");
      NBTRef<PrimitivePairList<String, String>> var8 = new NBTRef<>(
         new PrimitivePairList<>("widget.server-storage.proxy", "widget.server-storage.ip", NBTTypes.g, NBTTypes.g, CollectionUtils.j(this.xm.ipToFolder()))
      );
      var8.addUpdateListener(s -> this.IX(this.xm.VF(CollectionUtils.pairListToMap(s.list()))));
      acceptor.accept(this.createRefEditor(var7.asString(), var8, 0, dblank, dx, dy));
   }

   private <T extends IStorage> void JL(File folder, Codec<T> codec, Consumer<T> consumer) {
      if (folder.exists() && folder.isDirectory()) {
         File[] var4 = folder.listFiles(file -> file.isFile() && file.getName().endsWith(".nbt"));
         if (var4 != null) {
            for (File var8 : var4) {
               try (FileStorage var9 = FileManager.getInstance().j(var8, false, false)) {
                  if (var9 != null) {
                     T var10 = var9.read(codec, null);
                     if (var10 != null) {
                        var10.setDirty(false);
                        consumer.accept(var10);
                     } else {
                        var9.i();
                     }
                  }
               } catch (UnsupportedOperationException var14) {
                  var8.delete();
               } catch (Throwable var15) {
               }
            }
         }
      }
   }

   private static String JC(WorldStorage storage) {
      return Jz(storage.getDimension());
   }

   private static String JB(ChunkStorage storage) {
      return Jz(storage.getDimension()) + "_" + storage.getChunkPos().x + "_" + storage.getChunkPos().z;
   }

   public static void Jk(BlockStorage storage, boolean autoRemoval) {
   }

   private static String JD(EntityStorage storage) {
      return storage.getUuid().toString();
   }

   public void IX(TaskSubHelperL folder) {
      this.xm = folder;
      this.aA.f(TaskSubHelperL.CODEC, folder);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.M(), this::Jr, Integer.MAX_VALUE);
      this.registerListener(Listener.N(), this::Js, Integer.MIN_VALUE);
      this.registerListener(Listener.O(), this::Jt, Integer.MIN_VALUE);
      this.xu = KalamaHelperHelperG.b(() -> {
         if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getRegistryManager() != null) {
            this.Ju(mc.getNetworkHandler().getRegistryManager());
         }
      }, 15000L, 15000L);
   }

   private String Jn(String path) {
      path = path.trim().replace(" ", "_").replaceAll("[\\\\/:*?\"<>|]|\\p{Cntrl}", "_");
      return xt + path;
   }

   public static void Jd(BlockPos pos, BlockStorage blockStorage) {
      if (xo != null) {
         Map var2 = xp;
         if (var2 == null && mc.world != null) {
            Jq(mc.world.getRegistryKey());
         }

         if (var2 != null) {
            Jj(pos, blockStorage, var2);
         } else {
            Map var3 = xo.i.computeIfAbsent(mc.world.getRegistryKey(), k -> new ConcurrentHashMap<>());
            Jj(pos, blockStorage, var3);
         }
      }
   }

   public static WorldStorage Ji(RegistryKey<World> key, Supplier<WorldStorage> supplier) {
      return xo.k.computeIfAbsent(key, s -> (WorldStorage)supplier.get());
   }

   public boolean Jo() {
      String var1 = this.Jm();
      if (!Objects.equals(xn, var1)) {
         xn = var1;
         return true;
      } else {
         return xo == null;
      }
   }

   private void Ju(DynamicRegistryManager registryReference) {
      if (xn != null && xo != null) {
         TaskSubHelperJ var2 = xo;
         String var3 = xn;
         xs.h(var2, xn, registryReference);
         this.Jv(var3, var2);
      }
   }

   private static <W, T> void Jj(W key, T val, Map<W, T> mmm) {
      if (val != null) {
         mmm.put(key, val);
      } else if (mmm.get(key) instanceof IStorage var4) {
         var4.b.clear();
         var4.setDirty(true);
      } else {
         mmm.remove(key);
      }
   }

   public void Js(Event<World> event) {
      Jq(null);
   }

   private File Jx(String serverName) {
      return new File(this.Jw(serverName), "meta.nbt");
   }

   private <T extends IStorage> void JH(File folder, T storageValue, Function<T, String> fileNameGetter, Codec<T> codec) {
      if (storageValue.m()) {
         File var5 = JE(folder, (String)fileNameGetter.apply(storageValue));
         if (storageValue.j()) {
            if (var5.exists()) {
               var5.delete();
            }

            storageValue.setDirty(false);
         } else {
            try (FileStorage var6 = FileManager.getInstance().n(var5).t()) {
               var6.f(codec, storageValue);
               storageValue.setDirty(false);
            }
         }
      }
   }

   public void Jr(Event<ClientPlayerEntity> eventPlayerEntity) {
      if (this.Jo()) {
         this.Jp();
      }
   }

   public static BlockStorage Jc(BlockPos pos, Function<BlockPos, BlockStorage> supplier) {
      if (xo == null) {
         return null;
      } else {
         Map var2 = xp;
         if (var2 == null && mc.world != null) {
            Jq(mc.world.getRegistryKey());
         }

         if (var2 != null) {
            return Jh(pos, supplier, var2);
         } else {
            Map var3 = xo.i.computeIfAbsent(mc.world.getRegistryKey(), k -> new ConcurrentHashMap<>());
            return Jh(pos, supplier, var3);
         }
      }
   }

   public void Jv(String serverName, TaskSubHelperJ currentSaveStorage) {
      if (this.ae.get()) {
         synchronized (currentSaveStorage) {
            if (currentSaveStorage.isDirty()) {
               this.JK(serverName, currentSaveStorage);
            }
         }
      }
   }

   public static ChunkStorage Jf(ChunkPos pos, Function<ChunkPos, ChunkStorage> supplier) {
      if (xo == null) {
         return null;
      } else {
         Map var2 = xq;
         if (var2 == null && mc.world != null) {
            Jq(mc.world.getRegistryKey());
         }

         if (var2 != null) {
            return Jh(pos, supplier, var2);
         } else {
            Map var3 = xo.j.computeIfAbsent(mc.world.getRegistryKey(), k -> new ConcurrentHashMap<>());
            return Jh(pos, supplier, var3);
         }
      }
   }

   public String IY(String ip) {
      return this.xi.get() ? this.xm.VD(ip) : ip;
   }

   public static EventChannel<TaskSubHelperJ> JP() {
      return xr;
   }

   private static IStorage JF(NbtCompound compound) {
      HashMap var1 = new HashMap();

      for (String var3 : compound.getKeys()) {
         NbtElement var4 = compound.get(var3);
         if (var4 != null) {
            var1.put(var3, var4.copy());
         }
      }

      return new IStorage(World.OVERWORLD, var1);
   }

   @Override
   public <W> void unregisterAll() {
      super.unregisterAll();
      if (this.xu != null) {
         KalamaHelperHelperG.d(this.xu);
      }
   }

   private <K, T extends IStorage> void JI(File folder, Iterator<Entry<K, T>> iterator, Function<T, String> fileNameGetter, Codec<T> codec) {
      FileManager.getInstance().b(folder);

      while (iterator.hasNext()) {
         IStorage var5 = (IStorage)((Entry)iterator.next()).getValue();
         this.JH(folder, var5, fileNameGetter, codec);
         if (var5.j()) {
            iterator.remove();
         }
      }
   }

   private static String JA(BlockStorage storage) {
      return Jz(storage.getDimension()) + "_" + storage.v().asLong();
   }

   private <K, T extends IStorage> void JJ(
      File folder, Iterator<? extends Entry<K, ? extends Map<?, T>>> outerIterator, Function<T, String> fileNameGetter, Codec<T> codec
   ) {
      FileManager.getInstance().b(folder);

      while (outerIterator.hasNext()) {
         Map var5 = (Map)((Entry)outerIterator.next()).getValue();
         Iterator var6 = var5.entrySet().iterator();

         while (var6.hasNext()) {
            IStorage var7 = (IStorage)((Entry)var6.next()).getValue();
            this.JH(folder, var7, fileNameGetter, codec);
            if (var7.j()) {
               var6.remove();
            }
         }

         if (var5.isEmpty()) {
            outerIterator.remove();
         }
      }
   }

   public static ChunkStorage Je(ChunkPos pos, Supplier<ChunkStorage> supplier) {
      return Jf(pos, v -> (ChunkStorage)supplier.get());
   }

   public void Jt(Event<Void> eventVoid) {
      Jq(null);
      Immutable var2 = mc.getNetworkHandler().getRegistryManager();
      CompletableFuture.runAsync(() -> this.Ju(var2));
   }

   private TaskSubHelperJ JO(TaskSubHelperJ meta, FileStorage oldStorage) {
      TaskSubHelperJ var3 = oldStorage.e(TaskSubHelperJ.h).result().orElse(meta);
      TaskSubHelperJ var4 = new TaskSubHelperJ(var3.n, 1);
      var3.j().forEach(storage -> {
         storage.setDirty(true);
         var4.c(storage);
      });
      var3.k().forEach(storage -> {
         storage.setDirty(true);
         var4.d(storage);
      });
      var3.l().forEach(storage -> {
         storage.setDirty(true);
         var4.e(storage);
      });
      return var4;
   }

   private File Jy(String serverName, String folderName) {
      return new File(this.Jw(serverName), folderName);
   }

   public static BlockStorage Ja(BlockPos pos) {
      return Jc(pos, (Function<BlockPos, BlockStorage>)null);
   }

   public static void Jq(RegistryKey<World> world) {
      if (xo != null && world != null) {
         TaskSubHelperJ var1 = xo;
         CompletableFuture.runAsync(() -> {
            synchronized (var1) {
               xp = var1.i.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
               xq = var1.j.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
            }
         });
      } else {
         xp = null;
         xq = null;
      }
   }

   public ServerStorage() {
      super("ServerStorage");
      this.enableProxyBaritoneStorage = this.flagBuilder(this.iE.add("enable-proxy-baritone-storage")).build();
      this.aA = FileManager.getInstance().o("server-storage.nbt");
      this.xm = this.aA.read(TaskSubHelperL.CODEC, () -> new TaskSubHelperL(Map.of(), Map.of()));
      this.IX(this.xm);
      INSTANCE = this;
   }

   public static String Jl() {
      return INSTANCE.Jm();
   }

   private static <W, T> T Jh(W key, Function<W, T> supplier, Map<W, T> mmm) {
      Object var3 = mmm.get(key);
      if (var3 != null) {
         return (T)var3;
      } else if (supplier == null) {
         return null;
      } else {
         var3 = supplier.apply(key);
         mmm.put(key, var3);
         return (T)var3;
      }
   }

   private File Jw(String serverName) {
      return new File(C, this.Jn(serverName));
   }

   private void JM(String serverName, TaskSubHelperJ loadingStorage) {
      File var3 = this.Jw(serverName);
      FileManager.getInstance().b(var3);
      this.JL(this.Jy(serverName, "block-storage"), BlockStorage.CODEC, loadingStorage::c);
      this.JL(this.Jy(serverName, "chunk-storage"), ChunkStorage.CODEC, loadingStorage::d);
      this.JL(this.Jy(serverName, "world-storage"), WorldStorage.CODEC, loadingStorage::e);
      this.JL(this.Jy(serverName, "entity-storage"), EntityStorage.CODEC, loadingStorage::f);
   }

   public static EventChannel<TaskSubHelperJ> JQ() {
      return xs;
   }

   public TaskSubHelperJ JN(TaskSubHelperJ meta, FileStorage oldStorage, int oldVersion, int newVersion) {
      TaskSubHelperJ var5 = meta;

      for (int var6 = oldVersion; var6 < newVersion && var6 == 0; var6 = var5.version) {
         var5 = this.JO(var5, oldStorage);
      }

      return var5;
   }

   public void Jp() {
      String var1 = xn;
      xo = new TaskSubHelperJ(var1);
      TaskSubHelperJ var2 = xo;
      Immutable var3 = mc.getNetworkHandler().getRegistryManager();
      if (this.ae.get()) {
         CompletableFuture.runAsync(() -> {
            File var3x = this.Jw(var1);
            File var4 = this.Jx(var1);
            FileManager.getInstance().b(var3x);
            synchronized (var2) {
               try (FileStorage var6 = FileManager.getInstance().n(var4)) {
                  DataResult var7 = var6.e(TaskSubHelperJ.g);
                  TaskSubHelperJ var8;
                  if (!var7.isSuccess() || !(var8 = (TaskSubHelperJ)var7.getOrThrow()).n.equals(var1)) {
                     var6.f(TaskSubHelperJ.g, var2);
                  } else if (var8.version < 1) {
                     TaskSubHelperJ var9 = this.JN(var8, var6, var8.version, 1);
                     if (var9 != var8) {
                        this.JK(var1, var9);
                     }
                  }

                  this.JM(var1, var2);
               } catch (Throwable var13) {
                  Debug.a("Error while loading server storage:");
                  Debug.f(var13);
               }
            }
         }).thenRunAsync(() -> xr.h(var2, var1, var3), mc);
      } else {
         xr.h(var2, var1, var3);
      }

      Jq(null);
   }

   private static String Jz(RegistryKey<World> worldKey) {
      return worldKey.getValue().toString().replace(":", "_");
   }

   private String Jm() {
      String var1 = CommonUtils.getServerName();
      Preconditions.checkNotNull(var1);
      return this.xm.VC(var1);
   }

   public static BlockStorage Jb(BlockPos pos, Supplier<BlockStorage> supplier) {
      return Jc(pos, supplier == null ? null : v -> (BlockStorage)supplier.get());
   }

   public static TaskSubHelperJ IZ() {
      return xo;
   }
}
