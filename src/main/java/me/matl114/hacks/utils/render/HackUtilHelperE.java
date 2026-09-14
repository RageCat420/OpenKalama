package me.matl114.hacks.utils.render;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.ColorQuad;
import me.matl114.utils.render.Quad;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

class HackUtilHelperE implements RenderCollector<Box> {
   private final Map<HackUtilHelperL, Integer> lines = new LinkedHashMap<>();

   @Override
   public void clear() {
      this.lines.clear();
   }

   @Override
   public void b(VDrawContext vDrawContext) {
   }

   @Override
   public void a(MatrixStack matrices) {
      if (!this.lines.isEmpty()) {
         Vec3d var2 = RenderUtils.getCameraPos().negate();
         VRender.getInstance().j((op, vtx) -> {
            for (Entry var6 : this.lines.entrySet()) {
               Quad var7 = ((HackUtilHelperL)var6.getKey()).offset(var2).acm();
               op.drawQuad(matrices, vtx, var7, ColorQuad.of((Integer)var6.getValue()));
            }
         }, false);
      }
   }

   private void g(HackUtilHelperL quad, int color) {
      if (this.lines.containsKey(quad)) {
         this.lines.remove(quad);
      } else {
         this.lines.put(quad, color);
      }
   }

   @Override
   public void submit(Box val, int color) {
      for (HackUtilHelperL var4 : RenderElements.boxFaces(val)) {
         this.g(var4, color);
      }
   }

   public void h(Box val, int color) {
      this.submit(val, color);
   }



   

}
