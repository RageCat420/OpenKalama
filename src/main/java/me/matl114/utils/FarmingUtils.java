package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.world.World;

@KalamaHelperHelperA
public class FarmingUtils {
   private static final Set<Class<? extends Entity>> b = Set.of(ZombieHorseEntity.class);
   private static final Set<Item> a = o(
      Items.WHEAT,
      Items.CARROT,
      Items.POTATO,
      Items.BEETROOT,
      Items.WHEAT_SEEDS,
      Items.MELON_SEEDS,
      Items.PUMPKIN_SEEDS,
      Items.BEETROOT_SEEDS,
      Items.TORCHFLOWER_SEEDS,
      Items.PITCHER_POD,
      Items.GOLDEN_CARROT,
      Items.DANDELION,
      Items.BAMBOO,
      Items.SWEET_BERRIES,
      Items.GLOW_BERRIES,
      Items.SEAGRASS,
      Items.SLIME_BALL,
      Items.TROPICAL_FISH_BUCKET,
      Items.SPIDER_EYE,
      Items.CACTUS,
      Items.WARPED_FUNGUS,
      Items.CRIMSON_FUNGUS,
      Items.SNOWBALL,
      Items.RED_MUSHROOM,
      Items.RABBIT_FOOT,
      Items.HAY_BLOCK,
      Items.SUGAR,
      Items.APPLE,
      Items.GOLDEN_APPLE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.COD,
      Items.COOKED_COD,
      Items.SALMON,
      Items.COOKED_SALMON,
      Items.TROPICAL_FISH,
      Items.PUFFERFISH,
      Items.RABBIT_STEW,
      Items.BEEF,
      Items.COOKED_BEEF,
      Items.PORKCHOP,
      Items.COOKED_PORKCHOP,
      Items.MUTTON,
      Items.COOKED_MUTTON,
      Items.CHICKEN,
      Items.COOKED_CHICKEN,
      Items.RABBIT,
      Items.COOKED_RABBIT,
      Items.ROTTEN_FLESH,
      Items.PUFFERFISH_BUCKET,
      Items.COD_BUCKET,
      Items.SALMON_BUCKET,
      Items.POPPY,
      Items.BLUE_ORCHID,
      Items.ALLIUM,
      Items.AZURE_BLUET,
      Items.RED_TULIP,
      Items.ORANGE_TULIP,
      Items.WHITE_TULIP,
      Items.PINK_TULIP,
      Items.OXEYE_DAISY,
      Items.CORNFLOWER,
      Items.LILY_OF_THE_VALLEY,
      Items.WITHER_ROSE,
      Items.TORCHFLOWER,
      Items.SUNFLOWER,
      Items.LILAC,
      Items.PEONY,
      Items.ROSE_BUSH,
      Items.PITCHER_PLANT,
      Items.FLOWERING_AZALEA_LEAVES,
      Items.FLOWERING_AZALEA,
      Items.MANGROVE_PROPAGULE,
      Items.CHERRY_LEAVES,
      Items.PINK_PETALS,
      Items.CHORUS_FLOWER,
      Items.SPORE_BLOSSOM
   );
   private static final Predicate<ItemStack> c = stack -> true;

   @KalamaHelperHelperA
   public static boolean isVerticalHarvestable(World world, BlockPos pos, Block... blocks) {
      BlockState var3 = world.getBlockState(pos);
      BlockState var4 = world.getBlockState(pos.down());

      for (Block var8 : blocks) {
         if (var3.isOf(var8)) {
            for (Block var12 : blocks) {
               if (var4.isOf(var12)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @KalamaHelperHelperA
   public static Set<ItemStack> getBreedItems(Entity entity) {
      if (entity instanceof AnimalEntity var1 && !m(entity)) {
         LinkedHashSet var2 = new LinkedHashSet();

         for (Item var4 : a) {
            ItemStack var5 = new ItemStack(var4);
            if (var1.isBreedingItem(var5)) {
               var2.add(var5);
            }
         }

         return Collections.unmodifiableSet(var2);
      } else {
         return Set.of();
      }
   }

   @KalamaHelperHelperA
   public static KalamaHelperHelperXX b(Block block) {
      for (KalamaHelperHelperXX var4 : KalamaHelperHelperXX.values()) {
         if (var4.zP().contains(block)) {
            return var4;
         }
      }

      return null;
   }

   @SafeVarargs
   @KalamaHelperHelperA
   public static <T> Set<T> o(T... values) {
      return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList((T[])values)));
   }

   @KalamaHelperHelperA
   private static Predicate<ItemStack> g(Item item) {
      return stack -> stack != null && stack.isOf(item);
   }

   @KalamaHelperHelperA
   private static boolean isPitcherHarvestable(World world, BlockPos pos) {
      BlockState var2 = world.getBlockState(pos);
      if (!var2.isOf(Blocks.PITCHER_CROP)) {
         return false;
      } else if (var2.get(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER) {
         return (Integer)var2.get(PitcherCropBlock.AGE) >= 4;
      } else {
         BlockState var3 = world.getBlockState(pos.down());
         return var3.isOf(Blocks.PITCHER_CROP) && var3.get(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER && (Integer)var3.get(PitcherCropBlock.AGE) >= 4;
      }
   }

   @KalamaHelperHelperA
   private static Predicate<ItemStack> h(Item... items) {
      return stack -> {
         if (stack == null) {
            return false;
         } else {
            for (Item var5 : items) {
               if (stack.isOf(var5)) {
                  return true;
               }
            }

            return false;
         }
      };
   }

   @KalamaHelperHelperA
   private static BlockHitResult hit(BlockPos pos, Direction side, double x, double y, double z) {
      return new BlockHitResult(new Vec3d(pos.getX() + x, pos.getY() + y, pos.getZ() + z), side, pos, false);
   }

   @KalamaHelperHelperA
   private static boolean isBambooHarvestable(World world, BlockPos pos) {
      BlockState var2 = world.getBlockState(pos);
      if (!var2.isOf(Blocks.BAMBOO)) {
         return false;
      } else {
         BlockState var3 = world.getBlockState(pos.down());
         return var3.isOf(Blocks.BAMBOO) || var3.isOf(Blocks.BAMBOO_SAPLING);
      }
   }

   @KalamaHelperHelperA
   private static boolean canIncreaseComposterLevel(ItemStack stack) {
      return stack != null
         && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem())
         && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(stack.getItem()) > 0.0F;
   }

   @KalamaHelperHelperA
   public static Set<Block> blocksOfStates(Set<BlockState> states) {
      LinkedHashSet var1 = new LinkedHashSet();

      for (BlockState var3 : states) {
         var1.add(var3.getBlock());
      }

      return Collections.unmodifiableSet(var1);
   }

   @KalamaHelperHelperA
   public static BlockHitResult tryPlantAt(World world, BlockPos pos, KalamaHelperHelperXX plantType) {
      if (world != null && pos != null && plantType != null) {
         BlockState var3 = world.getBlockState(pos);
         if (!var3.isAir() && !var3.isOf(Blocks.WATER)) {
            return null;
         } else {
            for (BlockState var5 : plantType.zO()) {
               if (var5.canPlaceAt(world, pos)) {
                  Direction var6 = getSupportDirection(var5);
                  BlockPos var7 = pos.offset(var6);
                  return new BlockHitResult(Vec3d.ofCenter(var7), var6.getOpposite(), var7, false);
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @KalamaHelperHelperA
   public static Set<BlockState> allStates(Block... blocks) {
      LinkedHashSet var1 = new LinkedHashSet();

      for (Block var5 : blocks) {
         var1.addAll(var5.getStateManager().getStates());
      }

      return Collections.unmodifiableSet(var1);
   }

   @KalamaHelperHelperA
   private static Direction getSupportDirection(BlockState state) {
      if (state.contains(CocoaBlock.FACING)) {
         return (Direction)state.get(CocoaBlock.FACING);
      } else {
         return !state.isOf(Blocks.CAVE_VINES) && !state.isOf(Blocks.CAVE_VINES_PLANT) ? Direction.DOWN : Direction.UP;
      }
   }

   @KalamaHelperHelperA
   private static Item getRequiredCandleItem(Block candleCakeBlock) {
      Identifier var1 = Registries.BLOCK.getId(candleCakeBlock);
      String var2 = var1.getPath();
      if (!var2.endsWith("_cake")) {
         return null;
      } else {
         Item var3 = (Item)Registries.ITEM.get(var1.withPath(var2.substring(0, var2.length() - 5)));
         return var3 == Items.AIR ? null : var3;
      }
   }

   @KalamaHelperHelperA
   private static boolean m(Entity entity) {
      for (Class var2 : b) {
         if (var2.isInstance(entity)) {
            return true;
         }
      }

      return false;
   }

   @KalamaHelperHelperA
   public static boolean isAttachedFruitHarvestable(World world, BlockPos pos, Block fruit, Block attachedStem) {
      BlockState var4 = world.getBlockState(pos);
      if (!var4.isOf(fruit)) {
         return false;
      } else {
         for (Direction var6 : Type.HORIZONTAL) {
            if (world.getBlockState(pos.offset(var6)).isOf(attachedStem)) {
               return true;
            }
         }

         return false;
      }
   }

   @KalamaHelperHelperA
   public static Pair<Predicate<ItemStack>, BlockHitResult> getInteractTransition(World world, BlockPos pos, BlockState state1, BlockState state2) {
      if (world != null && pos != null && state1 != null && state2 != null) {
         Block var4 = state1.getBlock();
         Block var5 = state2.getBlock();
         if (var4 == var5) {
            if (var4 instanceof SlabBlock
               && state1.get(SlabBlock.TYPE) != SlabType.DOUBLE
               && state2.equals(((BlockState)state1.with(SlabBlock.TYPE, SlabType.DOUBLE)).with(SlabBlock.WATERLOGGED, false))) {
               BlockHitResult var13 = state1.get(SlabBlock.TYPE) == SlabType.BOTTOM
                  ? hit(pos, Direction.UP, 0.5, 1.0, 0.5)
                  : hit(pos, Direction.DOWN, 0.5, 0.0, 0.5);
               return Pair.of(g(var4.asItem()), var13);
            }

            if (var4 instanceof SnowBlock && state2.equals(state1.with(SnowBlock.LAYERS, Math.min(8, (Integer)state1.get(SnowBlock.LAYERS) + 1)))) {
               return Pair.of(g(var4.asItem()), hit(pos, Direction.UP, 0.5, 1.0, 0.5));
            }

            if (var4 instanceof CandleBlock) {
               if ((Integer)state1.get(CandleBlock.CANDLES) < 4
                  && state2.equals(state1.with(CandleBlock.CANDLES, (Integer)state1.get(CandleBlock.CANDLES) + 1))) {
                  return Pair.of(g(var4.asItem()), null);
               }

               if ((Boolean)state1.get(CandleBlock.LIT) && state2.equals(state1.with(CandleBlock.LIT, false))) {
                  return Pair.of(ItemStack::isEmpty, null);
               }

               if (!(Boolean)state1.get(CandleBlock.LIT) && !(Boolean)state1.get(CandleBlock.WATERLOGGED) && state2.equals(state1.with(CandleBlock.LIT, true))) {
                  return Pair.of(h(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE), null);
               }
            }

            if (var4 instanceof SeaPickleBlock
               && (Integer)state1.get(SeaPickleBlock.PICKLES) < 4
               && state2.equals(state1.with(SeaPickleBlock.PICKLES, (Integer)state1.get(SeaPickleBlock.PICKLES) + 1))) {
               return Pair.of(g(var4.asItem()), null);
            }

            if (var4 instanceof FlowerbedBlock
               && (Integer)state1.get(FlowerbedBlock.FLOWER_AMOUNT) < 4
               && state2.equals(state1.with(FlowerbedBlock.FLOWER_AMOUNT, (Integer)state1.get(FlowerbedBlock.FLOWER_AMOUNT) + 1))) {
               return Pair.of(g(var4.asItem()), null);
            }

            if (var4 instanceof RepeaterBlock && state2.equals(state1.cycle(RepeaterBlock.DELAY))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof ComparatorBlock
               && state1.get(ComparatorBlock.MODE) != state2.get(ComparatorBlock.MODE)
               && state2.get(ComparatorBlock.MODE) == ((BlockState)state1.cycle(ComparatorBlock.MODE)).get(ComparatorBlock.MODE)
               && state1.get(ComparatorBlock.FACING) == state2.get(ComparatorBlock.FACING)) {
               return Pair.of(c, null);
            }

            if (var4 instanceof NoteBlock && state2.equals(state1.with(NoteBlock.NOTE, ((Integer)state1.get(NoteBlock.NOTE) + 1) % 25))) {
               return Pair.of(c, hit(pos, Direction.NORTH, 0.5, 0.5, 0.0));
            }

            if (var4 instanceof DoorBlock && state2.equals(state1.cycle(DoorBlock.OPEN))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof TrapdoorBlock && state2.equals(state1.cycle(TrapdoorBlock.OPEN))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof FenceGateBlock
               && state1.get(FenceGateBlock.FACING) == state2.get(FenceGateBlock.FACING)
               && state2.equals(state1.cycle(FenceGateBlock.OPEN))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof LeverBlock && state2.equals(state1.cycle(LeverBlock.POWERED))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof ButtonBlock && !(Boolean)state1.get(ButtonBlock.POWERED) && state2.equals(state1.with(ButtonBlock.POWERED, true))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof CakeBlock
               && (Integer)state1.get(CakeBlock.BITES) < 6
               && state2.equals(state1.with(CakeBlock.BITES, (Integer)state1.get(CakeBlock.BITES) + 1))) {
               return Pair.of(c, null);
            }

            if (var4 instanceof RespawnAnchorBlock
               && (Integer)state1.get(RespawnAnchorBlock.CHARGES) < 4
               && state2.equals(state1.with(RespawnAnchorBlock.CHARGES, (Integer)state1.get(RespawnAnchorBlock.CHARGES) + 1))) {
               return Pair.of(g(Items.GLOWSTONE), null);
            }

            if (var4 instanceof FlowerPotBlock var6
               && var5 instanceof FlowerPotBlock var7
               && var6.getContent() != Blocks.AIR
               && var7.getContent() == Blocks.AIR) {
               return Pair.of(ItemStack::isEmpty, null);
            }

            if (var4 instanceof BeehiveBlock && (Integer)state1.get(BeehiveBlock.HONEY_LEVEL) >= 5 && state2.equals(state1.with(BeehiveBlock.HONEY_LEVEL, 0))) {
               return Pair.of(h(Items.SHEARS, Items.GLASS_BOTTLE), null);
            }

            if (var4 instanceof ComposterBlock) {
               int var9 = (Integer)state1.get(ComposterBlock.LEVEL);
               int var14 = (Integer)state2.get(ComposterBlock.LEVEL);
               if (var9 == 0 && var14 == 1) {
                  return Pair.of(FarmingUtils::canIncreaseComposterLevel, null);
               }

               if (var9 == 8 && var14 == 0) {
                  return Pair.of(c, null);
               }
            }
         }

         if (var4 instanceof CakeBlock && var5 instanceof CandleCakeBlock && (Integer)state1.get(CakeBlock.BITES) == 0) {
            Item var10 = getRequiredCandleItem(var5);
            if (var10 != null) {
               return Pair.of(g(var10), null);
            }
         }

         if (var4 instanceof CandleCakeBlock && var5 instanceof CakeBlock && state2.equals(Blocks.CAKE.getDefaultState().with(CakeBlock.BITES, 1))) {
            return Pair.of(c, null);
         } else {
            if (var4 instanceof CandleCakeBlock && var5 instanceof CandleCakeBlock) {
               if ((Boolean)state1.get(CandleCakeBlock.LIT) && state2.equals(state1.with(CandleCakeBlock.LIT, false))) {
                  return Pair.of(ItemStack::isEmpty, hit(pos, Direction.UP, 0.5, 0.75, 0.5));
               }

               if (!(Boolean)state1.get(CandleCakeBlock.LIT) && state2.equals(state1.with(CandleCakeBlock.LIT, true))) {
                  return Pair.of(h(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE), null);
               }
            }

            if (var4 instanceof FlowerPotBlock var11
               && var5 instanceof FlowerPotBlock var15
               && var11.getContent() == Blocks.AIR
               && var15.getContent() != Blocks.AIR) {
               Block var8 = var15.getContent();
               return Pair.of((Predicate<ItemStack>)stack -> stack.getItem() instanceof BlockItem var3 && var3.getBlock() == var8, null);
            } else if (var4 instanceof PumpkinBlock && var5 == Blocks.CARVED_PUMPKIN) {
               Direction var12 = (Direction)state2.get(CarvedPumpkinBlock.FACING);
               return Pair.of(g(Items.SHEARS), hit(pos, var12, 0.5, 0.5, 0.5));
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   @KalamaHelperHelperA
   public static KalamaHelperHelperXX a(Item item) {
      for (KalamaHelperHelperXX var4 : KalamaHelperHelperXX.values()) {
         if (var4.zJ() == item) {
            return var4;
         }
      }

      return null;
   }

   @KalamaHelperHelperA
   private static boolean isStatuePoseSwitchItem(ItemStack stack) {
      return stack != null && !stack.isIn(ItemTags.AXES) && !stack.isOf(Items.HONEYCOMB);
   }

   @KalamaHelperHelperA
   public static boolean e(Entity entity) {
      return entity instanceof AnimalEntity && !m(entity) && !getBreedItems(entity).isEmpty();
   }
}
