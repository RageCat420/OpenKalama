package me.matl114.hacks.modules.move;

import me.matl114.hacks.KalamaHelperHelperFX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ForwardTp extends BaseModule {
   public final DoubleRef maxDistance;
   public final FlagRef ignoreMoveCollision;
   public final KeyBindRef quickMove;
   public final ModulePath TS = makePath(Configs.m, "quick-move");
   public final KeyBindRef quickToWall;
   final double distance;

   public Vec3d calculateAvailableMovPlace(Entity executor, Vec3d curPose, Vec3d lookAt, double delta, double max) {
      lookAt = lookAt.normalize();
      Vec3d var8 = lookAt.multiply(max);
      KalamaHelperHelperFX var9 = new KalamaHelperHelperFX(executor, curPose, curPose.add(var8), false);
      lookAt = lookAt.multiply(delta);
      Vec3d var10 = curPose;
      Vec3d var11 = curPose;
      boolean var12 = false;

      for (double var13 = 0.0; var13 < max; var13 += delta) {
         curPose = curPose.add(lookAt);
         boolean var15 = this.ignoreMoveCollision.get();
         boolean var16;
         if (var15) {
            var16 = true;
         } else {
            Vec3d var17 = curPose.subtract(var10);
            Vec3d var18 = var9.simulateMovement(executor, var10, var17);
            var16 = MovTasks.validMovementAsServer(var17, var18);
         }

         boolean var21 = var16 && !var9.checkEnvironmentCollision(executor, curPose, true);
         if (var21) {
            var11 = curPose;
            if (var12) {
               break;
            }
         } else {
            var12 = true;
         }
      }

      return var11;
   }

   public static Vec3d alP(Entity executor, Vec3d curPose, Vec3d lookAt, double delta, double max) {
      lookAt = lookAt.normalize();
      Vec3d var7 = lookAt.multiply(max);
      KalamaHelperHelperFX var8 = new KalamaHelperHelperFX(executor, curPose, curPose.add(var7), false);
      lookAt = lookAt.multiply(delta);
      Vec3d var9 = curPose;
      Vec3d var10 = curPose;

      for (double var11 = 0.0; var11 < max; var11 += delta) {
         curPose = curPose.add(lookAt);
         Vec3d var13 = curPose.subtract(var9);
         Vec3d var14 = var8.simulateMovement(executor, var9, var13);
         boolean var15 = MovTasks.validMovementAsServer(var13, var14);
         boolean var16 = var15 && !var8.checkEnvironmentCollision(executor, curPose, true);
         if (!var16) {
            break;
         }

         var10 = curPose;
      }

      return var10;
   }

   public boolean quickMovFront() {
      ClientPlayerEntity var1 = mc.player;
      if (var1 == null) {
         return false;
      } else {
         Vec3d var2 = var1.getPos();
         Vec3d var3 = var1.getRotationVector().normalize();
         Vec3d var4 = this.calculateAvailableMovPlace(mc.player, var2, var3, 0.1, this.maxDistance.get());
         if (var4 != var2) {
            if (this.ignoreMoveCollision.get()) {
               MovTasks.executeTp(var4, 2.147483647E9, false, true);
            } else {
               MovTasks.farawayMoveTo(var4, true);
            }

            return true;
         } else {
            Debug.b("no available position in front of you!");
            return true;
         }
      }
   }

   public ForwardTp() {
      super("ForwardTp");
      this.quickMove = this.hotkey(this.TS.add("quick-move"), new MultiKeyBind()).registerHotkey(HotKeyUtils.c(this::quickMovFront)).build();
      this.quickToWall = this.hotkey(this.TS.add("quick-to-wall"), new MultiKeyBind()).registerHotkey(HotKeyUtils.c(this::alN)).build();
      this.maxDistance = this.builder(this.TS.add("max-distance"), DoubleRef.TYPE).defaultValue(120.0).validator(Configs.doubleRange(0.0, 114514.0)).build();
      this.ignoreMoveCollision = this.builder(this.TS.add("ignore-move-collision"), FlagRef.TYPE).defaultValue(true).build();
      this.distance = 0.1;
   }

   public boolean alN() {
      ClientPlayerEntity var1 = mc.player;
      if (var1 == null) {
         return false;
      } else {
         Vec3d var2 = var1.getPos();
         Vec3d var3 = var1.getRotationVector().normalize();
         Vec3d var4 = alP(mc.player, var2, var3, 0.1, this.maxDistance.get());
         if (var4 != var2) {
            if (this.ignoreMoveCollision.get()) {
               MovTasks.executeTp(var4, 2.147483647E9, false, true);
            } else {
               MovTasks.farawayMoveTo(var4, true);
            }

            return true;
         } else {
            Debug.b("no available position in front of you!");
            return true;
         }
      }
   }
}
