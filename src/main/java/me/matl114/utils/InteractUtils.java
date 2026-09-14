package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.versioned.api.VItem;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LightBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage;
import net.minecraft.block.entity.Spawner;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MooshroomEntity.Type;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorStandItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BoatItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.EmptyMapItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.KnowledgeBookItem;
import net.minecraft.item.LeadItem;
import net.minecraft.item.MinecartItem;
import net.minecraft.item.OnAStickItem;
import net.minecraft.item.PlaceableOnWaterItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SpyglassItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InteractUtils {
    public static final Set<Class<? extends Entity>> e;
    private static final MinecraftClient a = MinecraftClient.getInstance();
    public static final Set<Block> d = new HashSet<>();
    private static final Predicate<ItemStack> b = stack -> true;
    public static Set<Block> c = null;

    public static boolean isInteractAcceptable(
            World world, PlayerEntity player, Entity entity, ItemStack interactStack) {
        if (world == null || player == null || entity == null) {
            return false;
        } else if (!player.isSpectator() && entity.isAlive()) {
            ItemStack var4 = interactStack == null ? ItemStack.EMPTY : interactStack;
            if (isGenericAcceptedInteractItem(entity, var4)) {
                return true;
            } else {
                return isVehicleEntityInteractAcceptable(player, entity, var4)
                        ? true
                        : isSpecialEntityInteractAcceptable(player, entity, var4);
            }
        } else {
            return false;
        }
    }

    public static boolean E(BlockState fromState, BlockState toState) {
        if (fromState == null || toState == null) {
            return false;
        } else {
            return fromState.equals(toState)
                    ? true
                    : !getNextInteractionStep(fromState, toState).isEmpty();
        }
    }

    @Nullable
    public static BlockState getBlockPlacement(
            Block block, PlayerEntity player, World world, BlockHitResult blockHitResult) {
        return block.asItem() instanceof BlockItem var5 ? b(var5, player, world, blockHitResult) : null;
    }

    public static BlockPos getCurrentPlacePos(PlayerEntity player, BlockHitResult blockHitResult) {
        ItemPlacementContext var2 =
                new ItemPlacementContext(player, Hand.MAIN_HAND, new ItemStack(Blocks.STONE), blockHitResult);
        return var2.getBlockPos();
    }

    @Nullable
    public static BlockState b(BlockItem blockItem, PlayerEntity player, World world, BlockHitResult blockHitResult) {
        ItemPlacementContext var4 =
                new ItemPlacementContext(player, Hand.MAIN_HAND, new ItemStack(blockItem), blockHitResult);
        var4 = blockItem.getPlacementContext(var4);
        return blockItem.getPlacementState(var4);
    }

    public static boolean canBlockPlace(PlayerEntity player, BlockPos pos, BlockState state) {
        World var3 = player.getEntityWorld();
        return state.canPlaceAt(var3, pos) && var3.canPlace(state, pos, ShapeContext.of(player));
    }

    public static boolean canEnderChestOpen(World world, BlockPos pos) {
        return !world.getBlockState(pos.up()).isSolidBlock(world, pos.up());
    }

    public static boolean canBlockOpenScreen(World world, BlockState state, BlockPos pos) {
        return state.createScreenHandlerFactory(world, pos) != null;
    }

    static {
        d.add(Blocks.GRASS_BLOCK);
        d.add(Blocks.DIRT);
        d.add(Blocks.PODZOL);
        d.add(Blocks.COARSE_DIRT);
        d.add(Blocks.MYCELIUM);
        d.add(Blocks.ROOTED_DIRT);
        HashSet var0 = new HashSet();
        e = var0;
    }

    private static boolean isVehicleEntityInteractAcceptable(PlayerEntity player, Entity entity, ItemStack stack) {
        if (entity instanceof VehicleInventory) {
            return true;
        } else if (entity instanceof FurnaceMinecartEntity) {
            return true;
        } else if (entity instanceof CommandBlockMinecartEntity) {
            return player.isCreativeLevelTwoOp();
        } else if (!(entity instanceof MinecartEntity var3)) {
            return entity instanceof BoatEntity ? !player.shouldCancelInteraction() : false;
        } else {
            return !player.shouldCancelInteraction() && !var3.hasPassengers();
        }
    }

    private static boolean isSpecialEntityInteractAcceptable(PlayerEntity player, Entity entity, ItemStack stack) {
        if (entity instanceof ArmorStandEntity var18) {
            return !var18.isMarker();
        } else if (entity instanceof ItemFrameEntity var17) {
            return var17.isRemoved() ? false : !var17.getHeldItemStack().isEmpty() || !stack.isEmpty();
        } else if (entity instanceof LeashKnotEntity) {
            return true;
        } else if (entity instanceof AllayEntity var16) {
            if (var16.isDancing() && stack.isOf(Items.AMETHYST_SHARD) && var16.canDuplicate()) {
                return true;
            } else {
                return !var16.isHoldingItem() && !stack.isEmpty() ? true : var16.isHoldingItem() && stack.isEmpty();
            }
        } else if (entity instanceof CamelEntity var15) {
            return var15.isBaby() ? var15.isBreedingItem(stack) : true;
        } else if (entity instanceof AbstractHorseEntity var14) {
            return var14.isBaby() ? var14.isBreedingItem(stack) : true;
        } else if (entity instanceof PigEntity var3
                && var3.isSaddled()
                && !var3.hasPassengers()
                && !player.shouldCancelInteraction()) {
            return true;
        } else if (entity instanceof StriderEntity var6
                && var6.isSaddled()
                && !var6.hasPassengers()
                && !player.shouldCancelInteraction()) {
            return true;
        } else if (entity instanceof VillagerEntity var13) {
            return !var13.hasCustomer() && !var13.isSleeping();
        } else if (entity instanceof WanderingTraderEntity var12) {
            return !var12.hasCustomer() && !var12.isBaby();
        } else if (entity instanceof WolfEntity var11) {
            if (var11.isTamed()) {
                if (var11.isBreedingItem(stack) && var11.getHealth() < var11.getMaxHealth()) {
                    return true;
                } else if (var11.isOwner(player)) {
                    if (stack.getItem() instanceof DyeItem var20 && var20.getColor() != var11.getCollarColor()) {
                        return true;
                    } else if (stack.isOf(Items.WOLF_ARMOR) && !var11.isBaby() && !var11.isWearingBodyArmor()) {
                        return true;
                    } else {
                        return var11.isInSittingPose()
                                        && var11.isWearingBodyArmor()
                                        && var11.getBodyArmor().isDamaged()
                                ? true
                                : true;
                    }
                } else {
                    return false;
                }
            } else {
                return stack.isOf(Items.BONE) && !var11.hasAngerTime();
            }
        } else if (entity instanceof CatEntity var10) {
            if (!var10.isTamed()) {
                return var10.isBreedingItem(stack);
            } else if (var10.isOwner(player)) {
                if (stack.getItem() instanceof DyeItem var5 && var5.getColor() != var10.getCollarColor()) {
                    return true;
                } else {
                    return var10.isBreedingItem(stack) && var10.getHealth() < var10.getMaxHealth() ? true : true;
                }
            } else {
                return false;
            }
        } else if (entity instanceof OcelotEntity var9) {
            return !var9.isTrusting() && var9.isBreedingItem(stack);
        } else if (entity instanceof ParrotEntity var8) {
            return var8.isTamed()
                    ? var8.isOwner(player)
                    : stack.isIn(ItemTags.PARROT_FOOD) || stack.isIn(ItemTags.PARROT_POISONOUS_FOOD);
        } else {
            return entity instanceof PiglinEntity var7 ? var7.getActivity() != PiglinActivity.ADMIRING_ITEM : false;
        }
    }

    private static EquipmentSlot getArmorStandHitSlot(ArmorStandEntity armorStand, Vec3d hitPos) {
        EquipmentSlot var2 = EquipmentSlot.MAINHAND;
        boolean var3 = armorStand.isSmall();
        double var4 = hitPos.y / (armorStand.getScale() * armorStand.getScaleFactor());
        if (var4 >= 0.1
                && var4 < 0.1 + (var3 ? 0.8 : 0.45)
                && !armorStand.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
            var2 = EquipmentSlot.FEET;
        } else if (var4 >= 0.9 + (var3 ? 0.3 : 0.0)
                && var4 < 0.9 + (var3 ? 1.0 : 0.7)
                && !armorStand.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
            var2 = EquipmentSlot.CHEST;
        } else if (var4 >= 0.4
                && var4 < 0.4 + (var3 ? 1.0 : 0.8)
                && !armorStand.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) {
            var2 = EquipmentSlot.LEGS;
        } else if (var4 >= 1.6
                && !armorStand.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            var2 = EquipmentSlot.HEAD;
        } else if (armorStand.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()
                && !armorStand.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) {
            var2 = EquipmentSlot.OFFHAND;
        }

        return var2;
    }

    private static Item getRequiredCandleItem(Block candleCakeBlock) {
        Identifier var1 = Registries.BLOCK.getId(candleCakeBlock);
        String var2 = var1.getPath();
        if (!var2.endsWith("_cake")) {
            return null;
        } else {
            Item var3 = (Item) Registries.ITEM.get(var1.withPath(var2.substring(0, var2.length() - 5)));
            return var3 == Items.AIR ? null : var3;
        }
    }

    private static boolean canArmorStandUseSlot(ArmorStandEntity armorStand, EquipmentSlot slot) {
        return slot != EquipmentSlot.BODY && armorStand.canUseSlot(slot);
    }

    public static Set<Pair<BlockState, Predicate<ItemStack>>> getNextInteractionStep(
            BlockState fromState, BlockState toState) {
        HashSet<Pair<BlockState, Predicate<ItemStack>>> var2 = new HashSet<>();
        if (fromState != null && toState != null) {
            Block var3 = fromState.getBlock();
            Block var4 = toState.getBlock();
            if (var3 == var4) {
                if (var3 instanceof SlabBlock
                        && fromState.get(SlabBlock.TYPE) != SlabType.DOUBLE
                        && toState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
                    var2.add(Pair.of((BlockState) fromState.with(SlabBlock.TYPE, SlabType.DOUBLE), G(var3.asItem())));
                }

                int var5;
                if (var3 instanceof SnowBlock
                        && (var5 = (Integer) fromState.get(SnowBlock.LAYERS)) < 8
                        && (Integer) toState.get(SnowBlock.LAYERS) > var5) {
                    var2.add(Pair.of((BlockState) fromState.with(SnowBlock.LAYERS, var5 + 1), G(var3.asItem())));
                }

                if (var3 instanceof CandleBlock
                        && (var5 = (Integer) fromState.get(CandleBlock.CANDLES)) < 4
                        && (Integer) toState.get(CandleBlock.CANDLES) > var5) {
                    var2.add(Pair.of((BlockState) fromState.with(CandleBlock.CANDLES, var5 + 1), G(var3.asItem())));
                }

                if (var3 instanceof SeaPickleBlock
                        && (var5 = (Integer) fromState.get(SeaPickleBlock.PICKLES)) < 4
                        && (Integer) toState.get(SeaPickleBlock.PICKLES) > var5) {
                    var2.add(Pair.of((BlockState) fromState.with(SeaPickleBlock.PICKLES, var5 + 1), G(var3.asItem())));
                }

                if (var3 instanceof FlowerbedBlock
                        && (var5 = (Integer) fromState.get(FlowerbedBlock.FLOWER_AMOUNT)) < 4
                        && (Integer) toState.get(FlowerbedBlock.FLOWER_AMOUNT) > var5) {
                    var2.add(Pair.of(
                            (BlockState) fromState.with(FlowerbedBlock.FLOWER_AMOUNT, var5 + 1), G(var3.asItem())));
                }

                if (var3 instanceof RepeaterBlock
                        && !((Integer) toState.get(RepeaterBlock.DELAY)).equals(fromState.get(RepeaterBlock.DELAY))) {
                    var2.add(Pair.of((BlockState) fromState.cycle(RepeaterBlock.DELAY), b));
                }

                if (var3 instanceof ComparatorBlock
                        && toState.get(ComparatorBlock.MODE) != fromState.get(ComparatorBlock.MODE)) {
                    var2.add(Pair.of((BlockState) fromState.cycle(ComparatorBlock.MODE), b));
                }

                if (var3 instanceof DoorBlock && toState.get(DoorBlock.OPEN) != fromState.get(DoorBlock.OPEN)) {
                    var2.add(Pair.of((BlockState) fromState.cycle(DoorBlock.OPEN), b));
                }

                if (var3 instanceof TrapdoorBlock
                        && toState.get(TrapdoorBlock.OPEN) != fromState.get(TrapdoorBlock.OPEN)) {
                    var2.add(Pair.of((BlockState) fromState.cycle(TrapdoorBlock.OPEN), b));
                }

                if (var3 instanceof FenceGateBlock
                        && toState.get(FenceGateBlock.OPEN) != fromState.get(FenceGateBlock.OPEN)) {
                    var2.add(Pair.of((BlockState) fromState.cycle(FenceGateBlock.OPEN), b));
                }

                if (var3 instanceof LeverBlock
                        && toState.get(LeverBlock.POWERED) != fromState.get(LeverBlock.POWERED)) {
                    var2.add(Pair.of((BlockState) fromState.cycle(LeverBlock.POWERED), b));
                }

                if (var3 instanceof ButtonBlock
                        && !(Boolean) fromState.get(ButtonBlock.POWERED)
                        && toState.equals(fromState.with(ButtonBlock.POWERED, true))) {
                    var2.add(Pair.of(toState, b));
                }

                if (var3 instanceof NoteBlock && toState.get(NoteBlock.NOTE) != fromState.get(NoteBlock.NOTE)) {
                    var2.add(Pair.of((BlockState) fromState.cycle(NoteBlock.NOTE), b));
                }

                if (var3 instanceof CandleBlock
                        && (Boolean) fromState.get(CandleBlock.LIT)
                        && toState.equals(fromState.with(CandleBlock.LIT, false))) {
                    var2.add(Pair.of(toState, ItemStack::isEmpty));
                }

                if (var3 instanceof CandleBlock
                        && !(Boolean) fromState.get(CandleBlock.LIT)
                        && !(Boolean) fromState.get(CandleBlock.WATERLOGGED)
                        && toState.equals(fromState.with(CandleBlock.LIT, true))) {
                    var2.add(Pair.of(toState, isAnyOf(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE)));
                }

                if (var3 instanceof RespawnAnchorBlock
                        && (Integer) fromState.get(RespawnAnchorBlock.CHARGES) < 4
                        && (Integer) toState.get(RespawnAnchorBlock.CHARGES)
                                > (Integer) fromState.get(RespawnAnchorBlock.CHARGES)) {
                    var2.add(Pair.of(
                            (BlockState) fromState.with(
                                    RespawnAnchorBlock.CHARGES,
                                    (Integer) fromState.get(RespawnAnchorBlock.CHARGES) + 1),
                            G(Items.GLOWSTONE)));
                }

                if (var3 instanceof CakeBlock
                        && (Integer) fromState.get(CakeBlock.BITES) < 6
                        && toState.equals(
                                fromState.with(CakeBlock.BITES, (Integer) fromState.get(CakeBlock.BITES) + 1))) {
                    var2.add(Pair.of(toState, b));
                }

                if (var3 instanceof FlowerPotBlock var6
                        && var4 instanceof FlowerPotBlock var7
                        && var6.getContent() != Blocks.AIR
                        && var7.getContent() == Blocks.AIR) {
                    var2.add(Pair.of(toState, ItemStack::isEmpty));
                }
            }

            if (var3 instanceof CakeBlock
                    && var4 instanceof CandleCakeBlock
                    && (Integer) fromState.get(CakeBlock.BITES) == 0) {
                Item var11 = getRequiredCandleItem(var4);
                if (var11 != null) {
                    var2.add(Pair.of(toState, G(var11)));
                }
            }

            if (var3 instanceof CandleCakeBlock
                    && var4 instanceof CakeBlock
                    && (Integer) toState.get(CakeBlock.BITES) == 1) {
                var2.add(Pair.of(toState, b));
            }

            if (var3 instanceof FlowerPotBlock var12
                    && var4 instanceof FlowerPotBlock var13
                    && var12.getContent() == Blocks.AIR
                    && var13.getContent() != Blocks.AIR) {
                Block var14 = var13.getContent();
                var2.add(Pair.of(toState, (Predicate<ItemStack>) stack ->
                        stack != null && stack.getItem() instanceof BlockItem var3x && var3x.getBlock() == var14));
            }

            if (var3 instanceof PumpkinBlock && var4 == Blocks.CARVED_PUMPKIN) {
                var2.add(Pair.of(toState, G(Items.SHEARS)));
            }

            return var2;
        } else {
            return var2;
        }
    }

    public static boolean g(PlayerEntity player, BlockHitResult state) {
        BlockPos var2 = getCurrentPlacePos(player, state);
        return canCubePlace(player, var2);
    }

    private static boolean isGenericAcceptedInteractItem(Entity entity, ItemStack stack) {
        if (stack.isOf(Items.NAME_TAG) && entity instanceof LivingEntity) {
            return true;
        } else if (stack.getItem() instanceof SpawnEggItem && entity instanceof MobEntity) {
            return true;
        } else if (stack.isOf(Items.LEAD) && entity instanceof Leashable && !(entity instanceof LeashKnotEntity)) {
            return true;
        } else if (stack.isOf(Items.SADDLE) && entity instanceof Saddleable var2 && var2.canBeSaddled()) {
            return true;
        } else if (stack.isOf(Items.WATER_BUCKET) && entity instanceof Bucketable) {
            return true;
        } else if (stack.isOf(Items.SHEARS) && entity instanceof Shearable var3 && var3.isShearable()) {
            return true;
        } else if (entity instanceof AnimalEntity var4 && var4.isBreedingItem(stack)) {
            return true;
        } else if (entity instanceof MooshroomEntity var9) {
            return !var9.isBaby() && stack.isOf(Items.BOWL)
                    ? true
                    : var9.getVariant() == Type.BROWN && SuspiciousStewIngredient.of(stack.getItem()) != null;
        } else if (entity instanceof CowEntity var8) {
            return stack.isOf(Items.BUCKET) && !var8.isBaby();
        } else if (entity instanceof GoatEntity var7) {
            return stack.isOf(Items.BUCKET) && !var7.isBaby();
        } else if (entity instanceof IronGolemEntity var6) {
            return stack.isOf(Items.IRON_INGOT) && var6.getHealth() < var6.getMaxHealth();
        } else if (entity instanceof ArmadilloEntity var5) {
            return stack.isOf(Items.BRUSH) && !var5.isBaby();
        } else if (entity instanceof DolphinEntity) {
            return stack.isIn(ItemTags.FISHES);
        } else if (entity instanceof TadpoleEntity) {
            return stack.isIn(ItemTags.FROG_FOOD) || stack.isOf(Items.WATER_BUCKET);
        } else {
            return !(entity instanceof ParrotEntity)
                    ? false
                    : stack.isIn(ItemTags.PARROT_FOOD) || stack.isIn(ItemTags.PARROT_POISONOUS_FOOD);
        }
    }

    public static boolean canOpenScreen(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        Block var4 = state.getBlock();
        if (var4 instanceof ChestBlock) {
            return canChestOpen(world, pos, state);
        } else if (var4 instanceof ShulkerBoxBlock) {
            return canShulkerOpen(world, pos, state);
        } else if (var4 instanceof EnderChestBlock) {
            return canEnderChestOpen(world, pos);
        } else if (!(var4 instanceof LecternBlock)) {
            NamedScreenHandlerFactory var5 = state.createScreenHandlerFactory(world, pos);
            return var5 != null;
        } else {
            return state.contains(LecternBlock.HAS_BOOK) && (Boolean) state.get(LecternBlock.HAS_BOOK);
        }
    }

    public static boolean canChestOpen(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof ChestBlock)) {
            return false;
        } else if (ChestBlock.isChestBlocked(world, pos)) {
            return false;
        } else {
            if (state.contains(ChestBlock.CHEST_TYPE) && state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                BlockPos var3 = pos.offset(ChestBlock.getFacing(state));
                if (ChestBlock.isChestBlocked(world, var3)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean canShulkerOpen(World world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity var4
                && var4.getAnimationStage() != AnimationStage.CLOSED) {
            return true;
        } else {
            Box var5 = ShulkerEntity.calculateBoundingBox(
                            1.0F, (Direction) state.get(ShulkerBoxBlock.FACING), 0.0F, 0.5F)
                    .offset(pos)
                    .contract(1.0E-6);
            return world.isSpaceEmpty(var5);
        }
    }

    @Nullable
    public static BlockState c(PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hitResult) {
        return stack.getItem() instanceof BlockItem var5
                ? var5.getPlacementState(new ItemPlacementContext(player, hand, stack, hitResult))
                : null;
    }

    private static Predicate<ItemStack> G(Item item) {
        return stack -> stack != null && stack.isOf(item);
    }

    public static ActionResult simulateInteract(EntityHitResult entityHitResult) {
        ActionResult var1 = a.interactionManager.interactEntityAtLocation(
                a.player, entityHitResult.getEntity(), entityHitResult, Hand.MAIN_HAND);
        if (!var1.isAccepted()) {
            var1 = a.interactionManager.interactEntity(a.player, entityHitResult.getEntity(), Hand.MAIN_HAND);
        }

        if (var1.isAccepted() && var1.shouldSwingHand()) {
            a.player.swingHand(Hand.MAIN_HAND);
        }

        return var1;
    }

    private static boolean canFenceConsume(World world, BlockPos pos, @Nullable PlayerEntity player) {
        if (player == null) {
            return false;
        } else {
            boolean var3 = player.getMainHandStack().getItem() instanceof LeadItem
                    || player.getOffHandStack().getItem() instanceof LeadItem;
            if (!var3) {
                return false;
            } else {
                List var4 = LeadItem.collectLeashablesAround(world, pos, entity -> entity.getLeashHolder() == player);
                return !var4.isEmpty();
            }
        }
    }

    public static boolean canInteractAndPlace(PlayerEntity player, boolean flag) {
        return player.shouldCancelInteraction() || !flag;
    }

    private static boolean isInteractableRespawnAnchor(BlockState state, ItemStack stack) {
        int var2 = (Integer) state.get(RespawnAnchorBlock.CHARGES);
        return var2 != 0 || stack.isOf(Items.GLOWSTONE);
    }

    private static Predicate<ItemStack> isAnyOf(Item... items) {
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

    public static boolean canRespawnAnchorExplode(World world) {
        String var1 = world.getRegistryKey().getValue().toString();
        return Objects.equals(var1, "minecraft:overworld") || Objects.equals(var1, "minecraft:the_end")
                ? true
                : !world.getDimension().respawnAnchorWorks();
    }

    public static boolean isInteractAtAcceptable(
            World world, PlayerEntity player, Entity entity, Vec3d hitPos, ItemStack interactStack) {
        if (world == null || player == null || entity == null || hitPos == null) {
            return false;
        } else if (!player.isSpectator() && entity.isAlive()) {
            ItemStack var5 = interactStack == null ? ItemStack.EMPTY : interactStack;
            if (entity instanceof ArmorStandEntity var6) {
                if (var6.isMarker()) {
                    return false;
                } else if (var5.isOf(Items.NAME_TAG)) {
                    return false;
                } else if (!var5.isEmpty()) {
                    EquipmentSlot var10 = var6.getPreferredEquipmentSlot(var5);
                    return !canArmorStandUseSlot(var6, var10)
                            ? false
                            : var10.getType() != net.minecraft.entity.EquipmentSlot.Type.HAND || var6.shouldShowArms();
                } else {
                    EquipmentSlot var7 = getArmorStandHitSlot(var6, hitPos);
                    if (canArmorStandUseSlot(var6, var7)
                            && !var6.getEquippedStack(var7).isEmpty()) {
                        return true;
                    } else {
                        EquipmentSlot var8 = EquipmentSlot.MAINHAND;
                        if (canArmorStandUseSlot(var6, var8)
                                && !var6.getEquippedStack(var8).isEmpty()) {
                            return true;
                        } else {
                            EquipmentSlot var9 = EquipmentSlot.OFFHAND;
                            return canArmorStandUseSlot(var6, var9)
                                    && !var6.getEquippedStack(var9).isEmpty();
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean z(World world, PlayerEntity player, ItemStack interactStack) {
        if (world == null || player == null || interactStack == null || interactStack.isEmpty()) {
            return false;
        } else if (!interactStack.isItemEnabled(world.getEnabledFeatures())) {
            return false;
        } else if (player.getItemCooldownManager().isCoolingDown(interactStack.getItem())) {
            return false;
        } else {
            Item var3 = interactStack.getItem();
            if (var3 instanceof BowItem) {
                return player.isInCreativeMode()
                        || !player.getProjectileType(interactStack).isEmpty();
            } else if (var3 instanceof CrossbowItem) {
                ChargedProjectilesComponent var8 =
                        (ChargedProjectilesComponent) interactStack.get(DataComponentTypes.CHARGED_PROJECTILES);
                return var8 != null && !var8.isEmpty()
                        || !player.getProjectileType(interactStack).isEmpty();
            } else if (var3 instanceof TridentItem) {
                return true;
            } else if (interactStack.getMaxUseTime(player) > 0) {
                FoodComponent var7 = (FoodComponent) interactStack.get(DataComponentTypes.FOOD);
                return var7 != null ? player.canConsume(var7.canAlwaysEat()) : true;
            } else if (interactStack.getItem() instanceof Equipment var4
                    && (var4 instanceof ArmorItem || var4 instanceof ElytraItem)) {
                EquipmentSlot var9 = var4.getSlotType();
                if (!player.canUseSlot(var9)) {
                    return false;
                } else {
                    ItemStack var6 = player.getEquippedStack(var9);
                    return !ItemStack.areItemsAndComponentsEqual(interactStack, var6);
                }
            } else if (interactStack.getItem() instanceof ShieldItem
                    || VItem.w().b(interactStack)) {
                return true;
            } else if (var3 instanceof GoatHornItem) {
                return interactStack.contains(DataComponentTypes.INSTRUMENT);
            } else if (var3 instanceof FireworkRocketItem) {
                return player.isFallFlying();
            } else {
                return var3 instanceof OnAStickItem
                        ? player.hasVehicle()
                        : var3 instanceof SpyglassItem
                                || var3 instanceof BundleItem
                                || var3 instanceof FishingRodItem
                                || var3 instanceof BucketItem
                                || var3 instanceof BoatItem
                                || var3 instanceof PlaceableOnWaterItem
                                || var3 instanceof SpawnEggItem
                                || var3 instanceof EmptyMapItem
                                || var3 instanceof GlassBottleItem
                                || var3 instanceof WrittenBookItem
                                || var3 instanceof WritableBookItem
                                || var3 instanceof KnowledgeBookItem
                                || var3 instanceof EnderEyeItem
                                || var3 instanceof ProjectileItem;
            }
        }
    }

    public static boolean s(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        return t(world, player, pos, state, ItemStack.EMPTY);
    }

    public static boolean canCubePlace(PlayerEntity player, BlockPos pos) {
        World var2 = player.getEntityWorld();
        BlockState var3 = Blocks.STONE.getDefaultState();
        return var3.canPlaceAt(var2, pos) && var2.canPlace(var3, pos, ShapeContext.of(player));
    }

    public static boolean canHoldUse(ItemStack stack) {
        return VItem.w().h(stack)
                || stack.getItem() instanceof PotionItem
                || stack.getItem() instanceof ShieldItem
                || VItem.w().b(stack)
                || stack.getMaxUseTime(a.player) > 0;
    }

    public static boolean t(World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack interactStack) {
        Block var5 = state.getBlock();
        if (var5 instanceof RespawnAnchorBlock) {
            return isInteractableRespawnAnchor(state, interactStack);
        } else if (var5 instanceof LecternBlock) {
            return state.contains(LecternBlock.HAS_BOOK) && (Boolean) state.get(LecternBlock.HAS_BOOK);
        } else if (var5 instanceof FenceBlock) {
            return canFenceConsume(world, pos, player);
        } else if (var5 instanceof JukeboxBlock) {
            return state.contains(JukeboxBlock.HAS_RECORD) && (Boolean) state.get(JukeboxBlock.HAS_RECORD);
        } else if ((var5 instanceof CakeBlock || var5 instanceof CandleCakeBlock) && !player.canConsume(false)) {
            return false;
        } else if (var5 instanceof PumpkinBlock var14) {
            return interactStack.isOf(Items.SHEARS);
        } else if (var5 instanceof ComposterBlock var13) {
            return state.contains(ComposterBlock.LEVEL) && (Integer) state.get(ComposterBlock.LEVEL) == 8
                    || ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(interactStack.getItem());
        } else if (var5 instanceof BeehiveBlock var12) {
            return state.contains(BeehiveBlock.HONEY_LEVEL)
                    && (Integer) state.get(BeehiveBlock.HONEY_LEVEL) >= 5
                    && (interactStack.isOf(Items.SHEARS) || interactStack.isOf(Items.GLASS_BOTTLE));
        } else if (var5 instanceof CampfireBlock var11) {
            return world.getBlockEntity(pos) instanceof CampfireBlockEntity var21
                    && var21.getRecipeFor(interactStack).isPresent();
        } else if (var5 instanceof AbstractCauldronBlock var10) {
            return var10.behaviorMap.map().containsKey(interactStack.getItem());
        } else {
            Item var6 = interactStack.getItem();
            if (var6 instanceof MinecartItem && state.isIn(BlockTags.RAILS)) {
                return true;
            } else if (var6 instanceof ArmorStandItem) {
                return true;
            } else if (!(var6 instanceof EndCrystalItem)
                    || !state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.BEDROCK)) {
                if (var6 instanceof SpawnEggItem) {
                    return state.hasBlockEntity() && world.getBlockEntity(pos) instanceof Spawner
                            || state.getCollisionShape(world, pos).isEmpty();
                } else {
                    if (var6 instanceof FlintAndSteelItem || var6 instanceof FireChargeItem) {
                        if (CampfireBlock.canBeLit(state)
                                || CandleBlock.canBeLit(state)
                                || CandleCakeBlock.canBeLit(state)) {
                            return true;
                        }

                        BlockPos var8 = pos.offset(Direction.UP);
                        if (AbstractFireBlock.canPlaceAt(world, var8, player.getHorizontalFacing())) {
                            return true;
                        }
                    }

                    if (var6 instanceof BoneMealItem) {
                        if (state.getBlock() instanceof Fertilizable var7 && var7.isFertilizable(world, pos, state)) {
                            return true;
                        }

                        BlockPos var15 = pos.up();
                        if (state.isSideSolidFullSquare(world, pos, Direction.UP)
                                && world.getBlockState(var15).isOf(Blocks.WATER)
                                && world.getFluidState(var15).getLevel() == 8) {
                            return true;
                        }
                    }

                    if (var6 instanceof ShovelItem
                            && d.contains(var5)
                            && world.getBlockState(pos.up()).isAir()) {
                        return true;
                    } else if (var6 instanceof HoneycombItem
                            && HoneycombItem.getWaxedState(state).isPresent()) {
                        return true;
                    } else {
                        if (var6 instanceof PotionItem) {
                            PotionContentsComponent var19 = (PotionContentsComponent) interactStack.getOrDefault(
                                    DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
                            if (var19.matches(Potions.WATER) && state.isIn(BlockTags.CONVERTABLE_TO_MUD)) {
                                return true;
                            }
                        }

                        if (c == null) {
                            HashSet var20 = new HashSet();

                            for (Block var9 : Registries.BLOCK) {
                                if (var9 instanceof OperatorBlock
                                        || var9 instanceof AbstractSignBlock
                                        || var9 instanceof DoorBlock
                                        || var9 instanceof TrapdoorBlock
                                        || var9 instanceof FenceGateBlock
                                        || var9 instanceof BedBlock
                                        || var9 instanceof CakeBlock
                                        || var9 instanceof CandleCakeBlock
                                        || var9 instanceof FlowerPotBlock
                                        || var9 instanceof DecoratedPotBlock
                                        || var9 instanceof JukeboxBlock
                                        || var9 instanceof BellBlock
                                        || var9 instanceof LeverBlock
                                        || var9 instanceof ButtonBlock
                                        || var9 instanceof RedstoneOreBlock
                                        || var9 instanceof NoteBlock
                                        || var9 instanceof LightBlock
                                        || var9 instanceof DragonEggBlock
                                        || var9 instanceof ChestBlock
                                        || var9 instanceof ShulkerBoxBlock
                                        || var9 instanceof EnderChestBlock
                                        || var9 instanceof CraftingTableBlock
                                        || var9 instanceof StonecutterBlock
                                        || var9 instanceof LoomBlock
                                        || var9 instanceof SmithingTableBlock
                                        || var9 instanceof CartographyTableBlock
                                        || var9 instanceof GrindstoneBlock
                                        || var9 instanceof AnvilBlock
                                        || var9 instanceof BeaconBlock
                                        || var9 instanceof BarrelBlock
                                        || var9 instanceof BrewingStandBlock
                                        || var9 instanceof DispenserBlock
                                        || var9 instanceof HopperBlock
                                        || var9 instanceof CrafterBlock
                                        || var9 instanceof AbstractFurnaceBlock) {
                                    var20.add(var9);
                                }
                            }

                            c = var20;
                        }

                        return c.contains(var5);
                    }
                }
            } else {
                return true;
            }
        }
    }

    public static void swingHandIfSuccess(ActionResult actionResult, Hand hand) {
        if (actionResult.isAccepted() && actionResult.shouldSwingHand()) {
            a.player.swingHand(hand);
        }
    }

    public static boolean C(PlayerEntity player, FlagEntry<BlockHitResult> sneak) {
        return sneak != null && (player.shouldCancelInteraction() || !sneak.flag());
    }
}
