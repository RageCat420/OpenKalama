package me.matl114.hacks.modules.render;

import java.util.HashMap;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

class RenderSubHelperZ extends HashMap<String, List<Item>> {
    RenderSubHelperZ() {
        this.put("CLT_PLANT_SCRAPPY", StorageDisplay.ofNullableList(Items.NETHERITE_SCRAP));
        this.put("CLT_PLANT_NETHERRACK", StorageDisplay.ofNullableList(Items.NETHERRACK));
        this.put("CLT_PLANT_WITHER", StorageDisplay.ofNullableList(Items.NETHER_STAR));
        this.put("CLT_PLANT_RAW_IRON", StorageDisplay.ofNullableList(Items.RAW_IRON));
        this.put(
                "CLT_PLANT_ELDER_GUARDIAN",
                StorageDisplay.ofNullableList(Items.PRISMARINE_SHARD, Items.PRISMARINE_CRYSTALS, Items.SPONGE));
        this.put("CLT_PLANT_ENDERMAN", StorageDisplay.ofNullableList(Items.ENDER_PEARL, Items.ENDER_EYE));
        this.put(
                "CLT_PLANT_TERRA",
                StorageDisplay.ofNullableList(
                        Items.BLACK_TERRACOTTA,
                        Items.BLUE_TERRACOTTA,
                        Items.BROWN_TERRACOTTA,
                        Items.CYAN_TERRACOTTA,
                        Items.GRAY_TERRACOTTA,
                        Items.GREEN_TERRACOTTA,
                        Items.LIGHT_BLUE_TERRACOTTA,
                        Items.LIGHT_GRAY_TERRACOTTA,
                        Items.LIME_TERRACOTTA,
                        Items.MAGENTA_TERRACOTTA,
                        Items.ORANGE_TERRACOTTA,
                        Items.PINK_TERRACOTTA,
                        Items.PURPLE_TERRACOTTA,
                        Items.RED_TERRACOTTA,
                        Items.WHITE_TERRACOTTA,
                        Items.YELLOW_TERRACOTTA));
        this.put("CLT_PLANT_GLASS", StorageDisplay.ofNullableList(Items.GLASS));
        this.put("CLT_PLANT_SKELETON", StorageDisplay.ofNullableList(Items.BONE, Items.ARROW, Items.SKELETON_SKULL));
        this.put(
                "CLT_PLANT_SPIDER",
                StorageDisplay.ofNullableList(Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.STRING));
        this.put("CLT_PLANT_GRAVEL", StorageDisplay.ofNullableList(Items.GRAVEL));
        this.put("CLT_PLANT_RAW_GOLD", StorageDisplay.ofNullableList(Items.RAW_GOLD));
        this.put(
                "CLT_PLANT_WAXY",
                StorageDisplay.ofNullableList(
                        Items.BLACK_CANDLE,
                        Items.BLUE_CANDLE,
                        Items.BROWN_CANDLE,
                        Items.CYAN_CANDLE,
                        Items.GRAY_CANDLE,
                        Items.GREEN_CANDLE,
                        Items.LIGHT_BLUE_CANDLE,
                        Items.LIGHT_GRAY_CANDLE,
                        Items.LIME_CANDLE,
                        Items.MAGENTA_CANDLE,
                        Items.ORANGE_CANDLE,
                        Items.PINK_CANDLE,
                        Items.PURPLE_CANDLE,
                        Items.RED_CANDLE,
                        Items.WHITE_CANDLE,
                        Items.YELLOW_CANDLE));
        this.put("CLT_PLANT_CHICKEN", StorageDisplay.ofNullableList(Items.CHICKEN, Items.FEATHER, Items.EGG));
        this.put("CLT_PLANT_GHAST", StorageDisplay.ofNullableList(Items.GHAST_TEAR));
        this.put("CLT_PLANT_MUD", StorageDisplay.ofNullableList(Items.MUD));
        this.put("CLT_PLANT_DARK_GRASS", StorageDisplay.ofNullableList(Items.WARPED_NYLIUM, Items.CRIMSON_NYLIUM));
        this.put("CLT_PLANT_COBBLESTONE", StorageDisplay.ofNullableList(Items.COBBLESTONE));
        this.put("CLT_PLANT_REINFORCED", StorageDisplay.ofNullableList(Items.REINFORCED_DEEPSLATE));
        this.put("CLT_PLANT_GOAT", StorageDisplay.ofNullableList(Items.GOAT_HORN));
        this.put("CLT_PLANT_BLAZE", StorageDisplay.ofNullableList(Items.BLAZE_POWDER, Items.BLAZE_ROD));
        this.put("CLT_PLANT_COW", StorageDisplay.ofNullableList(Items.BEEF, Items.LEATHER));
        this.put("CLT_PLANT_DIAMOND", StorageDisplay.ofNullableList(Items.DIAMOND));
        this.put("CLT_PLANT_PIG", StorageDisplay.ofNullableList(Items.PORKCHOP));
        this.put("CLT_PLANT_MAGMA", StorageDisplay.ofNullableList(Items.MAGMA_BLOCK));
        this.put("CLT_PLANT_ECHO", StorageDisplay.ofNullableList(Items.ECHO_SHARD));
        this.put(
                "CLT_PLANT_DIM_LIT",
                StorageDisplay.ofNullableList(
                        Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT));
        this.put("CLT_PLANT_FROG", StorageDisplay.ofNullableList(Items.FROGSPAWN));
        this.put("CLT_PLANT_WITHER_SKELETON", StorageDisplay.ofNullableList(Items.BONE, Items.WITHER_SKELETON_SKULL));
        this.put("CLT_PLANT_VINE", StorageDisplay.ofNullableList(Items.VINE));
        this.put("CLT_PLANT_BLACKSTONE", StorageDisplay.ofNullableList(Items.BLACKSTONE));
        this.put("CLT_PLANT_SQUID", StorageDisplay.ofNullableList(Items.INK_SAC));
        this.put(
                "CLT_PLANT_FLOWER",
                StorageDisplay.ofNullableList(
                        Items.CORNFLOWER,
                        Items.LILAC,
                        Items.LILY_OF_THE_VALLEY,
                        Items.DANDELION,
                        Items.POPPY,
                        Items.BLUE_ORCHID,
                        Items.ALLIUM,
                        Items.AZURE_BLUET,
                        Items.ORANGE_TULIP,
                        Items.PINK_TULIP,
                        Items.RED_TULIP,
                        Items.WHITE_TULIP,
                        Items.OXEYE_DAISY));
        this.put("CLT_PLANT_GLOWING_VINE", StorageDisplay.ofNullableList(Items.GLOW_LICHEN));
        this.put("CLT_PLANT_GLOW_SQUID", StorageDisplay.ofNullableList(Items.GLOW_INK_SAC));
        this.put(
                "CLT_PLANT_STAINED",
                StorageDisplay.ofNullableList(
                        Items.BLACK_STAINED_GLASS,
                        Items.BLUE_STAINED_GLASS,
                        Items.BROWN_STAINED_GLASS,
                        Items.CYAN_STAINED_GLASS,
                        Items.GRAY_STAINED_GLASS,
                        Items.GREEN_STAINED_GLASS,
                        Items.LIGHT_BLUE_STAINED_GLASS,
                        Items.LIGHT_GRAY_STAINED_GLASS,
                        Items.LIME_STAINED_GLASS,
                        Items.MAGENTA_STAINED_GLASS,
                        Items.ORANGE_STAINED_GLASS,
                        Items.PINK_STAINED_GLASS,
                        Items.PURPLE_STAINED_GLASS,
                        Items.RED_STAINED_GLASS,
                        Items.WHITE_STAINED_GLASS,
                        Items.YELLOW_STAINED_GLASS));
        this.put("CLT_PLANT_RED_SAND", StorageDisplay.ofNullableList(Items.RED_SAND));
        this.put(
                "CLT_PLANT_DUSTY",
                StorageDisplay.ofNullableList(
                        Items.BLACK_CONCRETE_POWDER,
                        Items.BLUE_CONCRETE_POWDER,
                        Items.BROWN_CONCRETE_POWDER,
                        Items.CYAN_CONCRETE_POWDER,
                        Items.GRAY_CONCRETE_POWDER,
                        Items.GREEN_CONCRETE_POWDER,
                        Items.LIGHT_BLUE_CONCRETE_POWDER,
                        Items.LIGHT_GRAY_CONCRETE_POWDER,
                        Items.LIME_CONCRETE_POWDER,
                        Items.MAGENTA_CONCRETE_POWDER,
                        Items.ORANGE_CONCRETE_POWDER,
                        Items.PINK_CONCRETE_POWDER,
                        Items.PURPLE_CONCRETE_POWDER,
                        Items.RED_CONCRETE_POWDER,
                        Items.WHITE_CONCRETE_POWDER,
                        Items.YELLOW_CONCRETE_POWDER));
        this.put("CLT_PLANT_PURPUR", StorageDisplay.ofNullableList(Items.PURPUR_BLOCK));
        this.put(
                "CLT_PLANT_GLAZED",
                StorageDisplay.ofNullableList(
                        Items.BLACK_GLAZED_TERRACOTTA,
                        Items.BLUE_GLAZED_TERRACOTTA,
                        Items.BROWN_GLAZED_TERRACOTTA,
                        Items.CYAN_GLAZED_TERRACOTTA,
                        Items.GRAY_GLAZED_TERRACOTTA,
                        Items.GREEN_GLAZED_TERRACOTTA,
                        Items.LIGHT_BLUE_GLAZED_TERRACOTTA,
                        Items.LIGHT_GRAY_GLAZED_TERRACOTTA,
                        Items.LIME_GLAZED_TERRACOTTA,
                        Items.MAGENTA_GLAZED_TERRACOTTA,
                        Items.ORANGE_GLAZED_TERRACOTTA,
                        Items.PINK_GLAZED_TERRACOTTA,
                        Items.PURPLE_GLAZED_TERRACOTTA,
                        Items.RED_GLAZED_TERRACOTTA,
                        Items.WHITE_GLAZED_TERRACOTTA,
                        Items.YELLOW_GLAZED_TERRACOTTA));
        this.put(
                "CLT_PLANT_DROWNED",
                StorageDisplay.ofNullableList(Items.ROTTEN_FLESH, Items.NAUTILUS_SHELL, Items.TRIDENT));
        this.put("CLT_PLANT_SAND", StorageDisplay.ofNullableList(Items.SAND));
        this.put("CLT_PLANT_VILLAGER", StorageDisplay.ofNullableList(Items.PAPER));
        this.put("CLT_PLANT_EMERALD", StorageDisplay.ofNullableList(Items.EMERALD));
        this.put("CLT_PLANT_GRASS", StorageDisplay.ofNullableList(Items.GRASS_BLOCK));
        this.put("CLT_PLANT_ZOMBIE", StorageDisplay.ofNullableList(Items.ROTTEN_FLESH, Items.ZOMBIE_HEAD));
        this.put("CLT_PLANT_DIRT", StorageDisplay.ofNullableList(Items.DIRT));
        this.put("CLT_PLANT_MOSS", StorageDisplay.ofNullableList(Items.MOSS_BLOCK));
        this.put(
                "CLT_PLANT_MUSHROOM",
                StorageDisplay.ofNullableList(
                        Items.BROWN_MUSHROOM,
                        Items.RED_MUSHROOM,
                        Items.CRIMSON_FUNGUS,
                        Items.WARPED_FUNGUS,
                        Items.MYCELIUM));
        this.put("CLT_PLANT_SLIME", StorageDisplay.ofNullableList(Items.SLIME_BALL));
        this.put("CLT_PLANT_REDSTONE", StorageDisplay.ofNullableList(Items.REDSTONE));
        this.put(
                "CLT_PLANT_WOOLLY",
                StorageDisplay.ofNullableList(
                        Items.BLACK_WOOL,
                        Items.BLUE_WOOL,
                        Items.BROWN_WOOL,
                        Items.CYAN_WOOL,
                        Items.GRAY_WOOL,
                        Items.GREEN_WOOL,
                        Items.LIGHT_BLUE_WOOL,
                        Items.LIGHT_GRAY_WOOL,
                        Items.LIME_WOOL,
                        Items.MAGENTA_WOOL,
                        Items.ORANGE_WOOL,
                        Items.PINK_WOOL,
                        Items.PURPLE_WOOL,
                        Items.RED_WOOL,
                        Items.WHITE_WOOL,
                        Items.YELLOW_WOOL));
        this.put("CLT_PLANT_BEE", StorageDisplay.ofNullableList(Items.HONEYCOMB, Items.HONEY_BOTTLE));
        this.put("CLT_PLANT_DARK_FLORA", StorageDisplay.ofNullableList(Items.WEEPING_VINES, Items.TWISTING_VINES));
        this.put("CLT_PLANT_RABBIT", StorageDisplay.ofNullableList(Items.RABBIT, Items.RABBIT_HIDE, Items.RABBIT_FOOT));
        this.put("CLT_PLANT_SHEEP", StorageDisplay.ofNullableList(Items.MUTTON, Items.WHITE_WOOL));
        this.put("CLT_PLANT_NETHER_QUARTZ", StorageDisplay.ofNullableList(Items.QUARTZ));
        this.put("CLT_PLANT_LAPIS", StorageDisplay.ofNullableList(Items.LAPIS_LAZULI));
        this.put("CLT_PLANT_COAL", StorageDisplay.ofNullableList(Items.COAL));
        this.put(
                "CLT_PLANT_SAPLING",
                StorageDisplay.ofNullableList(
                        Items.ACACIA_SAPLING,
                        Items.BIRCH_SAPLING,
                        Items.DARK_OAK_SAPLING,
                        Items.JUNGLE_SAPLING,
                        Items.OAK_SAPLING,
                        Items.SPRUCE_SAPLING,
                        Items.MANGROVE_PROPAGULE));
        this.put(
                "CLT_PLANT_CONCRETE",
                StorageDisplay.ofNullableList(
                        Items.BLACK_CONCRETE,
                        Items.BLUE_CONCRETE,
                        Items.BROWN_CONCRETE,
                        Items.CYAN_CONCRETE,
                        Items.GRAY_CONCRETE,
                        Items.GREEN_CONCRETE,
                        Items.LIGHT_BLUE_CONCRETE,
                        Items.LIGHT_GRAY_CONCRETE,
                        Items.LIME_CONCRETE,
                        Items.MAGENTA_CONCRETE,
                        Items.ORANGE_CONCRETE,
                        Items.PINK_CONCRETE,
                        Items.PURPLE_CONCRETE,
                        Items.RED_CONCRETE,
                        Items.WHITE_CONCRETE,
                        Items.YELLOW_CONCRETE));
        this.put("CLT_PLANT_DEEPSLATE", StorageDisplay.ofNullableList(Items.DEEPSLATE));
        this.put(
                "CLT_PLANT_FISH",
                StorageDisplay.ofNullableList(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH));
        this.put(
                "CLT_PLANT_RAINBOW",
                StorageDisplay.ofNullableList(
                        Items.BLACK_DYE,
                        Items.BLUE_DYE,
                        Items.BROWN_DYE,
                        Items.CYAN_DYE,
                        Items.GRAY_DYE,
                        Items.GREEN_DYE,
                        Items.LIGHT_BLUE_DYE,
                        Items.LIGHT_GRAY_DYE,
                        Items.LIME_DYE,
                        Items.MAGENTA_DYE,
                        Items.ORANGE_DYE,
                        Items.PINK_DYE,
                        Items.PURPLE_DYE,
                        Items.RED_DYE,
                        Items.WHITE_DYE,
                        Items.YELLOW_DYE));
        this.put("CLT_PLANT_CLAY", StorageDisplay.ofNullableList(Items.CLAY));
        this.put(
                "CLT_PLANT_GUARDIAN", StorageDisplay.ofNullableList(Items.PRISMARINE_SHARD, Items.PRISMARINE_CRYSTALS));
        this.put("CLT_PLANT_SHULKER", StorageDisplay.ofNullableList(Items.SHULKER_SHELL));
        this.put("CLT_PLANT_END_STONE", StorageDisplay.ofNullableList(Items.END_STONE));
        this.put("CLT_PLANT_MAGMA_CUBE", StorageDisplay.ofNullableList(Items.MAGMA_CREAM));
        this.put("CLT_PLANT_SOUL", StorageDisplay.ofNullableList(Items.SOUL_SAND, Items.SOUL_SOIL, Items.GHAST_TEAR));
        this.put("CLT_PLANT_WITHER_ROSE", StorageDisplay.ofNullableList(Items.WITHER_ROSE));
        this.put("CLT_PLANT_BASALT", StorageDisplay.ofNullableList(Items.BASALT));
        this.put("CLT_PLANT_CREEPER", StorageDisplay.ofNullableList(Items.GUNPOWDER, Items.CREEPER_HEAD));
        this.put(
                "CLT_PLANT_TURTLE",
                StorageDisplay.ofNullableList(Items.TURTLE_SCUTE, Items.SEAGRASS, Items.TURTLE_EGG));
        this.put("CLT_PLANT_PHANTOM", StorageDisplay.ofNullableList(Items.PHANTOM_MEMBRANE));
        this.put(
                "CLT_PLANT_ENDER_DRAGON",
                StorageDisplay.ofNullableList(Items.DRAGON_BREATH, Items.DRAGON_HEAD, Items.DRAGON_EGG));
        this.put("CLT_PLANT_AMETHYST", StorageDisplay.ofNullableList(Items.AMETHYST_SHARD));
        this.put("CLT_PLANT_WITCH", StorageDisplay.ofNullableList(Items.REDSTONE, Items.GLOWSTONE));
        this.put("CLT_PLANT_RAW_COPPER", StorageDisplay.ofNullableList(Items.RAW_COPPER));
        this.put(
                "CLT_PLANT_IGNEOUS",
                StorageDisplay.ofNullableList(
                        Items.GRANITE,
                        Items.DIORITE,
                        Items.ANDESITE,
                        Items.CALCITE,
                        Items.TUFF,
                        Items.DRIPSTONE_BLOCK));
    }
}
