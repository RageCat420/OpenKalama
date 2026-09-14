package me.matl114.utils;

import com.mojang.authlib.GameProfile;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import me.matl114.versioned.api.VRecord;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldUtils {
    public static final int UPDATE_BLOCK_NO_PHYSICS = 530;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Stream<String> c() {
        return Stream.empty();
    }

    public static boolean canEntitySpawnAt(World world, BlockPos pos, EntityType<?> type) {
        BlockState var3 = world.getBlockState(pos);
        BlockState var4 = world.getBlockState(pos.up());
        BlockState var5 = world.getBlockState(pos.down());
        Vec3d var6 = pos.toBottomCenterPos();
        return var5.allowsSpawning(world, pos.down(), type)
                && world.isSpaceEmpty(type.getSpawnBox(var6.x, var6.y, var6.z))
                && SpawnHelper.isClearForSpawn(world, pos, var3, var3.getFluidState(), type)
                && SpawnHelper.isClearForSpawn(world, pos.up(), var4, var4.getFluidState(), type);
    }

    public static Stream<KalamaHelperHelperAk> d() {
        return Stream.empty();
    }

    public static boolean n(int chunkX, int chunkZ) {
        return isChunkLoaded(chunkX, chunkZ);
    }

    public static float getPlayerBlockBreakingSpeedAt(BlockState state) {
        return getPlayerBlockBreakingSpeedWithCanMineMultiply(mc.player, state, mc.player.getMainHandStack());
    }

    public static boolean areWorldEquals(ClientWorld world1, ClientWorld world2) {
        return world1 == world2
                || world1 != null
                        && world2 != null
                        && Objects.equals(
                                world1.getRegistryKey().getValue(),
                                world2.getRegistryKey().getValue());
    }

    private static boolean canToolHarvest(BlockState state, ItemStack stack) {
        return !state.isToolRequired() || stack.isSuitableFor(state);
    }

    public static boolean isServerChunkLoaded(BlockPos pos) {
        return n(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public static Map<BlockPos, BlockState> scannChunk(Chunk chunk, BiPredicate<BlockPos, BlockState> predicate) {
        ChunkPos var2 = chunk.getPos();
        int var3 = var2.getStartX();
        int var4 = chunk.getBottomY();
        int var5 = var2.getStartZ();
        int var6 = var2.getEndX();
        int var7 = chunk.getHighestNonEmptySection();
        int var8 = var7 == -1 ? chunk.getBottomY() : ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(var7 + 1));
        int var9 = var2.getEndZ();
        LinkedHashMap var10 = new LinkedHashMap();

        for (int var11 = var3; var11 <= var6; var11++) {
            for (int var12 = var4; var12 <= var8; var12++) {
                for (int var13 = var5; var13 <= var9; var13++) {
                    BlockPos var14 = new BlockPos(var11, var12, var13);
                    BlockState var15 = chunk.getBlockState(var14);
                    if (predicate.test(var14, var15)) {
                        var10.put(var14, var15);
                    }
                }
            }
        }

        return var10;
    }

    public static KalamaHelperHelperAk getWaypoint(String lookup) {
        PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(lookup);
        String var2;
        if (var1 != null) {
            var2 = VRecord.getId(var1.getProfile()).toString();
        } else {
            var2 = null;
        }

        return d().filter(s -> lookup.equalsIgnoreCase(s.getDisplayName())
                        || var2 != null && var2.equalsIgnoreCase(s.getDisplayName()))
                .findFirst()
                .orElse(null);
    }

    public static Stream<String> getPlayerListNames() {
        return mc.getNetworkHandler().getPlayerList().stream()
                .<GameProfile>map(PlayerListEntry::getProfile)
                .map(VRecord::getName);
    }

    public static boolean isInfiniteWater(World world, BlockPos pos) {
        int var2 = 0;

        for (Direction var4 : Type.HORIZONTAL) {
            FluidState var5 = world.getFluidState(pos.offset(var4));
            if (var5.isOf(Fluids.WATER) && var5.isStill()) {
                var2++;
            }
        }

        if (var2 < 2) {
            return false;
        } else {
            BlockPos var6 = pos.down();
            BlockState var7 = world.getBlockState(var6);
            FluidState var8 = var7.getFluidState();
            return var7.isSolid() || var8.isOf(Fluids.WATER) && var8.isStill();
        }
    }

    public static boolean isChunkLoaded(int chunkX, int chunkZ) {
        return mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }

    public static boolean o(BlockPos pos) {
        return isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public static float getPlayerBlockBreakingSpeedWithCanMineMultiply(
            PlayerEntity player, BlockState state, ItemStack stack) {
        float var3 = stack.getMiningSpeedMultiplier(state);
        if (var3 > 1.0F) {
            AttributeContainer var4 = new AttributeContainer(DefaultAttributeRegistry.get(
                    (net.minecraft.entity.EntityType<? extends net.minecraft.entity.LivingEntity>) player.getType()));
            var4.setFrom(player.getAttributes());
            stack.applyAttributeModifiers(EquipmentSlot.MAINHAND, (holder, attr) -> {
                EntityAttributeInstance var3x = var4.getCustomInstance(holder);
                if (var3x != null) {
                    var3x.removeModifier(attr.id());
                    var3x.addTemporaryModifier(attr);
                }
            });
            var3 = (float) (var3 + var4.getValue(EntityAttributes.PLAYER_MINING_EFFICIENCY));
        }

        if (StatusEffectUtil.hasHaste(player)) {
            var3 *= 1.0F + (StatusEffectUtil.getHasteAmplifier(player) + 1) * 0.2F;
        }

        if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            var3 *= switch (player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;};
        }

        var3 *= (float) player.getAttributeValue(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
        if (player.isSubmergedIn(FluidTags.WATER)) {
            var3 *= (float) player.getAttributeInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED)
                    .getValue();
        }

        if (!player.isOnGround()) {
            var3 /= 5.0F;
        }

        int var8 = canToolHarvest(state, stack) ? 30 : 100;
        return var3 / var8;
    }

    public static float i(BlockState state, BlockView world, BlockPos pos) {
        float var3 = getPlayerBlockBreakingSpeedAt(state);
        return calcBlockBreakingDelta(state, world, pos, var3);
    }

    public static float calcBlockBreakingDelta(
            BlockState state, BlockView world, BlockPos pos, float playerBreakSpeed) {
        float var4 = state.getHardness(world, pos);
        return var4 == -1.0F ? 0.0F : playerBreakSpeed / var4;
    }

    public static boolean isServerPosLoaded(int blockPosX, int blockPosZ) {
        return n(ChunkSectionPos.getSectionCoord(blockPosX), ChunkSectionPos.getSectionCoord(blockPosZ));
    }
}
