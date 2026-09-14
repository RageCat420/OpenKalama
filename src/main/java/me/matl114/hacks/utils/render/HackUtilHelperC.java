package me.matl114.hacks.utils.render;

import java.util.function.Function;
import me.matl114.events.RenderListener;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.KalamaHelperHelperG;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2d;

class HackUtilHelperC extends KalamaHelperHelperG<Box> {
   public void render3D(MatrixStack matrices) {
      if (!this.entries.isEmpty()) {
         Vec3d var2 = RenderUtils.getCameraPos().negate();
         if (this.a) {
            VRender.getInstance().j((operation, vertexConsumer) -> {
               if (!this.entries.isEmpty()) {
                  for (KalamaHelperHelperK var6 : this.entries) {
                     Box var7 = ((Box)var6.val()).offset(var2);
                     operation.b(matrices, vertexConsumer, var7.getMinPos(), var7.getMaxPos(), var6.index());
                  }
               }
            }, true);
         }

         if (this.b || this.c) {
            VRender.getInstance().h((op, vtx) -> {
               if (drawOutline) {
                  for (KalamaHelperHelperK var8 : this.entries) {
                     Box var9 = ((Box)var8.val()).offset(var2);
                     op.a(matrices, vtx, var9.getMinPos(), var9.getMaxPos(), var8.index());
                  }
               }

               if (drawTraceLine) {
                  Vec3d var11 = RenderUtils.getTracerOrigin(0.0F);

                  for (KalamaHelperHelperK var13 : this.entries) {
                     Vec3d var10 = ((Box)var13.val()).getCenter().add(var2);
                     op.e(matrices, vtx, var11, var10, var13.index());
                  }
               }
            });
         }
      }
   }

   HackUtilHelperC(boolean var1, boolean var2, boolean var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public void render2D(VDrawContext vdraw) {
      if (!this.entries.isEmpty()) {
         Matrix4f var2 = RenderListener.B();
         Matrix4f var3 = RenderListener.D();
         if (this.b || this.a) {
            Function var4 = RenderUtils.createProjector(var2, var3, true);

            for (KalamaHelperHelperK var6 : this.entries) {
               Box var7 = (Box)var6.val();
               Vec3d var8 = var7.getMinPos();
               Vec3d var9 = var7.getMaxPos();
               double[] var10 = new double[]{var8.x, var9.x};
               double[] var11 = new double[]{var8.y, var9.y};
               double[] var12 = new double[]{var8.z, var9.z};
               double var13 = Double.MAX_VALUE;
               double var15 = -Double.MAX_VALUE;
               double var17 = Double.MAX_VALUE;
               double var19 = -Double.MAX_VALUE;
               boolean var21 = false;

               label157:
               for (double var25 : var10) {
                  for (double var30 : var11) {
                     for (double var35 : var12) {
                        Vec3d var37 = new Vec3d(var25, var30, var35);
                        Vector2d var38 = (Vector2d)var4.apply(var37);
                        if (var38 == null) {
                           var21 = true;
                           break label157;
                        }

                        if (var38.x < var13) {
                           var13 = var38.x;
                        }

                        if (var38.x > var15) {
                           var15 = var38.x;
                        }

                        if (var38.y < var17) {
                           var17 = var38.y;
                        }

                        if (var38.y > var19) {
                           var19 = var38.y;
                        }
                     }
                  }
               }

               if (!var21) {
                  int var51 = var6.index();
                  int var52 = (int)Math.round(var13);
                  int var53 = (int)Math.round(var17);
                  int var39 = (int)Math.round(var15);
                  int var40 = (int)Math.round(var19);
                  if (this.a) {
                     vdraw.fill(var52, var53, var39, var40, var51);
                  }

                  if (this.b) {
                     vdraw.aa(var52, var53, var39, var53, var51, 0);
                     vdraw.aa(var39, var53, var39, var40, var51, 0);
                     vdraw.aa(var39, var40, var52, var40, var51, 0);
                     vdraw.aa(var52, var40, var52, var53, var51, 0);
                  }
               }
            }
         }

         if (this.c) {
            Function var44 = RenderUtils.createProjector(var2, var3, false);
            Vector2d var45 = RenderUtils.getScreenSize().mul(0.5, 0.5);
            vdraw.f().pushMatrix();

            try {
               vdraw.f().translate((float)var45.x, (float)var45.y);

               for (KalamaHelperHelperK var47 : this.entries) {
                  Vec3d var48 = ((Box)var47.val()).getCenter();
                  int var49 = var47.index();
                  Vector2d var50 = (Vector2d)var44.apply(var48);
                  if (var50 != null) {
                     vdraw.H((int)(var50.x - var45.x), (int)(var50.y - var45.y), 0, 0, var49, var49, 0);
                  }
               }
            } finally {
               vdraw.f().popMatrix();
            }
         }
      }
   }
   boolean a;
   boolean b;
   boolean c;

   @Override
   public void a(Object arg0) { }

}
