package me.matl114.hacks.modules.render;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

class RenderSubHelperW {
   Entity e;
   private static final Random f = Random.create();
   RenderSubHelperAc d;
   private static Vec3d g;
   Vec3d b;
   private static int lastRandTime = 0;
   Vec3d c;

   public static RenderSubHelperW of(PersistentProjectileEntity arrow, float tickDelta) {
      return new RenderSubHelperW(RenderUtils.getLerpedPos(arrow, tickDelta), arrow.getVelocity(), RenderSubHelperAc.Wo, arrow);
   }

   public static RenderSubHelperW i(CrossbowUser user, float tickDelta) {
      Entity var2 = (Entity)user;
      Vec3d var3 = var2.getRotationVec(1.0F);
      float var4 = 1.6F;
      Vec3d var5 = calculateVelocity(var3.x, var3.y, var3.z, var4);
      Vec3d var6 = new Vec3d(var2.getX(), var2.getEyeY() - 0.1F, var2.getZ()).add(RenderUtils.getLerpedDelta((Entity)user, tickDelta));
      return new RenderSubHelperW(var6, var5, RenderSubHelperAc.Wp, var2);
   }

   public Pair<List<Vec3d>, HitResult> predictLineWithHitResult(int ticks) {
      Vec3d var2 = this.b;
      Vec3d var3 = this.c;
      double var4 = EntityUtils.A(Items.BOW);
      ArrayList var6 = new ArrayList();
      if (this.c.lengthSquared() < 1.0E-5) {
         return Pair.of(List.of(), null);
      } else {
         Object var7 = null;

         for (int var8 = 0; var8 < ticks; var8++) {
            var6.add(var2);
            var2 = var2.add(var3.multiply(0.1));
            var3 = var3.multiply(0.999);
            var3 = var3.add(0.0, -var4 * 0.1, 0.0);
            if (var6.size() > 2) {
               Vec3d var9 = (Vec3d)var6.get(var6.size() - 2);
               var7 = RaycastUtils.raycastSolidBlockResult(this.e, var9, var2);
               if (var7 != null && var7.getType() != Type.MISS) {
                  break;
               }

               var7 = RaycastUtils.raycastHitEntityExceptPlayerResult(this.e, var9, var2);
               if (var7 != null && var7.getType() != Type.MISS) {
                  break;
               }

               var7 = null;
            }
         }

         return Pair.of(var6, var7);
      }
   }

   private static float getPullProgress(int useTicks) {
      float var1 = useTicks / 20.0F;
      var1 = (var1 * var1 + var1 * 2.0F) / 3.0F;
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   public List<Vec3d> predictLine(int ticks) {
      Vec3d var2 = this.b;
      Vec3d var3 = this.c;
      double var4 = EntityUtils.A(Items.BOW);
      ArrayList var6 = new ArrayList();
      if (this.c.lengthSquared() < 1.0E-5) {
         return List.of();
      } else {
         for (int var7 = 0; var7 < ticks; var7++) {
            var6.add(var2);
            var2 = var2.add(var3.multiply(0.1));
            var3 = var3.multiply(0.999);
            var3 = var3.add(0.0, -var4 * 0.1, 0.0);
            if (var6.size() > 2) {
               Vec3d var8 = (Vec3d)var6.get(var6.size() - 2);
               if (RaycastUtils.raycastAnySolidBlock(this.e, var8, var2) || RaycastUtils.raycastHitAnyEntityExceptPlayer(this.e, var8, var2)) {
                  break;
               }
            }
         }

         return var6;
      }
   }

   public static RenderSubHelperW d(AbstractSkeletonEntity entity, float tickDelta) {
      Vec3d var2 = new Vec3d(entity.getX(), entity.getEyeY() - 0.1F, entity.getZ()).add(RenderUtils.getLerpedDelta(entity, tickDelta));
      Vec3d var3 = entity.getRotationVector();
      double var4 = var3.getX();
      double var6 = var3.getZ();
      double var8 = Math.sqrt(var4 * var4 + var6 * var6);
      Vec3d var10 = calculateVelocity(var4, var3.y + var8 * 0.2, var6, 1.6F);
      return new RenderSubHelperW(var2, var10, RenderSubHelperAc.Wm, entity);
   }

   public RenderSubHelperW(Vec3d pos, Vec3d vec, RenderSubHelperAc type, Entity owner) {
      this.b = pos;
      this.c = vec;
      this.d = type;
      this.e = owner;
   }

   private static Vec3d getHandOffset(PlayerEntity player, Hand hand) {
      double var2 = Math.toRadians(player.getYaw());
      Arm var4 = (Arm)ProjectileESP.access$000().options.getMainArm().getValue();
      boolean var5 = var4 == Arm.RIGHT && hand == Hand.MAIN_HAND || var4 == Arm.LEFT && hand == Hand.OFF_HAND;
      double var6 = var5 ? -1.0 : 1.0;
      double var8 = Math.cos(var2) * 0.16 * var6;
      double var10 = Math.sin(var2) * 0.16 * var6;
      return new Vec3d(var8, 0.0, var10);
   }

   private static Vec3d getArrowRand() {
      return Vec3d.ZERO;
   }

   private static Vec3d calculateVelocity(double x, double y, double z, float power) {
      return new Vec3d(x, y, z).normalize().add(getArrowRand()).multiply(power);
   }

   public static RenderSubHelperW g(PlayerEntity player, RangedWeaponItem weaponItem, Hand hand, float tickDelta) {
      Vec3d var4 = getHandOffset(player, hand);
      Vec3d var5 = new Vec3d(player.getX(), player.getEyeY() - 0.1F, player.getZ()).add(var4).add(RenderUtils.getLerpedDelta(player, tickDelta));
      Vec3d var14;
      if (weaponItem instanceof BowItem) {
         int var6 = player.isUsingItem() && player.getActiveHand() == hand ? player.getItemUseTime() : 1000;
         float var7 = getPullProgress(var6);
         float var8 = var7 * 3.0F;
         Vec3d var9 = player.getRotationVector();
         var14 = calculateVelocity(var9.x, var9.y, var9.z, var8);
         Vec3d var11 = player.getVelocity();
         var14 = var14.add(var11.x, player.isOnGround() ? 0.0 : var11.y, var11.z);
      } else {
         Vec3d var12 = player.getRotationVec(1.0F);
         float var13 = 3.15F;
         var14 = calculateVelocity(var12.x, var12.y, var12.z, var13);
      }

      return new RenderSubHelperMX(var5, var14, RenderSubHelperAc.Wn, player, var4);
   }
}
