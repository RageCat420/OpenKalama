package me.matl114.utils;

import java.util.Set;
import java.util.function.BiPredicate;
import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@KalamaHelperHelperA
public enum KalamaHelperHelperXX {
    qK(
            Items.PUMPKIN_SEEDS,
            false,
            FarmingUtils.allStates(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM, Blocks.PUMPKIN),
            Set.of(Blocks.PUMPKIN),
            FarmingUtils.o(Blocks.PUMPKIN_STEM.getDefaultState()),
            (world, pos) ->
                    FarmingUtils.isAttachedFruitHarvestable(world, pos, Blocks.PUMPKIN, Blocks.ATTACHED_PUMPKIN_STEM)),
    qO(
            Items.CACTUS,
            false,
            FarmingUtils.allStates(Blocks.CACTUS),
            Set.of(Blocks.CACTUS),
            FarmingUtils.o(Blocks.CACTUS.getDefaultState()),
            (world, pos) -> FarmingUtils.isVerticalHarvestable(world, pos, Blocks.CACTUS)),
    qP(
            Items.SUGAR_CANE,
            false,
            FarmingUtils.allStates(Blocks.SUGAR_CANE),
            Set.of(Blocks.SUGAR_CANE),
            FarmingUtils.o(Blocks.SUGAR_CANE.getDefaultState()),
            (world, pos) -> FarmingUtils.isVerticalHarvestable(world, pos, Blocks.SUGAR_CANE)),
    qG(
            Items.BEETROOT_SEEDS,
            true,
            FarmingUtils.allStates(Blocks.BEETROOTS),
            Set.of(Blocks.BEETROOTS),
            FarmingUtils.o(Blocks.BEETROOTS.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.BEETROOTS) && (Integer) var2.get(BeetrootsBlock.AGE) >= 3;
            }),
    qS(
            Items.GLOW_BERRIES,
            false,
            FarmingUtils.allStates(Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT),
            Set.of(Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT),
            FarmingUtils.o(Blocks.CAVE_VINES.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return (var2.isOf(Blocks.CAVE_VINES) || var2.isOf(Blocks.CAVE_VINES_PLANT))
                        && (Boolean) var2.get(CaveVines.BERRIES);
            }),
    qF(
            Items.POTATO,
            true,
            FarmingUtils.allStates(Blocks.POTATOES),
            Set.of(Blocks.POTATOES),
            FarmingUtils.o(Blocks.POTATOES.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.POTATOES) && (Integer) var2.get(CropBlock.AGE) >= 7;
            }),
    qM(
            Items.COCOA_BEANS,
            true,
            FarmingUtils.allStates(Blocks.COCOA),
            Set.of(Blocks.COCOA),
            FarmingUtils.o(Blocks.COCOA.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.COCOA) && (Integer) var2.get(CocoaBlock.AGE) >= 2;
            }),
    qN(
            Items.SWEET_BERRIES,
            false,
            FarmingUtils.allStates(Blocks.SWEET_BERRY_BUSH),
            Set.of(Blocks.SWEET_BERRY_BUSH),
            FarmingUtils.o(Blocks.SWEET_BERRY_BUSH.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.SWEET_BERRY_BUSH) && (Integer) var2.get(SweetBerryBushBlock.AGE) > 1;
            }),
    qH(
            Items.TORCHFLOWER_SEEDS,
            true,
            FarmingUtils.allStates(Blocks.TORCHFLOWER_CROP, Blocks.TORCHFLOWER),
            Set.of(Blocks.TORCHFLOWER),
            FarmingUtils.o(Blocks.TORCHFLOWER_CROP.getDefaultState()),
            (world, pos) -> world.getBlockState(pos).isOf(Blocks.TORCHFLOWER)),
    qE(
            Items.CARROT,
            true,
            FarmingUtils.allStates(Blocks.CARROTS),
            Set.of(Blocks.CARROTS),
            FarmingUtils.o(Blocks.CARROTS.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.CARROTS) && (Integer) var2.get(CropBlock.AGE) >= 7;
            }),
    qQ(
            Items.BAMBOO,
            false,
            FarmingUtils.allStates(Blocks.BAMBOO, Blocks.BAMBOO_SAPLING),
            Set.of(Blocks.BAMBOO),
            FarmingUtils.o(Blocks.BAMBOO_SAPLING.getDefaultState(), Blocks.BAMBOO.getDefaultState()),
            FarmingUtils::isBambooHarvestable),
    qL(
            Items.NETHER_WART,
            true,
            FarmingUtils.allStates(Blocks.NETHER_WART),
            Set.of(Blocks.NETHER_WART),
            FarmingUtils.o(Blocks.NETHER_WART.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.NETHER_WART) && (Integer) var2.get(NetherWartBlock.AGE) >= 3;
            }),
    qD(
            Items.WHEAT_SEEDS,
            true,
            FarmingUtils.allStates(Blocks.WHEAT),
            Set.of(Blocks.WHEAT),
            FarmingUtils.o(Blocks.WHEAT.getDefaultState()),
            (world, pos) -> {
                BlockState var2 = world.getBlockState(pos);
                return var2.isOf(Blocks.WHEAT) && (Integer) var2.get(CropBlock.AGE) >= 7;
            }),
    qR(
            Items.KELP,
            false,
            FarmingUtils.allStates(Blocks.KELP, Blocks.KELP_PLANT),
            Set.of(Blocks.KELP, Blocks.KELP_PLANT),
            FarmingUtils.o(Blocks.KELP.getDefaultState()),
            (world, pos) -> FarmingUtils.isVerticalHarvestable(world, pos, Blocks.KELP, Blocks.KELP_PLANT)),
    qJ(
            Items.MELON_SEEDS,
            false,
            FarmingUtils.allStates(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM, Blocks.MELON),
            Set.of(Blocks.MELON),
            FarmingUtils.o(Blocks.MELON_STEM.getDefaultState()),
            (world, pos) ->
                    FarmingUtils.isAttachedFruitHarvestable(world, pos, Blocks.MELON, Blocks.ATTACHED_MELON_STEM)),
    qI(
            Items.PITCHER_POD,
            true,
            FarmingUtils.allStates(Blocks.PITCHER_CROP),
            Set.of(Blocks.PITCHER_CROP),
            FarmingUtils.o((BlockState)
                    Blocks.PITCHER_CROP.getDefaultState().with(PitcherCropBlock.HALF, DoubleBlockHalf.LOWER)),
            FarmingUtils::isPitcherHarvestable);
    private final Set<BlockState> qX;
    private final boolean qU;
    private final Set<Block> qW;
    private final BiPredicate<World, BlockPos> qZ;
    // $VF: synthetic field
    private final Set<Block> qY;
    private final Item qT;
    private final Set<BlockState> qV;

    @KalamaHelperHelperA
    public Set<BlockState> zO() {
        return this.qX;
    }

    @KalamaHelperHelperA
    public boolean needReplant() {
        return this.qU;
    }

    @KalamaHelperHelperA
    public Item zJ() {
        return this.qT;
    }

    @KalamaHelperHelperA
    public Set<Block> zP() {
        return this.qY;
    }

    @KalamaHelperHelperA
    public Set<Block> zM() {
        return this.qW;
    }

    @KalamaHelperHelperA
    public boolean canHarvest(World world, BlockPos pos) {
        return this.qZ.test(world, pos);
    }

    private KalamaHelperHelperXX(
            Item seedItem,
            boolean needReplant,
            Set<BlockState> optionalStates,
            Set<Block> harvestableBlocks,
            Set<BlockState> placementStates,
            BiPredicate<World, BlockPos> harvestPredicate) {
        this.qT = seedItem;
        this.qU = needReplant;
        this.qV = optionalStates;
        this.qW = harvestableBlocks;
        this.qX = placementStates;
        this.qY = FarmingUtils.blocksOfStates(optionalStates);
        this.qZ = harvestPredicate;
    }

    @KalamaHelperHelperA
    public Set<BlockState> zK() {
        return this.qV;
    }
}
