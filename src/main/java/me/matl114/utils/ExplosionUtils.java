package me.matl114.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.matl114.utils.j.KalamaHelperHelperA;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;

@KalamaHelperHelperA
public final class ExplosionUtils {
   public static final KalamaHelperHelperQ g = (pos, state) -> true;
   public static final float f = 1.0F;
   public static final MinecraftClient b = MinecraftClient.getInstance();
   public static final float c = 6.0F;
   private static final double EPSILON = 1.0E-7;
   public static final float e = 5.0F;
   public static final KalamaHelperHelperQ h = (pos, state) -> state.getBlock().getBlastResistance() > 600.0F;
   public static final float d = 5.0F;

   @KalamaHelperHelperA
   public static KalamaHelperHelperHX emptyAccess() {
      return new KalamaHelperHelperJX();
   }

   public static float o(ClientWorld world, float amount) {
      if (world.getDifficulty() == Difficulty.PEACEFUL) {
         amount = 0.0F;
      }

      if (world.getDifficulty() == Difficulty.EASY) {
         amount = Math.min(amount / 2.0F + 1.0F, amount);
      }

      if (world.getDifficulty() == Difficulty.HARD) {
         amount = amount * 3.0F / 2.0F;
      }

      return amount;
   }

   private static float getExposure(KalamaHelperHelperHX access, Vec3d source, Box box, KalamaHelperHelperQ hitRule) {
      double var4 = box.maxX - box.minX;
      double var6 = box.maxY - box.minY;
      double var8 = box.maxZ - box.minZ;
      double var10 = 1.0 / (var4 * 2.0 + 1.0);
      double var12 = 1.0 / (var6 * 2.0 + 1.0);
      double var14 = 1.0 / (var8 * 2.0 + 1.0);
      if (var10 > 0.0 && var12 > 0.0 && var14 > 0.0) {
         int var16 = 0;
         int var17 = 0;
         double var18 = (1.0 - Math.floor(1.0 / var10) * var10) * 0.5;
         double var20 = (1.0 - Math.floor(1.0 / var14) * var14) * 0.5;
         var10 *= var4;
         var12 *= var6;
         var14 *= var8;
         double var22 = box.minX + var18;
         double var24 = box.minY;
         double var26 = box.minZ + var20;
         double var28 = box.maxX + var18;
         double var30 = box.maxY;
         double var32 = box.maxZ + var20;

         for (double var34 = var22; var34 <= var28; var34 += var10) {
            for (double var36 = var24; var36 <= var30; var36 += var12) {
               for (double var38 = var26; var38 <= var32; var38 += var14) {
                  Vec3d var40 = new Vec3d(var34, var36, var38);
                  if (!rayCastAccept(access, source, var40, hitRule)) {
                     var16++;
                  }

                  var17++;
               }
            }
         }

         return (float)var16 / var17;
      } else {
         return 0.0F;
      }
   }

   @KalamaHelperHelperA
   public static double getNormalizedDistance(float power, Vec3d explosionPos, Vec3d targetPos) {
      return power <= 0.0F ? Double.POSITIVE_INFINITY : explosionPos.distanceTo(targetPos) / (power * 2.0);
   }

   private static boolean rayCastAccept(KalamaHelperHelperHX stateAccess, Vec3d source, Vec3d pos, KalamaHelperHelperQ hitRule) {
      BlockPos var4 = BlockPos.ofFloored(MathUtils.lerp(-1.0E-7, source, pos));
      BlockPos var5 = BlockPos.ofFloored(MathUtils.lerp(-1.0E-7, pos, source));

      for (BlockPos var7 : RaycastUtils.s(pos, source, true)) {
         boolean var8 = Objects.equals(var4, var7) || Objects.equals(var5, var7);
         BlockState var9 = stateAccess.a(var7);
         if (hitRule.mayHit(pos, source, var7, var9, var8)) {
            return true;
         }
      }

      return false;
   }

   @KalamaHelperHelperA
   private static List<Vec3d> collectExposureSamplePoints(Box box) {
      double var1 = box.maxX - box.minX;
      double var3 = box.maxY - box.minY;
      double var5 = box.maxZ - box.minZ;
      double var7 = 1.0 / (var1 * 2.0 + 1.0);
      double var9 = 1.0 / (var3 * 2.0 + 1.0);
      double var11 = 1.0 / (var5 * 2.0 + 1.0);
      if (!(var7 <= 0.0) && !(var9 <= 0.0) && !(var11 <= 0.0)) {
         double var13 = (1.0 - Math.floor(1.0 / var7) * var7) * 0.5;
         double var15 = (1.0 - Math.floor(1.0 / var11) * var11) * 0.5;
         var7 *= var1;
         var9 *= var3;
         var11 *= var5;
         double var17 = box.minX + var13;
         double var19 = box.minY;
         double var21 = box.minZ + var15;
         double var23 = box.maxX + var13;
         double var25 = box.maxY;
         double var27 = box.maxZ + var15;
         ArrayList var29 = new ArrayList();

         for (double var30 = var17; var30 <= var23 + 1.0E-7; var30 += var7) {
            for (double var32 = var19; var32 <= var25 + 1.0E-7; var32 += var9) {
               for (double var34 = var21; var34 <= var27 + 1.0E-7; var34 += var11) {
                  var29.add(new Vec3d(var30, var32, var34));
               }
            }
         }

         return var29;
      } else {
         return List.of();
      }
   }

   public static float calculateExplosionRawDamage(float power, Vec3d explosionPos, Box targetBox, KalamaHelperHelperHX access, KalamaHelperHelperQ hitRule) {
      Vec3d var5 = new Vec3d((targetBox.minX + targetBox.maxX) / 2.0, targetBox.minY, (targetBox.minZ + targetBox.maxZ) / 2.0);
      double var6 = getNormalizedDistance(power, explosionPos, var5);
      if (var6 >= 1.0) {
         return 0.0F;
      } else {
         double var8 = getRawDamage(power, 1.0);
         double var10;
         if (var8 > 2.0) {
            var10 = getExposure(access, explosionPos, targetBox, hitRule);
         } else {
            var10 = getExposureSimplified(access, explosionPos, targetBox, hitRule);
         }

         double var12 = getImpact(var6, var10);
         return (float)getRawDamage(power, var12);
      }
   }

   @KalamaHelperHelperA
   public static KalamaHelperHelperHX q(BlockView world, Map<BlockPos, BlockState> overrides) {
      Map var2 = overrides == null ? Map.of() : overrides;
      return new KalamaHelperHelperSX(var2, world);
   }

   @KalamaHelperHelperA
   public static double getImpact(double normalizedDistance, double exposure) {
      return !(normalizedDistance >= 1.0) && !(exposure <= 0.0) ? (1.0 - normalizedDistance) * MathHelper.clamp(exposure, 0.0, 1.0) : 0.0;
   }

   @KalamaHelperHelperA
   private static Set<BlockPos> collectPotentiallyDestroyedBlocks(float power, Vec3d explosionPos, KalamaHelperHelperHX access) {
      if (power <= 0.0F) {
         return Set.of();
      } else {
         HashSet var3 = new HashSet();

         for (int var4 = 0; var4 < 16; var4++) {
            for (int var5 = 0; var5 < 16; var5++) {
               for (int var6 = 0; var6 < 16; var6++) {
                  if (var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15 || var6 == 0 || var6 == 15) {
                     double var7 = var4 / 15.0 * 2.0 - 1.0;
                     double var9 = var5 / 15.0 * 2.0 - 1.0;
                     double var11 = var6 / 15.0 * 2.0 - 1.0;
                     double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
                     var7 /= var13;
                     var9 /= var13;
                     var11 /= var13;
                     double var15 = power * 1.3;
                     double var17 = explosionPos.x;
                     double var19 = explosionPos.y;

                     for (double var21 = explosionPos.z; var15 > 0.0; var15 -= 0.22500001) {
                        BlockPos var23 = BlockPos.ofFloored(var17, var19, var21);
                        BlockState var24 = access.a(var23);
                        if (access.hasBlastResistance(var23, var24)) {
                           var15 -= (access.e(var23, var24) + 0.3) * 0.3;
                        }

                        if (var15 > 0.0 && var24 != null && !var24.isAir()) {
                           var3.add(var23.toImmutable());
                        }

                        var17 += var7 * 0.3;
                        var19 += var9 * 0.3;
                        var21 += var11 * 0.3;
                     }
                  }
               }
            }
         }

         return var3;
      }
   }

   public static double getRawDamage(float power, double impact) {
      double var3 = power * 2.0F;
      return impact <= 0.0 ? 0.0 : (impact * impact + impact) / 2.0 * 7.0 * var3 + 1.0;
   }

   @KalamaHelperHelperA
   public static KalamaHelperHelperHX r(Map<BlockPos, BlockState> overrides, KalamaHelperHelperHX fallback) {
      Map var2 = overrides == null ? Map.of() : overrides;
      KalamaHelperHelperHX var3 = fallback == null ? emptyAccess() : fallback;
      return new KalamaHelperHelperIX(var2, var3);
   }

   public static float b(Box predictedPos, Vec3d explosionPos, float power, BlockView world, KalamaHelperHelperQ hitRule) {
      return calculateExplosionRawDamage(power, explosionPos, predictedPos, p(world), hitRule);
   }

   private static float getExposureSimplified(KalamaHelperHelperHX access, Vec3d source, Box box, KalamaHelperHelperQ hitRule) {
      Vec3d var4 = box.getCenter();
      List var5 = List.of(
         new Vec3d(box.minX, var4.y, var4.z),
         new Vec3d(box.maxX, var4.y, var4.z),
         new Vec3d(var4.x, box.minY, var4.z),
         new Vec3d(var4.x, box.maxY, var4.z),
         new Vec3d(var4.x, var4.y, box.minZ),
         new Vec3d(var4.x, var4.y, box.maxZ)
      );
      int var6 = 0;

      for (Vec3d var8 : var5) {
         if (!rayCastAccept(access, source, var8, hitRule)) {
            var6++;
         }
      }

      return (float)var6 / var5.size();
   }

   @KalamaHelperHelperA
   public static KalamaHelperHelperHX p(BlockView world) {
      return new KalamaHelperHelperZ(world);
   }

   public static float respawnAnchorDamage(Box predictedPos, Vec3d explosionPos, BlockView world, KalamaHelperHelperQ hitRule) {
      return calculateExplosionRawDamage(
         5.0F, explosionPos, predictedPos, q(world, Map.of(BlockPos.ofFloored(explosionPos), Blocks.AIR.getDefaultState())), hitRule
      );
   }

   public static float calculateExplosionMaxDamage(float power, Box targetBox, Vec3d explosionPos) {
      Vec3d var3 = new Vec3d((targetBox.minX + targetBox.maxX) / 2.0, targetBox.minY, (targetBox.minZ + targetBox.maxZ) / 2.0);
      double var4 = getNormalizedDistance(power, explosionPos, var3);
      if (var4 >= 1.0) {
         return 0.0F;
      } else {
         double var6 = getImpact(var4, 1.0);
         return (float)getRawDamage(power, var6);
      }
   }

   public static float explosionDamage(LivingEntity target, Vec3d explosionPos, float power, BlockView world, KalamaHelperHelperQ hitRule) {
      return target == null ? 0.0F : calculateExplosionRawDamage(power, explosionPos, target.getBoundingBox(), p(world), hitRule);
   }

   public static float c(Box predicatedPos, Vec3d explosionPos, BlockView world, KalamaHelperHelperQ hitRule) {
      return calculateExplosionRawDamage(6.0F, explosionPos, predicatedPos, p(world), hitRule);
   }
}
