package me.matl114.hacks.modules.combat;


import net.minecraft.client.MinecraftClient;
import com.mojang.datafixers.util.Pair;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.LabelVec3;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.config.Vec2;
import me.matl114.managers.Tasks;
import me.matl114.utils.MathUtils;
import me.matl114.utils.algorithms.StateMachine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

public class CombatSubHelperFX extends CombatSubHelperOX implements CombatSubHelperG {
   boolean currentTargetUpFly = false;

   private void setTargetToEat(Vec3d predictor, boolean headSimulation) {
      if (headSimulation) {
         Vec3d var3 = this.t.uI.getPos().subtract(MinecraftClient.getInstance().player.getPos());
         if (var3.horizontalLength() > this.t.DE()) {
            this.d(predictor);
         } else {
            this.u = new Vec3d(-var3.x, 0.0, -var3.z).normalize().multiply(10.0);
         }
      } else {
         this.d(predictor);
      }
   }

   @Override
   protected void e(Vec3d targetPos) {
      this.u = targetPos.subtract(MinecraftClient.getInstance().player.getPos());
      boolean var2 = this.t.uS;
      if (var2) {
         OptionalPrimitive var3 = this.t.followOnGroundHeightExtra.get();
         if (var3.isPresent()) {
            this.u = this.u.add(0.0, (Double)var3.getValue(), 0.0);
         }
      } else if (targetPos.y > MinecraftClient.getInstance().player.getY()) {
         this.u = this.u.withAxis(Axis.Y, 0.0);
      } else if (!this.t.combatAngleOptimizeFollow.get() && this.t.combatSmoothFlight3.get() && targetPos.y <= MinecraftClient.getInstance().player.getY()) {
         Vec3d var17 = this.t.uI.dimensions.getBoxAt(targetPos).getCenter();
         double var4 = this.t.combatSmoothFlightArgument2.get();
         Pair var6 = MathUtils.C(var17, var4, MinecraftClient.getInstance().player.getEyePos());
         Vec3d var7 = (Vec3d)var6.getFirst();
         Vec3d var8 = (Vec3d)var6.getSecond();
         Vec3d var9 = var7.y < var8.y ? var7 : var8;
         if (var9.y < 0.0) {
            this.u = var9.multiply(10.0);
         }
      }

      boolean var18 = this.willUseAntiSpear(this.t.flyAntiSpear.get(), targetPos);
      if (!var2 && this.t.combatAngleOptimizeFollow.get() && this.u.y < -1.0E-6) {
         LabelVec3 var10 = this.t.combatAngleOptimizeRange.get();
         double var11;
         if (SpearEnhance.isUsingSpear(MinecraftClient.getInstance().player)) {
            var11 = var10.y();
         } else if (var18) {
            var11 = var10.z();
         } else {
            var11 = var10.x();
         }

         double var13 = MinecraftClient.getInstance().player.getPos().subtract(targetPos).horizontalLength();
         if (var13 > var11) {
            double var15 = Math.max(Math.abs(this.u.x), Math.abs(this.u.z));
            if (var15 > 0.1
               && (
                  !this.t.combatAngleOptimizeRadicalFollow.get().isPresent()
                     || Math.abs(this.u.y) < this.u.horizontalLength() * Math.tan(Math.toRadians(this.t.combatAngleOptimizeRadicalFollow.get().getValue()))
               )
               && Math.abs(this.u.y) > var15) {
               this.u = this.u.withAxis(Axis.Y, -var15);
            }
         }
      }

      double var19 = this.u.length();
      if (var19 < 5.0) {
         this.u = this.u.normalize().multiply(5.0);
      }

      if (var18) {
         this.antiSpear(this.t.flyAntiSpear.get());
      }
   }

   public synchronized void onAttack(Entity entity) {
      if ((MinecraftClient.getInstance().player.isFallFlying() || MinecraftClient.getInstance().player.getAbilities().flying) && this.stateMachine.getState() != 1) {
         if (this.q >= Tasks.b() - 1 && this.currentTargetUpFly) {
            this.stateMachine.c(1);
            this.stateMachine.f();
         } else {
            this.stateMachine.c(2);
         }
      }
   }

   @Override
   protected void d(Vec3d predictor) {
      Vec3d var2 = null;
      boolean var3 = this.t.uS;
      boolean var4 = false;
      boolean var5 = this.willUseAntiSpear(this.t.flyAntiSpearWhenPullUp.get(), predictor)
         && (
            MinecraftClient.getInstance().player.getY() > predictor.getY()
               || MinecraftClient.getInstance().player.getPos().subtract(predictor).horizontalLength() < this.t.combatSpearRange.get()
         );
      boolean var6 = predictor.getY() >= MinecraftClient.getInstance().player.getY();
      if (this.t.combatSmoothFlight.get() && !var3) {
         double var7 = this.t.combatRange.get();
         if (var6) {
            Vec3d var9 = this.t.uI.dimensions.getBoxAt(predictor).getCenter();
            double var10 = var7 + this.t.combatSmoothFlightArgument1.get();
            Pair var12 = MathUtils.C(var9, var10, MinecraftClient.getInstance().player.getEyePos());
            Vec3d var13 = (Vec3d)var12.getFirst();
            Vec3d var14 = (Vec3d)var12.getSecond();
            Vec3d var15 = var13.y < var14.y ? var14 : var13;
            var15 = var15.add(0.0, 0.01, 0.0);
            if (Math.abs(this.t.combatSmoothFlightArgument11.get()) > 1.0E-6
               && (
                     this.t.combatSmoothFlightArgument15.get()
                        ? var9.subtract(MinecraftClient.getInstance().player.getEyePos()).horizontalLengthSquared()
                        : var9.squaredDistanceTo(MinecraftClient.getInstance().player.getEyePos())
                  )
                  < MathUtils.a(this.t.combatSmoothFlightArgument16.get())) {
               var15 = var15.normalize();
               Vec3d var16 = MinecraftClient.getInstance().player.getEyePos().subtract(var9);
               Vec3d var17 = new Vec3d(var16.x, 0.0, var16.z).normalize().multiply(this.t.combatSmoothFlightArgument11.get());
               var15 = var15.add(var17).normalize();
            }

            if (var15.y > 0.0) {
               var2 = var15.normalize().multiply(10.0);
            }

            var4 = var9.squaredDistanceTo(MinecraftClient.getInstance().player.getEyePos()) < MathUtils.a(var10);
         }
      }

      if (var2 == null) {
         var2 = predictor.withAxis(Axis.Y, predictor.getY() + this.t.maceHeight.get()).subtract(MinecraftClient.getInstance().player.getPos());
         if (var2.length() < 5.0) {
            var2 = var2.normalize().multiply(5.0);
         }
      }

      if (this.t.combatPullUpAngleOptimize.get().isPresent() && !var3 && !var4) {
         Vec2 var18 = this.t.combatPullUpAngleOptimize.get().getValue();
         double var19 = var2.horizontalLength();
         if (var19 > 0.1) {
            Vec3d var21 = PlayerStateManager.INSTANCE.jr;
            Vec3d var22 = var21.withAxis(Axis.Y, 0.0);
            double var23 = MinecraftClient.getInstance().player.getY() < predictor.getY() ? var18.y() : var18.x();
            if (var23 > var19 && var22.dotProduct(var2) < 0.0) {
               var2 = var2.multiply(-1.0, 1.0, -1.0);
            }
         }
      }

      this.u = var2;
      if (this.t.combatAngleOptimize.get() && this.u.y > 1.0E-6) {
         double var29 = Math.max(Math.abs(this.u.x), Math.abs(this.u.z));
         if (var29 > 0.1) {
            if (var4) {
               double var25 = MathUtils.j(this.u.x);
               double var27 = MathUtils.j(this.u.z);
               this.u = new Vec3d(var25 * var29, var29, var27 * var29);
            } else {
               this.u = this.u.withAxis(Axis.Y, var29);
            }
         }
      }

      double var30 = this.u.length();
      if (var30 < 5.0) {
         this.u = this.u.normalize().multiply(5.0);
      }

      if (var5) {
         this.antiSpear(this.t.flyAntiSpearWhenPullUp.get());
      }
   }

   @Override
   public synchronized void onUpdate() {
      this.currentTargetUpFly = false;
      super.onUpdate();
   }

   public int onStatePullUp(StateMachine machine) {
      Vec3d var2 = new Vec3d(0.0, 0.1, 0.0);
      Vec3d var3 = MovTasks.simulateMovement(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getPos(), var2, true);
      boolean var4 = var3.squaredDistanceTo(var2) > 1.0E-4;
      boolean var5 = this.G();
      Vec3d var6 = this.t.macePullUpUsePredictor.get() ? PositionPredict.INSTANCE.attackPredictArgument.get().predict(this.t.uI) : this.t.uI.getPos();
      if (var5) {
         this.setTargetToEat(var6, var4);
         machine.e();
         return 1;
      } else if (var4) {
         return 2;
      } else {
         double var7 = this.t.uI.getY() + this.t.maceHeight.get();
         boolean var9 = MinecraftClient.getInstance().player.getY() >= var7
            || MinecraftClient.getInstance().player.getY() > this.t.uI.getY()
               && this.o != 0
               && Tasks.b() > this.t.maceMaxExtraPullUpTick.get() + this.o + this.t.maceHeight.get();
         if (var9 && this.t.combatMaceChaseFollowYBias.get().isPresent() && MinecraftClient.getInstance().player.getY() > this.t.uI.getY() && !this.t.uR) {
            double var10 = this.t.combatMaceChaseFollowYBias.get().getValue();
            Vec3d var12 = var6.subtract(MinecraftClient.getInstance().player.getPos());
            double var13 = Math.max(Math.abs(var12.x), Math.abs(var12.z));
            double var15 = Math.abs(var12.y);
            if (var15 < var13 + var10) {
               var9 = false;
            }
         }

         if (!var9) {
            this.d(var6);
            machine.e();
            return 1;
         } else {
            return 2;
         }
      }
   }

   public int onStateFollow(StateMachine machine) {
      if (PlayerStateManager.INSTANCE.jh < 1.0E-6 && this.s > 1.0E-6) {
         return 1;
      } else if (this.G()) {
         return 1;
      } else {
         Vec3d var2 = this.t.macePullUpUsePredictor.get() ? PositionPredict.INSTANCE.attackPredictArgument.get().predict(this.t.uI) : this.t.uI.getPos();
         double var3 = this.t.uI.getY();
         this.currentTargetUpFly = var3 + 0.5 < var2.y;
         double var5 = this.currentTargetUpFly ? var3 : var2.y;
         var2 = var2.withAxis(Axis.Y, var5);
         boolean var7 = this.H() || this.I();
         boolean var8 = this.t.DK(this.t.uI);
         if (var7 && (this.t.uS || MinecraftClient.getInstance().player.getY() < var2.getY() + this.t.minAttackHeight.get()) && var8) {
            this.e(var2);
            this.M();
            machine.e();
            if (this.t.flyAntiSpearUseSpearResetWhenFollow.get()) {
               SpearEnhance.INSTANCE.setForceSpearReset(true);
            }

            return 3;
         } else {
            Vec3d var9 = new Vec3d(0.0, -0.1, 0.0);
            Vec3d var10 = MovTasks.simulateMovement(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getPos(), var9, true);
            boolean var11 = var10.squaredDistanceTo(var9) > 1.0E-4;
            boolean var12 = var11;
            double var13 = this.t.maceMaxFollowHeight.get();
            if (!var11 && this.t.uI.getY() > MinecraftClient.getInstance().player.getY() + var13) {
               var12 = true;
            }

            if (var12) {
               if (var8) {
                  this.e(var2);
                  this.M();
                  machine.e();
                  return 3;
               } else {
                  return 1;
               }
            } else {
               this.e(var2);
               if (this.t.flyAntiSpearUseSpearResetWhenFollow.get()) {
                  SpearEnhance.INSTANCE.setForceSpearReset(true);
               }

               machine.e();
               return 2;
            }
         }
      }
   }

}
