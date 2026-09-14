package me.matl114.hacks.modules.combat;


import net.minecraft.client.MinecraftClient;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.MathUtils;
import me.matl114.utils.algorithms.StateMachine;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class CombatSubHelperDX extends CombatSubHelperOX implements CombatSubHelperG {
   public int onStateFollow(StateMachine machine) {
      if (PlayerStateManager.INSTANCE.jh < 1.0E-6 && this.s > 1.0E-6) {
         return 1;
      } else if (this.G()) {
         return 1;
      } else {
         Vec3d var2 = this.t.macePullUpUsePredictor.get() ? PositionPredict.INSTANCE.attackPredictArgument.get().predict(this.t.uI) : this.t.uI.getPos();
         boolean var3 = this.t.DK(this.t.uI);
         if (this.t.flyAntiSpearUseSpearResetWhenFollow.get()) {
            SpearEnhance.INSTANCE.setForceSpearReset(true);
         }

         if (this.t.uS) {
            if (var3) {
               this.setTargetToPlayer(this.t.uI.getPos());
               this.M();
               machine.e();
               return 3;
            } else {
               this.setTargetToPlayer(this.t.uI.getPos());
               Vec3d var8 = this.u.normalize().multiply(0.1);
               Vec3d var9 = MovTasks.simulateMovement(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getPos(), var8, true);
               boolean var10 = var9.squaredDistanceTo(var8) > 1.0E-4;
               if (var10) {
                  this.u = Vec3d.ZERO;
                  return 1;
               } else {
                  machine.e();
                  return 2;
               }
            }
         } else {
            Vec3d var4 = new Vec3d(0.0, 1.0, 0.0);
            Vec3d var5 = MovTasks.simulateMovement(this.t.uI, var2, var4, false);
            boolean var6 = var5.squaredDistanceTo(var4) > 1.0E-4;
            if (var6) {
               Vec3d var7 = var2.add(0.0, -4.42 + var5.length(), 0.0);
               this.u = var7.subtract(MinecraftClient.getInstance().player.getPos());
               if (this.u.y > -1.0 && var3) {
                  this.M();
                  machine.e();
                  return this.u.y > 0.0 ? 1 : 2;
               } else if (this.u.y > 0.0) {
                  return 1;
               } else {
                  machine.e();
                  return 2;
               }
            } else if (var3) {
               this.setTargetToPlayer(this.t.uI.getPos());
               this.M();
               machine.e();
               return 2;
            } else {
               this.setTargetToPlayer(var2);
               if (this.u.y > 0.0) {
                  this.u = Vec3d.ZERO;
                  return 1;
               } else {
                  machine.e();
                  return 2;
               }
            }
         }
      }
   }

   protected void setTargetToPlayer(Vec3d targetPos) {
      Vec3d var2 = MinecraftClient.getInstance().player.getPos();
      Vec3d var3;
      if (this.t.uS) {
         var3 = targetPos.add(0.0, this.t.followOnGroundHeightExtra.get().orElse(0.5), 0.0);
         ElytraExtra.INSTANCE.afo();
      } else {
         var3 = targetPos;
      }

      boolean var4 = false;
      if (this.t.uS) {
         double var5 = CombatTasks.j().getAttackAtTargetRange(this.t.uI);
         Vec3d var7 = this.t.uI.getEyePos();
         BlockHitResult var8 = MinecraftClient.getInstance()
            .world
            .raycast(new RaycastContext(MinecraftClient.getInstance().player.getEyePos(), var7, ShapeType.COLLIDER, FluidHandling.NONE, MinecraftClient.getInstance().player));
         if (var8 != null && var8.getType() != Type.MISS && var8.getPos().squaredDistanceTo(var7) <= MathUtils.a(2.0 * var5)) {
            BlockPos var9 = BlockPos.ofFloored(var8.getPos());
            int var10 = Math.max(1, (int)Math.ceil(var5));
            ArrayList<BlockPos> var11 = new ArrayList();

            for (int var12 = var9.getX() - var10; var12 <= var9.getX() + var10; var12++) {
               for (int var13 = var9.getY() - var10; var13 <= var9.getY() + var10; var13++) {
                  for (int var14 = var9.getZ() - var10; var14 <= var9.getZ() + var10; var14++) {
                     var11.add(new BlockPos(var12, var13, var14));
                  }
               }
            }

            var11.sort(Comparator.comparingDouble(pos -> pos.toCenterPos().squaredDistanceTo(var7)));

            for (BlockPos var17 : var11) {
               Vec3d var18 = var17.toCenterPos();
               if (TargetSelector.INSTANCE.akm(var18, this.t.uI.getBoundingBox(), var5 + 0.5)
                  && MinecraftClient.getInstance().world.raycast(new RaycastContext(var2, var18, ShapeType.COLLIDER, FluidHandling.NONE, MinecraftClient.getInstance().player)).getType()
                     == Type.MISS) {
                  Box var15 = MinecraftClient.getInstance().player.dimensions.getBoxAt(var18);
                  if (MinecraftClient.getInstance().world.isSpaceEmpty(var15)) {
                     this.u = var18.subtract(var2);
                     var4 = true;
                     break;
                  }
               }
            }
         }
      }

      if (!var4) {
         this.u = var3.subtract(var2);
      }

      if (this.u.length() < 5.0 && this.u.length() > 1.0E-6) {
         this.u = this.u.normalize().multiply(5.0);
      }
   }

   protected void setTargetToPlayerUpper(Vec3d predictor) {
      if (this.t.uS) {
         if (predictor.y > MinecraftClient.getInstance().player.getY()) {
            this.u = predictor.withAxis(Axis.Y, predictor.getY() + this.t.maceHeightGround.get()).subtract(MinecraftClient.getInstance().player.getPos());
         } else {
            Vec3d var2 = MinecraftClient.getInstance().player.getPos().subtract(predictor);
            double var3 = var2.horizontalLength();
            double var5 = this.t.maceHeightGround.get();
            if (var3 < var5 && var2.y > var3) {
               this.u = var2;
            } else {
               Vec3d var7 = var2.withAxis(Axis.Y, 0.0).normalize().multiply(var5).withAxis(Axis.Y, var5);
               this.u = var7.subtract(var2);
            }
         }
      } else {
         Vec3d var23 = new Vec3d(0.0, 1.0, 0.0);
         Vec3d var8 = MovTasks.simulateMovement(this.t.uI, predictor, var23, false);
         boolean var9 = var8.squaredDistanceTo(var23) > 1.0E-4;
         if (var9) {
            this.u = predictor.add(var23).subtract(MinecraftClient.getInstance().player.getPos());
         } else {
            Vec3d var10 = null;
            boolean var11 = predictor.getY() >= MinecraftClient.getInstance().player.getY();
            if (this.t.combatSmoothFlight.get()) {
               double var12 = this.t.combatRange.get();
               var12 /= 2.0;
               if (var11) {
                  Vec3d var14 = this.t.uI.dimensions.getBoxAt(predictor).getCenter();
                  double var15 = var12 + this.t.combatSmoothFlightArgument1.get();
                  Pair var17 = MathUtils.C(var14, var15, MinecraftClient.getInstance().player.getEyePos());
                  Vec3d var18 = (Vec3d)var17.getFirst();
                  Vec3d var19 = (Vec3d)var17.getSecond();
                  Vec3d var20 = var18.y < var19.y ? var19 : var18;
                  var20 = var20.add(0.0, 0.01, 0.0);
                  if (Math.abs(this.t.combatSmoothFlightArgument11.get()) > 1.0E-6
                     && (
                           this.t.combatSmoothFlightArgument15.get()
                              ? var14.subtract(MinecraftClient.getInstance().player.getEyePos()).horizontalLengthSquared()
                              : var14.squaredDistanceTo(MinecraftClient.getInstance().player.getEyePos())
                        )
                        < MathUtils.a(this.t.combatSmoothFlightArgument16.get())) {
                     var20 = var20.normalize();
                     Vec3d var21 = MinecraftClient.getInstance().player.getEyePos().subtract(var14);
                     Vec3d var22 = new Vec3d(var21.x, 0.0, var21.z).normalize().multiply(this.t.combatSmoothFlightArgument11.get());
                     var20 = var20.add(var22).normalize();
                  }

                  if (var20.y > 0.0) {
                     var10 = var20.normalize().multiply(10.0);
                  }
               }
            }

            if (var10 == null) {
               var10 = predictor.withAxis(Axis.Y, predictor.getY() + this.t.maceHeightGround.get()).subtract(MinecraftClient.getInstance().player.getPos());
               if (var10.length() < 5.0) {
                  var10 = var10.normalize().multiply(5.0);
               }
            }

            this.u = var10;
         }
      }

      if (this.u.length() < 5.0) {
         this.u = this.u.normalize().multiply(5.0);
      }
   }

   public void onAttack(Entity entity) {
      if ((MinecraftClient.getInstance().player.isFallFlying() || MinecraftClient.getInstance().player.getAbilities().flying) && this.stateMachine.getState() != 2) {
         this.stateMachine.c(2);
      }
   }

   private void setTargetToEat(Vec3d predictor, boolean headSimulation) {
      if (headSimulation) {
         Vec3d var3 = this.t.uI.getPos().subtract(MinecraftClient.getInstance().player.getPos());
         if (var3.horizontalLength() > this.t.DE()) {
            this.setTargetToPlayerUpper(predictor);
         } else {
            this.u = new Vec3d(-var3.x, 0.0, -var3.z).normalize().multiply(10.0);
         }
      } else {
         this.setTargetToPlayerUpper(predictor);
      }
   }

   public int onStatePullUp(StateMachine machine) {
      boolean var2 = this.G();
      Vec3d var3 = new Vec3d(0.0, 0.1, 0.0);
      Vec3d var4 = MovTasks.simulateMovement(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getPos(), var3, true);
      boolean var5 = var4.squaredDistanceTo(var3) > 1.0E-4;
      double var6 = this.t.uI.getY() + this.t.maceHeightGround.get();
      boolean var8 = MinecraftClient.getInstance().player.getY() >= var6
         || MinecraftClient.getInstance().player.getY() > this.t.uI.getY()
            && this.o != 0
            && Tasks.b() > this.t.maceMaxExtraPullUpTick.get() + this.o + this.t.maceHeightGround.get();
      boolean var9 = var5 || var8;
      if (var9) {
         if (!var2) {
            return 2;
         } else {
            this.setTargetToEat(this.t.uI.getPos(), var5);
            machine.e();
            return 1;
         }
      } else {
         Vec3d var10 = this.t.macePullUpUsePredictor.get() ? PositionPredict.INSTANCE.attackPredictArgument.get().predict(this.t.uI) : this.t.uI.getPos();
         this.setTargetToPlayerUpper(var10);
         machine.e();
         return 1;
      }
   }



   @Override
   public void d(Object arg0) { }

}
