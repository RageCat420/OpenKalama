package me.matl114.jsApi;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Modifiable
public class WorldHelper {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static Entity getEntityByIdUnsafe(int i) {
      return mc.world.getEntityById(i);
   }

   public static BlockState getBlockState(World world, BlockPos pos) {
      return world.getBlockState(pos);
   }

   public static <T> List<T> s(List<T> en) {
      return en.stream()
         .sorted(Comparator.comparingDouble(d -> JsHelper.a(d, Entity.class).getPos().squaredDistanceTo(mc.player.getPos())))
         .collect(Collectors.toCollection(ArrayList::new));
   }

   public static BlockState getDefaultState(Block block) {
      return block.getDefaultState();
   }

   public static BlockEntity getBlockEntity(World world, BlockPos pos) {
      return world.getBlockEntity(pos);
   }

   public static boolean isInWorldBorder(Object pos0) {
      BlockPos var1 = DataHelper.f(pos0);
      return mc.world.getWorldBorder().contains(var1);
   }

   public static boolean isWorldClient(World world) {
      return world.isClient();
   }

   public static List<Entity> getEntitiesUnsafe() {
      return ImmutableList.copyOf(mc.world.getEntities());
   }

   public static Object getBlockData(World world, BlockPos pos) throws Throwable {
      return JsMacrosBridge.i().newBlockData(world.getBlockState(pos), world.getBlockEntity(pos), pos);
   }

   public static Map<String, Object> getStateMap(BlockState state) {
      return (Map<String, Object>)BlockState.CODEC.encodeStart(ItemStackUtils.registry().getOps(JavaOps.INSTANCE), state).getOrThrow();
   }

   public static List<Entity> getEntitiesInBox(Object center, int xhalf, int yhalf, int zhalf) throws ExecutionException, InterruptedException {
      Vec3d var4 = JsHelper.a(center, Vec3d.class);
      Box var5 = new Box(var4.subtract(xhalf, yhalf, zhalf), var4.add(xhalf, yhalf, zhalf));
      ArrayList var6 = new ArrayList();
      FutureTask var7 = new FutureTask<>(() -> {
         mc.world.getEntityLookup().forEachIntersects(var5, var6::add);
         return null;
      });
      var7.get();
      return var6;
   }

   public static List<Entity> getEntities() throws ExecutionException, InterruptedException {
      FutureTask var0 = new FutureTask<>(WorldHelper::getEntitiesUnsafe);
      mc.execute(var0);
      return (List<Entity>)var0.get();
   }

   public static boolean l(int x, int y, int z) {
      return isInWorldBorder(new BlockPos(x, y, z));
   }

   public static List<Entity> v(double distance) throws ExecutionException, InterruptedException {
      List<Entity> var2 = getEntities();
      double var3 = distance * distance;
      return var2.stream().filter(s -> s.squaredDistanceTo(mc.player) <= var3).collect(Collectors.toCollection(ArrayList::new));
   }

   public static BlockState createStateByMap(Map<String, Object> obj) {
      return (BlockState)((Pair)BlockState.CODEC.decode(ItemStackUtils.registry().getOps(JavaOps.INSTANCE), obj).getOrThrow()).getFirst();
   }

   public static Entity getEntityById(int i) throws ExecutionException, InterruptedException {
      FutureTask var1 = new FutureTask<>(() -> mc.world.getEntityById(i));
      mc.execute(var1);
      return (Entity)var1.get();
   }

   public static Entity getEntityByUidUnsafe(Object obj) {
      UUID var2 = obj instanceof UUID var1 ? var1 : UUID.fromString(obj.toString());
      return (Entity)mc.world.getEntityLookup().get(var2);
   }

   public static FluidState getFluidState(World world, BlockPos pos) {
      return world.getFluidState(pos);
   }

   public static void setBlockState(World world, BlockPos pos, BlockState state) {
      mc.execute(() -> world.setBlockState(pos, state));
   }

   public static String getBlockIdOfState(BlockState state) {
      return RegistryHelper.getIdInRegistry(Registries.BLOCK, state.getBlock());
   }

   public static Entity getEntityByUid(Object obj) throws ExecutionException, InterruptedException {
      UUID var2 = obj instanceof UUID var1 ? var1 : UUID.fromString(obj.toString());
      FutureTask var3 = new FutureTask<>(() -> (Entity)mc.world.getEntityLookup().get(var2));
      mc.execute(var3);
      return (Entity)var3.get();
   }

   public static Block getBlockOfState(BlockState state) {
      return state.getBlock();
   }

   public static List<Entity> r() throws Throwable {
      return s(getEntities());
   }
}
