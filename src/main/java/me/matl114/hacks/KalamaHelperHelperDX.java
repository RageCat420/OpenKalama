package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperDX implements KalamaHelperHelperRX {
   Color c;
   Vec3d d;
   float f = 0.25F;
   Vec3d e;

   public float opacity() {
      return this.f;
   }

   public KalamaHelperHelperDX k(Vec3d startVec) {
      this.d = startVec;
      return this;
   }

   public KalamaHelperHelperDX n(float opacity) {
      this.f = opacity;
      return this;
   }

   public Vec3d h() {
      return this.d;
   }

   public KalamaHelperHelperDX m(Color color) {
      this.c = color;
      return this;
   }

   @Override
   public void render(MatrixStack stack, float partialTicks) {
      RenderUtils.r(stack, this.d, this.e, ColorUtils.k(this.c, this.f));
   }

   public KalamaHelperHelperDX l(Vec3d endVec) {
      this.e = endVec;
      return this;
   }

   public Vec3d i() {
      return this.e;
   }

   public KalamaHelperHelperDX(Box box, Color color) {
      this(box.getMinPos(), box.getMaxPos(), color);
   }

   public KalamaHelperHelperDX(Vec3d start, Vec3d end, Color color) {
      this.d = start;
      this.e = end;
      this.c = color;
   }

   public Color d() {
      return this.c;
   }
}
