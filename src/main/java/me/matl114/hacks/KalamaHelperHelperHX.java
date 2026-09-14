package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperHX implements KalamaHelperHelperRX {
   Vec3d e;
   Vec3d d;
   Color c;

   public Color d() {
      return this.c;
   }

   public KalamaHelperHelperHX p(Vec3d endVec) {
      this.e = endVec;
      return this;
   }

   public KalamaHelperHelperHX(Vec3d startVec, Vec3d endVec, Color color) {
      this.d = startVec;
      this.e = endVec;
      this.c = color;
   }

   @Override
   public void render(MatrixStack stack, float partialTicks) {
      RenderUtils.drawOutlinedBox(stack, this.d, this.e, this.c);
   }

   public KalamaHelperHelperHX o(Vec3d startVec) {
      this.d = startVec;
      return this;
   }

   public KalamaHelperHelperHX q(Color color) {
      this.c = color;
      return this;
   }

   public Vec3d h() {
      return this.d;
   }

   public Vec3d i() {
      return this.e;
   }
}
