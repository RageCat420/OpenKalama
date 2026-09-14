package me.matl114.utils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import me.matl114.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record BlockLocation(RegistryKey<World> world, int x, int y, int z) {
   public static MapCodec<BlockLocation> JL = RecordCodecBuilder.mapCodec(
      o -> o.group(
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf("world").forGetter(BlockLocation::vS),
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockLocation::YO)
         )
         .apply(o, BlockLocation::YN)
   );
   public static MapCodec<BlockLocation> wJ = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf("world").forGetter(BlockLocation::vS),
            Codec.INT.fieldOf("x").forGetter(BlockLocation::x),
            Codec.INT.fieldOf("y").forGetter(BlockLocation::y),
            Codec.INT.fieldOf("z").forGetter(BlockLocation::z)
         )
         .apply(instance, BlockLocation::new)
   );
   public static Codec<BlockLocation> dt = Codec.withAlternative(wJ.codec(), JL.codec());

   public boolean isLocationLoaded(World world) {
      return Objects.equals(world.getRegistryKey(), this.vS()) ? world.getChunkManager().isChunkLoaded(this.x >> 4, this.z >> 4) : false;
   }

   public boolean YQ(BlockLocation location, double distance) {
      return Objects.equals(location.world, this.world) ? MathUtils.b(this.x - location.x) + MathUtils.b(this.z - location.z) <= MathUtils.a(distance) : false;
   }

   public boolean YP(BlockLocation location, double distance) {
      return Objects.equals(location.world, this.world)
         ? MathUtils.b(this.x - location.x) + MathUtils.b(this.z - location.z) + MathUtils.b(this.y - location.y) <= MathUtils.a(distance)
         : false;
   }

   public static BlockLocation of(Entity entity) {
      return new BlockLocation(entity.getEntityWorld().getRegistryKey(), entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
   }

   public RegistryKey<World> vS() {
      return this.world;
   }

   public static BlockLocation YM(World world, BlockPos pos) {
      return new BlockLocation(world.getRegistryKey(), pos.getX(), pos.getY(), pos.getZ());
   }

   public BlockPos YO() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public static BlockLocation YN(RegistryKey<World> world, BlockPos pos) {
      return new BlockLocation(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
