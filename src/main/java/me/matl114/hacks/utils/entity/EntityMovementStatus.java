package me.matl114.hacks.utils.entity;

import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class EntityMovementStatus<T extends Entity> {
   public boolean d;
   public boolean o;
   public boolean f;
   public boolean b;
   public Vec3d j;
   public float l;
   public boolean m;
   public boolean n;
   public boolean p;
   public T a;
   public boolean e;
   public boolean c;
   public float k;
   public float i;
   public float h;
   public Vec3d g;

   public Vec3d calculateLastMoveVelocity(int forward, int sideward) {
      if (this.a instanceof LivingEntity var4) {
         Vec2f var10 = new Vec2f(sideward, forward).normalize();
         var10 = EntityUtils.applyMovementFactors(this.a, var10);
         Vec3d var7 = new Vec3d(var10.x, this.a instanceof LivingEntity var6 ? var6.upwardSpeed : 0.0, var10.y);
         float var13 = this.a.isOnGround() ? this.a.getEntityWorld().getBlockState(this.a.getVelocityAffectingPos()).getBlock().getSlipperiness() : 1.0F;
         float var12 = var4.getMovementSpeed(var13);
         Vec3d var8 = EntityUtils.movementInputToVelocity(var7, var12, this.i);
         Vec3d var9 = this.j.add(var8);
         return var4.applyClimbingSpeed(var9);
      } else {
         return this.a.getVelocity();
      }
   }

   public void restorePos() {
      this.a.setPosition(this.g);
      this.a.touchingWater = this.n;
      this.a.submergedInWater = this.o;
      this.a.inPowderSnow = this.p;
   }

   public void restore() {
      this.a.horizontalCollision = this.c;
      this.a.verticalCollision = this.d;
      this.a.groundCollision = this.e;
      this.a.collidedSoftly = this.f;
      this.c();
      this.restoreOnGround();
      this.a.setVelocity(this.j);
      this.a.speed = this.k;
      this.a.distanceTraveled = this.l;
      this.a.setSprinting(this.m);
   }

   public void restoreOnGround() {
      this.a.setOnGround(this.b);
   }

   public EntityMovementStatus(T entity) {
      this.a = (T)entity;
      this.b = entity.isOnGround();
      this.c = entity.horizontalCollision;
      this.d = entity.verticalCollision;
      this.e = entity.groundCollision;
      this.g = entity.getPos();
      this.h = entity.getPitch();
      this.i = entity.getYaw();
      this.j = entity.getVelocity();
      this.k = entity.speed;
      this.l = entity.distanceTraveled;
      this.m = entity.isSprinting();
      this.n = entity.isTouchingWater();
      this.f = entity.collidedSoftly;
      this.o = entity.isSubmergedInWater();
      this.p = entity.inPowderSnow;
   }

   public void d() {
      EntityUtils.setEntityPitchSafe(this.a, this.h);
      if (this.a instanceof ClientPlayerEntity var2) {
         PlayerStateManager.nT(var2, this.i);
      } else {
         EntityUtils.setEntityYawSafe(this.a, this.i);
      }
   }

   public void c() {
      this.d();
      this.restorePos();
   }
}
