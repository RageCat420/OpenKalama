package me.matl114.hacks.modules.survival;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.StringRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.ChunkRandom.RandomProvider;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

public class SeedOre extends BaseModule {
   public final FlagRef ae;
   boolean fakeOre;
   public static SeedOre INSTANCE;
   public final FileStorage Ew;
   public final StringRef oreWhiteList;
   public final FlagRef renderOre;
   public final FlagRef enableFakeOre;
   public final ModulePath Eq;
   public final IntRef chunkRadius;
   private final Map<Long, Map<BlockPos, BlockState>> Ep;
   private Map<RegistryKey<Biome>, List<SurvivalSubHelperC>> Ex;
   public static final String[] SEED_MAP = new String[]{"seed", "seed-cache"};
   private final Gson nZ;
   private final Map<Long, Map<SurvivalSubHelperC, Set<Vec3d>>> Eo;
   public final Object2LongMap<String> En = new Object2LongOpenHashMap();

   private static ArrayList<Vec3d> generateHidden(ClientWorld world, ChunkRandom random, BlockPos blockPos, int size) {
      ArrayList var4 = new ArrayList();
      int var5 = random.nextInt(size + 1);

      for (int var6 = 0; var6 < var5; var6++) {
         size = Math.min(var6, 7);
         int var7 = randomCoord(random, size) + blockPos.getX();
         int var8 = randomCoord(random, size) + blockPos.getY();
         int var9 = randomCoord(random, size) + blockPos.getZ();
         if (world.getBlockState(new BlockPos(var7, var8, var9)).isOpaque() && shouldPlace(world, new BlockPos(var7, var8, var9), 1.0F, random)) {
            var4.add(new Vec3d(var7, var8, var9));
         }
      }

      return var4;
   }

   public static boolean isSeedValid(long seed) {
      long var2 = mc.world.getBiomeAccess().seed;
      return BiomeAccess.hashSeed(seed) == var2;
   }

   public void Tl(MainCommand command) {
      TreeSubCommand var2 = command.bD().a("seedore").k();
      var2.subBuilder(SubCommand.bo())
         .u("toggle")
         .x("message.command.seedore.toggle.help")
         .A(KalamaHelperHelperA.a().B("toggle").o().v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::Tm)))
         .r();
      var2.subBuilder(SubCommand.bo())
         .u("render")
         .x("message.command.seedore.render.help")
         .A(KalamaHelperHelperA.a().B("toggle").o().v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::Tq)))
         .r();
      var2.subBuilder(SubCommand.bo())
         .u("fakeore")
         .x("message.command.seedore.fakeore.help")
         .A(KalamaHelperHelperA.a().B("operation").k(List.of("on", "off", "reload")).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::Tr)))
         .r();
      TreeSubCommand var3 = command.bD().a("seed").k();
      var3.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
         .u("set")
         .x("message.command.seed.set.help")
         .A(KalamaHelperHelperA.a().B("seed").f().d(() -> this.En.keySet().stream()).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::onSeedSet)))
         .r()
         .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
         .u("remove")
         .x("message.command.seed.remove.help")
         .A(KalamaHelperHelperA.a().B("world").d(() -> this.En.keySet().stream()).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::To)))
         .r()
         .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
         .u("list")
         .x("message.command.seed.list.help")
         .z(e -> e.executor(KalamaHelperHelperH.g(this::onSeedList)))
         .r()
         .subBuilder(SubCommand.bo())
         .u("validate")
         .x("message.command.seed.validate.help")
         .z(e -> e.executor(KalamaHelperHelperH.g(this::validateCurrentSeed)))
         .r();
   }

   private void renderChunk(int x, int z, MatrixStack event) {
      long var4 = ChunkPos.toLong(x, z);
      if (this.Eo.containsKey(var4)) {
         Map<SurvivalSubHelperC, Set<Vec3d>> var6 = this.Eo.get(var4);

         for (Entry<SurvivalSubHelperC, Set<Vec3d>> var8 : var6.entrySet()) {
            if (((SurvivalSubHelperC)var8.getKey()).q.getOriginValue() == Boolean.TRUE) {
               Color var9 = ((SurvivalSubHelperC)var8.getKey()).x;

               for (Vec3d var11 : var8.getValue()) {
                  Vec3d var12 = BlockPos.ofFloored(var11).toCenterPos();
                  RenderUtils.drawOutlinedBox(event, var12.add(RenderTasks.l), var12.add(RenderTasks.n), var9);
               }
            }
         }
      }
   }

   private void updateOreClientSide(long chunkey, Map<SurvivalSubHelperC, Set<Vec3d>> ores) {
      if (mc.world != null) {
         if (this.enableFakeOre.get()) {
            Map var4 = this.Ep.remove(chunkey);
            if (var4 != null && !var4.isEmpty()) {
               this.Tc(chunkey, var4);
            }

            ConcurrentHashMap var5 = new ConcurrentHashMap();
            int var6 = mc.world.getBottomY();

            for (Entry var8 : ores.entrySet()) {
               SurvivalSubHelperC var9 = (SurvivalSubHelperC)var8.getKey();
               Block var10 = var9.o;
               Block var11 = var9.p;

               label71:
               for (Vec3d var13 : (Set)var8.getValue()) {
                  if (!(var13.y < var6 + 4)) {
                     Block var14 = var13.y > 0.0 ? var10 : var11;
                     BlockState var15 = var14.getDefaultState();
                     BlockPos var16 = BlockPos.ofFloored(var13);
                     BlockState var17 = mc.world.getBlockState(var16);
                     if (!var17.isAir() && var17.getBlock() != var10 && var17.getBlock() != var11) {
                        for (Direction var21 : Direction.values()) {
                           BlockPos var22 = var16.offset(var21);
                           if (mc.world.getBlockState(var22).isAir()) {
                              continue label71;
                           }
                        }

                        var5.put(var16, var17);
                        Tasks.l(() -> {
                           if (mc.world != null) {
                              mc.world.setBlockState(var16, var15);
                           }
                        }, 2);
                     }
                  }
               }
            }

            this.Ep.put(chunkey, var5);
         }
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      acceptor.accept(this.createTitleLabel("widget.seed-ore.command", 0, dblank, dx, dy));
   }

   public SeedOre() {
      super("SeedOre");
      this.Eo = new ConcurrentHashMap<>();
      this.Ep = new ConcurrentHashMap<>();
      this.nZ = new GsonBuilder().disableHtmlEscaping().create();
      this.Eq = makePath(Configs.o, "survival-mine-utils.aaxray-seed-ore");
      portConfigs(makePath(Configs.g, "aaxray.seed-ore"), this.Eq);
      this.ae = this.flagBuilder(this.Eq.addEnable()).build();
      this.chunkRadius = this.builder(this.Eq.add("chunk-radius"), IntRef.TYPE).defaultValue(6).validator(Configs.e).build();
      this.renderOre = this.flagBuilder(this.Eq.add("render-ore")).build();
      this.fakeOre = false;
      this.enableFakeOre = this.flagBuilder(this.Eq.add("enable-fake-ore")).defaultValue(false).updateListener(this::onFakeOreToggle).build();
      this.oreWhiteList = this.builder(this.Eq.add("ore-white-list"), String.class)
         .defaultValue("^(diamond)$")
         .validator(Configs.a)
         .updateListener(SurvivalSubHelperC::reloadOreSettings)
         .build();
      this.Ew = FileManager.getInstance().o("seed-storage.nbt");
      NbtCompound var1 = this.Ew.b(NbtOps.INSTANCE);
      this.En.clear();

      for (Entry var3 : var1.entrySet()) {
         String var4 = (String)var3.getKey();
         NbtElement var5 = (NbtElement)var3.getValue();
         if (var5 instanceof NbtLong var6) {
            this.En.put(var4, var6.longValue());
         }
      }

      this.bindFlag(this.ae);
      INSTANCE = this;
   }

   public void SO(Event<World> v) {
      this.Td();
      if (this.isActive()) {
         this.onReloadSeedOre();
      }
   }

   public void saveSeedMap() {
      this.Ew.write(this.En, JavaOps.INSTANCE);
   }

   public void onBlockUpdate(Event<BlockUpdateS2CPacket> event) {
      if (!this.Eo.isEmpty() || !this.Ep.isEmpty()) {
         BlockUpdateS2CPacket var2 = (BlockUpdateS2CPacket)event.b;
         long var3 = ChunkPos.toLong(var2.getPos());
         Map<SurvivalSubHelperC, Set<Vec3d>> var5 = this.Eo.get(var3);
         Vec3d var6 = Vec3d.of(var2.getPos());
         if (var5 != null && !var5.isEmpty()) {
            for (Set<Vec3d> var8 : var5.values()) {
               var8.remove(var6);
            }
         }

         Map var9 = this.Ep.get(var3);
         if (var9 != null && !var9.isEmpty()) {
            var9.remove(var2.getPos());
         }
      }
   }

   private void updateChunk(Chunk chunk) {
      if (this.ae.get()) {
         if (this.checkCurrentSeedExistence()) {
            ChunkPos var2 = chunk.getPos();
            long var3 = var2.toLong();
            ClientWorld var5 = mc.world;
            Map<SurvivalSubHelperC, Set<Vec3d>> var6;
            if (!this.Eo.containsKey(var3) && var5 != null && this.Ex != null) {
               HashSet var7 = new HashSet();
               ChunkPos.stream(var2, 1).forEach(chunkPosx -> {
                  Chunk var3x = var5.getChunk(chunkPosx.x, chunkPosx.z, ChunkStatus.BIOMES, false);
                  if (var3x != null) {
                     for (ChunkSection var7x : var3x.getSectionArray()) {
                        var7x.getBiomeContainer().forEachValue(entry -> var7.add((RegistryKey)entry.getKey().get()));
                     }
                  }
               });
               Set<SurvivalSubHelperC> var8 = var7.stream().flatMap(b -> this.getDefaultOres((RegistryKey<Biome>)b).stream()).collect(Collectors.toSet());
               int var9 = var2.x << 4;
               int var10 = var2.z << 4;
               ChunkRandom var11 = new ChunkRandom(RandomProvider.XOROSHIRO.create(0L));
               long var12 = var11.setPopulationSeed(this.getCurrentSeed(), var9, var10);
               var6 = new ConcurrentHashMap<>();

               for (SurvivalSubHelperC var15 : var8) {
                  Set<Vec3d> var16 = ConcurrentHashMap.newKeySet();
                  var11.setDecoratorSeed(var12, var15.n, var15.m);
                  int var17 = var15.r.get(var11);

                  for (int var18 = 0; var18 < var17; var18++) {
                     if (var15.u == 1.0F || !(var11.nextFloat() >= 1.0F / var15.u)) {
                        int var19 = var11.nextInt(16) + var9;
                        int var20 = var11.nextInt(16) + var10;
                        int var21 = var15.s.get(var11, var15.t);
                        BlockPos var22 = new BlockPos(var19, var21, var20);
                        RegistryKey var23 = (RegistryKey)chunk.getBiomeForNoiseGen(var19, var21, var20).getKey().get();
                        if (this.getDefaultOres(var23).contains(var15)) {
                           if (var15.scattered) {
                              var16.addAll(generateHidden(var5, var11, var22, var15.w));
                           } else {
                              var16.addAll(generateNormal(var5, var11, var22, var15.w, var15.v));
                           }
                        }
                     }
                  }

                  if (!var16.isEmpty()) {
                     var6.put(var15, var16);
                  }
               }
            } else {
               var6 = this.Eo.get(var3);
            }

            if (var6 != null && !var6.isEmpty()) {
               this.Eo.put(var3, (Map<SurvivalSubHelperC, Set<Vec3d>>)var6);
               if (this.enableFakeOre.get()) {
                  this.updateOreClientSide(var3, (Map<SurvivalSubHelperC, Set<Vec3d>>)var6);
               }
            }
         }
      }
   }

   private static ArrayList<Vec3d> generateVeinPart(
      ClientWorld world,
      ChunkRandom random,
      int veinSize,
      double startX,
      double endX,
      double startZ,
      double endZ,
      double startY,
      double endY,
      int x,
      int y,
      int z,
      int size,
      int i,
      float discardOnAir
   ) {
      BitSet var21 = new BitSet(size * i * size);
      Mutable var22 = new Mutable();
      double[] var23 = new double[veinSize * 4];
      ArrayList var24 = new ArrayList();

      for (int var25 = 0; var25 < veinSize; var25++) {
         float var26 = (float)var25 / veinSize;
         double var27 = MathHelper.lerp(var26, startX, endX);
         double var29 = MathHelper.lerp(var26, startY, endY);
         double var31 = MathHelper.lerp(var26, startZ, endZ);
         double var33 = random.nextDouble() * veinSize / 16.0;
         double var35 = ((MathHelper.sin((float) Math.PI * var26) + 1.0F) * var33 + 1.0) / 2.0;
         var23[var25 * 4] = var27;
         var23[var25 * 4 + 1] = var29;
         var23[var25 * 4 + 2] = var31;
         var23[var25 * 4 + 3] = var35;
      }

      for (int var61 = 0; var61 < veinSize - 1; var61++) {
         if (!(var23[var61 * 4 + 3] <= 0.0)) {
            for (int var63 = var61 + 1; var63 < veinSize; var63++) {
               if (!(var23[var63 * 4 + 3] <= 0.0)) {
                  double var64 = var23[var61 * 4] - var23[var63 * 4];
                  double var65 = var23[var61 * 4 + 1] - var23[var63 * 4 + 1];
                  double var66 = var23[var61 * 4 + 2] - var23[var63 * 4 + 2];
                  double var67 = var23[var61 * 4 + 3] - var23[var63 * 4 + 3];
                  if (var67 * var67 > var64 * var64 + var65 * var65 + var66 * var66) {
                     if (var67 > 0.0) {
                        var23[var63 * 4 + 3] = -1.0;
                     } else {
                        var23[var61 * 4 + 3] = -1.0;
                     }
                  }
               }
            }
         }
      }

      for (int var62 = 0; var62 < veinSize; var62++) {
         double var37 = var23[var62 * 4 + 3];
         if (!(var37 < 0.0)) {
            double var39 = var23[var62 * 4];
            double var41 = var23[var62 * 4 + 1];
            double var43 = var23[var62 * 4 + 2];
            int var45 = Math.max(MathHelper.floor(var39 - var37), x);
            int var46 = Math.max(MathHelper.floor(var41 - var37), y);
            int var47 = Math.max(MathHelper.floor(var43 - var37), z);
            int var48 = Math.max(MathHelper.floor(var39 + var37), var45);
            int var49 = Math.max(MathHelper.floor(var41 + var37), var46);
            int var50 = Math.max(MathHelper.floor(var43 + var37), var47);

            for (int var51 = var45; var51 <= var48; var51++) {
               double var52 = (var51 + 0.5 - var39) / var37;
               if (var52 * var52 < 1.0) {
                  for (int var54 = var46; var54 <= var49; var54++) {
                     double var55 = (var54 + 0.5 - var41) / var37;
                     if (var52 * var52 + var55 * var55 < 1.0) {
                        for (int var57 = var47; var57 <= var50; var57++) {
                           double var58 = (var57 + 0.5 - var43) / var37;
                           if (var52 * var52 + var55 * var55 + var58 * var58 < 1.0) {
                              int var60 = var51 - x + (var54 - y) * size + (var57 - z) * size * i;
                              if (!var21.get(var60)) {
                                 var21.set(var60);
                                 var22.set(var51, var54, var57);
                                 if (var54 >= -64 && var54 < 320 && world.getBlockState(var22).isOpaque() && shouldPlace(world, var22, discardOnAir, random)) {
                                    var24.add(new Vec3d(var51, var54, var57));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var24;
   }

   @Modifiable
   public void setWorldSeed(long seed) {
      this.putSeed(CommonUtils.getWorldName(), seed);
   }

   public void Tr(ArgumentInputStream re) {
      String var2 = re.n();
      switch (var2) {
         case "on":
            Debug.b("[种子矿透] 切换假矿: true");
            this.enableFakeOre.set(true);
            break;
         case "off":
            Debug.b("[种子矿透] 切换假矿: false");
            this.enableFakeOre.set(false);
            break;
         case "reload":
            Debug.b("[种子矿透] 重载可视距离内的假矿");
            this.SN();
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.Ex = null;
      if (mc.player != null && mc.world != null) {
         Debug.b(Text.literal("[种子矿透] 禁用该功能").formatted(Formatting.RED));
      }

      this.onReloadFakeOreVisibleChunks();
   }

   public void To(ArgumentInputStream re) {
      String var2 = re.n();
      this.removeSeed(var2);
   }

   private static ArrayList<Vec3d> generateNormal(ClientWorld world, ChunkRandom random, BlockPos blockPos, int veinSize, float discardOnAir) {
      float var5 = random.nextFloat() * (float) Math.PI;
      float var6 = veinSize / 8.0F;
      int var7 = MathHelper.ceil((veinSize / 16.0F * 2.0F + 1.0F) / 2.0F);
      double var8 = blockPos.getX() + Math.sin(var5) * var6;
      double var10 = blockPos.getX() - Math.sin(var5) * var6;
      double var12 = blockPos.getZ() + Math.cos(var5) * var6;
      double var14 = blockPos.getZ() - Math.cos(var5) * var6;
      double var16 = blockPos.getY() + random.nextInt(3) - 2;
      double var18 = blockPos.getY() + random.nextInt(3) - 2;
      int var20 = blockPos.getX() - MathHelper.ceil(var6) - var7;
      int var21 = blockPos.getY() - 2 - var7;
      int var22 = blockPos.getZ() - MathHelper.ceil(var6) - var7;
      int var23 = 2 * (MathHelper.ceil(var6) + var7);
      int var24 = 2 * (2 + var7);

      for (int var25 = var20; var25 <= var20 + var23; var25++) {
         for (int var26 = var22; var26 <= var22 + var23; var26++) {
            if (var21 <= world.getTopY(Type.MOTION_BLOCKING, var25, var26)) {
               return generateVeinPart(world, random, veinSize, var8, var10, var12, var14, var16, var18, var20, var21, var22, var23, var24, discardOnAir);
            }
         }
      }

      return new ArrayList<>();
   }

   public boolean hasCurrentSeed() {
      return this.En.containsKey(CommonUtils.getWorldName());
   }

   public Map<String, Set<Vec3d>> getSeedOres(int x, int z) {
      HashMap var3 = new HashMap();

      for (Entry var5 : this.Eo.getOrDefault(ChunkPos.toLong(x, z), Map.of()).entrySet()) {
         var3.put(((SurvivalSubHelperC)var5.getKey()).q.getKeyName(), (Set)var5.getValue());
      }

      return var3;
   }

   public long getCurrentSeed() {
      return this.En.getLong(CommonUtils.getWorldName());
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      if (mc.world != null) {
         this.onReloadSeedOre();
      }
   }

   private static int randomCoord(ChunkRandom random, int size) {
      return Math.round((random.nextFloat() - random.nextFloat()) * size);
   }

   private static boolean shouldPlace(ClientWorld world, BlockPos orePos, float discardOnAir, ChunkRandom random) {
      if (discardOnAir != 0.0F && (discardOnAir == 1.0F || !(random.nextFloat() >= discardOnAir))) {
         for (Direction var7 : Direction.values()) {
            if (!world.getBlockState(orePos.add(var7.getVector())).isOpaque() && discardOnAir != 1.0F) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public void SZ() {
      if (mc.world != null) {
         for (Chunk var2 : CommonUtils.chunks(false)) {
            this.updateChunk(var2);
         }
      }
   }

   public void onReloadFakeOreVisibleChunks() {
      if (mc.world != null) {
         for (Chunk var2 : CommonUtils.chunks(false)) {
            long var3 = var2.getPos().toLong();
            Map var5 = this.Ep.remove(var3);
            if (var5 != null && !var5.isEmpty()) {
               this.Tc(var3, var5);
            }
         }
      }
   }

   public void Ta() {
      if (mc.world != null) {
         for (Chunk var2 : CommonUtils.chunks(false)) {
            long var3 = var2.getPos().toLong();
            Map<SurvivalSubHelperC, Set<Vec3d>> var5 = this.Eo.get(var3);
            if (var5 != null && !var5.isEmpty()) {
               this.updateOreClientSide(var3, var5);
            }
         }
      }
   }

   public boolean checkCurrentSeedExistence() {
      if (this.hasCurrentSeed()) {
         return true;
      } else {
         Debug.b(Text.literal("[世界种子] 暂时没有设置 %s 世界的种子".formatted(CommonUtils.getWorldName())).formatted(Formatting.RED));
         this.ae.set(false);
         return false;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.N(), this::SO);
      this.registerListener(Listener.ar().getChannel(ChunkDataS2CPacket.class), this::onChunkUpdate);
      this.registerListener(Listener.ap().getChannel(BlockUpdateS2CPacket.class), this::onBlockUpdate);
      this.registerListener(RenderListener.q(), this::onRenderOreSimulation);
      this.registerCommandBootstrap(this::Tl);
   }

   public void onReloadSeedOre() {
      try {
         this.Td();
         if (mc.world != null) {
            this.onReloadFakeOreVisibleChunks();
         }

         this.Ex = SurvivalSubHelperC.getRegistry();
         if (mc.player != null && mc.world != null) {
            Debug.b(Text.literal("[种子矿透] 启用该功能, 范围 %d".formatted(this.chunkRadius.get())).formatted(Formatting.GREEN));
            this.SZ();
         }
      } catch (Throwable var2) {
         Debug.f(var2);
         if (mc.player != null) {
            Debug.b(Text.literal("[种子矿透] 启用时出现报错, 已关闭..."));
         }

         this.ae.set(false);
      }
   }

   public void onRenderOreSimulation(Event<MatrixStack> event) {
      MatrixStack var2 = (MatrixStack)event.b;
      if (mc.player != null && this.Ex != null) {
         if (this.ae.get()) {
            if (this.renderOre.get()) {
               if (this.checkCurrentSeedExistence()) {
                  RenderUtils.startDrawVirtual(var2);

                  try {
                     int var3 = mc.player.getChunkPos().x;
                     int var4 = mc.player.getChunkPos().z;
                     int var5 = this.chunkRadius.get();

                     for (int var6 = 0; var6 <= var5; var6++) {
                        for (int var7 = -var6 + var3; var7 <= var6 + var3; var7++) {
                           this.renderChunk(var7, var4 + var6 - var5, var2);
                        }

                        for (int var11 = -var6 + 1 + var3; var11 < var6 + var3; var11++) {
                           this.renderChunk(var11, var4 - var6 + var5 + 1, var2);
                        }
                     }
                  } finally {
                     RenderUtils.stopDrawVirtual(var2);
                  }
               }
            }
         }
      }
   }

   static {
      SurvivalSubHelperC.init();
   }

   private List<SurvivalSubHelperC> getDefaultOres(RegistryKey<Biome> biomeRegistryKey) {
      return this.Ex.containsKey(biomeRegistryKey) ? this.Ex.get(biomeRegistryKey) : this.Ex.values().stream().findAny().get();
   }

   private void Tc(long key, Map<BlockPos, BlockState> originDatas) {
      for (Entry var5 : originDatas.entrySet()) {
         BlockPos var6 = (BlockPos)var5.getKey();
         BlockState var7 = (BlockState)var5.getValue();
         Tasks.l(() -> {
            if (mc.world != null) {
               mc.world.setBlockState(var6, var7);
            }
         }, 1);
      }
   }

   public void Tq(ArgumentInputStream re) {
      boolean var2 = re.h();
      this.renderOre.set(var2);
      Debug.chat("[种子矿透] 切换渲染:", var2);
   }

   public void putSeed(String key, long seed) {
      if (this.En.containsKey(key)) {
         long var4 = this.En.getLong(key);
         if (var4 != seed) {
            this.En.put(key, seed);
            this.saveSeedMap();
            this.SR(key);
         }
      } else {
         this.En.put(key, seed);
         this.saveSeedMap();
         this.SR(key);
      }
   }

   public void onSeedSet(ArgumentInputStream re) {
      String var2 = re.n();
      long var3;
      if (this.En.containsKey(var2)) {
         var3 = this.En.getLong(var2);
      } else {
         var3 = Long.parseLong(var2);
      }

      this.setWorldSeed(var3);
      Debug.chat("[世界种子] 设置", CommonUtils.getWorldName(), "的种子为", var3);
   }

   public void onSeedList() {
      Debug.b("[世界种子] 列表");
      ObjectIterator var1 = this.En.object2LongEntrySet().iterator();

      while (var1.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2LongMap.Entry var2 = (it.unimi.dsi.fastutil.objects.Object2LongMap.Entry)var1.next();
         Debug.chat(var2.getKey(), ":", ChatUtils.getDisplayedLong(var2.getLongValue()));
      }
   }

   public void SR(String key) {
      if (this.checkCurrentSeedExistence()) {
         if (Objects.equals(CommonUtils.getWorldName(), key) && this.ae.get()) {
            Debug.b("[种子矿透] 重载Seed Ore Simulation功能");
            this.onReloadSeedOre();
         }
      }
   }

   public void Tm(ArgumentInputStream re) {
      boolean var2 = re.h();
      if (var2) {
         if (!this.isActive()) {
            this.ae.set(true);
         }
      } else if (this.isActive()) {
         this.ae.set(false);
      }
   }

   public void onChunkUpdate(Event<ChunkDataS2CPacket> packet) {
      Packet var2 = (Packet)packet.b;
      if (this.ae.get()) {
         ChunkDataS2CPacket var3 = (ChunkDataS2CPacket)packet.b;
         int var4 = var3.getChunkX();
         int var5 = var3.getChunkZ();
         Tasks.l(() -> this.updateChunk(mc.world.getChunk(var4, var5)), 2);
      }
   }

   public void removeSeed(String key) {
      if (this.En.containsKey(key)) {
         this.En.removeLong(key);
         this.SR(key);
      }
   }

   public void validateCurrentSeed() {
      if (this.checkCurrentSeedExistence()) {
         long var1 = this.En.getLong(CommonUtils.getWorldName());
         Debug.b(Text.literal("[世界种子] 核验当前世界种子中:").formatted(Formatting.GREEN));
         Debug.b(Text.literal("[世界种子] 输入的种子: ").formatted(Formatting.GREEN).append(ChatUtils.getDisplayedLong(var1)));
         long var3 = mc.world.getBiomeAccess().seed;
         Debug.b(Text.literal("[世界种子] 服务器加密种子: ").append(ChatUtils.getDisplayedLong(var3)));
         if (isSeedValid(var1)) {
            Debug.b(Text.literal("[世界种子] 验证通过").formatted(Formatting.GREEN));
         } else {
            Debug.b(Text.literal("[世界种子] 验证失败").formatted(Formatting.RED));
         }
      }
   }

   private void Td() {
      this.Eo.clear();
      this.Ep.clear();
   }

   @Modifiable
   public void SV() {
      this.removeSeed(CommonUtils.getWorldName());
   }

   public void SN() {
      if (mc.world != null) {
         this.onReloadFakeOreVisibleChunks();
         this.Ta();
      }
   }

   public void onFakeOreToggle(boolean va) {
      if (va != this.fakeOre) {
         this.fakeOre = va;
         if (this.ae.get()) {
            if (this.fakeOre) {
               this.SN();
            } else {
               this.onReloadFakeOreVisibleChunks();
            }
         }
      }
   }
}
