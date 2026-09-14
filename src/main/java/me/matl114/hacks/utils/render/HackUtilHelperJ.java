package me.matl114.hacks.utils.render;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

class HackUtilHelperJ implements RenderCollector<Box> {
   private final Map<HackUtilHelperD, Integer> quads = new LinkedHashMap<>();

   @Override
   public void clear() {
      this.quads.clear();
   }

   @Override
   public void b(VDrawContext vDrawContext) {
   }

   public void render3D(MatrixStack matrices) {
      if (!this.quads.isEmpty()) {
         Vec3d var2 = RenderUtils.getCameraPos().negate();
         VRender.getInstance().h((op, vtx) -> {
            for (Entry var6 : this.quads.entrySet()) {
               HackUtilHelperD var7 = ((HackUtilHelperD)var6.getKey()).offset(var2);
               op.e(matrices, vtx, new Vec3d(var7.z0(), var7.z0(), var7.y0()), new Vec3d(var7.x0(), var7.y0(), var7.x0()), (Integer)var6.getValue());
            }
         });
      }
   }

   private void l(HackUtilHelperD line, int color) {
      if (this.quads.containsKey(line)) {
         this.quads.remove(line);
      } else {
         this.quads.put(line, color);
      }
   }

   public void h(Box val, int color) {
      for (HackUtilHelperD var4 : RenderElements.boxOutline(val)) {
         this.l(var4, color);
      }
   }



   @Override
   public void a(Object arg0) { }

}
