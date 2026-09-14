package me.matl114.utils;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.matl114.accessors.moonrise.MoonriseBlockStateBaseAccess;
import me.matl114.accessors.moonrise.MoonriseChunkBlockCountingAccess;
import me.matl114.accessors.moonrise.MoonriseVoxelShapeAccess;
import me.matl114.hacks.RenderTasks;
import me.matl114.utils.world.CachedShapeData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.PalettedContainer;

public final class CollisionUtil {
   public static final int d = 2;
   public static final int e = 4;
   public static final DoubleArrayList ZERO_ONE = DoubleArrayList.wrap(new double[]{0.0, 1.0});
   public static final double COLLISION_EPSILON = 1.0E-7;
   public static final int c = 1;
   public static final int f = 8;

   public static boolean voxelShapeIntersectHorizontal(Box box1, Box box2) {
      return box1.minX - box2.maxX < -1.0E-7 && box1.maxX - box2.minX > 1.0E-7 && box1.minZ - box2.maxZ < -1.0E-7 && box1.maxZ - box2.minZ > 1.0E-7;
   }

   public static double collideX(Box target, Box source, double source_move) {
      if (!(source.minX - target.maxX < -1.0E-7)
         || !(source.maxX - target.minX > 1.0E-7)
         || !(source.minZ - target.maxZ < -1.0E-7)
         || !(source.maxZ - target.minZ > 1.0E-7)) {
         return source_move;
      } else if (source_move >= 0.0) {
         double var6 = target.minY - source.maxY;
         return var6 < -1.0E-7 ? source_move : Math.min(var6, source_move);
      } else {
         double var4 = target.maxY - source.minY;
         return var4 > 1.0E-7 ? source_move : Math.max(var4, source_move);
      }
   }

   public static Box resetY(Box box, double y1, double y2) {
      return new Box(box.minX, y1, box.minZ, box.maxX, y2, box.maxZ);
   }

   public static double J(Box currentBoundingBox, double value, List<Box> potentialCollisions) {
      int var4 = 0;

      for (int var5 = potentialCollisions.size(); var4 < var5; var4++) {
         if (Math.abs(value) < 1.0E-7) {
            return 0.0;
         }

         Box var6 = (Box)potentialCollisions.get(var4);
         value = i(var6, currentBoundingBox, value);
      }

      return value;
   }

   public static boolean strictlyContains(VoxelShape voxel, Vec3d point) {
      return r(voxel, point.x, point.y, point.z);
   }

   public static boolean f(
      double minX1,
      double minY1,
      double minZ1,
      double maxX1,
      double maxY1,
      double maxZ1,
      double minX2,
      double minY2,
      double minZ2,
      double maxX2,
      double maxY2,
      double maxZ2
   ) {
      return minX1 - maxX2 < -1.0E-7
         && maxX1 - minX2 > 1.0E-7
         && minY1 - maxY2 < -1.0E-7
         && maxY1 - minY2 > 1.0E-7
         && minZ1 - maxZ2 < -1.0E-7
         && maxZ1 - minZ2 > 1.0E-7;
   }

   public static boolean isEntitySupported(Entity entity, double yDepth) {
      World var3 = entity.getEntityWorld();
      Box var4 = entity.getBoundingBox();
      Box var5 = var4.stretch(0.0, -yDepth, 0.0);
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      byte var8 = 0;
      return U(var3, entity, var5, var6, var7, var8, null, null);
   }

   public static Box offsetX(Box box, double dz) {
      return new Box(box.minX, box.minY, box.minZ - dz, box.maxX, box.maxY, box.maxZ);
   }

   public static Box cutUpwards(Box box, double dy) {
      return new Box(box.minX, box.maxY, box.minZ, box.maxX, box.maxY + dy, box.maxZ);
   }

   public static Box cutBackwards(Box box, double dz) {
      return new Box(box.minX, box.minY, box.minZ + dz, box.maxX, box.maxY, box.minZ);
   }

   public static boolean isEmpty(Box aabb) {
      return aabb.maxX - aabb.minX < 1.0E-7 || aabb.maxY - aabb.minY < 1.0E-7 || aabb.maxZ - aabb.minZ < 1.0E-7;
   }

   public static Box w(Box box, double dx) {
      return new Box(box.minX, box.minY, box.minZ, box.maxX + dx, box.maxY, box.maxZ);
   }

   public static boolean voxelShapeIntersect(Box box, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return box.minX - maxX < -1.0E-7
         && box.maxX - minX > 1.0E-7
         && box.minY - maxY < -1.0E-7
         && box.maxY - minY > 1.0E-7
         && box.minZ - maxZ < -1.0E-7
         && box.maxZ - minZ > 1.0E-7;
   }

   public static double O(Box currentBoundingBox, double value, List<VoxelShape> potentialCollisions) {
      int var4 = 0;

      for (int var5 = potentialCollisions.size(); var4 < var5; var4++) {
         if (Math.abs(value) < 1.0E-7) {
            return 0.0;
         }

         VoxelShape var6 = (VoxelShape)potentialCollisions.get(var4);
         value = p(var6, currentBoundingBox, value);
      }

      return value;
   }

   public static Vec3d performVoxelCollisions(Vec3d moveVector, Box axisalignedbb, List<VoxelShape> potentialCollisions) {
      double var3 = moveVector.x;
      double var5 = moveVector.y;
      double var7 = moveVector.z;
      if (var5 != 0.0) {
         var5 = N(axisalignedbb, var5, potentialCollisions);
         if (var5 != 0.0) {
            axisalignedbb = u(axisalignedbb, var5);
         }
      }

      boolean var9 = Math.abs(var3) < Math.abs(var7);
      if (var9 && var7 != 0.0) {
         var7 = O(axisalignedbb, var7, potentialCollisions);
         if (var7 != 0.0) {
            axisalignedbb = v(axisalignedbb, var7);
         }
      }

      if (var3 != 0.0) {
         var3 = M(axisalignedbb, var3, potentialCollisions);
         if (!var9 && var3 != 0.0) {
            axisalignedbb = t(axisalignedbb, var3);
         }
      }

      if (!var9 && var7 != 0.0) {
         var7 = O(axisalignedbb, var7, potentialCollisions);
      }

      return new Vec3d(var3, var5, var7);
   }

   public static boolean h(Box box1, Box box2) {
      return box1.minX - box2.maxX < -1.0E-7
         && box1.maxX - box2.minX > 1.0E-7
         && box1.minY - box2.maxY < -1.0E-7
         && box1.maxY - box2.minY > 1.0E-7
         && box1.minZ - box2.maxZ < -1.0E-7
         && box1.maxZ - box2.minZ > 1.0E-7;
   }

   public static List<Vec3d> getBoxVertices(Box box) {
      ArrayList var1 = new ArrayList(8);

      for (int var2 = 0; var2 < 8; var2++) {
         double var3 = (var2 & 1) == 0 ? box.minX : box.maxX;
         double var5 = (var2 & 2) == 0 ? box.minY : box.maxY;
         double var7 = (var2 & 4) == 0 ? box.minZ : box.maxZ;
         var1.add(new Vec3d(var3, var5, var7));
      }

      return var1;
   }

   public static List<Box> getIntersectBox(Box box, List<Box> boxList) {
      ArrayList var2 = new ArrayList();

      for (Box var4 : boxList) {
         if (h(box, var4)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public static boolean collisionDebugRender() {
      return RenderTasks.e && RenderTasks.g;
   }

   public static Box cutRight(Box box, double dx) {
      return new Box(box.maxX, box.minY, box.minZ, box.maxX + dx, box.maxY, box.maxZ);
   }

   public static double calculateAxisMin(MoonriseVoxelShapeAccess access, Axis axis) {
      CachedShapeData var2 = access.moonrise$getCachedVoxelData();
      switch (axis) {
         case X: {
            int var5 = var2.minFullX();
            return var5 >= var2.sizeX() ? Double.POSITIVE_INFINITY : access.moonrise$rootCoordinatesX()[var5] + access.moonrise$offsetX();
         }
         case Y: {
            int var4 = var2.minFullY();
            return var4 >= var2.sizeY() ? Double.POSITIVE_INFINITY : access.moonrise$rootCoordinatesY()[var4] + access.moonrise$offsetY();
         }
         case Z: {
            int var3 = var2.minFullZ();
            return var3 >= var2.sizeZ() ? Double.POSITIVE_INFINITY : access.moonrise$rootCoordinatesZ()[var3] + access.moonrise$offsetZ();
         }
         default:
            return Double.POSITIVE_INFINITY;
      }
   }

   public static Box z(Box box, double dy) {
      return new Box(box.minX, box.minY - dy, box.minZ, box.maxX, box.maxY, box.maxZ);
   }

   public static Box x(Box box, double dx) {
      return new Box(box.minX - dx, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
   }

   public static boolean voxelShapeIntersectNoEmpty(VoxelShape voxel, Box aabb) {
      if (voxel.isEmpty()) {
         return false;
      } else {
         double var2 = MoonriseVoxelShapeAccess.of(voxel).moonrise$offsetX();
         double var4 = MoonriseVoxelShapeAccess.of(voxel).moonrise$offsetY();
         double var6 = MoonriseVoxelShapeAccess.of(voxel).moonrise$offsetZ();
         double[] var8 = MoonriseVoxelShapeAccess.of(voxel).moonrise$rootCoordinatesX();
         double[] var9 = MoonriseVoxelShapeAccess.of(voxel).moonrise$rootCoordinatesY();
         double[] var10 = MoonriseVoxelShapeAccess.of(voxel).moonrise$rootCoordinatesZ();
         CachedShapeData var11 = MoonriseVoxelShapeAccess.of(voxel).moonrise$getCachedVoxelData();
         int var12 = var11.minFullX();
         int var13 = var11.minFullY();
         int var14 = var11.sizeX();
         int var15 = Math.max(0, findFloor(var8, aabb.minX - var2 + 1.0E-7, 0, var12));
         if (var15 >= var12) {
            return false;
         } else {
            int var16 = Math.min(var12, findFloor(var8, aabb.maxX - var2 - 1.0E-7, var15, var12) + 1);
            if (var15 >= var16) {
               return false;
            } else {
               int var17 = Math.max(0, findFloor(var9, aabb.minY - var4 + 1.0E-7, 0, var13));
               if (var17 >= var13) {
                  return false;
               } else {
                  int var18 = Math.min(var13, findFloor(var9, aabb.maxY - var4 - 1.0E-7, var17, var13) + 1);
                  if (var17 >= var18) {
                     return false;
                  } else {
                     int var19 = Math.max(0, findFloor(var10, aabb.minZ - var6 + 1.0E-7, 0, var14));
                     if (var19 >= var14) {
                        return false;
                     } else {
                        int var20 = Math.min(var14, findFloor(var10, aabb.maxZ - var6 - 1.0E-7, var19, var14) + 1);
                        if (var19 >= var20) {
                           return false;
                        } else {
                           long[] var21 = var11.voxelSet();
                           int var22 = var13 * var14;

                           for (int var23 = var15; var23 < var16; var23++) {
                              for (int var24 = var17; var24 < var18; var24++) {
                                 for (int var25 = var19; var25 < var20; var25++) {
                                    int var26 = var25 + var24 * var14 + var23 * var22;
                                    if ((var21[var26 >>> 6] & 1L << var26) != 0L) {
                                       return true;
                                    }
                                 }
                              }
                           }

                           return false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static Box t(Box box, double dx) {
      return new Box(box.minX + dx, box.minY, box.minZ, box.maxX + dx, box.maxY, box.maxZ);
   }

   private static double S(double x, double y) {
      return x < y ? x : y;
   }

   public static Vec3d performCollisions(Vec3d moveVector, Box axisalignedbb, List<VoxelShape> voxels, List<Box> aabbs) {
      if (voxels.isEmpty()) {
         return Q(moveVector, axisalignedbb, aabbs);
      } else {
         double var4 = moveVector.x;
         double var6 = moveVector.y;
         double var8 = moveVector.z;
         if (var6 != 0.0) {
            double var12 = K(axisalignedbb, var6, aabbs);
            var6 = N(axisalignedbb, var12, voxels);
            RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(0.0, var6, 0.0));
            if (var6 != 0.0) {
               axisalignedbb = u(axisalignedbb, var6);
            }
         }

         boolean var10 = Math.abs(var4) < Math.abs(var8);
         if (var10 && var8 != 0.0) {
            double var13 = L(axisalignedbb, var8, aabbs);
            var8 = O(axisalignedbb, var13, voxels);
            RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(0.0, 0.0, var8));
            if (var8 != 0.0) {
               axisalignedbb = v(axisalignedbb, var8);
            }
         }

         if (var4 != 0.0) {
            double var11 = J(axisalignedbb, var4, aabbs);
            var4 = M(axisalignedbb, var11, voxels);
            RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(var4, 0.0, 0.0));
            if (!var10 && var4 != 0.0) {
               axisalignedbb = t(axisalignedbb, var4);
            }
         }

         if (!var10 && var8 != 0.0) {
            double var14 = L(axisalignedbb, var8, aabbs);
            var8 = O(axisalignedbb, var14, voxels);
            RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(0.0, 0.0, var8));
         }

         return new Vec3d(var4, var6, var8);
      }
   }

   public static double p(VoxelShape target, Box source, double source_move) {
      Box var4 = MoonriseVoxelShapeAccess.of(target).moonrise$getSingleAABBRepresentation();
      if (var4 != null) {
         return k(var4, source, source_move);
      } else {
         double var5 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetX();
         double var7 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetY();
         double var9 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetZ();
         double[] var11 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesX();
         double[] var12 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesY();
         double[] var13 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesZ();
         CachedShapeData var14 = MoonriseVoxelShapeAccess.of(target).moonrise$getCachedVoxelData();
         int var15 = var14.minFullX();
         int var16 = var14.minFullY();
         int var17 = var14.sizeX();
         int var18 = Math.max(0, findFloor(var11, source.minX - var5 + 1.0E-7, 0, var15));
         if (var18 >= var15) {
            return source_move;
         } else {
            int var19 = Math.min(var15, findFloor(var11, source.maxX - var5 - 1.0E-7, var18, var15) + 1);
            if (var18 >= var19) {
               return source_move;
            } else {
               int var20 = Math.max(0, findFloor(var12, source.minY - var7 + 1.0E-7, 0, var16));
               if (var20 >= var16) {
                  return source_move;
               } else {
                  int var21 = Math.min(var16, findFloor(var12, source.maxY - var7 - 1.0E-7, var20, var16) + 1);
                  if (var20 >= var21) {
                     return source_move;
                  } else {
                     long[] var22 = var14.voxelSet();
                     if (source_move > 0.0) {
                        double var33 = source.maxZ - var9;
                        int var34 = findFloor(var13, var33 - 1.0E-7, 0, var17) + 1;
                        int var35 = var16 * var17;

                        for (int var36 = var34; var36 < var17; var36++) {
                           double var37 = var13[var36] - var33;
                           if (var37 >= source_move) {
                              return source_move;
                           }

                           if (var37 >= -1.0E-7) {
                              var37 = Math.min(var37, source_move);
                           }

                           for (int var38 = var18; var38 < var19; var38++) {
                              for (int var39 = var20; var39 < var21; var39++) {
                                 int var40 = var36 + var39 * var17 + var38 * var35;
                                 if ((var22[var40 >>> 6] & 1L << var40) != 0L) {
                                    return var37;
                                 }
                              }
                           }
                        }

                        return source_move;
                     } else {
                        double var23 = source.minZ - var9;
                        int var25 = findFloor(var13, var23 + 1.0E-7, 0, var17);
                        int var26 = var16 * var17;

                        for (int var27 = var25 - 1; var27 >= 0; var27--) {
                           double var28 = var13[var27 + 1] - var23;
                           if (var28 <= source_move) {
                              return source_move;
                           }

                           if (var28 <= 1.0E-7) {
                              var28 = Math.max(var28, source_move);
                           }

                           for (int var30 = var18; var30 < var19; var30++) {
                              for (int var31 = var20; var31 < var21; var31++) {
                                 int var32 = var27 + var31 * var17 + var30 * var26;
                                 if ((var22[var32 >>> 6] & 1L << var32) != 0L) {
                                    return var28;
                                 }
                              }
                           }
                        }

                        return source_move;
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean getEntityHardCollisions(World world, Entity entity, Box aabb, List<Box> into, int collisionFlags, Predicate<Entity> predicate) {
      boolean var6 = (collisionFlags & 8) != 0;
      boolean var7 = false;
      aabb = aabb.expand(-1.0E-7, -1.0E-7, -1.0E-7);
      List var8;
      if (entity != null && isHardColliding(entity)) {
         var8 = world.getOtherEntities(entity, aabb, e -> predicate == null || predicate.test(e));
      } else {
         var8 = world.getOtherEntities(entity, aabb, e -> isHardColliding(e) && (predicate == null || predicate.test(e)));
      }

      int var9 = 0;

      for (int var10 = var8.size(); var9 < var10; var9++) {
         Entity var11 = (Entity)var8.get(var9);
         if (!var11.isSpectator() && (entity == null && var11.isCollidable() || entity != null && entity.collidesWith(var11))) {
            if (var6) {
               return true;
            }

            into.add(var11.getBoundingBox());
            var7 = true;
         }
      }

      return var7;
   }

   public static double N(Box currentBoundingBox, double value, List<VoxelShape> potentialCollisions) {
      int var4 = 0;

      for (int var5 = potentialCollisions.size(); var4 < var5; var4++) {
         if (Math.abs(value) < 1.0E-7) {
            return 0.0;
         }

         VoxelShape var6 = (VoxelShape)potentialCollisions.get(var4);
         value = o(var6, currentBoundingBox, value);
      }

      return value;
   }

   public static double k(Box target, Box source, double source_move) {
      if (!(source.minX - target.maxX < -1.0E-7)
         || !(source.maxX - target.minX > 1.0E-7)
         || !(source.minY - target.maxY < -1.0E-7)
         || !(source.maxY - target.minY > 1.0E-7)) {
         return source_move;
      } else if (source_move >= 0.0) {
         double var6 = target.minZ - source.maxZ;
         return var6 < -1.0E-7 ? source_move : Math.min(var6, source_move);
      } else {
         double var4 = target.maxZ - source.minZ;
         return var4 > 1.0E-7 ? source_move : Math.max(var4, source_move);
      }
   }

   public static boolean af(Entity entity) {
      return isEntitySupported(entity, 0.5);
   }

   public static boolean r(VoxelShape voxel, double x, double y, double z) {
      Box var7 = MoonriseVoxelShapeAccess.of(voxel).moonrise$getSingleAABBRepresentation();
      if (var7 != null) {
         return var7.contains(x, y, z);
      } else if (voxel.isEmpty()) {
         return false;
      } else {
         x -= MoonriseVoxelShapeAccess.of(voxel).moonrise$offsetX();
         y -= MoonriseVoxelShapeAccess.of(voxel).moonrise$offsetY();
         z -= MoonriseVoxelShapeAccess.of(voxel).moonrise$offsetZ();
         double[] var8 = MoonriseVoxelShapeAccess.of(voxel).moonrise$rootCoordinatesX();
         double[] var9 = MoonriseVoxelShapeAccess.of(voxel).moonrise$rootCoordinatesY();
         double[] var10 = MoonriseVoxelShapeAccess.of(voxel).moonrise$rootCoordinatesZ();
         CachedShapeData var11 = MoonriseVoxelShapeAccess.of(voxel).moonrise$getCachedVoxelData();
         int var12 = var11.minFullX();
         int var13 = var11.minFullY();
         int var14 = var11.sizeX();
         int var15 = findFloor(var8, x, 0, var12);
         if (var15 >= 0 && var15 < var12) {
            int var16 = findFloor(var9, y, 0, var13);
            if (var16 >= 0 && var16 < var13) {
               int var17 = findFloor(var10, z, 0, var14);
               if (var17 >= 0 && var17 < var14) {
                  int var18 = var17 + var16 * var14 + var15 * var14 * var13;
                  long[] var19 = var11.voxelSet();
                  return (var19[var18 >>> 6] & 1L << var18) != 0L;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static List<BlockPos> getIntersectingBlockPositions(World world, Box box, boolean loadChunks) {
      ArrayList var3 = new ArrayList();
      double var4 = 1.0E-7;
      int var6 = MathHelper.floor(box.minX - 1.0E-7) - 1;
      int var7 = MathHelper.floor(box.maxX + 1.0E-7) + 1;
      int var8 = MathHelper.floor(box.minY - 1.0E-7) - 1;
      int var9 = MathHelper.floor(box.maxY + 1.0E-7) + 1;
      int var10 = MathHelper.floor(box.minZ - 1.0E-7) - 1;
      int var11 = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
      int var12 = var6 >> 4;
      int var13 = var7 >> 4;
      int var14 = var8 >> 4;
      int var15 = var9 >> 4;
      int var16 = var10 >> 4;
      int var17 = var11 >> 4;
      ChunkManager var18 = world.getChunkManager();
      Mutable var19 = new Mutable();

      for (int var20 = var16; var20 <= var17; var20++) {
         for (int var21 = var12; var21 <= var13; var21++) {
            Chunk var22 = var18.getChunk(var21, var20, ChunkStatus.FULL, loadChunks);
            if (var22 != null) {
               ChunkSection[] var23 = var22.getSectionArray();
               int var24 = world.getBottomSectionCoord();

               for (int var25 = var14; var25 <= var15; var25++) {
                  int var26 = var25 - var24;
                  if (var26 >= 0 && var26 < var23.length) {
                     ChunkSection var27 = var23[var26];
                     if (var27 != null && !var27.isEmpty()) {
                        PalettedContainer var28 = var27.getBlockStateContainer();
                        int var29 = var21 == var12 ? var6 & 15 : 0;
                        int var30 = var21 == var13 ? var7 & 15 : 15;
                        int var31 = var25 == var14 ? var8 & 15 : 0;
                        int var32 = var25 == var15 ? var9 & 15 : 15;
                        int var33 = var20 == var16 ? var10 & 15 : 0;
                        int var34 = var20 == var17 ? var11 & 15 : 15;

                        for (int var35 = var31; var35 <= var32; var35++) {
                           int var36 = (var25 << 4) + var35;

                           for (int var37 = var33; var37 <= var34; var37++) {
                              int var38 = (var20 << 4) + var37;

                              for (int var39 = var29; var39 <= var30; var39++) {
                                 int var40 = (var21 << 4) + var39;
                                 int var41 = var39 + (var37 << 4) + (var35 << 8);
                                 BlockState var42 = (BlockState)var28.get(var41);
                                 if (!var42.isAir()) {
                                    VoxelShape var43 = MoonriseBlockStateBaseAccess.of(var42).moonrise$getConstantCollisionShape();
                                    if (var43 == null) {
                                       var19.set(var40, var36, var38);
                                       var43 = var42.getCollisionShape(world, var19, ShapeContext.absent());
                                    }

                                    if (!var43.isEmpty()) {
                                       var43 = var43.offset(var40, var36, var38);
                                       if (voxelShapeIntersectNoEmpty(var43, box)) {
                                          var3.add(new BlockPos(var40, var36, var38));
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var3;
   }

   public static Vec3d Q(Vec3d moveVector, Box axisalignedbb, List<Box> potentialCollisions) {
      double var3 = moveVector.x;
      double var5 = moveVector.y;
      double var7 = moveVector.z;
      if (var5 != 0.0) {
         var5 = K(axisalignedbb, var5, potentialCollisions);
         RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(0.0, var5, 0.0));
         if (var5 != 0.0) {
            axisalignedbb = u(axisalignedbb, var5);
         }
      }

      boolean var9 = Math.abs(var3) < Math.abs(var7);
      if (var9 && var7 != 0.0) {
         var7 = L(axisalignedbb, var7, potentialCollisions);
         RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(0.0, 0.0, var7));
         if (var7 != 0.0) {
            axisalignedbb = v(axisalignedbb, var7);
         }
      }

      if (var3 != 0.0) {
         var3 = J(axisalignedbb, var3, potentialCollisions);
         RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(var3, 0.0, 0.0));
         if (!var9 && var3 != 0.0) {
            axisalignedbb = t(axisalignedbb, var3);
         }
      }

      if (!var9 && var7 != 0.0) {
         var7 = L(axisalignedbb, var7, potentialCollisions);
         RenderTasks.debugBoxMov(axisalignedbb, new Vec3d(0.0, 0.0, var7));
      }

      return new Vec3d(var3, var5, var7);
   }

   private static int makeBitset(boolean ft, boolean tf, boolean tt) {
      return (ft ? 1 : 0) << 1 | (tf ? 1 : 0) << 2 | (tt ? 1 : 0) << 3;
   }

   public static Box getBoxForChunk(int chunkX, int chunkZ) {
      double var2 = chunkX << 4;
      double var4 = chunkZ << 4;
      return new Box(var2 - 3.0E-7, Double.NEGATIVE_INFINITY, var4 - 3.0E-7, var2 + 16.0000003, Double.POSITIVE_INFINITY, var4 + 16.0000003);
   }

   public static boolean c(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return maxX - minX < 1.0E-7 || maxY - minY < 1.0E-7 || maxZ - minZ < 1.0E-7;
   }

   public static double aa(MoonriseVoxelShapeAccess access, Axis axis) {
      CachedShapeData var2 = access.moonrise$getCachedVoxelData();
      switch (axis) {
         case X: {
            int var5 = var2.maxFullX();
            return var5 <= 0 ? Double.NEGATIVE_INFINITY : access.moonrise$rootCoordinatesX()[var5] + access.moonrise$offsetX();
         }
         case Y: {
            int var4 = var2.maxFullY();
            return var4 <= 0 ? Double.NEGATIVE_INFINITY : access.moonrise$rootCoordinatesY()[var4] + access.moonrise$offsetY();
         }
         case Z: {
            int var3 = var2.maxFullZ();
            return var3 <= 0 ? Double.NEGATIVE_INFINITY : access.moonrise$rootCoordinatesZ()[var3] + access.moonrise$offsetZ();
         }
         default:
            return Double.NEGATIVE_INFINITY;
      }
   }

   private static int findFloor(double[] values, double value, int startIndex, int endIndex) {
      do {
         int var5 = startIndex + endIndex >>> 1;
         double var6 = values[var5];
         if (value < var6) {
            endIndex = var5 - 1;
         } else {
            startIndex = var5 + 1;
         }
      } while (startIndex <= endIndex);

      return startIndex - 1;
   }

   private CollisionUtil() {
      throw new RuntimeException();
   }

   public static double M(Box currentBoundingBox, double value, List<VoxelShape> potentialCollisions) {
      int var4 = 0;

      for (int var5 = potentialCollisions.size(); var4 < var5; var4++) {
         if (Math.abs(value) < 1.0E-7) {
            return 0.0;
         }

         VoxelShape var6 = (VoxelShape)potentialCollisions.get(var4);
         value = n(var6, currentBoundingBox, value);
      }

      return value;
   }

   public static double n(VoxelShape target, Box source, double source_move) {
      Box var4 = MoonriseVoxelShapeAccess.of(target).moonrise$getSingleAABBRepresentation();
      if (var4 != null) {
         return i(var4, source, source_move);
      } else {
         double var5 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetX();
         double var7 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetY();
         double var9 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetZ();
         double[] var11 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesX();
         double[] var12 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesY();
         double[] var13 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesZ();
         CachedShapeData var14 = MoonriseVoxelShapeAccess.of(target).moonrise$getCachedVoxelData();
         int var15 = var14.minFullX();
         int var16 = var14.minFullY();
         int var17 = var14.sizeX();
         int var18 = Math.max(0, findFloor(var12, source.minY - var7 + 1.0E-7, 0, var16));
         if (var18 >= var16) {
            return source_move;
         } else {
            int var19 = Math.min(var16, findFloor(var12, source.maxY - var7 - 1.0E-7, var18, var16) + 1);
            if (var18 >= var19) {
               return source_move;
            } else {
               int var20 = Math.max(0, findFloor(var13, source.minZ - var9 + 1.0E-7, 0, var17));
               if (var20 >= var17) {
                  return source_move;
               } else {
                  int var21 = Math.min(var17, findFloor(var13, source.maxZ - var9 - 1.0E-7, var20, var17) + 1);
                  if (var20 >= var21) {
                     return source_move;
                  } else {
                     long[] var22 = var14.voxelSet();
                     if (source_move > 0.0) {
                        double var33 = source.maxX - var5;
                        int var34 = findFloor(var11, var33 - 1.0E-7, 0, var15) + 1;
                        int var35 = var16 * var17;

                        for (int var36 = var34; var36 < var15; var36++) {
                           double var37 = var11[var36] - var33;
                           if (var37 >= source_move) {
                              return source_move;
                           }

                           if (var37 >= -1.0E-7) {
                              var37 = Math.min(var37, source_move);
                           }

                           for (int var38 = var18; var38 < var19; var38++) {
                              for (int var39 = var20; var39 < var21; var39++) {
                                 int var40 = var39 + var38 * var17 + var36 * var35;
                                 if ((var22[var40 >>> 6] & 1L << var40) != 0L) {
                                    return var37;
                                 }
                              }
                           }
                        }

                        return source_move;
                     } else {
                        double var23 = source.minX - var5;
                        int var25 = findFloor(var11, var23 + 1.0E-7, 0, var15);
                        int var26 = var16 * var17;

                        for (int var27 = var25 - 1; var27 >= 0; var27--) {
                           double var28 = var11[var27 + 1] - var23;
                           if (var28 <= source_move) {
                              return source_move;
                           }

                           if (var28 <= 1.0E-7) {
                              var28 = Math.max(var28, source_move);
                           }

                           for (int var30 = var18; var30 < var19; var30++) {
                              for (int var31 = var20; var31 < var21; var31++) {
                                 int var32 = var31 + var30 * var17 + var27 * var26;
                                 if ((var22[var32 >>> 6] & 1L << var32) != 0L) {
                                    return var28;
                                 }
                              }
                           }
                        }

                        return source_move;
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean hasAnyIntersects(World world, Predicate<Entity> exceptPredicate, VoxelShape shape) {
      if (shape.isEmpty()) {
         return false;
      } else {
         for (Entity var4 : world.getOtherEntities(null, shape.getBoundingBox())) {
            if (!var4.isRemoved()
               && var4.intersectionChecked
               && !exceptPredicate.test(var4)
               && VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(var4.getBoundingBox()), BooleanBiFunction.AND)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isHardColliding(Entity entity) {
      return entity instanceof BoatEntity || entity instanceof AbstractMinecartEntity || entity instanceof ShulkerEntity || entity.isCollidable();
   }

   public static Box cutLeft(Box box, double dx) {
      return new Box(box.minX + dx, box.minY, box.minZ, box.minX, box.maxY, box.maxZ);
   }

   public static boolean isSpecialCollidingBlock(AbstractBlockState block) {
      return block.exceedsCube() || block.getBlock() == Blocks.MOVING_PISTON;
   }

   public static double i(Box target, Box source, double source_move) {
      if (!(source.minY - target.maxY < -1.0E-7)
         || !(source.maxY - target.minY > 1.0E-7)
         || !(source.minZ - target.maxZ < -1.0E-7)
         || !(source.maxZ - target.minZ > 1.0E-7)) {
         return source_move;
      } else if (source_move >= 0.0) {
         double var6 = target.minX - source.maxX;
         return var6 < -1.0E-7 ? source_move : Math.min(var6, source_move);
      } else {
         double var4 = target.maxX - source.minX;
         return var4 > 1.0E-7 ? source_move : Math.max(var4, source_move);
      }
   }

   public static Box v(Box box, double dz) {
      return new Box(box.minX, box.minY, box.minZ + dz, box.maxX, box.maxY, box.maxZ + dz);
   }

   public static Box cutForwards(Box box, double dz) {
      return new Box(box.minX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ + dz);
   }

   public static Box A(Box box, double dz) {
      return new Box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ + dz);
   }

   public static double K(Box currentBoundingBox, double value, List<Box> potentialCollisions) {
      int var4 = 0;

      for (int var5 = potentialCollisions.size(); var4 < var5; var4++) {
         if (Math.abs(value) < 1.0E-7) {
            return 0.0;
         }

         Box var6 = (Box)potentialCollisions.get(var4);
         value = collideX(var6, currentBoundingBox, value);
      }

      return value;
   }

   public static boolean getCollisions(
      World world,
      Entity entity,
      Box aabb,
      List<VoxelShape> intoVoxel,
      List<Box> intoAABB,
      int collisionFlags,
      BiPredicate<BlockState, BlockPos> blockPredicate,
      Predicate<Entity> entityPredicate,
      BiFunction<BlockState, BlockPos, Box> environmentFilter
   ) {
      return (collisionFlags & 8) == 0
         ? U(world, entity, aabb, intoVoxel, intoAABB, collisionFlags, blockPredicate, environmentFilter)
            | getEntityHardCollisions(world, entity, aabb, intoAABB, collisionFlags, entityPredicate)
         : U(world, entity, aabb, intoVoxel, intoAABB, collisionFlags, blockPredicate, environmentFilter)
            || getEntityHardCollisions(world, entity, aabb, intoAABB, collisionFlags, entityPredicate);
   }

   private static double T(double x, double y) {
      return x > y ? x : y;
   }

   public static boolean U(
      World world,
      Entity entity,
      Box aabb,
      List<VoxelShape> intoVoxel,
      List<Box> intoAABB,
      int collisionFlags,
      BiPredicate<BlockState, BlockPos> predicate,
      BiFunction<BlockState, BlockPos, Box> environmentFilter
   ) {
      boolean var8 = (collisionFlags & 8) != 0;
      boolean var9 = false;
      int var10 = world.getBottomSectionCoord();
      int var11 = MathHelper.floor(aabb.minX - 1.0E-7) - 1;
      int var12 = MathHelper.floor(aabb.maxX + 1.0E-7) + 1;
      int var13 = Math.max((var10 << 4) - 1, MathHelper.floor(aabb.minY - 1.0E-7) - 1);
      int var14 = Math.min((world.getTopSectionCoord() - 1 << 4) + 16, MathHelper.floor(aabb.maxY + 1.0E-7) + 1);
      int var15 = MathHelper.floor(aabb.minZ - 1.0E-7) - 1;
      int var16 = MathHelper.floor(aabb.maxZ + 1.0E-7) + 1;
      Mutable var17 = new Mutable();
      KalamaHelperHelperAr var18 = new KalamaHelperHelperAr(entity);
      if (var13 > var14) {
         return var9;
      } else {
         int var19 = var11 >> 4;
         int var20 = var12 >> 4;
         int var21 = var13 >> 4;
         int var22 = var14 >> 4;
         int var23 = var15 >> 4;
         int var24 = var16 >> 4;
         boolean var25 = (collisionFlags & 1) != 0;
         ChunkManager var26 = world.getChunkManager();

         for (int var27 = var23; var27 <= var24; var27++) {
            for (int var28 = var19; var28 <= var20; var28++) {
               Chunk var29 = var26.getChunk(var28, var27, ChunkStatus.FULL, var25);
               if (var29 == null) {
                  if ((collisionFlags & 2) != 0) {
                     if (var8) {
                        return true;
                     }

                     intoAABB.add(getBoxForChunk(var28, var27));
                     var9 = true;
                  }
               } else {
                  ChunkSection[] var30 = var29.getSectionArray();

                  for (int var31 = var21; var31 <= var22; var31++) {
                     int var32 = var31 - var10;
                     if (var32 >= 0 && var32 < var30.length) {
                        ChunkSection var33 = var30[var32];
                        if (var33 != null && !var33.isEmpty()) {
                           boolean var34 = MoonriseChunkBlockCountingAccess.of(var33).getSpecialCollidingBlockCount() != 0;
                           int var35 = !var34 ? 1 : 0;
                           PalettedContainer var36 = var33.getBlockStateContainer();
                           int var37 = var28 == var19 ? (var11 & 15) + var35 : 0;
                           int var38 = var28 == var20 ? (var12 & 15) - var35 : 15;
                           int var39 = var27 == var23 ? (var15 & 15) + var35 : 0;
                           int var40 = var27 == var24 ? (var16 & 15) - var35 : 15;
                           int var41 = var31 == var21 ? (var13 & 15) + var35 : 0;
                           int var42 = var31 == var22 ? (var14 & 15) - var35 : 15;

                           for (int var43 = var41; var43 <= var42; var43++) {
                              int var44 = var43 | var31 << 4;

                              for (int var45 = var39; var45 <= var40; var45++) {
                                 int var46 = var45 | var27 << 4;

                                 for (int var47 = var37; var47 <= var38; var47++) {
                                    int var48 = var47 | var45 << 4 | var43 << 8;
                                    int var49 = var47 | var28 << 4;
                                    int var50 = var34
                                       ? (var49 != var11 && var49 != var12 ? 0 : 1)
                                          + (var44 != var13 && var44 != var14 ? 0 : 1)
                                          + (var46 != var15 && var46 != var16 ? 0 : 1)
                                       : 0;
                                    if (var50 != 3) {
                                       BlockState var51 = (BlockState)var36.get(var48);
                                       var17.set(var49, var44, var46);
                                       if (environmentFilter != null) {
                                          Box var52 = (Box)environmentFilter.apply(var51, var17);
                                          if (var52 != null) {
                                             intoAABB.add(var52);
                                          }
                                       }

                                       if (!var51.getBlock()
                                          .getCollisionShape(var51, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent())
                                          .isEmpty()) {
                                          VoxelShape var55 = MoonriseBlockStateBaseAccess.of(var51).moonrise$getConstantCollisionShape();
                                          if (var50 == 0 || (var50 != 1 || var51.exceedsCube()) && (var50 != 2 || var51.getBlock() == Blocks.MOVING_PISTON)) {
                                             if (var55 == null) {
                                                var17.set(var49, var44, var46);
                                                var55 = var51.getCollisionShape(world, var17, var18);
                                             }

                                             Box var53 = MoonriseVoxelShapeAccess.of(var55).moonrise$getSingleAABBRepresentation();
                                             if (var53 != null) {
                                                var53 = var53.offset(var49, var44, var46);
                                                if (h(aabb, var53)) {
                                                   if (predicate != null) {
                                                      var17.set(var49, var44, var46);
                                                      if (!predicate.test(var51, var17)) {
                                                         continue;
                                                      }
                                                   }

                                                   if (var8) {
                                                      return true;
                                                   }

                                                   var9 = true;
                                                   intoAABB.add(var53);
                                                }
                                             } else if (!var55.isEmpty()) {
                                                VoxelShape var54 = var55.offset(var49, var44, var46);
                                                if (voxelShapeIntersectNoEmpty(var54, aabb)) {
                                                   if (predicate != null) {
                                                      var17.set(var49, var44, var46);
                                                      if (!predicate.test(var51, var17)) {
                                                         continue;
                                                      }
                                                   }

                                                   if (var8) {
                                                      return true;
                                                   }

                                                   var9 = true;
                                                   intoVoxel.add(var54);
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return var9;
      }
   }

   public static double L(Box currentBoundingBox, double value, List<Box> potentialCollisions) {
      int var4 = 0;

      for (int var5 = potentialCollisions.size(); var4 < var5; var4++) {
         if (Math.abs(value) < 1.0E-7) {
            return 0.0;
         }

         Box var6 = (Box)potentialCollisions.get(var4);
         value = k(var6, currentBoundingBox, value);
      }

      return value;
   }

   public static double o(VoxelShape target, Box source, double source_move) {
      Box var4 = MoonriseVoxelShapeAccess.of(target).moonrise$getSingleAABBRepresentation();
      if (var4 != null) {
         return collideX(var4, source, source_move);
      } else {
         double var5 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetX();
         double var7 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetY();
         double var9 = MoonriseVoxelShapeAccess.of(target).moonrise$offsetZ();
         double[] var11 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesX();
         double[] var12 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesY();
         double[] var13 = MoonriseVoxelShapeAccess.of(target).moonrise$rootCoordinatesZ();
         CachedShapeData var14 = MoonriseVoxelShapeAccess.of(target).moonrise$getCachedVoxelData();
         int var15 = var14.minFullX();
         int var16 = var14.minFullY();
         int var17 = var14.sizeX();
         int var18 = Math.max(0, findFloor(var11, source.minX - var5 + 1.0E-7, 0, var15));
         if (var18 >= var15) {
            return source_move;
         } else {
            int var19 = Math.min(var15, findFloor(var11, source.maxX - var5 - 1.0E-7, var18, var15) + 1);
            if (var18 >= var19) {
               return source_move;
            } else {
               int var20 = Math.max(0, findFloor(var13, source.minZ - var9 + 1.0E-7, 0, var17));
               if (var20 >= var17) {
                  return source_move;
               } else {
                  int var21 = Math.min(var17, findFloor(var13, source.maxZ - var9 - 1.0E-7, var20, var17) + 1);
                  if (var20 >= var21) {
                     return source_move;
                  } else {
                     long[] var22 = var14.voxelSet();
                     if (source_move > 0.0) {
                        double var33 = source.maxY - var7;
                        int var34 = findFloor(var12, var33 - 1.0E-7, 0, var16) + 1;
                        int var35 = var16 * var17;

                        for (int var36 = var34; var36 < var16; var36++) {
                           double var37 = var12[var36] - var33;
                           if (var37 >= source_move) {
                              return source_move;
                           }

                           if (var37 >= -1.0E-7) {
                              var37 = Math.min(var37, source_move);
                           }

                           for (int var38 = var18; var38 < var19; var38++) {
                              for (int var39 = var20; var39 < var21; var39++) {
                                 int var40 = var39 + var36 * var17 + var38 * var35;
                                 if ((var22[var40 >>> 6] & 1L << var40) != 0L) {
                                    return var37;
                                 }
                              }
                           }
                        }

                        return source_move;
                     } else {
                        double var23 = source.minY - var7;
                        int var25 = findFloor(var12, var23 + 1.0E-7, 0, var16);
                        int var26 = var16 * var17;

                        for (int var27 = var25 - 1; var27 >= 0; var27--) {
                           double var28 = var12[var27 + 1] - var23;
                           if (var28 <= source_move) {
                              return source_move;
                           }

                           if (var28 <= 1.0E-7) {
                              var28 = Math.max(var28, source_move);
                           }

                           for (int var30 = var18; var30 < var19; var30++) {
                              for (int var31 = var20; var31 < var21; var31++) {
                                 int var32 = var31 + var27 * var17 + var30 * var26;
                                 if ((var22[var32 >>> 6] & 1L << var32) != 0L) {
                                    return var28;
                                 }
                              }
                           }
                        }

                        return source_move;
                     }
                  }
               }
            }
         }
      }
   }

   public static Box cutDownwards(Box box, double dy) {
      return new Box(box.minX, box.minY + dy, box.minZ, box.maxX, box.minY, box.maxZ);
   }

   public static Box u(Box box, double dy) {
      return new Box(box.minX, box.minY + dy, box.minZ, box.maxX, box.maxY + dy, box.maxZ);
   }

   public static double calculateAxisCollide(VoxelShape voxelShape, Axis axis, Box source, double source_move) {
      switch (axis) {
         case X:
            return n(voxelShape, source, source_move);
         case Y:
            return o(voxelShape, source, source_move);
         case Z:
            return p(voxelShape, source, source_move);
         default:
            throw new RuntimeException("Unknown axis: " + axis);
      }
   }

   public static Box y(Box box, double dy) {
      return new Box(box.minX, box.minY, box.minZ, box.maxX, box.maxY + dy, box.maxZ);
   }
}
