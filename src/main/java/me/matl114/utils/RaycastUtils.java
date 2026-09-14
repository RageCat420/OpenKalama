package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.world.AlignedFace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

@Modifiable
public class RaycastUtils {
   private static final Comparator<Vec3i> b = Comparator.comparingDouble(
      vec -> -Vec3d.of(vec).add(0.5, 0.5, 0.5).squaredDistanceTo(RaycastUtils.a.player.getPos())
   );
   private static final MinecraftClient a = MinecraftClient.getInstance();

   public static Optional<BlockPos> rayTraceSpecificBlock(Predicate<Block> blockPredicate) {
      if (a.world != null && a.player != null) {
         if (a.crosshairTarget != null && a.crosshairTarget.getType() == Type.BLOCK) {
            BlockHitResult var1 = (BlockHitResult)a.crosshairTarget;
            Block var2 = a.world.getBlockState(var1.getBlockPos()).getBlock();
            if (blockPredicate.test(var2)) {
               return Optional.of(var1.getBlockPos());
            }
         }

         for (BlockPos var5 : r(a.player.getEyePos(), a.player.getEyePos().add(a.player.getRotationVector().multiply(6.0)))) {
            Block var3 = a.world.getBlockState(var5).getBlock();
            if (blockPredicate.test(var3)) {
               return Optional.of(var5);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   private static Vec3d findTargetPointOnFace(BlockState currState, BlockPos currPos, Direction direction) {
      List<Box> var3 = currState.getOutlineShape(a.world, currPos, ShapeContext.of(a.player)).getBoundingBoxes();
      return var3.stream()
         .map(it -> {
            AlignedFace var2 = getBoxFace(it, direction);
            AlignedFace var3x = var2;
            if (var2.e().y >= 0.9) {
               AlignedFace var4 = var2.truncateY(0.6);
               if (var4 != null && !var4.isEmpty()) {
                  var3x = var4;
               }
            }

            Vec3d var5 = var3x.getCenter();
            return var5 == null ? null : new Pair<>(var3x, var5);
         })
         .filter(Objects::nonNull)
         .max(
            Comparator.<Pair<AlignedFace, Vec3d>>comparingDouble(
                  it -> it.getSecond().subtract(new Vec3d(0.5, 0.5, 0.5)).multiply(Vec3d.of(direction.getVector())).lengthSquared()
               )
               .thenComparingDouble(it -> it.getSecond().y)
         )
         .map(Pair::getSecond)
         .orElse(null);
   }

   public static Iterator<BlockPos> t(Vec3d start, Vec3d end) {
      return createRaycastBlockPosIterator(start, end, false);
   }

   public static Iterable<BlockPos> s(Vec3d start, Vec3d end, boolean enableThreshold) {
      return () -> createRaycastBlockPosIterator(start, end, enableThreshold);
   }

   public static Optional<Entity> rayTraceSpecificEntity(Predicate<Entity> entityPredicate) {
      if (a.world != null && a.player != null) {
         if (a.crosshairTarget != null && a.crosshairTarget.getType() == Type.ENTITY) {
            EntityHitResult var1 = (EntityHitResult)a.crosshairTarget;
            if (entityPredicate.test(var1.getEntity())) {
               return Optional.of(var1.getEntity());
            }
         }

         Vec3d var6 = a.player.getEyePos();
         Vec3d var2 = a.player.getEyePos().add(a.player.getRotationVector().multiply(6.0));
         Box var3 = new Box(var6, var2);

         for (Entity var5 : a.world.getOtherEntities(a.player, var3, entityPredicate)) {
            if (var5.getBoundingBox().raycast(var6, var2).isPresent()) {
               return Optional.of(var5);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
      Vec3d var4 = hitResult.getPos();
      if (!var4.isInRange(cameraPos, interactionRange)) {
         Vec3d var5 = hitResult.getPos();
         Direction var6 = Direction.getFacing(var5.x - cameraPos.x, var5.y - cameraPos.y, var5.z - cameraPos.z);
         return BlockHitResult.createMissed(var5, var6, BlockPos.ofFloored(var5));
      } else {
         return hitResult;
      }
   }

   public static EntityHitResult createRealHitResult(Entity entity, Vec3d playerEyePos) {
      Box var2 = entity.getBoundingBox();
      Vec3d var3 = var2.getCenter();
      Optional var4 = var2.raycast(playerEyePos, var3);
      return var4 != null && var4.isPresent() ? new EntityHitResult(entity, (Vec3d)var4.get()) : new EntityHitResult(entity);
   }

   public static BlockHitResult createHitResult(BlockPos pos, Direction blockFace) {
      if (a.player == null) {
         return null;
      } else {
         Vec3d var2 = pos.toCenterPos().offset(blockFace, 0.5);
         return new BlockHitResult(var2, blockFace, pos, false);
      }
   }

   public static HitResult findBestBlockPlacement(BlockPos pos) {
      BlockState var1 = a.world.getBlockState(pos);
      return var1.isReplaceable() ? null : null;
   }

   public static boolean raycastAnySolidBlock(Entity e, Vec3d from, Vec3d to) {
      BlockHitResult var3 = raycastSolidBlockResult(e, from, to);
      return var3 != null && var3.getType() != Type.MISS;
   }

   public static boolean raycastHitAnyEntityExceptPlayer(Entity e, Vec3d from, Vec3d to) {
      EntityHitResult var3 = raycastHitEntityExceptPlayerResult(e, from, to);
      return var3 != null && var3.getType() != Type.MISS;
   }

   public static double getFirstIntersection(double start, double dir, double step) {
      if (dir > 0.0) {
         return (Math.floor(start) + 1.0 - start) * step;
      } else {
         return dir < 0.0 ? (start - Math.floor(start)) * step : Double.POSITIVE_INFINITY;
      }
   }

   public static EntityHitResult raycastHitEntityExceptPlayerResult(Entity e, Vec3d from, Vec3d to) {
      return ProjectileUtil.raycast(e, from, to, new Box(from, to), es -> !es.isSpectator() && es.canHit() && es != a.player, 16384.0);
   }

   public static Iterator<BlockPos> createRaycastBlockPosIterator(Vec3d start, Vec3d end, boolean enableThreshold) {
      if (start.equals(end)) {
         ArrayList var3 = new ArrayList();
         var3.add(BlockPos.ofFloored(start));
         return var3.iterator();
      } else {
         double var4 = enableThreshold ? -1.0E-7 : 0.0;
         double var6 = MathHelper.lerp(var4, end.x, start.x);
         double var8 = MathHelper.lerp(var4, end.y, start.y);
         double var10 = MathHelper.lerp(var4, end.z, start.z);
         double var12 = MathHelper.lerp(var4, start.x, end.x);
         double var14 = MathHelper.lerp(var4, start.y, end.y);
         double var16 = MathHelper.lerp(var4, start.z, end.z);
         BlockPos var18 = BlockPos.ofFloored(var12, var14, var16);
         double var19 = var6 - var12;
         double var21 = var8 - var14;
         double var23 = var10 - var16;
         int var25 = MathHelper.sign(var19);
         int var26 = MathHelper.sign(var21);
         int var27 = MathHelper.sign(var23);
         double var28 = var25 == 0 ? Double.MAX_VALUE : var25 / var19;
         double var30 = var26 == 0 ? Double.MAX_VALUE : var26 / var21;
         double var32 = var27 == 0 ? Double.MAX_VALUE : var27 / var23;
         return new KalamaHelperHelperQX(var18, var28, var25, var12, var30, var26, var14, var32, var27, var16);
      }
   }

   public static AlignedFace getBoxFace(Box box, Direction direction) {
      return switch (direction) {
         case DOWN -> new AlignedFace(new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.maxX, box.minY, box.maxZ));
         case UP -> new AlignedFace(new Vec3d(box.minX, box.maxY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ));
         case NORTH -> new AlignedFace(new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.maxX, box.maxY, box.minZ));
         case SOUTH -> new AlignedFace(new Vec3d(box.minX, box.minY, box.maxZ), new Vec3d(box.maxX, box.maxY, box.maxZ));
         case WEST -> new AlignedFace(new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.minX, box.maxY, box.maxZ));
         case EAST -> new AlignedFace(new Vec3d(box.maxX, box.minY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ));
         default -> throw new MatchException(null, null);
      };
   }

   public static HitResult createEntityOnlyCrossHairResult(Entity camera, double entityInteractionRange, float tickDelta, Predicate<Entity> filter) {
      double var7 = MathHelper.square(entityInteractionRange);
      Vec3d var9 = camera.getCameraPosVec(tickDelta);
      Vec3d var10 = camera.getRotationVec(tickDelta);
      Vec3d var11 = var9.add(var10.x * entityInteractionRange, var10.y * entityInteractionRange, var10.z * entityInteractionRange);
      Box var12 = camera.getBoundingBox().stretch(var10.multiply(entityInteractionRange)).expand(1.0, 1.0, 1.0);
      EntityHitResult var13 = ProjectileUtil.raycast(
         camera, var9, var11, var12, entity -> !entity.isSpectator() && entity.canHit() && (filter == null || filter.test(entity)), var7
      );
      return var13 != null && var13.getPos().squaredDistanceTo(var9) < var7 ? ensureTargetInRange(var13, var9, entityInteractionRange) : null;
   }

   public static boolean canRaycastHit(PlayerEntity player, float pitch, float yaw, BlockPos pos, double distance) {
      Vec3d var6 = player.getEyePos();
      Vec3d var7 = EntityUtils.pitchYawToRotation(pitch, yaw);
      Vec3d var8 = var7.normalize().multiply(distance);
      Box var9 = new Box(pos);
      return var9.raycast(var6, var6.add(var8)).isPresent();
   }

   public static boolean w(PlayerEntity player, float pitch, float yaw, Entity target) {
      return x(player, pitch, yaw, target, player.getEntityInteractionRange());
   }

   public static Iterable<BlockPos> r(Vec3d start, Vec3d end) {
      return () -> t(start, end);
   }

   public static boolean raycastHitAnyEntity(Entity e, Vec3d from, Vec3d to) {
      EntityHitResult var3 = ProjectileUtil.raycast(e, from, to, new Box(from, to), es -> !es.isSpectator() && es.canHit(), 16384.0);
      return var3 != null && var3.getType() != Type.MISS;
   }

   public static BlockHitResult f(BlockPos pos) {
      Vec3d var1 = a.player.getCameraPosVec(1.0F);
      Vec3d var2 = pos.toCenterPos();
      Vec3d var3 = var1.subtract(var2);
      Direction var4 = Direction.getFacing(var3.x, var3.y, var3.z);
      Vec3d var10000;
      if (var3.lengthSquared() > 0.25) {
         switch (var4) {
            case DOWN:
               var10000 = var1.subtract(var3.multiply((var1.y - (var2.y - 0.5)) / var3.y));
               break;
            case UP:
               var10000 = var1.subtract(var3.multiply((var1.y - (var2.y + 0.5)) / var3.y));
               break;
            case NORTH:
               var10000 = var1.subtract(var3.multiply((var1.z - (var2.z - 0.5)) / var3.z));
               break;
            case SOUTH:
               var10000 = var1.subtract(var3.multiply((var1.z - (var2.z + 0.5)) / var3.z));
               break;
            case WEST:
               var10000 = var1.subtract(var3.multiply((var1.x - (var2.x - 0.5)) / var3.x));
               break;
            case EAST:
               var10000 = var1.subtract(var3.multiply((var1.x - (var2.x + 0.5)) / var3.x));
               break;
            default:
               throw new MatchException(null, null);
         }
      } else {
         var10000 = var2.offset(var4, 0.5);
      }

      Vec3d var5 = var10000;
      return new BlockHitResult(var5, var4, pos, false);
   }

   public static BlockHitResult raycastSolidBlockResult(Entity e, Vec3d from, Vec3d to) {
      return a.world.raycast(new RaycastContext(from, to, ShapeType.COLLIDER, FluidHandling.NONE, e));
   }

   public static HitResult createCrossHairHitResult(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
      double var6 = Math.max(blockInteractionRange, entityInteractionRange);
      double var8 = MathHelper.square(var6);
      Vec3d var10 = camera.getCameraPosVec(tickDelta);
      HitResult var11 = camera.raycast(var6, tickDelta, false);
      double var12 = var11.getPos().squaredDistanceTo(var10);
      if (var11.getType() != Type.MISS) {
         var8 = var12;
         var6 = Math.sqrt(var12);
      }

      Vec3d var14 = camera.getRotationVec(tickDelta);
      Vec3d var15 = var10.add(var14.x * var6, var14.y * var6, var14.z * var6);
      float var16 = 1.0F;
      Box var17 = camera.getBoundingBox().stretch(var14.multiply(var6)).expand(1.0, 1.0, 1.0);
      EntityHitResult var18 = ProjectileUtil.raycast(camera, var10, var15, var17, entity -> !entity.isSpectator() && entity.canHit(), var8);
      return var18 != null && var18.getPos().squaredDistanceTo(var10) < var12
         ? ensureTargetInRange(var18, var10, entityInteractionRange)
         : ensureTargetInRange(var11, var10, blockInteractionRange);
   }

   public static BlockHitResult g(BlockPos pos, Vec3d playerEyePos) {
      Direction var2 = Direction.getFacing(pos.toCenterPos().subtract(playerEyePos)).getOpposite();
      return createHitResult(pos, var2);
   }

   public static boolean x(PlayerEntity player, float pitch, float yaw, Entity target, double distance) {
      Vec3d var6 = player.getEyePos();
      Vec3d var7 = EntityUtils.pitchYawToRotation(pitch, yaw);
      Vec3d var8 = var7.normalize().multiply(distance);
      Box var9 = target.getBoundingBox();
      return var9.raycast(var6, var6.add(var8)).isPresent();
   }
}
