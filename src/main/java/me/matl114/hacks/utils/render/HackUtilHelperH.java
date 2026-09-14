package me.matl114.hacks.utils.render;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;
import me.matl114.events.RenderListener;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.KalamaHelperHelperG;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2d;

class HackUtilHelperH extends KalamaHelperHelperG<HackUtilHelperB> {
   public void render2D(VDrawContext vdraw) {
      if (!this.entries.isEmpty()) {
         Matrix4f var2 = RenderListener.B();
         Matrix4f var3 = RenderListener.D();
         Function var4 = RenderUtils.D(var2, var3);

         for (KalamaHelperHelperK var6 : this.entries) {
            HackUtilHelperB var7 = (HackUtilHelperB)var6.val();
            Vec3d var8 = var7.position();
            int var9 = var6.index();
            Vector2d var10 = (Vector2d)var4.apply(var8);
            if (var10 != null) {
               vdraw.b();

               try {
                  vdraw.f().translate((float)var10.x, (float)var10.y);
                  float var11 = ((HackUtilHelperB)var6.val()).scale();
                  vdraw.f().scale(var11, var11);
                  Text var12 = var7.text();
                  int var13 = var7.offSetFlag();
                  double var14 = RenderCollectors.mc.textRenderer.getTextHandler().getWidth(var12);
                  int var16 = var13 % 3;
                  int var17 = var13 / 3;
                  vdraw.f().translate((float)(-(var14 * var16 / 2.0)), (float)(-(RenderCollectors.HEIGHT * var17 / 2.0)));
                  vdraw.z(RenderCollectors.mc.textRenderer, var12.asOrderedText(), 0, 0, var9, true);
               } finally {
                  vdraw.c();
               }
            }
         }
      }
   }

   public void render3D(MatrixStack stack) {
      if (!this.entries.isEmpty()) {
         Vec3d var2 = RenderUtils.getCameraPos().negate();

         for (KalamaHelperHelperK var4 : this.entries) {
            int var5 = var4.index();
            Vec3d var6 = ((HackUtilHelperB)var4.val()).position();
            Text var7 = ((HackUtilHelperB)var4.val()).text();
            Vec3d var8 = var6.add(var2);
            float var9 = ((HackUtilHelperB)var4.val()).scale();
            stack.push();
            stack.translate(var8.x, var8.y, var8.z);
            stack.multiply(RenderUtils.getBillboardRotation(BillboardMode.CENTER, 0.0F, 0.0F));
            stack.scale(0.03125F * var9, 0.03125F * var9, 1.0F);
            List<Text> var10 = ChatUtils.splitToMultiLineText(var7, Integer.MAX_VALUE);
            int var11 = 0;

            for (Text var13 : var10) {
               VRender.getInstance()
                  .drawTextCameraCoord(
                     var13.asOrderedText(), stack, Vec3d.ZERO.add(0.0, -var11 * 9.0, 0.0), ((HackUtilHelperB)var4.val()).offSetFlag(), new Color(var5), VRender.a
                  );
               var11++;
            }

            stack.pop();
         }
      }
   }



   @Override
   public void a(Object arg0) { }

}
