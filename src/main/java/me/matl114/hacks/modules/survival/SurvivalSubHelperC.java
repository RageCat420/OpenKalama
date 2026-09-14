package me.matl114.hacks.modules.survival;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.Impl;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OrePlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.ScatteredOreFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer.IndexedFeatures;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;

public class SurvivalSubHelperC {
    public float v;
    private static final AttrKeyValue<Boolean> j = AttrKeyValue.bool("Ancient Debris");
    public int n;
    private static final AttrKeyValue<Boolean> g = AttrKeyValue.bool("Copper");
    public float u;
    public IntProvider r = ConstantIntProvider.create(1);
    private static final AttrKeyValue<Boolean> a = AttrKeyValue.bool("Coal");
    private static final AttrKeyValue<Boolean> h = AttrKeyValue.bool("Emerald");
    public int w;
    private static final AttrKeyValue<Boolean> c = AttrKeyValue.bool("Gold");
    public boolean scattered;
    public static final List<AttrKeyValue<Boolean>> l = new ArrayList<>(Arrays.asList(
            a,
            SurvivalSubHelperC.b,
            c,
            SurvivalSubHelperC.d,
            SurvivalSubHelperC.e,
            SurvivalSubHelperC.f,
            g,
            h,
            SurvivalSubHelperC.i,
            j));
    private static final AttrKeyValue<Boolean> i = AttrKeyValue.bool("Quartz");
    private static final AttrKeyValue<Boolean> d = AttrKeyValue.bool("Redstone");
    public AttrKeyValue<Boolean> q;
    private static final AttrKeyValue<Boolean> e = AttrKeyValue.bool("Diamond");
    public HeightProvider s;
    public Block p;
    public Color x;
    public Block o;
    private static final AttrKeyValue<Boolean> f = AttrKeyValue.bool("Lapis");
    private static final AttrKeyValue<Boolean> b = AttrKeyValue.bool("Iron");
    private static final Map<String, Pair<Block, Block>> k = ImmutableMap.<String, Pair<Block, Block>>builder()
            .put("Coal", Pair.of(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE))
            .put("Iron", Pair.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE))
            .put("Gold", Pair.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE))
            .put("Redstone", Pair.of(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE))
            .put("Diamond", Pair.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE))
            .put("Lapis", Pair.of(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE))
            .put("Copper", Pair.of(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE))
            .put("Emerald", Pair.of(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE))
            .put("Quartz", Pair.of(Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_QUARTZ_ORE))
            .put("Ancient Debris", Pair.of(Blocks.ANCIENT_DEBRIS, Blocks.ANCIENT_DEBRIS))
            .build();
    public HeightContext t;
    public int m;

    private static void registerOre(
            Map<PlacedFeature, SurvivalSubHelperC> map,
            List<IndexedFeatures> indexer,
            Impl<PlacedFeature> oreRegistry,
            RegistryKey<PlacedFeature> oreKey,
            int genStep,
            AttrKeyValue<Boolean> active,
            Color color) {
        PlacedFeature var7 = (PlacedFeature) oreRegistry.getOrThrow(oreKey).value();
        int var8 = ((IndexedFeatures) indexer.get(genStep)).indexMapping().applyAsInt(var7);
        Pair<Block, Block> var9 = k.getOrDefault(active.getKeyName(), Pair.of(Blocks.IRON_ORE, Blocks.IRON_ORE));
        SurvivalSubHelperC var10 =
                new SurvivalSubHelperC(var7, var9.getFirst(), var9.getSecond(), genStep, var8, active, color);
        map.put(var7, var10);
    }

    public static void reloadOreSettings(String value) {
        try {
            Predicate var1 = Pattern.compile(value).asMatchPredicate();

            for (AttrKeyValue var3 : l) {
                if (var1.test(var3.getKeyName().toLowerCase(Locale.ROOT))) {
                    var3.valueChange(var3, "true");
                } else {
                    var3.valueChange(var3, "false");
                }
            }
        } catch (Throwable var4) {
        }
    }

    private SurvivalSubHelperC(
            PlacedFeature feature,
            Block block,
            Block deepslate,
            int step,
            int index,
            AttrKeyValue<Boolean> active,
            Color color) {
        this.u = 1.0F;
        this.m = step;
        this.n = index;
        this.q = active;
        this.x = color;
        this.o = block;
        this.p = deepslate;
        int var8 = MinecraftClient.getInstance().world.getBottomY();
        int var9 = MinecraftClient.getInstance().world.getDimension().logicalHeight();
        this.t = new HeightContext(null, HeightLimitView.create(var8, var9));

        for (PlacementModifier var11 : feature.placementModifiers()) {
            if (var11 instanceof CountPlacementModifier var12) {
                this.r = var12.count;
            } else if (var11 instanceof HeightRangePlacementModifier var13) {
                this.s = var13.height;
            } else if (var11 instanceof RarityFilterPlacementModifier var14) {
                this.u = var14.chance;
            }
        }

        if (((ConfiguredFeature) feature.feature().value()).config() instanceof OreFeatureConfig var16) {
            this.v = var16.discardOnAirChance;
            this.w = var16.size;
            if (((ConfiguredFeature) feature.feature().value()).feature() instanceof ScatteredOreFeature) {
                this.scattered = true;
            }
        } else {
            throw new IllegalStateException("config for " + feature + "is not OreFeatureConfig.class");
        }
    }

    public static void init() {}

    public static Map<RegistryKey<Biome>, List<SurvivalSubHelperC>> getRegistry() {
        WrapperLookup var0 = BuiltinRegistries.createWrapperLookup();
        Impl<PlacedFeature> var1 = var0.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
        Map<RegistryKey<DimensionOptions>, DimensionOptions> var2 = var0.getWrapperOrThrow(RegistryKeys.WORLD_PRESET)
                .getOrThrow(WorldPresets.DEFAULT)
                .value()
                .createDimensionsRegistryHolder()
                .dimensions();
        RegistryKey<DimensionOptions> var3 = CommonUtils.getCurrentDimensionOption();
        DimensionOptions var4 = var2.get(var3);
        Set<RegistryEntry<Biome>> var5 = var4.chunkGenerator().getBiomeSource().getBiomes();
        List<RegistryEntry<Biome>> var6 = var5.stream().toList();
        List<IndexedFeatures> var7 = PlacedFeatureIndexer.collectIndexedFeatures(
                var6, biomeEntry -> biomeEntry.value().getGenerationSettings().getFeatures(), true);
        Map<PlacedFeature, SurvivalSubHelperC> var8 = new HashMap<>();
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_COAL_LOWER, 6, a, new Color(47, 44, 54));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_COAL_UPPER, 6, a, new Color(47, 44, 54));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_IRON_MIDDLE, 6, b, new Color(236, 173, 119));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_IRON_SMALL, 6, b, new Color(236, 173, 119));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_IRON_UPPER, 6, b, new Color(236, 173, 119));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_GOLD, 6, c, new Color(247, 229, 30));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_GOLD_LOWER, 6, c, new Color(247, 229, 30));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_GOLD_EXTRA, 6, c, new Color(247, 229, 30));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_GOLD_NETHER, 7, c, new Color(247, 229, 30));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_GOLD_DELTAS, 7, c, new Color(247, 229, 30));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_REDSTONE, 6, d, new Color(245, 7, 23));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_REDSTONE_LOWER, 6, d, new Color(245, 7, 23));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_DIAMOND, 6, e, new Color(33, 244, 255));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_DIAMOND_BURIED, 6, e, new Color(33, 244, 255));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_DIAMOND_LARGE, 6, e, new Color(33, 244, 255));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_DIAMOND_MEDIUM, 6, e, new Color(33, 244, 255));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_LAPIS, 6, f, new Color(8, 26, 189));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_LAPIS_BURIED, 6, f, new Color(8, 26, 189));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_COPPER, 6, g, new Color(239, 151, 0));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_COPPER_LARGE, 6, g, new Color(239, 151, 0));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_EMERALD, 6, h, new Color(27, 209, 45));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_QUARTZ_NETHER, 7, i, new Color(205, 205, 205));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_QUARTZ_DELTAS, 7, i, new Color(205, 205, 205));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_DEBRIS_SMALL, 7, j, new Color(209, 27, 245));
        registerOre(var8, var7, var1, OrePlacedFeatures.ORE_ANCIENT_DEBRIS_LARGE, 7, j, new Color(209, 27, 245));
        Map<RegistryKey<Biome>, List<SurvivalSubHelperC>> var9 = new HashMap<>();
        var6.forEach(biome -> {
            var9.put(biome.getKey().get(), new ArrayList<>());
            biome.value().getGenerationSettings().getFeatures().stream()
                    .flatMap(RegistryEntryList::stream)
                    .<PlacedFeature>map(RegistryEntry::value)
                    .filter(var8::containsKey)
                    .forEach(feature -> var9.get(biome.getKey().get()).add(var8.get(feature)));
        });
        return var9;
    }
}
