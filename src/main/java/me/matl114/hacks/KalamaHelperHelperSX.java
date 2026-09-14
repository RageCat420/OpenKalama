package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperSX implements KalamaHelperHelperRX {
   Color c;
   Vec3d g;

   public KalamaHelperHelperSX s(Vec3d vec3d) {
      this.g = vec3d;
      return this;
   }

   public KalamaHelperHelperSX t(Color color) {
      this.c = color;
      return this;
   }

   public Color d() {
      return this.c;
   }

   public KalamaHelperHelperSX(Vec3d vec3d, Color color) {
      this.g = vec3d;
      this.c = color;
   }

   @Override
   public void render(MatrixStack stack, float partialTicks) {
      Vec3d var3 = RenderUtils.getCameraPos();
      Vec3d var4 = this.g.subtract(var3);
      Vec3d var5 = RenderUtils.getTracerOrigin(1.0F);
      RenderUtils.m(stack, var5, var4, this.c);
   }

   public Vec3d r() {
      return this.g;
   }
}
