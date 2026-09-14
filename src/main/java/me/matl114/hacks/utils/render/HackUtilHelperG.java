package me.matl114.hacks.utils.render;

import java.util.function.Function;
import me.matl114.events.RenderListener;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.KalamaHelperHelperG;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2d;

class HackUtilHelperG extends KalamaHelperHelperG<Vec3d> {
   @Override
   public void a(MatrixStack matrices) {
      if (!this.entries.isEmpty()) {
         Vec3d var2 = RenderUtils.getCameraPos().negate();
         Vec3d var3 = RenderUtils.getTracerOrigin(0.0F);
         VRender.getInstance().h((op, vtx) -> {
            for (KalamaHelperHelperK var7 : this.entries) {
               Vec3d var8 = ((Vec3d)var7.val()).add(var2);
               op.e(matrices, vtx, var3, var8, var7.index());
            }
         });
      }
   }

   @Override
   public void b(VDrawContext vdraw) {
      if (!this.entries.isEmpty()) {
         Matrix4f var2 = RenderListener.B();
         Matrix4f var3 = RenderListener.D();
         Function var4 = RenderUtils.createProjector(var2, var3, false);
         Vector2d var5 = RenderUtils.getScreenSize().mul(0.5, 0.5);
         vdraw.f().pushMatrix();

         try {
            vdraw.f().translate((float)var5.x, (float)var5.y);

            for (KalamaHelperHelperK var7 : this.entries) {
               Vec3d var8 = (Vec3d)var7.val();
               int var9 = var7.index();
               Vector2d var10 = (Vector2d)var4.apply(var8);
               if (var10 != null) {
                  vdraw.H((int)(var10.x - var5.x), (int)(var10.y - var5.y), 0, 0, var9, var9, 0);
               }
            }
         } finally {
            vdraw.f().popMatrix();
         }
      }
   }



   }
