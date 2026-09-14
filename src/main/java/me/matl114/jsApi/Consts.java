package me.matl114.jsApi;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

@Modifiable
public interface Consts {
   Class<?> BlockState = BlockState.class;
   Class<?> JavaMap = Map.class;
   Class<?> BlockEntity = BlockEntity.class;
   Class<?> JavaList = List.class;
   Class<?> HashMap = HashMap.class;
   Class<?> ClientPlayerEntity = ClientPlayerEntity.class;
   Class<?> JavaArrays = Arrays.class;
   Type HitResult_BLOCK = Type.BLOCK;
   Class<?> Matcher = Matcher.class;
   Class<?> CompletableFuture = CompletableFuture.class;
   Direction Direction_NORTH = net.minecraft.util.math.Direction.NORTH;
   Class<?> Byte = Byte.class;
   Class<?> LinkedHashMap = LinkedHashMap.class;
   Class<?> Collections = Collections.class;
   Class<?> NbtElement = NbtElement.class;
   Type HitResult_MISS = Type.MISS;
   Class<?> JavaNumber = Number.class;
   Class<?> HandledScreen = HandledScreen.class;
   Direction Direction_DOWN = net.minecraft.util.math.Direction.DOWN;
   Class<?> DoubleStream = DoubleStream.class;
   Class<?> File = File.class;
   Class<?> AtomicReference = AtomicReference.class;
   Class<?> MobEntity = MobEntity.class;
   Class<?> Vec3d = Vec3d.class;
   Class<?> Integer = Integer.class;
   Class<?> Pattern = Pattern.class;
   Class<?> ItemStack = ItemStack.class;
   Class<?> TreeMap = TreeMap.class;
   Class<?> Float = Float.class;
   Class<?> NbtList = NbtList.class;
   Class<?> Chunk = Chunk.class;
   Class<?> Collection = Collection.class;
   Class<?> Item = Item.class;
   Class<?> Random = Random.class;
   Class<?> NbtString = NbtString.class;
   Class<?> ArrayList = ArrayList.class;
   Class<?> Container = Inventory.class;
   Class<?> Inventory = Inventory.class;
   Class<?> PlayerInventory = PlayerInventory.class;
   Class<?> Block = Block.class;
   Class<?> FluidState = FluidState.class;
   Class<?> LongStream = LongStream.class;
   Class<?> LinkedList = LinkedList.class;
   Supplier<Map<String, String>> AliasMap = Suppliers.memoize(
      () -> ImmutableMap.<String, String>builder().put("NBT", "NbtElement").put("NBTMap", "NbtCompound").put("NBTList", "NbtList").build()
   );
   Class<?> AnimalEntity = AnimalEntity.class;
   Class<?> AtomicInteger = AtomicInteger.class;
   Class<?> HostileEntity = HostileEntity.class;
   Class<?> Screen = Screen.class;
   Class<?> Color = Color.class;
   Class<?> Short = Short.class;
   Class<?> BlockHitResult = BlockHitResult.class;
   Direction Direction_WEST = net.minecraft.util.math.Direction.WEST;
   BlockPos BlockPos_ZERO = net.minecraft.util.math.BlockPos.ORIGIN;
   Class<?> ClientWorld = ClientWorld.class;
   Class<?> Comparator = Comparator.class;
   MinecraftClient MC = MinecraftClient.getInstance();
   Class<?> TreeSet = TreeSet.class;
   Class<?> HashSet = HashSet.class;
   Class<?> PlayerEntity = PlayerEntity.class;
   Class<?> Supplier = Supplier.class;
   Class<?> Function = Function.class;
   Class<?> Path = Path.class;
   Class<?> Clazz = Class.class;
   Class<?> Hand = Hand.class;
   Direction Direction_EAST = net.minecraft.util.math.Direction.EAST;
   Class<?> Enchantment = Enchantment.class;
   Class<?> JavaObject = Object.class;
   Vec3d Vec3d_ZERO = net.minecraft.util.math.Vec3d.ZERO;
   Direction Direction_UP = net.minecraft.util.math.Direction.UP;
   Class<?> WorldChunk = WorldChunk.class;
   Class<?> JavaSystem = System.class;
   Class<?> LivingEntity = LivingEntity.class;
   Class<?> NbtInt = NbtInt.class;
   Class<?> Consumer = Consumer.class;
   Class<?> Long = Long.class;
   Class<?> BiFunction = BiFunction.class;
   Class<?> Callable = Callable.class;
   Class<?> NbtCompound = NbtElement.class;
   Class<?> Paths = Paths.class;
   Class<?> JavaMath = Math.class;
   Type HitResult_ENTITY = Type.ENTITY;
   Class<?> UUID = UUID.class;
   Class<?> BlockPos = BlockPos.class;
   Class<?> IWorld = World.class;
   Supplier<Map<String, Object>> ConstantMap = Suppliers.memoize(
      () -> Arrays.stream(Consts.class.getFields())
         .filter(field -> Modifier.isStatic(field.getModifiers()))
         .filter(f -> !Supplier.class.isAssignableFrom(f.getType()))
         .map(f -> {
            try {
               return Pair.of(f.getName(), f.get(null));
            } catch (IllegalAccessException var2) {
               throw new RuntimeException(var2);
            }
         })
         .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
   );
   Class<?> Runnable = Runnable.class;
   Class<?> AtomicLong = AtomicLong.class;
   Class<?> Text = Text.class;
   Class<?> BigInteger = BigInteger.class;
   Class<?> ThreadLocalRandom = ThreadLocalRandom.class;
   Class<?> Predicate = Predicate.class;
   Class<?> Optional = Optional.class;
   Class<?> Direction = Direction.class;
   Class<?> Iterator = Iterator.class;
   Class<?> Objects = Objects.class;
   Class<?> EntityHitResult = EntityHitResult.class;
   Class<?> Slot = Slot.class;
   Class<?> JavaBoolean = Boolean.class;
   Class<?> Stream = Stream.class;
   Class<?> PacketByteBuf = PacketByteBuf.class;
   Class<?> IntStream = IntStream.class;
   Class<?> Character = Character.class;
   Class<?> IPlayer = PlayerEntity.class;
   Class<?> BiConsumer = BiConsumer.class;
   Class<?> Files = Files.class;
   Class<?> HitResult = HitResult.class;
   Class<?> AtomicBoolean = AtomicBoolean.class;
   Class<?> BigDecimal = BigDecimal.class;
   Class<?> Double = Double.class;
   Direction Direction_SOUTH = net.minecraft.util.math.Direction.SOUTH;
   Class<?> JavaSet = Set.class;
   Class<?> Entity = Entity.class;
   Class<?> JavaString = String.class;

   static void importConstantToContext(Object context0) throws Throwable {
      Object bindingMap = JsMacrosBridge.i().h(context0);
      importConstantNames(bindingMap);
   }

   static void importConstantNames(Object varMap) throws Throwable {
      Map<String, Object> map = ConstantMap.get();
      Map<String, Object> obj = new LinkedHashMap<>(map);
      Map<String, String> alias = AliasMap.get();
      alias.forEach((k, v) -> {
         if (map.containsKey(v)) {
            obj.put(k, map.get(v));
         }
      });
      Method m = ReflectHelper.x(varMap, "putMember", String.class, Object.class).get(0);
      Method get = ReflectHelper.x(varMap, "getMember", String.class).get(0);
      Method invoke = ReflectHelper.x(varMap, "invokeMember", String.class, Object[].class).get(0);
      Object javaFactory = get.invoke(varMap, "Java");
      obj.forEach((k, v) -> {
         try {
            Object toPut = v;
            if (v instanceof Class<?> clazz) {
               toPut = invoke.invoke(javaFactory, "type", new Object[]{clazz.getName()});
            }

            m.invoke(varMap, k, toPut);
         } catch (InvocationTargetException | IllegalAccessException var8) {
            throw new RuntimeException(var8);
         }
      });
   }
}
