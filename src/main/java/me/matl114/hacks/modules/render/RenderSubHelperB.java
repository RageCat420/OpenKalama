package me.matl114.hacks.modules.render;

import java.util.HashMap;
import java.util.List;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.item.ItemStack;

class RenderSubHelperB extends HashMap<String, List<ItemStack>> {
    RenderSubHelperB() {
        this.put(
                "CLT_BUSH_BAY_LEAF", StorageDisplay.ofNullableList(ItemStackUtils.newItem("LILY_PAD", "CLT_BAY_LEAF")));
        this.put(
                "CLT_BUSH_RICE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$cb70f2fb5ebf49f79ff3e873616863ae5d362fbbfc31aef2dfb93d6e17dbf2", "CLT_RICE")));
        this.put(
                "CLT_BUSH_SHALLOT",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$a6ecc46dc3dc85fcd57198176ee841f1a041b15f73ecb19fde62ee4315c4a6", "CLT_SHALLOT")));
        this.put(
                "CLT_BUSH_MARJORAM",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("DARK_OAK_LEAVES", "CLT_MARJORAM")));
        this.put(
                "CLT_BUSH_STRAWBERRY",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$b9708d818be97dc7e2c3bb5c35663eb36269236e9bc98286f429dfdf375aa9",
                        "CLT_STRAWBERRY")));
        this.put(
                "CLT_BUSH_KALE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$1b913d9d6e306d953461d1f7468321868a9bad4df53d1a7dc64b0797628f0", "CLT_KALE")));
        this.put(
                "CLT_BUSH_RAPESEED",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("MELON_SEEDS", "CLT_RAPESEED")));
        this.put(
                "CLT_BUSH_LAVENDER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("PURPLE_DYE", "CLT_LAVENDER")));
        this.put("CLT_BUSH_DILL", StorageDisplay.ofNullableList(ItemStackUtils.newItem("GRASS", "CLT_DILL")));
        this.put(
                "CLT_BUSH_JASMINE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("WHITE_TULIP", "CLT_JASMINE")));
        this.put(
                "CLT_BUSH_RHUBARB",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$c144a523b05063c81f006fdbd8d9b75249aa009b4c1f46db27dbbeafc4a8578", "CLT_RHUBARB")));
        this.put(
                "CLT_BUSH_CELERIAC",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$d0f61fe6d3c0e7f01434d38367b1a50db04f8cccbdbbda1806bf1198318dc7eb",
                        "CLT_CELERIAC")));
        this.put(
                "CLT_BUSH_LETTUCE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$2d52e883c6436ca7e764bda4b58afa53da6a649030f663953dce9fd6315f9fea",
                        "CLT_LETTUCE")));
        this.put(
                "CLT_BUSH_SWEETCORN",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$d391dffbea2fc3f2ad78a623f49bf7e1121694112c3759feed4156fc2ba46c0",
                        "CLT_SWEETCORN")));
        this.put(
                "CLT_BUSH_LICORICE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("HANGING_ROOTS", "CLT_LICORICE")));
        this.put(
                "CLT_BUSH_CABBAGE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$58e59dc722e419fe064f0c7992a7897b73cc7c5205e19d067a174bcb018e9429",
                        "CLT_CABBAGE")));
        this.put(
                "CLT_BUSH_BROCCOLI",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$65106f0cc4c12dcfe7736a1ffc92772f41b66e1b7682b1f47537787720567262",
                        "CLT_BROCCOLI")));
        this.put("CLT_BUSH_SPINACH", StorageDisplay.ofNullableList(ItemStackUtils.newItem("KELP", "CLT_SPINACH")));
        this.put(
                "CLT_BUSH_MARROW",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$36ae076649ef22f60e8511831c68fd2b6ea63c32164dab33a8aebc18ff2a54c8", "CLT_MARROW")));
        this.put("CLT_BUSH_CELERY", StorageDisplay.ofNullableList(ItemStackUtils.newItem("BAMBOO", "CLT_CELERY")));
        this.put("CLT_BUSH_CUCUMBER", StorageDisplay.ofNullableList(ItemStackUtils.newItem("BAMBOO", "CLT_CUCUMBER")));
        this.put(
                "CLT_BUSH_CILANTRO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("MANGROVE_LEAVES", "CLT_CILANTRO")));
        this.put("CLT_BUSH_MACE", StorageDisplay.ofNullableList(ItemStackUtils.newItem("RED_DYE", "CLT_MACE")));
        this.put("CLT_BUSH_CLOVE", StorageDisplay.ofNullableList(ItemStackUtils.newItem("POPPY", "CLT_CLOVE")));
        this.put(
                "CLT_BUSH_ROSEMARY",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("BIRCH_LEAVES", "CLT_ROSEMARY")));
        this.put(
                "CLT_BUSH_SOY_BEANS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("MELON_SEEDS", "CLT_SOY_BEANS")));
        this.put(
                "CLT_BUSH_STAR_ANISE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("NETHER_STAR", "CLT_STAR_ANISE")));
        this.put(
                "CLT_BUSH_RUTABAGA",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$b3661e4bf4c3e730a3aaa1053a3fc524dc03df67bf7a20979efdb2ad1a9e4084",
                        "CLT_RUTABAGA")));
        this.put(
                "CLT_BUSH_PARSLEY",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("ACACIA_LEAVES", "CLT_PARSLEY")));
        this.put(
                "CLT_BUSH_GREEN_BEANS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("FROGSPAWN", "CLT_GREEN_BEANS")));
        this.put(
                "CLT_BUSH_PEANUTS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$cf823e05353ae9122150bc67a5df7de628f4d4c30b36bdae3c1c18582bfca776", "CLT_PEANUT")));
        this.put(
                "CLT_BUSH_HORSERADISH",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("HANGING_ROOTS", "CLT_HORSERADISH")));
        this.put(
                "CLT_BUSH_CHILLI_PEPPER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$a3ef14c40251844c3ae39b6028db86a9098df325e50b7a475972cd1ac918e9d5",
                        "CLT_CHILLY_PEPPER")));
        this.put("CLT_BUSH_THYME", StorageDisplay.ofNullableList(ItemStackUtils.newItem("BIRCH_LEAVES", "CLT_THYME")));
        this.put(
                "CLT_BUSH_PARSNIP",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$d0f61fe6d3c0e7f01434d38367b1a50db04f8cccbdbbda1806bf1198318dc7eb",
                        "CLT_PARSNIP")));
        this.put("CLT_BUSH_MINT", StorageDisplay.ofNullableList(ItemStackUtils.newItem("MANGROVE_LEAVES", "CLT_MINT")));
        this.put(
                "CLT_BUSH_NETTLES",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("AZALEA_LEAVES", "CLT_NETTLES")));
        this.put(
                "CLT_BUSH_PINTO_BEANS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("MELON_SEEDS", "CLT_PINTO_BEANS")));
        this.put(
                "CLT_BUSH_LEEK",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$c144a523b05063c81f006fdbd8d9b75249aa009b4c1f46db27dbbeafc4a8578", "CLT_LEEK")));
        this.put(
                "CLT_BUSH_AVOCADO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$6af2bf32bb8937a5aadfbf6d8dc56a21ef65f6884db67206280fa1e149f8c4b", "CLT_AVOCADO")));
        this.put(
                "CLT_BUSH_BLACK_BEANS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("MELON_SEEDS", "CLT_BLACK_BEANS")));
        this.put(
                "CLT_BUSH_TURNIP",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$bfac2d1d21aeb8d7a0c289733511d903df57924ad6bd61d00567a55af649bc0d", "CLT_TURNIP")));
        this.put(
                "CLT_BUSH_GINGER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("HANGING_ROOTS", "CLT_GINGER")));
        this.put(
                "CLT_BUSH_CHICORY",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("BLUE_ORCHID", "CLT_CHICORY")));
        this.put("CLT_BUSH_FENNEL", StorageDisplay.ofNullableList(ItemStackUtils.newItem("OXEYE_DAISY", "CLT_FENNEL")));
        this.put(
                "CLT_BUSH_RUNNER_BEANS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("KELP", "CLT_RUNNER_BEANS")));
        this.put(
                "CLT_BUSH_OREGANO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("SPRUCE_LEAVES", "CLT_OREGANO")));
        this.put(
                "CLT_BUSH_WASABI",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("HANGING_ROOTS", "CLT_WASABI")));
        this.put(
                "CLT_BUSH_CHICKPEAS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("BEETROOT_SEEDS", "CLT_CHICKPEAS")));
        this.put(
                "CLT_BUSH_CAULIFLOWER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$14a6dedd99bb9af3f1b2f338d509a926606cddfdc351e018aad1c07015ad566d",
                        "CLT_CAULIFLOWER")));
        this.put(
                "CLT_BUSH_CAYENNE_PEPPER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$a3ef14c40251844c3ae39b6028db86a9098df325e50b7a475972cd1ac918e9d5",
                        "CLT_CAYENNE_PEPPER")));
        this.put(
                "CLT_BUSH_ASPARAGUS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$2ba5599e26cf2252d431a950b2078a0af2e45c60edff9d4fadf62c323df5411f",
                        "CLT_ASPARAGUS")));
        this.put(
                "CLT_BUSH_AUBERGINE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$521358c5b2e2526ae6aab91a5fb09198461a7cc4d860e8647d5e10fb6c87be67",
                        "CLT_AUBERGINE")));
        this.put(
                "CLT_BUSH_OKRA",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$2d52e883c6436ca7e764bda4b58afa53da6a649030f663953dce9fd6315f9fea", "CLT_OKRA")));
        this.put(
                "CLT_BUSH_BASIL", StorageDisplay.ofNullableList(ItemStackUtils.newItem("SMALL_DRIPLEAF", "CLT_BASIL")));
        this.put(
                "CLT_BUSH_COURGETTE", StorageDisplay.ofNullableList(ItemStackUtils.newItem("BAMBOO", "CLT_COURGETTE")));
        this.put(
                "CLT_BUSH_RADICCHIO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$cd56f448876ebfdaa88ca7f0137a6837f4d2ba14dd3d6615d82d617c39b39daf",
                        "CLT_RADICCHIO")));
        this.put(
                "CLT_BUSH_PEPPER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$d209d3d0d8daa4628b9b3e10a235e22089d76bffe156ddc5852e5fdc12a3d12c", "CLT_PEPPER")));
        this.put(
                "CLT_BUSH_ONION",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$a6ecc46dc3dc85fcd57198176ee841f1a041b15f73ecb19fde62ee4315c4a6", "CLT_ONION")));
        this.put(
                "CLT_BUSH_TARRAGON",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("JUNGLE_LEAVES", "CLT_TARRAGON")));
        this.put(
                "CLT_BUSH_SWEET_POTATO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("BEETROOT", "CLT_SWEET_POTATO")));
        this.put(
                "CLT_BUSH_SHISO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("MANGROVE_LEAVES", "CLT_SHISO")));
        this.put(
                "CLT_BUSH_BRUSSELS_SPROUTS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$1b913d9d6e306d953461d1f7468321868a9bad4df53d1a7dc64b0797628f0",
                        "CLT_BRUSSELS_SPROUTS")));
        this.put(
                "CLT_BUSH_TOMATO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$72df4e674951c138e7311127561fdbd27e2150716b02bb568747f8545fb20145", "CLT_TOMATO")));
        this.put(
                "CLT_BUSH_JALAPENO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$a3ef14c40251844c3ae39b6028db86a9098df325e50b7a475972cd1ac918e9d5",
                        "CLT_JALAPENO")));
        this.put(
                "CLT_BUSH_CURRY_LEAF", StorageDisplay.ofNullableList(ItemStackUtils.newItem("KELP", "CLT_CURRY_LEAF")));
        this.put(
                "CLT_BUSH_TURMERIC",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("YELLOW_DYE", "CLT_TURMERIC")));
        this.put("CLT_BUSH_CUMIN", StorageDisplay.ofNullableList(ItemStackUtils.newItem("DANDELION", "CLT_CUMIN")));
        this.put(
                "CLT_BUSH_GRAPE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$e44c359f6f28c5fa6c5a09d8c57fa4174da4e8ae110e5f2cd7e93f2e76176cd5", "CLT_GRAPE")));
        this.put("CLT_BUSH_CHIVES", StorageDisplay.ofNullableList(ItemStackUtils.newItem("SEAGRASS", "CLT_CHIVES")));
        this.put("CLT_BUSH_CINNAMON", StorageDisplay.ofNullableList(ItemStackUtils.newItem("STICK", "CLT_CINNAMON")));
        this.put(
                "CLT_BUSH_ARTICHOKE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$58e59dc722e419fe064f0c7992a7897b73cc7c5205e19d067a174bcb018e9429",
                        "CLT_ARTICHOKE")));
        this.put(
                "CLT_BUSH_GARLIC",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$ca5b1539b698c217cb3b4163a00e336131043311b83b08de02f1a66505be5b29", "CLT_GARLIC")));
        this.put(
                "CLT_BUSH_PEA",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$96c15fb4e9a31191f0cb4da56fe60334dd46eb3a582111b4f8f27eddb760e2c", "CLT_PEA")));
        this.put(
                "CLT_BUSH_KAFFIR_LIME",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("KELP", "CLT_KAFFIR_LIME")));
        this.put(
                "CLT_BUSH_MUSTARD",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("PUMPKIN_SEEDS", "CLT_MUSTARD_SEEDS")));
        this.put(
                "CLT_BUSH_RADDISH",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$cd56f448876ebfdaa88ca7f0137a6837f4d2ba14dd3d6615d82d617c39b39daf",
                        "CLT_RADDISH")));
        this.put(
                "CLT_BUSH_BELL_PEPPER",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$65f7810414a2cee2bc1de12ecef7a4c89fc9b38e9d0414a90991241a5863705f",
                        "CLT_BELL_PEPPER")));
        this.put(
                "CLT_BUSH_JUNIPER_BERRY",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("GLOW_BERRIES", "CLT_JUNIPER_BERRY")));
        this.put(
                "CLT_BUSH_SASSAFRAS",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem("OAK_LEAVES", "CLT_SASSAFRAS")));
        this.put(
                "CLT_BUSH_GREEN_ONION",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$c2dd5433db4fddebc4a77166735699400cb18d43672ab31326a83f0b7c2586cc",
                        "CLT_GREEN_ONION")));
        this.put("CLT_BUSH_VANILLA", StorageDisplay.ofNullableList(ItemStackUtils.newItem("BLACK_DYE", "CLT_VANILLA")));
        this.put(
                "CLT_TREE_GREEN_APPLE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$96c15fb4e9a31191f0cb4da56fe60334dd46eb3a582111b4f8f27eddb760e2c",
                        "CLT_GREEN_APPLE")));
        this.put(
                "CLT_TREE_MANGO",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$e4895aa67247c3eb406fb905d3f6d35acd660c6f14a85bcf7bfb898b82646e70", "CLT_MANGO")));
        this.put(
                "CLT_TREE_HAZELNUT",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$678784703fa59cd153fcabe3ccd9d44c469a8d63e6d438626ad9ebc70707fc3",
                        "CLT_HAZELNUT")));
        this.put(
                "CLT_TREE_PEACH",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$fadbbab3881aacba577e27bbee1fbe4b9a50e19f5a87f8d49b636054fa1788fc", "CLT_PEACH")));
        this.put(
                "CLT_TREE_PEAR",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$c68dd595bdc68e1a8dc84d789f21791edd053ba3bbedbfec2e7daa7243aea217", "CLT_PEAR")));
        this.put(
                "CLT_TREE_CHESTNUT",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$7aea6b06c058546247e6567009440140946ea53623829721bd46b3e6a0e5cce8",
                        "CLT_CHESTNUT")));
        this.put(
                "CLT_TREE_PECAN",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$9813444497a48b3b68946961e49e13dd6b19467aeed1eb9d0e876bc878e0a734", "CLT_PECAN")));
        this.put(
                "CLT_TREE_LEMON",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$4378b582d19ccc55b023eb82eda271bac4744fa2006cf5e190246e2b4d5d", "CLT_LEMON")));
        this.put(
                "CLT_TREE_KIWI",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$4cc18ec4649f07d5a38a583d9271fd83a6f37318758e46ea87fc2b2d1afc2d9", "CLT_KIWI")));
        this.put(
                "CLT_TREE_LIME",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$4cc18ec4649f07d5a38a583d9271fd83a6f37318758e46ea87fc2b2d1afc2d9", "CLT_LIME")));
        this.put(
                "CLT_TREE_APRICOT",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$534104bb1442b4034cf32595a087b7a51d96ce5915c182d833eefa68e1cec1ff",
                        "CLT_APRICOT")));
        this.put(
                "CLT_TREE_CHERRY",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$8b9b2383bae7b84fdc31b54179afb713a1c187b83e7a0c5e38470ae2a3e2a30f", "CLT_CHERRY")));
        this.put(
                "CLT_TREE_BANANA",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$20aaa1425d2b99383697d57193f27d872442bcb995508f42d19de4af1f8612", "CLT_BANANA")));
        this.put(
                "CLT_TREE_PINEAPPLE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$57c5e925a949e55db2c25efaad64512eb6dab74affb2e9f304c385b4f4b30ba5",
                        "CLT_PINEAPPLE")));
        this.put(
                "CLT_TREE_ORANGE",
                StorageDisplay.ofNullableList(ItemStackUtils.newItem(
                        "PLAYER_HEAD$91b0fb313d90ddafd4fd7c95ef0d51b2ff9cc13013f1d318b11d87aa002fbaa0", "CLT_ORANGE")));
    }
}
