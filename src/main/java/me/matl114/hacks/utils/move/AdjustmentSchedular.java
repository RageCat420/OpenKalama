package me.matl114.hacks.utils.move;

import java.util.List;
import java.util.function.Supplier;
import me.matl114.events.Event;
import me.matl114.hacks.modules.move.MoveSubHelperBX;
import me.matl114.hacks.modules.move.PlayerInputManager;
import me.matl114.hacks.modules.survival.SchedularSettings;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class AdjustmentSchedular {
   boolean f;
   ClientPlayerEntity i;
   double d = 0.0;
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   Supplier<Vec3d> e;
   double g;
   private static final double c = 0.8;
   private static final double b = 0.01;
   RenderCollector<List<Vec3d>> j;
   double h;

   private MoveSubHelperBX buildWasdModifier(ClientPlayerEntity player, Vec3d direction) {
      double var3 = Math.toRadians(player.getYaw());
      double var5 = -direction.x * Math.sin(var3) + direction.z * Math.cos(var3);
      double var7 = direction.x * Math.cos(var3) + direction.z * Math.sin(var3);
      MoveSubHelperBX var9 = MoveSubHelperBX.gI(0).gJ(var5 > 0.001).gK(var5 < -0.001).gL(var7 > 0.001).gM(var7 < -0.001);
      if (!var9.hf() && !var9.hg() && !var9.hh() && !var9.hi()) {
         var9 = var9.gJ(true);
      }

      return var9;
   }

   public double n() {
      return this.h;
   }

   public AdjustmentSchedular e(double extraRange) {
      this.d = extraRange;
      return this;
   }

   public void tickAdjustment(ClientPlayerEntity player) {
      this.j.clear();
      this.i = player;
      if (player != null && this.e != null) {
         if (!PathingSchedular.z()) {
            Vec3d var2 = this.e.get();
            if (var2 != null) {
               Vec3d var3 = player.getPos();
               this.j.submit(List.of(player.getPos(), var2), SchedularSettings.INSTANCE.colorLines.get().withAlpha(255));
               if (!MathUtils.m(var3, var2, this.h)) {
                  double var4 = this.g + this.d;
                  if (MathUtils.isInBox(var3.subtract(var2), var4)) {
                     Vec3d var6 = this.f ? var3.subtract(var2) : var2.subtract(var3);
                     Vec3d var7 = new Vec3d(var6.x, 0.0, var6.z);
                     if (!(var7.lengthSquared() < 1.0E-8)) {
                        int var8 = this.countAdjacentXZCollisions(player, var7);
                        MoveSubHelperBX var9;
                        if (var8 >= 2) {
                           var9 = null;
                           Vec3d var10 = Vec3d.ZERO;
                        } else if (var8 == 1) {
                           Vec2f var11 = EntityUtils.q(var2.subtract(player.getPos()));
                           Vec3d var12 = var7.normalize();
                           var9 = MoveSubHelperBX.gI(0).gJ(true).gK(false).gL(false).gM(false).gQ(var11.x).gR(var11.y);
                        } else {
                           Vec3d var13 = var7.normalize();
                           var9 = this.buildWasdModifier(player, var13);
                        }

                        if (var9 != null) {
                           PlayerInputManager.INSTANCE.addInputModifier(var9, 1);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public AdjustmentSchedular opposite(boolean opposite) {
      this.f = opposite;
      return this;
   }

   public AdjustmentSchedular k(double adjustRange) {
      this.g = adjustRange;
      return this;
   }

   public double l() {
      return this.g;
   }

   public AdjustmentSchedular m(double availableRange) {
      this.h = availableRange;
      return this;
   }

   public AdjustmentSchedular() {
      this.f = false;
      this.g = 1.5;
      this.h = 0.05;
      this.j = RenderCollectors.e();
   }

   public void d(Event<MatrixStack> event) {
      if (SchedularSettings.INSTANCE.enableRender.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)event.b);

         try {
            this.j.a((MatrixStack)event.b);
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.b);
         }
      }
   }

   private int countAdjacentXZCollisions(ClientPlayerEntity player, Vec3d direction) {
      Vec3d var3 = direction.normalize().multiply(0.01);
      Box var4 = player.getBoundingBox().offset(var3);
      BlockPos var5 = player.getBlockPos();
      int var6 = (int)Math.floor(var4.minY);
      int var7 = (int)Math.ceil(var4.maxY) - 1;
      int var8 = 0;

      for (int var9 = -1; var9 <= 1; var9++) {
         for (int var10 = -1; var10 <= 1; var10++) {
            if (var9 != 0 || var10 != 0) {
               boolean var11 = false;

               for (int var12 = var6; var12 <= var7; var12++) {
                  BlockPos var13 = new BlockPos(var5.getX() + var9, var12, var5.getZ() + var10);
                  VoxelShape var14 = mc.world.getBlockState(var13).getCollisionShape(mc.world, var13);
                  if (!var14.isEmpty()) {
                     for (Box var16 : var14.getBoundingBoxes()) {
                        if (var4.intersects(var16.offset(var13))) {
                           var11 = true;
                           break;
                        }
                     }

                     if (var11) {
                        break;
                     }
                  }
               }

               if (var11) {
                  if (++var8 >= 2) {
                     return var8;
                  }
               }
            }
         }
      }

      return var8;
   }

   public boolean j() {
      return this.f;
   }

   public Supplier<Vec3d> center() {
      return this.e;
   }

   public AdjustmentSchedular g(Supplier<Vec3d> center) {
      this.e = center;
      return this;
   }

   public double f() {
      return this.d;
   }
}
